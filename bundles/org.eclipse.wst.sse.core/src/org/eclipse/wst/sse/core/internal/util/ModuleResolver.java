/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

public class ModuleResolver extends ProjectResolver {
	/**
	 * @param project
	 */
	public ModuleResolver(IProject project) {
		super(project);
	}

	public String getLocationByURI(String uri, String baseReference) {
		URL url = null;
		try {
			url = new URL(uri);
		}
		catch (MalformedURLException e) {
			// continue trying to resolve
		}

		// if it's a URL, it's already absolute
		if (url == null) {
			IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(getFileBaseLocation()));
			if (fileForLocation != null) {
				IVirtualResource[] virtualResources = ComponentCore.createResources(fileForLocation);
				if (virtualResources != null) {
					for (int i = 0; i < virtualResources.length; i++) {
						IPath runtimePath = null;
						if (uri.startsWith("/")) { //$NON-NLS-1$
							runtimePath = new Path(uri);
						}
						else {
							runtimePath = virtualResources[i].getRuntimePath().removeLastSegments(1).append(uri);
						}
						IVirtualFile file = ComponentCore.createFile(getProject(), virtualResources[i].getComponent().getName(), runtimePath);
						IFile sourceFile = file.getUnderlyingFile();
						if (sourceFile != null && sourceFile.getLocation() != null) {
							return sourceFile.getLocation().toString();
						}
					}
				}
			}
		}
		return super.getLocationByURI(uri, baseReference);
	}

	public IContainer getRootLocation() {
		IContainer rootContainer = null;

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(getFileBaseLocation()));
		if (file != null) {
			IVirtualResource[] resources = ComponentCore.createResources(file);
			for (int i = 0; i < resources.length; i++) {
				/*
				 * WORKAROUND for
				 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=96260
				 * 
				 * Object o = resource.getUnderlyingResource();
				 */

				if (resources[i].getProject().equals(getProject())) {
					IPath rootPath = resources[i].getComponent().getProjectRelativePath();
					rootContainer = getProject().getFolder(rootPath);
				}
			}
			if (resources.length > 0 && rootContainer == null) {
				IPath rootPath = resources[0].getComponent().getProjectRelativePath();
				rootContainer = getProject().getFolder(rootPath);
			}
		}
		if (rootContainer == null) {
			rootContainer = super.getRootLocation();
		}
		return rootContainer;
	}

	protected String getRootLocationString() {
		return getRootLocation().getLocation().toString();
	}
}
