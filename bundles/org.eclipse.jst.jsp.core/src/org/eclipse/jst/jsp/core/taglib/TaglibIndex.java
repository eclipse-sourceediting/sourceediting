/*******************************************************************************
 * Copyright (c) 2005,2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.taglib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.osgi.framework.Bundle;

/**
 * A non-extendable index manager for taglibs similar to the previous J2EE
 * ITaglibRegistry but lacking any ties to project natures. Each record
 * returned from the index represents a single tag library descriptor.
 * 
 * Indexing is only persisted between sessions for entries on the Java Build
 * Path. New ADD events will be sent to ITaglibIndexListeners during each
 * workbench session for both cached and newly found records. REMOVE events
 * are not fired on workbench shutdown. The record's contents should be
 * examined for any further information.
 * 
 * @since 1.0
 */
public final class TaglibIndex {
	class ClasspathChangeListener implements IElementChangedListener {
		List projectsIndexed = new ArrayList(1);

		public void elementChanged(ElementChangedEvent event) {
			if (!isIndexAvailable())
				return;
			try {
				LOCK.acquire();
				projectsIndexed.clear();
				elementChanged(event.getDelta(), true);
			}
			finally {
				LOCK.release();
			}
		}

		private void elementChanged(IJavaElementDelta delta, boolean forceUpdate) {
			if (frameworkIsShuttingDown())
				return;

			IJavaElement element = delta.getElement();
			if (element.getElementType() == IJavaElement.JAVA_MODEL) {
				IJavaElementDelta[] changed = delta.getAffectedChildren();
				for (int i = 0; i < changed.length; i++) {
					elementChanged(changed[i], forceUpdate);
				}
			}
			// Handle any changes at the project level
			else if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
				if ((delta.getFlags() & IJavaElementDelta.F_CLASSPATH_CHANGED) != 0) {
					IJavaElement proj = element;
					handleClasspathChange((IJavaProject) proj, forceUpdate);
				}
				else {
					IJavaElementDelta[] deltas = delta.getAffectedChildren();
					if (deltas.length == 0) {
						/*
						 * If project is being deleted or closed, remove the
						 * stored description
						 */
						if (delta.getKind() == IJavaElementDelta.REMOVED || (delta.getFlags() & IJavaElementDelta.F_CLOSED) != 0) {
							IProject project = ((IJavaProject) element).getProject();
							removeDescription(project);
						}
						/*
						 * (else) Without the classpath changing, there's
						 * nothing else to do
						 */
					}
					else {
						for (int i = 0; i < deltas.length; i++) {
							elementChanged(deltas[i], false);
						}
					}
				}
			}
			/*
			 * Other modification to the classpath (such as within a classpath
			 * container like "Web App Libraries") go to the description
			 * itself
			 */
			else if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) != 0 || (delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) != 0) {
				IJavaProject affectedProject = element.getJavaProject();
				if (affectedProject != null) {
					/*
					 * If the affected project has an index on-disk, it's
					 * going to be invalid--we need to create/load the
					 * description so it will be up to date [loading now and
					 * updating is usually faster than regenerating the entire
					 * index]. If there is no index on disk, do nothing more.
					 */
					File indexFile = new File(computeIndexLocation(affectedProject.getProject().getFullPath()));
					if (indexFile.exists()) {
						ProjectDescription affectedDescription = createDescription(affectedProject.getProject());
						if (affectedDescription != null) {
							affectedDescription.handleElementChanged(delta);
						}
					}
					projectsIndexed.add(affectedProject.getProject());
				}
			}
		}

		private void handleClasspathChange(IJavaProject project, boolean forceUpdate) {
			if (frameworkIsShuttingDown())
				return;

			try {
				/* Handle large changes to this project's build path */
				IResource resource = project.getCorrespondingResource();
				if (resource.getType() == IResource.PROJECT && !projectsIndexed.contains(resource)) {
					/*
					 * Use get instead of create since the downstream
					 * (upstream?) project wasn't itself modified.
					 */
					ProjectDescription description = null;
					if (forceUpdate) {
						description = createDescription((IProject) resource);
					}
					else {
						description = getDescription((IProject) resource);
					}
					if (description != null && !frameworkIsShuttingDown()) {
						projectsIndexed.add(resource);
						description.setBuildPathIsDirty();
					}
				}
			}
			catch (JavaModelException e) {
				Logger.logException(e);
			}
		}
	}

	class ResourceChangeListener implements IResourceChangeListener {
		public void resourceChanged(IResourceChangeEvent event) {
			if (!isIndexAvailable())
				return;
			try {
				LOCK.acquire();
				switch (event.getType()) {
					case IResourceChangeEvent.PRE_CLOSE :
					case IResourceChangeEvent.PRE_DELETE : {
						try {
							// pair deltas with projects
							IResourceDelta[] deltas = new IResourceDelta[]{event.getDelta()};
							IProject[] projects = null;

							if (deltas != null && deltas.length > 0) {
								IResource resource = null;
								if (deltas[0] != null) {
									resource = deltas[0].getResource();
								}
								else {
									resource = event.getResource();
								}

								if (resource != null) {
									if (resource.getType() == IResource.ROOT) {
										deltas = deltas[0].getAffectedChildren();
										projects = new IProject[deltas.length];
										for (int i = 0; i < deltas.length; i++) {
											if (deltas[i].getResource().getType() == IResource.PROJECT) {
												projects[i] = (IProject) deltas[i].getResource();
											}
										}
									}
									else {
										projects = new IProject[1];
										if (resource.getType() != IResource.PROJECT) {
											projects[0] = resource.getProject();
										}
										else {
											projects[0] = (IProject) resource;
										}
									}
								}
								for (int i = 0; i < projects.length; i++) {
									if (_debugIndexCreation) {
										Logger.log(Logger.INFO, "TaglibIndex noticed " + projects[i].getName() + " is about to be deleted/closed"); //$NON-NLS-1$ //$NON-NLS-2$
									}
									removeDescription(projects[i]);
								}
							}
						}
						catch (Exception e) {
							Logger.logException("Exception while processing resource deletion", e); //$NON-NLS-1$
						}
					}
					case IResourceChangeEvent.POST_CHANGE : {
						try {
							// pair deltas with projects
							IResourceDelta[] deltas = new IResourceDelta[]{event.getDelta()};
							IProject[] projects = null;

							if (deltas != null && deltas.length > 0) {
								IResource resource = null;
								if (deltas[0] != null) {
									resource = deltas[0].getResource();
								}
								else {
									resource = event.getResource();
								}

								if (resource != null) {
									if (resource.getType() == IResource.ROOT) {
										deltas = deltas[0].getAffectedChildren();
										projects = new IProject[deltas.length];
										for (int i = 0; i < deltas.length; i++) {
											if (deltas[i].getResource().getType() == IResource.PROJECT) {
												projects[i] = (IProject) deltas[i].getResource();
											}
										}
									}
									else {
										projects = new IProject[1];
										if (resource.getType() != IResource.PROJECT) {
											projects[0] = resource.getProject();
										}
										else {
											projects[0] = (IProject) resource;
										}
									}
								}
								for (int i = 0; i < projects.length; i++) {
									try {
										if (deltas[i] != null && deltas[i].getKind() != IResourceDelta.REMOVED && projects[i].isAccessible()) {
											ProjectDescription description = getDescription(projects[i]);
											if (description != null && !frameworkIsShuttingDown()) {
												deltas[i].accept(description.getVisitor());
											}
										}
										if (!projects[i].isAccessible() || (deltas[i] != null && deltas[i].getKind() == IResourceDelta.REMOVED)) {
											if (_debugIndexCreation) {
												Logger.log(Logger.INFO, "TaglibIndex noticed " + projects[i].getName() + " was removed or is no longer accessible"); //$NON-NLS-1$ //$NON-NLS-2$
											}
											removeDescription(projects[i]);
										}
									}
									catch (CoreException e) {
										Logger.logException(e);
									}
								}
							}
						}
						catch (Exception e) {
							Logger.logException("Exception while processing resource change", e); //$NON-NLS-1$
						}
					}
				}
			}
			finally {
				LOCK.release();
			}
		}
	}

	static final boolean _debugChangeListener = false;

	static boolean _debugEvents = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/events")); //$NON-NLS-1$ //$NON-NLS-2$

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation")); //$NON-NLS-1$ //$NON-NLS-2$

	static final boolean _debugResolution = "true".equals(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/resolve")); //$NON-NLS-1$ //$NON-NLS-2$

	static TaglibIndex _instance;

	private static final CRC32 checksumCalculator = new CRC32();

	private static final String CLEAN = "CLEAN";
	private static final String DIRTY = "DIRTY";
	static boolean ENABLED = false;

	static ILock LOCK = Platform.getJobManager().newLock();

	/**
	 * NOT API.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public static void addTaglibIndexListener(ITaglibIndexListener listener) {
		try {
			LOCK.acquire();
			if (_instance != null)
				_instance.internalAddTaglibIndexListener(listener);
		}
		finally {
			LOCK.release();
		}
	}

	static void fireTaglibRecordEvent(ITaglibRecordEvent event) {
		if (_debugEvents) {
			Logger.log(Logger.INFO, "TaglibIndex fired event:" + event); //$NON-NLS-1$
		}
		/*
		 * Flush any shared cache entries, the TaglibControllers should handle
		 * updating their documents as needed.
		 */
		TLDCMDocumentManager.getSharedDocumentCache().remove(TLDCMDocumentManager.getUniqueIdentifier(event.getTaglibRecord()));

		if (_instance != null) {
			ITaglibIndexListener[] listeners = _instance.fTaglibIndexListeners;
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					try {
						listeners[i].indexChanged(event);
					}
					catch (Exception e) {
						Logger.log(Logger.WARNING, e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Finds all of the visible ITaglibRecords for the given path in the
	 * workspace. Taglib mappings from web.xml files are only visible to paths
	 * within the web.xml's corresponding web content folder.
	 * <p>
	 * Values defined within the XML Catalog will not be returned.
	 * </p>
	 * 
	 * @param fullPath -
	 *            a path within the workspace
	 * @return All of the visible ITaglibRecords from the given path.
	 */
	public static ITaglibRecord[] getAvailableTaglibRecords(IPath fullPath) {
		try {
			LOCK.acquire();
			ITaglibRecord[] records = null;
			if (_instance != null)
				records = _instance.internalGetAvailableTaglibRecords(fullPath);
			else
				records = new ITaglibRecord[0];
			return records;
		}
		finally {
			LOCK.release();
		}
	}

	/**
	 * Returns the IPath considered to be the web-app root for the given path.
	 * All resolution from the given path beginning with '/' will be relative
	 * to the computed web-app root.
	 * 
	 * @deprecated - is not correct in flexible projects
	 * @param path -
	 *            a path under the web-app root
	 * @return the IPath considered to be the web-app's root for the given
	 *         path
	 */
	public static IPath getContextRoot(IPath path) {
		try {
			LOCK.acquire();
			return _instance.internalGetContextRoot(path);
		}
		finally {
			LOCK.release();
		}
	}

	private String getState() {
		String state = JSPCorePlugin.getDefault().getPluginPreferences().getString(TaglibIndex.class.getName());
		return state;
	}

	/**
	 * NOT API.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public static void removeTaglibIndexListener(ITaglibIndexListener listener) {
		try {
			LOCK.acquire();
			if (_instance != null)
				_instance.internalRemoveTaglibIndexListener(listener);
		}
		finally {
			LOCK.release();
		}
	}

	/**
	 * Finds a matching ITaglibRecord given the reference. Typically the
	 * result will have to be cast to a subiinterface of ITaglibRecord.
	 * 
	 * @param basePath -
	 *            the workspace-relative path for IResources, full filesystem
	 *            path otherwise
	 * @param reference -
	 *            the URI to lookup, for example the uri value from a taglib
	 *            directive
	 * @param crossProjects -
	 *            whether to search across projects (currently ignored)
	 * 
	 * @return a visible ITaglibRecord or null if the reference points to no
	 *         known tag library descriptor
	 * 
	 * @See ITaglibRecord
	 */
	public static ITaglibRecord resolve(String basePath, String reference, boolean crossProjects) {
		ITaglibRecord result = null;
		try {
			LOCK.acquire();
			if (_instance != null)
				result = _instance.internalResolve(basePath, reference, crossProjects);
		}
		finally {
			LOCK.release();
		}
		if (_debugResolution) {
			if (result == null) {
				Logger.log(Logger.INFO, "TaglibIndex could not resolve \"" + reference + "\" from " + basePath); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				switch (result.getRecordType()) {
					case (ITaglibRecord.TLD) : {
						ITLDRecord record = (ITLDRecord) result;
						Logger.log(Logger.INFO, "TaglibIndex resolved " + basePath + ":" + reference + " = " + record.getPath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
						break;
					case (ITaglibRecord.JAR) : {
						IJarRecord record = (IJarRecord) result;
						Logger.log(Logger.INFO, "TaglibIndex resolved " + basePath + ":" + reference + " = " + record.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
						break;
					case (ITaglibRecord.TAGDIR) : {
					}
						break;
					case (ITaglibRecord.URL) : {
						IURLRecord record = (IURLRecord) result;
						Logger.log(Logger.INFO, "TaglibIndex resolved " + basePath + ":" + reference + " = " + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
						break;
				}
			}
		}
		return result;
	}

	/**
	 * Removes index files. Used for maintenance and keeping the index folder
	 * a manageable size.
	 * 
	 * @param staleOnly -
	 *            if <b>true</b>, removes only the indexes for projects not
	 *            open in the workspace, if <b>false</b>, removes all of the
	 *            indexes
	 */
	private void removeIndexes(boolean staleOnly) {
		String osPath = getTaglibIndexStateLocation().toOSString();
		File folder = new File(osPath);
		if (!folder.isDirectory()) {
			try {
				folder.mkdir();
			}
			catch (SecurityException e) {
			}
		}

		// remove any extraneous index files
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List indexNames = new ArrayList(projects.length);
		if (staleOnly) {
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].isAccessible()) {
					indexNames.add(computeIndexName(projects[i].getFullPath()));
				}
			}
		}

		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (int i = 0; files != null && i < files.length; i++) {
				if (!indexNames.contains(files[i].getName()))
					files[i].delete();
			}
		}
	}

	/**
	 * Instructs the index to stop listening for resource and classpath
	 * changes, and to forget all information about the workspace.
	 */
	public static synchronized void shutdown() {
		try {
			LOCK.acquire();
			if (_instance != null) {
				_instance.stop();
			}
			_instance = null;
		}
		finally {
			LOCK.release();
		}
	}

	/**
	 * Instructs the index to begin listening for resource and classpath
	 * changes.
	 */
	public static synchronized void startup() {
		try {
			LOCK.acquire();
			ENABLED = !"false".equalsIgnoreCase(System.getProperty(TaglibIndex.class.getName())); //$NON-NLS-1$
			_instance = new TaglibIndex();
		}
		finally {
			LOCK.release();
		}
	}

	private ClasspathChangeListener fClasspathChangeListener = null;

	Map fProjectDescriptions;

	private ResourceChangeListener fResourceChangeListener;

	private ITaglibIndexListener[] fTaglibIndexListeners = null;
	/** symbolic name for OSGI framework */
	private final String OSGI_FRAMEWORK_ID = "org.eclipse.osgi"; //$NON-NLS-1$

	private TaglibIndex() {
		super();

		/*
		 * Only consider a crash if a value exists and is DIRTY (not a new
		 * workspace)
		 */
		if (DIRTY.equalsIgnoreCase(getState())) {
			Logger.log(Logger.WARNING, "A workspace crash was detected. The previous session did not exit normally. Not using saved tag library indexes"); //$NON-NLS-3$
			removeIndexes(false);
		}

		fProjectDescriptions = new Hashtable();
		fResourceChangeListener = new ResourceChangeListener();
		fClasspathChangeListener = new ClasspathChangeListener();
		if (ENABLED) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
			JavaCore.addElementChangedListener(fClasspathChangeListener);
		}
	}

	/**
	 * Based on org.eclipse.jdt.internal.core.search.indexing.IndexManager
	 * 
	 * @param containerPath
	 * @return
	 */
	String computeIndexLocation(IPath containerPath) {
		String fileName = computeIndexName(containerPath);
		if (_debugIndexCreation)
			Logger.log(Logger.INFO, "-> index name for " + containerPath + " is " + fileName); //$NON-NLS-1$ //$NON-NLS-2$
		String indexLocation = getTaglibIndexStateLocation().append(fileName).toOSString();
		return indexLocation;
	}

	String computeIndexName(IPath containerPath) {
		checksumCalculator.reset();
		checksumCalculator.update(containerPath.toOSString().getBytes());
		// use ".dat" so we're not confused with JDT indexes
		String fileName = Long.toString(checksumCalculator.getValue()) + ".dat"; //$NON-NLS-1$
		return fileName;
	}

	/**
	 * @param project
	 * @return
	 */
	ProjectDescription createDescription(IProject project) {
		ProjectDescription description = null;
		description = (ProjectDescription) fProjectDescriptions.get(project);
		if (description == null) {
			// Once we've started indexing, we're dirty again
			if (fProjectDescriptions.isEmpty()) {
				setState(DIRTY);
			}
			description = new ProjectDescription(project, computeIndexLocation(project.getFullPath()));
			fProjectDescriptions.put(project, description);
		}
		return description;
	}

	/**
	 * A check to see if the OSGI framework is shutting down.
	 * 
	 * @return true if the System Bundle is stopped (ie. the framework is
	 *         shutting down)
	 */
	boolean frameworkIsShuttingDown() {
		// in the Framework class there's a note:
		// set the state of the System Bundle to STOPPING.
		// this must be done first according to section 4.19.2 from the OSGi
		// R3 spec.
		boolean shuttingDown = !Platform.isRunning() || Platform.getBundle(OSGI_FRAMEWORK_ID).getState() == Bundle.STOPPING;
		return shuttingDown;
	}

	ProjectDescription getDescription(IProject project) {
		ProjectDescription description = null;
		description = (ProjectDescription) fProjectDescriptions.get(project);
		return description;
	}

	private IPath getTaglibIndexStateLocation() {
		return JSPCorePlugin.getDefault().getStateLocation().append("taglibindex/");
	}

	private void internalAddTaglibIndexListener(ITaglibIndexListener listener) {
		if (fTaglibIndexListeners == null) {
			fTaglibIndexListeners = new ITaglibIndexListener[]{listener};
		}
		else {
			List listeners = new ArrayList(Arrays.asList(fTaglibIndexListeners));
			listeners.add(listener);
			fTaglibIndexListeners = (ITaglibIndexListener[]) listeners.toArray(new ITaglibIndexListener[0]);
		}
	}

	private ITaglibRecord[] internalGetAvailableTaglibRecords(IPath path) {
		ITaglibRecord[] records = new ITaglibRecord[0];
		if (path.segmentCount() > 0) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
			if(project.isAccessible()) {
				ProjectDescription description = createDescription(project);
				List availableRecords = description.getAvailableTaglibRecords(path);
	
				// ICatalog catalog =
				// XMLCorePlugin.getDefault().getDefaultXMLCatalog();
				// while (catalog != null) {
				// ICatalogEntry[] entries = catalog.getCatalogEntries();
				// for (int i = 0; i < entries.length; i++) {
				// // System.out.println(entries[i].getURI());
				// }
				// INextCatalog[] nextCatalogs = catalog.getNextCatalogs();
				// for (int i = 0; i < nextCatalogs.length; i++) {
				// ICatalogEntry[] entries2 =
				// nextCatalogs[i].getReferencedCatalog().getCatalogEntries();
				// for (int j = 0; j < entries2.length; j++) {
				// // System.out.println(entries2[j].getURI());
				// }
				// }
				// }
	
				records = (ITaglibRecord[]) availableRecords.toArray(records);
			}
		}
		return records;
	}

	private IPath internalGetContextRoot(IPath path) {
		IFile baseResource = FileBuffers.getWorkspaceFileAtLocation(path);
		if (baseResource != null) {
			IProject project = baseResource.getProject();
			ProjectDescription description = _instance.createDescription(project);
			IPath rootPath = description.getLocalRoot(baseResource.getFullPath());
			return rootPath;
		}
		// try to handle out-of-workspace paths
		IPath root = path;
		while (root != null && !root.isRoot())
			root = root.removeLastSegments(1);
		if (root == null)
			root = path;
		return root;
	}

	private void internalRemoveTaglibIndexListener(ITaglibIndexListener listener) {
		if (fTaglibIndexListeners != null) {
			List listeners = new ArrayList(Arrays.asList(fTaglibIndexListeners));
			listeners.remove(listener);
			fTaglibIndexListeners = (ITaglibIndexListener[]) listeners.toArray(new ITaglibIndexListener[0]);
		}
	}

	private ITaglibRecord internalResolve(String basePath, final String reference, boolean crossProjects) {
		IProject project = null;
		ITaglibRecord resolved = null;

		Path baseIPath = new Path(basePath);
		IResource baseResource = FileBuffers.getWorkspaceFileAtLocation(baseIPath);

		if (baseResource == null) {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			// Try the base path as a folder first
			if (baseResource == null && baseIPath.segmentCount() > 1) {
				baseResource = workspaceRoot.getFolder(baseIPath);
			}
			// If not a folder, then try base path as a file
			if (baseResource != null && !baseResource.exists() && baseIPath.segmentCount() > 1) {
				baseResource = workspaceRoot.getFile(baseIPath);
			}
			if (baseResource == null && baseIPath.segmentCount() == 1) {
				baseResource = workspaceRoot.getProject(baseIPath.segment(0));
			}
		}

		if (baseResource == null) {
			/*
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=116529
			 * 
			 * This method produces a less accurate result, but doesn't
			 * require that the file exist yet.
			 */
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(baseIPath);
			if (files.length > 0)
				baseResource = files[0];
		}
		if (baseResource != null && baseResource.isAccessible()) {
			project = baseResource.getProject();
			ProjectDescription description = createDescription(project);
			resolved = description.resolve(basePath, reference);
		}

		return resolved;
	}

	boolean isIndexAvailable() {
		return _instance != null && ENABLED;
	}

	private void setState(String state) {
		if (!state.equals(getState())) {
			JSPCorePlugin.getDefault().getPluginPreferences().setValue(TaglibIndex.class.getName(), state);
			JSPCorePlugin.getDefault().savePluginPreferences();
		}
	}

	private void stop() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceChangeListener);
		JavaCore.removeElementChangedListener(fClasspathChangeListener);

		/*
		 * Clearing the existing saved states helps prune dead data from the
		 * index folder.
		 */
		removeIndexes(true);

		Iterator i = fProjectDescriptions.values().iterator();
		while (i.hasNext()) {
			ProjectDescription description = (ProjectDescription) i.next();
			description.saveReferences();
		}

		fProjectDescriptions.clear();

		setState(CLEAN);
	}

	void removeDescription(IProject project) {
		ProjectDescription description = (ProjectDescription) fProjectDescriptions.remove(project);
		if (description != null) {
			if (_debugIndexCreation) {
				Logger.log(Logger.INFO, "removing index of " + description.fProject.getName()); //$NON-NLS-1$
			}
			description.setBuildPathIsDirty();
			description.clear();
			description.saveReferences();
		}
	}
}
