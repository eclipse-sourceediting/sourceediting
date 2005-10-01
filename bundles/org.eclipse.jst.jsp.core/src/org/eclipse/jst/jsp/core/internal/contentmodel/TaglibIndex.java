/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.sse.core.internal.util.StringUtils;

/**
 * @author nitin
 * 
 * A non-extendable index manager for taglibs similar to the J2EE
 * ITaglibRegistry but lacking any ties to project natures.
 * 
 * Indexing is not persisted between sessions, so new ADD events will be sent
 * to ITaglibIndexListeners during each workbench session. REMOVE events are
 * not fired on workbench shutdown. Events for TAGDIR, JAR, and WEBXML type
 * records are only fired for the .jar and web.xml file itself. The record's
 * contents should be examined for any further information.
 */
public class TaglibIndex {

	static boolean ENABLED = true;

	class ClasspathChangeListener implements IElementChangedListener {
		Stack classpathStack = new Stack();
		List projectsIndexed = new ArrayList(1);

		public void elementChanged(ElementChangedEvent event) {
			if (!isIndexAvailable())
				return;
			classpathStack.clear();
			projectsIndexed.clear();
			elementChanged(event.getDelta());
		}

		private void elementChanged(IJavaElementDelta delta) {
			if (delta.getElement().getElementType() == IJavaElement.JAVA_MODEL) {
				IJavaElementDelta[] changed = delta.getChangedChildren();
				for (int i = 0; i < changed.length; i++) {
					elementChanged(changed[i]);
				}
			}
			else if (delta.getElement().getElementType() == IJavaElement.JAVA_PROJECT) {
				if ((delta.getFlags() & IJavaElementDelta.F_CLASSPATH_CHANGED) != 0) {
					IJavaElement proj = delta.getElement();
					handleClasspathChange((IJavaProject) proj);
				}
			}
		}

		private void handleClasspathChange(IJavaProject project) {
			classpathStack.push(project.getElementName());
			try {
				/* Handle changes to this project's build path */
				IResource resource = project.getCorrespondingResource();
				if (resource.getType() == IResource.PROJECT && !projectsIndexed.contains(resource)) {
					projectsIndexed.add(resource);
					boolean classpathIndexIsOld = fProjectDescriptions.containsKey(resource);
					ProjectDescription description = createDescription((IProject) resource);
					if (classpathIndexIsOld) {
						description.indexClasspath();
					}
				}
				/*
				 * Update indeces for projects who include this project in
				 * their build path (e.g. toggling the "exportation" of a
				 * taglib JAR in this project affects the JAR's visibility in
				 * other projects)
				 */
				IJavaProject[] projects = project.getJavaModel().getJavaProjects();
				for (int i = 0; i < projects.length; i++) {
					IJavaProject otherProject = projects[i];
					if (StringUtils.contains(otherProject.getRequiredProjectNames(), project.getElementName(), false) && !classpathStack.contains(otherProject.getElementName())) {
						handleClasspathChange(otherProject);
					}
				}
			}
			catch (JavaModelException e) {
			}
			classpathStack.pop();
		}
	}

	class ResourceChangeListener implements IResourceChangeListener {
		public void resourceChanged(IResourceChangeEvent event) {
			if (!isIndexAvailable())
				return;
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
									System.out.println("TaglibIndex noticed " + projects[i].getName() + " is about to be deleted/closed"); //$NON-NLS-1$ //$NON-NLS-2$
								}
								ProjectDescription description = (ProjectDescription) fProjectDescriptions.remove(projects[i]);
								if (description != null) {
									if (_debugIndexCreation) {
										System.out.println("removing index of " + description.fProject.getName()); //$NON-NLS-1$
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
										ProjectDescription description = createDescription(projects[i]);
										deltas[i].accept(description.getVisitor());
									}
									if (!projects[i].isAccessible() || (deltas[i] != null && deltas[i].getKind() == IResourceDelta.REMOVED)) {
										if (_debugIndexCreation) {
											System.out.println("TaglibIndex noticed " + projects[i].getName() + " is no longer accessible"); //$NON-NLS-1$ //$NON-NLS-2$
										}
										ProjectDescription description = (ProjectDescription) fProjectDescriptions.remove(projects[i]);
										if (description != null) {
											if (_debugIndexCreation) {
												System.out.println("removing index of " + description.fProject.getName()); //$NON-NLS-1$
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
		}
	}

	static final boolean _debugChangeListener = false;

	static boolean _debugEvents = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/events")); //$NON-NLS-1$ //$NON-NLS-2$
	static boolean _debugIndexCreation = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/indexcreation")); //$NON-NLS-1$ //$NON-NLS-2$
	static final boolean _debugResolution = "true".equals(Platform.getDebugOption("org.eclipse.jst.jsp.core/taglib/resolve")); //$NON-NLS-1$ //$NON-NLS-2$
	static TaglibIndex _instance;

	public static void addTaglibIndexListener(ITaglibIndexListener listener) {
		_instance.internalAddTaglibIndexListener(listener);
	}

	static void fireTaglibRecordEvent(ITaglibRecordEvent event) {
		if (_debugEvents) {
			System.out.println("TaglibIndex fired event:" + event); //$NON-NLS-1$
		}
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

	/**
	 * Returns all of the visible ITaglibRecords for the given path in the
	 * workspace.
	 * 
	 * @param workspacePath
	 * @return
	 */
	public static ITaglibRecord[] getAvailableTaglibRecords(IPath workspacePath) {
		ITaglibRecord[] records = _instance.internalGetAvailableTaglibRecords(workspacePath);
		return records;
	}

	/**
	 * @deprecated - is not correct in flexible projects
	 * @param path
	 * @return
	 */
	public static IPath getContextRoot(IPath path) {
		return _instance.internalGetContextRoot(path);
	}

	public static void removeTaglibIndexListener(ITaglibIndexListener listener) {
		_instance.internalRemoveTaglibIndexListener(listener);
	}

	/**
	 * Find a matching ITaglibRecord given the reference.
	 * 
	 * @param basePath -
	 *            the workspace-relative path for IResources, full filesystem
	 *            path otherwise
	 * @param reference -
	 *            the URI to lookup, usually the uri value from a taglib
	 *            directive
	 * @param crossProjects -
	 *            whether to search across projects (currently ignored)
	 * @return
	 */
	public static ITaglibRecord resolve(String basePath, String reference, boolean crossProjects) {
		ITaglibRecord result = _instance.internalResolve(basePath, reference, crossProjects);
		if (_debugResolution) {
			if (result == null) {
				System.out.println("TaglibIndex could not resolve \"" + reference + "\" from " + basePath); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				switch (result.getRecordType()) {
					case (ITaglibRecord.TLD) : {
						ITLDRecord record = (ITLDRecord) result;
						System.out.println("TaglibIndex resolved " + basePath + ":" + reference + " = " + record.getPath()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
						break;
					case (ITaglibRecord.JAR) : {
						IJarRecord record = (IJarRecord) result;
						System.out.println("TaglibIndex resolved " + basePath + ":" + reference + " = " + record.getLocation()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
						break;
					case (ITaglibRecord.TAGDIR) : {
					}
						break;
					case (ITaglibRecord.URL) : {
						IURLRecord record = (IURLRecord) result;
						System.out.println("TaglibIndex resolved " + basePath + ":" + reference + " = " + record.getURL()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
						break;
				}
			}
		}
		return result;
	}

	public static synchronized void shutdown() {
		if (_instance != null) {
			_instance.stop();
		}
		_instance = null;
	}

	public static synchronized void startup() {
		ENABLED = !"false".equalsIgnoreCase(System.getProperty(TaglibIndex.class.getName()));
		_instance = new TaglibIndex();
	}

	private ClasspathChangeListener fClasspathChangeListener = null;

	Map fProjectDescriptions;
	private ResourceChangeListener fResourceChangeListener;
	private ITaglibIndexListener[] fTaglibIndexListeners = null;

	private TaglibIndex() {
		super();
		fResourceChangeListener = new ResourceChangeListener();
		fClasspathChangeListener = new ClasspathChangeListener();
		if (ENABLED) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
			JavaCore.addElementChangedListener(fClasspathChangeListener);
		}
		fProjectDescriptions = new HashMap();
	}

	/**
	 * @param project
	 * @return
	 */
	synchronized ProjectDescription createDescription(IProject project) {
		ProjectDescription description = (ProjectDescription) fProjectDescriptions.get(project);
		if (description == null) {
			description = new ProjectDescription(project);
			if (ENABLED) {
				description.index();
				description.indexClasspath();
			}
			fProjectDescriptions.put(project, description);
		}
		return description;
	}

	private synchronized void internalAddTaglibIndexListener(ITaglibIndexListener listener) {
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
			ProjectDescription description = createDescription(project);
			List availableRecords = description.getAvailableTaglibRecords(path);
			records = (ITaglibRecord[]) availableRecords.toArray(records);
		}
		return records;
	}

	private IPath internalGetContextRoot(IPath path) {
		IFile baseResource = FileBuffers.getWorkspaceFileAtLocation(path);
		if (baseResource != null) {
			IProject project = baseResource.getProject();
			ProjectDescription description = _instance.createDescription(project);
			IPath rootPath = description.getLocalRoot(baseResource.getFullPath());
			return ResourcesPlugin.getWorkspace().getRoot().getLocation().append(rootPath);
		}
		// try to handle out-of-workspace paths
		IPath root = path;
		while (root != null && !root.isRoot())
			root = root.removeLastSegments(1);
		if (root == null)
			root = path;
		return root;
	}

	private synchronized void internalRemoveTaglibIndexListener(ITaglibIndexListener listener) {
		if (fTaglibIndexListeners != null) {
			List listeners = new ArrayList(Arrays.asList(fTaglibIndexListeners));
			listeners.remove(listener);
			fTaglibIndexListeners = (ITaglibIndexListener[]) listeners.toArray(new ITaglibIndexListener[0]);
		}
	}

	private ITaglibRecord internalResolve(String basePath, final String reference, boolean crossProjects) {
		IProject project = null;
		ITaglibRecord resolved = null;
		IFile baseResource = FileBuffers.getWorkspaceFileAtLocation(new Path(basePath));
		if (baseResource != null) {
			project = baseResource.getProject();
			ProjectDescription description = createDescription(project);
			resolved = description.resolve(basePath, reference);
		}
		else {
			// try simple file support outside of the workspace
			File baseFile = FileBuffers.getSystemFileAtLocation(new Path(basePath));
			if (baseFile != null) {
				final String normalizedReference = URIHelper.normalize(reference, basePath, "/"); //$NON-NLS-1$
				if (normalizedReference != null) {
					ITLDRecord record = new ITLDRecord() {
						public int getRecordType() {
							return ITaglibRecord.TLD;
						}

						public String getURI() {
							return reference;
						}

						public String getPrefix() {
							return null;
						}

						public IPath getPath() {
							return new Path(normalizedReference);
						}
					};
					resolved = record;
				}
			}
		}
		return resolved;
	}

	boolean isIndexAvailable() {
		return _instance != null;
	}

	private void stop() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceChangeListener);
		JavaCore.removeElementChangedListener(fClasspathChangeListener);
		fProjectDescriptions.clear();
	}
}
