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
import java.util.HashMap;
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
import org.eclipse.core.runtime.Path;

/**
 * @author nsd
 * 
 * A non-extendable index manager for taglibs similar to the J2EE
 * ITaglibRegistry but lacking any ties to project natures.
 */
public class TaglibIndex {

	class ResourceChangeListener implements IResourceChangeListener {
		public void resourceChanged(IResourceChangeEvent event) {
			// pair deltas with projects
			IResourceDelta[] deltas = new IResourceDelta[]{event.getDelta()};
			IProject[] projects = null;

			if (deltas != null && deltas.length > 0) {
				IResource resource = deltas[0].getResource();

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
				for (int i = 0; i < projects.length; i++) {
					ProjectDescription description = createDescription(projects[i]);
					try {
						deltas[i].accept(description.getVisitor());
					}
					catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	static final boolean _debugChangeListener = true;

	static TaglibIndex _instance;

	static {
		_instance = new TaglibIndex();
	}

	public static void addTaglibIndexListener(ITaglibIndexListener listener) {
	}

	public static void removeTaglibIndexListener(ITaglibIndexListener listener) {
	}

	public static ITaglibRecord resolve(String baseLocation, String reference, boolean crossProjects) {
		return _instance.internalResolve(baseLocation, reference, crossProjects);
	}

	Map fProjectDescriptions;
	private ResourceChangeListener fResourceChangeListener;

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
			// TODO: support outside of the workspace?
			File baseFile = FileBuffers.getSystemFileAtLocation(new Path(baseLocation));
			if (baseFile != null) {
			}
		}
		return resolved;
	}
}
