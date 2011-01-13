/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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
import java.lang.ref.Reference;
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
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.wst.sse.core.internal.util.AbstractMemoryListener;
import org.osgi.framework.Bundle;
import org.osgi.service.event.Event;

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
				if (_debugEvents) {
					Logger.log(Logger.INFO, "TaglibIndex responding to:" + event); //$NON-NLS-1$
				}
				projectsIndexed.clear();
				elementChanged(event.getDelta(), true);
				fireCurrentDelta(event);
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
					handleClasspathChange((IJavaProject) proj, delta, forceUpdate);
				}
				else {
					IJavaElementDelta[] deltas = delta.getAffectedChildren();
					if (deltas.length == 0) {
						if (delta.getKind() == IJavaElementDelta.REMOVED || (delta.getFlags() & IJavaElementDelta.F_CLOSED) != 0) {
							/*
							 * If the project is being deleted or closed, just
							 * remove the description
							 */
							IJavaProject proj = (IJavaProject) element;
							ProjectDescription description = (ProjectDescription) fProjectDescriptions.remove(proj.getProject());
							if (description != null) {
								if (_debugIndexCreation) {
									Logger.log(Logger.INFO, "removing index of " + description.fProject.getName()); //$NON-NLS-1$
								}
								// removing the index file ensures that we
								// don't get stale data if the project is
								// reopened
								removeIndexFile(proj.getProject());
							}
						}
					}
					/*
					 * (else) Without the classpath changing, there's nothing
					 * else to do
					 */
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

		private void handleClasspathChange(IJavaProject project, IJavaElementDelta delta, boolean forceUpdate) {
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
						description.queueElementChanged(delta);
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
				if (_debugEvents) {
					Logger.log(Logger.INFO, "TaglibIndex responding to:" + event + "\n" + event.getDelta()); //$NON-NLS-2$ //$NON-NLS-1$
				}
				switch (event.getType()) {
					case IResourceChangeEvent.PRE_CLOSE :
					case IResourceChangeEvent.PRE_DELETE : {
						try {
							// pair deltas with projects
							IResourceDelta[] deltas = new IResourceDelta[]{event.getDelta()};
							IProject[] projects = null;

							if (deltas.length > 0) {
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
									ProjectDescription description = (ProjectDescription) fProjectDescriptions.remove(projects[i]);
									if (description != null) {
										if (_debugIndexCreation) {
											Logger.log(Logger.INFO, "removing index of " + description.fProject.getName()); //$NON-NLS-1$
										}
										description.clear();
									}
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

							if (deltas.length > 0) {
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
											ProjectDescription description = (ProjectDescription) fProjectDescriptions.remove(projects[i]);
											if (description != null) {
												if (_debugIndexCreation) {
													Logger.log(Logger.INFO, "removing index of " + description.fProject.getName()); //$NON-NLS-1$
												}
												description.clear();
											}
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

				fireCurrentDelta(event);
			}
			finally {
				LOCK.release();
			}
		}
	}

	/**
	 * <p>A {@link AbstractMemoryListener} that clears the {@link ProjectDescription} cache
	 * whenever specific memory events are received.</p>
	 * 
	 * <p>Events:
	 * <ul>
	 * <li>{@link AbstractMemoryListener#SEV_SERIOUS}</li>
	 * <li>{@link AbstractMemoryListener#SEV_CRITICAL}</li>
	 * </ul>
	 * </p>
	 */
	private class MemoryListener extends AbstractMemoryListener {
		/**
		 * <p>Constructor causes this listener to listen for specific memory events.</p>
		 * <p>Events:
		 * <ul>
		 * <li>{@link AbstractMemoryListener#SEV_SERIOUS}</li>
		 * <li>{@link AbstractMemoryListener#SEV_CRITICAL}</li>
		 * </ul>
		 * </p>
		 */
		MemoryListener() {
			super(new String[] { SEV_SERIOUS, SEV_CRITICAL });
		}
		
		/**
		 * On any memory event we handle clear out the project descriptions
		 * 
		 * @see org.eclipse.jst.jsp.core.internal.util.AbstractMemoryListener#handleMemoryEvent(org.osgi.service.event.Event)
		 */
		protected void handleMemoryEvent(Event event) {
			clearProjectDescriptions();
		}
		
	}
	
	static final boolean _debugChangeListener = false;

	static boolean _debugEvents = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/events")); //$NON-NLS-1$ //$NON-NLS-2$

	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation")); //$NON-NLS-1$ //$NON-NLS-2$

	static final boolean _debugResolution = "true".equals(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/resolve")); //$NON-NLS-1$ //$NON-NLS-2$

	static TaglibIndex _instance = new TaglibIndex();

	private boolean initialized;

	private static final CRC32 checksumCalculator = new CRC32();

	private static final String CLEAN = "CLEAN";
	private static final String DIRTY = "DIRTY";
	static boolean ENABLED = false;

	static final ILock LOCK = Job.getJobManager().newLock();

	/**
	 * NOT API.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public static void addTaglibIndexListener(ITaglibIndexListener listener) {
		if (getInstance().isInitialized())
			getInstance().internalAddTaglibIndexListener(listener);
	}

	static void fireTaglibDelta(ITaglibIndexDelta delta) {
		if (_debugEvents) {
			Logger.log(Logger.INFO, "TaglibIndex fired delta:" + delta + " [" + delta.getAffectedChildren().length + "]\n" + ((TaglibIndexDelta) delta).trigger); //$NON-NLS-1$
		}
		/*
		 * Flush any shared cache entries, the TaglibControllers should handle
		 * updating their documents as needed.
		 */
		ITaglibIndexDelta[] deltas = delta.getAffectedChildren();
		for (int i = 0; i < deltas.length; i++) {
			ITaglibRecord taglibRecord = deltas[i].getTaglibRecord();
			if (taglibRecord != null) {
				Object uniqueIdentifier = TLDCMDocumentManager.getUniqueIdentifier(taglibRecord);
				if (uniqueIdentifier != null) {
					TLDCMDocumentManager.getSharedDocumentCache().remove(uniqueIdentifier);
				}
				else {
					Logger.log(Logger.ERROR, "identifier for " + taglibRecord + " was null");
				}
			}
		}
		synchronized (TLDCMDocumentManager.getSharedDocumentCache()) {
			Iterator values = TLDCMDocumentManager.getSharedDocumentCache().values().iterator();
			while (values.hasNext()) {
				Object o = values.next();
				if (o instanceof Reference) {
					values.remove();
				}
			}
		}

		if (_instance.isInitialized()) {
			ITaglibIndexListener[] listeners = _instance.fTaglibIndexListeners;
			if (listeners != null) {
				for (int j = 0; j < listeners.length; j++) {
					try {
						listeners[j].indexChanged(delta);
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
	 * This method will almost certainly require a workspace lock to complete.
	 * 
	 * @param fullPath -
	 *            a path within the workspace
	 * @return All of the visible ITaglibRecords from the given path.
	 */
	public static ITaglibRecord[] getAvailableTaglibRecords(IPath fullPath) {
		if (!_instance.isInitialized()) {
			return new ITaglibRecord[0];
		}
		ITaglibRecord[] records = null;
		if (getInstance().isInitialized()) {
			records = getInstance().internalGetAvailableTaglibRecords(fullPath);
		}
		else {
			records = new ITaglibRecord[0];
		}
		getInstance().fireCurrentDelta("enumerate: " + fullPath); //$NON-NLS-1$
		return records;
	}

	/**
	 * Returns the IPath considered to be the web-app root for the given path.
	 * All resolution from the given path beginning with '/' will be relative
	 * to the computed web-app root.
	 * 
	 * @deprecated - is not correct in flexible projects, use the {@link org.eclipse.jst.jsp.core.internal.util.FacetModuleCoreSupport} class instead
	 * @param path -
	 *            a path under the web-app root
	 * @return the IPath considered to be the web-app's root for the given
	 *         path or null if one could not be determined
	 */
	public static IPath getContextRoot(IPath path) {
		try {
			LOCK.acquire();
			if (getInstance().isInitialized()) {
				return getInstance().internalGetContextRoot(path);
			}
		}
		finally {
			LOCK.release();
		}
		return null;
	}

	public static TaglibIndex getInstance() {
		return _instance;
	}

	/**
	 * NOT API.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public static void removeTaglibIndexListener(ITaglibIndexListener listener) {
		if (!getInstance().isInitialized())
			return;
		if (getInstance().isInitialized())
			getInstance().internalRemoveTaglibIndexListener(listener);
	}

	/**
	 * Finds a matching ITaglibRecord given the reference. Typically the
	 * result will have to be cast to a subinterface of ITaglibRecord. This
	 * method will almost certainly require a workspace lock to complete.
	 * 
	 * @param basePath
	 *            - the workspace-relative path for IResources, full
	 *            filesystem path otherwise
	 * @param reference
	 *            - the URI to lookup, for example the uri value from a taglib
	 *            directive
	 * @param crossProjects
	 *            - whether to search across projects (currently ignored)
	 * 
	 * @return a visible ITaglibRecord or null if the reference points to no
	 *         known tag library descriptor
	 * 
	 * @See ITaglibRecord
	 */
	public static ITaglibRecord resolve(String basePath, String reference, boolean crossProjects) {
		ITaglibRecord result = null;
		if (getInstance().isInitialized()) {
			result = getInstance().internalResolve(basePath, reference, crossProjects);
		}
		getInstance().fireCurrentDelta("resolve: " + reference); //$NON-NLS-1$
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
						ITagDirRecord record = (ITagDirRecord) result;
						Logger.log(Logger.INFO, "TaglibIndex resolved " + basePath + ":" + reference + " = " + record.getPath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
	 * Instructs the index to stop listening for resource and classpath
	 * changes, and to forget all information about the workspace.
	 */
	public static void shutdown() {
		try {
			LOCK.acquire();
			if (getInstance().isInitialized()) {
				getInstance().stop();
			}
		}
		finally {
			LOCK.release();
		}
	}

	/**
	 * Instructs the index to begin listening for resource and classpath
	 * changes.
	 */
	public static void startup() {
		boolean shuttingDown = !Platform.isRunning() || Platform.getBundle(OSGI_FRAMEWORK_ID).getState() == Bundle.STOPPING;
		if (!shuttingDown) {
			try {
				LOCK.acquire();
				ENABLED = !"false".equalsIgnoreCase(System.getProperty(TaglibIndex.class.getName())); //$NON-NLS-1$
				getInstance().initializeInstance();
			}
			finally {
				LOCK.release();
			}
		}
	}

	private ClasspathChangeListener fClasspathChangeListener = null;

	private TaglibIndexDelta fCurrentTopLevelDelta = null;

	Map fProjectDescriptions = null;

	private ResourceChangeListener fResourceChangeListener;

	private ITaglibIndexListener[] fTaglibIndexListeners = null;
	
	/**
	 * Used to keep the {@link ProjectDescription} cache clean when memory is low
	 */
	private MemoryListener fMemoryListener;

	/** symbolic name for OSGI framework */
	private final static String OSGI_FRAMEWORK_ID = "org.eclipse.osgi"; //$NON-NLS-1$

	private TaglibIndex() {
		super();
	}

	private void initializeInstance() {

		if (isInitialized())
			return;
		try {
			LOCK.acquire();
			/*
			 * check again, just incase it was initialized on another thread,
			 * while we were waiting for the lock
			 */
			if (!isInitialized()) {
				getWorkingLocation();
				/*
				 * Only consider a crash if a value exists and is DIRTY (not a
				 * new workspace)
				 */
				if (DIRTY.equalsIgnoreCase(getState())) {
					Logger.log(Logger.ERROR, "A workspace crash was detected. The previous session did not exit normally. Not using saved taglib indexes."); //$NON-NLS-3$
					removeIndexes(false);
				}

				fProjectDescriptions = new Hashtable();
				fResourceChangeListener = new ResourceChangeListener();
				fClasspathChangeListener = new ClasspathChangeListener();
				fMemoryListener = new MemoryListener();

				if (ENABLED) {
					ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
					JavaCore.addElementChangedListener(fClasspathChangeListener);
					//register the memory listener
					fMemoryListener.connect();
				}
				setIntialized(true);
			}
		}
		finally {
			LOCK.release();
		}
	}

	/**
	 * Adds the given delta as a child to an overall delta
	 * 
	 * @param delta
	 */
	synchronized void addDelta(ITaglibIndexDelta delta) {
		ensureDelta(delta.getProject()).addChildDelta(delta);
	}

	/**
	 * Based on org.eclipse.jdt.internal.core.search.indexing.IndexManager
	 * 
	 * @param containerPath
	 * @return the index file location for the given workspace path
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
	 * @return the ProjectDescription representing the given project
	 */
	ProjectDescription createDescription(IProject project) {
		if (fProjectDescriptions == null)
			return null;

		ProjectDescription description = null;
		try {
			LOCK.acquire();
			description = (ProjectDescription) fProjectDescriptions.get(project);
			if (description == null) {
				// Once we've started indexing, we're dirty again
				if (fProjectDescriptions.isEmpty()) {
					setState(DIRTY);
				}
				description = new ProjectDescription(project, computeIndexLocation(project.getFullPath()));
				fProjectDescriptions.put(project, description);
			}
		}
		finally {
			LOCK.release();
		}
		return description;
	}

	/**
	 * Ensures that a delta exists for holding index change information
	 */
	private TaglibIndexDelta ensureDelta(IProject project) {
		/*
		 * The first delta to be added will determine which project the
		 * top-level delta will contain.
		 */
		if (fCurrentTopLevelDelta == null) {
			fCurrentTopLevelDelta = new TaglibIndexDelta(project, null, ITaglibIndexDelta.CHANGED);
		}
		return fCurrentTopLevelDelta;
	}

	void fireCurrentDelta(Object trigger) {
		if (fCurrentTopLevelDelta != null) {
			fCurrentTopLevelDelta.trigger = trigger;
			ITaglibIndexDelta delta = fCurrentTopLevelDelta;
			fCurrentTopLevelDelta = null;
			fireTaglibDelta(delta);
		}
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
		if (isInitialized()) {
			description = (ProjectDescription) fProjectDescriptions.get(project);
		}
		return description;
	}

	private String getState() {
		String state = JSPCorePlugin.getDefault().getPluginPreferences().getString(TaglibIndex.class.getName());
		return state;
	}

	private IPath getTaglibIndexStateLocation() {
		return JSPCorePlugin.getDefault().getStateLocation().append("taglibindex/");
	}

	private void internalAddTaglibIndexListener(ITaglibIndexListener listener) {
		try {
			LOCK.acquire();
			if (fTaglibIndexListeners == null) {
				fTaglibIndexListeners = new ITaglibIndexListener[]{listener};
			}
			else {
				List listeners = new ArrayList(Arrays.asList(fTaglibIndexListeners));
				if (!listeners.contains(listener)) {
					listeners.add(listener);
				}
				fTaglibIndexListeners = (ITaglibIndexListener[]) listeners.toArray(new ITaglibIndexListener[0]);
			}
		}
		finally {
			LOCK.release();
		}
	}

	private ITaglibRecord[] internalGetAvailableTaglibRecords(IPath path) {
		ITaglibRecord[] records = new ITaglibRecord[0];
		if (path.segmentCount() > 0) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
			if (project.isAccessible()) {
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
		if (baseResource != null && baseResource.getProject().isAccessible()) {
			IProject project = baseResource.getProject();
			ProjectDescription description = getInstance().createDescription(project);
			IPath rootPath = description.getLocalRoot(baseResource.getFullPath());
			return rootPath;
		}
		// try to handle out-of-workspace paths
		IPath root = path.makeAbsolute();
		while (root.segmentCount() > 0 && !root.isRoot())
			root = root.removeLastSegments(1);
		return root;
	}

	private void internalRemoveTaglibIndexListener(ITaglibIndexListener listener) {
		try {
			LOCK.acquire();
			if (fTaglibIndexListeners != null) {
				List listeners = new ArrayList(Arrays.asList(fTaglibIndexListeners));
				listeners.remove(listener);
				fTaglibIndexListeners = (ITaglibIndexListener[]) listeners.toArray(new ITaglibIndexListener[0]);
			}
		}
		finally {
			LOCK.release();
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
			if (baseIPath.segmentCount() > 1) {
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
		if (baseResource != null) {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(baseIPath.segment(0));
			if (project.isAccessible()) {
				ProjectDescription description = createDescription(project);
				resolved = description.resolve(basePath, reference);
			}
		}

		return resolved;
	}

	boolean isIndexAvailable() {
		return _instance.isInitialized() && ENABLED;
	}

	/**
	 * Removes index file for the given project.
	 */
	void removeIndexFile(IProject project) {
		File indexFile = new File(computeIndexLocation(project.getFullPath()));
		if (indexFile.exists()) {
			indexFile.delete();
		}
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
		File folder = getWorkingLocation();

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

	private void setState(String state) {
		if (!state.equals(getState())) {
			JSPCorePlugin.getDefault().getPluginPreferences().setValue(TaglibIndex.class.getName(), state);
			JSPCorePlugin.getDefault().savePluginPreferences();
		}
	}

	private void stop() {
		if (isInitialized()) {
			setIntialized(false);

			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceChangeListener);
			JavaCore.removeElementChangedListener(fClasspathChangeListener);
			//unregister the memory listener
			fMemoryListener.disconnect();

			/*
			 * Clearing the existing saved states helps prune dead data from
			 * the index folder.
			 */
			removeIndexes(true);

			clearProjectDescriptions();

			setState(CLEAN);
			fProjectDescriptions = null;
			fResourceChangeListener = null;
			fClasspathChangeListener = null;
			fMemoryListener = null;
		}
	}

	/**
	 * Get the working location for the taglib index
	 * @return The File representing the taglib index's working location
	 */
	private File getWorkingLocation() {
		File folder = new File(getTaglibIndexStateLocation().toOSString());
		if (!folder.isDirectory()) {
			try {
				folder.mkdir();
			}
			catch (SecurityException e) {
			}
		}
		return folder;
	}

	/**
	 * Have all of the ProjectDescriptions write their information to disk and
	 * then clear our map of them
	 */
	void clearProjectDescriptions() {
		try {
			LOCK.acquire();
			Iterator i = fProjectDescriptions.values().iterator();
			while (i.hasNext()) {
				ProjectDescription description = (ProjectDescription) i.next();
				description.saveReferences();
			}

			fProjectDescriptions.clear();
		} finally {
			LOCK.release();
		}
	}

	private boolean isInitialized() {
		return initialized;
	}

	private void setIntialized(boolean intialized) {
		this.initialized = intialized;
	}
}
