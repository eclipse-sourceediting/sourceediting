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
package org.eclipse.jst.jsp.core.contentmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;

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

	class ResourceChangeListener implements IResourceChangeListener {
		public void resourceChanged(IResourceChangeEvent event) {
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
							if (deltas[i] != null) {
								ProjectDescription description = createDescription(projects[i]);
								deltas[i].accept(description.getVisitor());
							}
							if (!projects[i].isAccessible()) {
								ProjectDescription description = (ProjectDescription) fProjectDescriptions.remove(projects[i]);
								if (description != null) {
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

	static final boolean _debugChangeListener = true;

	static TaglibIndex _instance;

	static {
		_instance = new TaglibIndex();
	}

	public static void addTaglibIndexListener(ITaglibIndexListener listener) {
		_instance.internalAddTaglibIndexListener(listener);
	}

	static void fireTaglibRecordEvent(ITaglibRecordEvent event) {
		ITaglibIndexListener[] listeners = _instance.fTaglibIndexListeners;
		if (listeners != null) {
			for (int i = 0; i < listeners.length; i++) {
				listeners[i].indexChanged(event);
			}
		}
	}

	/**
	 * Returns all of the visible ITaglibRecords for the given filepath in the
	 * workspace.
	 * 
	 * @param workspacePath
	 * @return
	 */
	public static ITaglibRecord[] getAvailableTaglibRecords(IPath workspacePath) {
		ITaglibRecord[] records = _instance.internalGetAvailableTaglibRecords(workspacePath);
		return records;
	}

	public static IPath getContextRoot(IPath path) {
		return _instance.internalGetContextRoot(path);
	}

	public static void removeTaglibIndexListener(ITaglibIndexListener listener) {
		_instance.internalRemoveTaglibIndexListener(listener);
	}

	public static ITaglibRecord resolve(String baseLocation, String reference, boolean crossProjects) {
		return _instance.internalResolve(baseLocation, reference, crossProjects);
	}

	Map fProjectDescriptions;
	private ResourceChangeListener fResourceChangeListener;

	private ITaglibIndexListener[] fTaglibIndexListeners = null;

	private TaglibIndex() {
		super();
		fResourceChangeListener = new ResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceChangeListener);
		fProjectDescriptions = new HashMap();
	}

	/**
	 * @param project
	 * @return
	 */
	ProjectDescription createDescription(IProject project) {
		ProjectDescription description = (ProjectDescription) fProjectDescriptions.get(project);
		if (description == null) {
			description = new ProjectDescription(project);
			description.index();
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

	private ITaglibRecord[] internalGetAvailableTaglibRecords(IPath location) {
		ITaglibRecord[] records = null;
		IFile baseResource = ResourcesPlugin.getWorkspace().getRoot().getFile(location);
		if (baseResource != null) {
			IProject project = baseResource.getProject();
			ProjectDescription description = createDescription(project);
			List availableRecords = description.getAvailableTaglibRecords(location);
			records = (ITaglibRecord[]) availableRecords.toArray(records);
		}
		else {
			records = new ITaglibRecord[0];
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

	private ITaglibRecord internalResolve(String baseLocation, String reference, boolean crossProjects) {
		IProject project = null;
		ITaglibRecord resolved = null;
		IFile baseResource = FileBuffers.getWorkspaceFileAtLocation(new Path(baseLocation));
		if (baseResource != null) {
			project = baseResource.getProject();
			ProjectDescription description = createDescription(project);
			resolved = description.resolve(baseLocation, reference);
		}
		else {
			// try simple file support outside of the workspace
			File baseFile = FileBuffers.getSystemFileAtLocation(new Path(baseLocation));
			if (baseFile != null) {
				String normalizedReference = URIHelper.normalize(reference, baseLocation, "/"); //$NON-NLS-1$
				if (normalizedReference != null) {
					TLDRecord record = new TLDRecord();
					record.location = new Path(normalizedReference);
					record.uri = reference;
					resolved = record;
				}
			}
		}
		return resolved;
	}
}
