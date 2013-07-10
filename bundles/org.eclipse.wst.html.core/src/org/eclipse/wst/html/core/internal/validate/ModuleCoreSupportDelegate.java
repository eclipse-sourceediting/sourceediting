/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;

/**
 * Wrapper class for all Facet-related calls. If the Facet or ModuleCore
 * bundles are not available, this class will not load, or if it does, its
 * methods will cause NoClassDefFoundErrors. This allows us to
 * compartmentalize the dependencies.
 * 
 */
final class ModuleCoreSupportDelegate {
	private static final String SLASH = "/";
	private static Map fResolvedMap = new HashMap();

	/**
	 * @param path
	 *            - the full path to a resource within the workspace
	 * @return - the runtime path of the resource if one exists, null
	 *         otherwise
	 */
	static IPath getRuntimePath(IPath path) {
		if (path == null)
			return null;

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));

		if (!ModuleCoreNature.isFlexibleProject(project))
			return null;

		IVirtualResource[] virtualResources = ComponentCore.createResources(ResourcesPlugin.getWorkspace().getRoot().getFile(path));
		if (virtualResources != null && virtualResources.length > 0) {
			return virtualResources[0].getRuntimePath();
		}
		return null;
	}

	/**
	 * @param project
	 * @return the IPath to the "root" of the web contents
	 */
	static IPath getWebContentRootPath(IProject project) {
		if (project == null)
			return null;

		if (!ModuleCoreNature.isFlexibleProject(project))
			return null;

		IPath path = null;
		IVirtualComponent component = ComponentCore.createComponent(project);
		if (component != null && component.exists()) {
			path = component.getRootFolder().getWorkspaceRelativePath();
		}
		return path;
	}

	/**
	 * @param basePath
	 *            - the full path to a resource within the workspace
	 * @param reference
	 *            - the reference string to resolve
	 * @return - the full path within the workspace that corresponds to the
	 *         given reference according to the virtual pathing support
	 */
	static IPath resolve(IPath basePath, String reference) {
		if (reference == null || basePath == null || basePath.segmentCount() == 0)
			return null;

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(basePath.segment(0));

		if (!ModuleCoreNature.isFlexibleProject(project))
			return null;

		if (basePath.segmentCount() > 1) {
			/*
			 * It can take the better part of a full second to do this, so
			 * cache the result.
			 */
			IPath resolved = null;
			Map mapForBaseResource = null;
			mapForBaseResource = (Map) fResolvedMap.get(basePath);
			if (mapForBaseResource != null) {
				Reference resolvedReference = (Reference) mapForBaseResource.get(reference);
				if (resolvedReference != null)
					resolved = (IPath) resolvedReference.get();
			}
			else {
				mapForBaseResource = new HashMap();
				fResolvedMap.put(basePath, mapForBaseResource);
			}

			if (resolved == null) {
				IFile baseFile = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
				IVirtualResource[] virtualResources = ComponentCore.createResources(baseFile);
				for (int i = 0; i < virtualResources.length; i++) {
					IPath baseRuntimePath = virtualResources[i].getRuntimePath();
					IPath referenceRuntimePath = null;
					if (reference.startsWith(SLASH)) {
						referenceRuntimePath = new Path(reference);
					}
					else {
						referenceRuntimePath = baseRuntimePath.removeLastSegments(1).append(reference);
					}
					IVirtualFile virtualFile = ComponentCore.createFile(project, referenceRuntimePath);
					if (virtualFile != null && virtualFile.exists()) {
						IFile[] underlyingFiles = virtualFile.getUnderlyingFiles();
						for (int j = 0; j < underlyingFiles.length; j++) {
							if (underlyingFiles[j].getProject().equals(project) && underlyingFiles[j].exists()) {
								mapForBaseResource.put(reference, new SoftReference(underlyingFiles[j].getFullPath()));
								resolved = underlyingFiles[j].getFullPath();
							}

						}
					}
					else {
						// http://bugs.eclipse.org/338751 
						IVirtualFolder virtualFolder = ComponentCore.createFolder(project, referenceRuntimePath);
						if (virtualFolder != null && virtualFolder.exists()) {
							IContainer[] underlyingFolders = virtualFolder.getUnderlyingFolders();
							for (int j = 0; j < underlyingFolders.length; j++) {
								if (underlyingFolders[j].getProject().equals(project) && underlyingFolders[j].isAccessible()) {
									return underlyingFolders[j].getFullPath();
								}
							}
						}
						else {
							// check assembled projects
							return resolveInReferenced(project, referenceRuntimePath);
						}
					}
				}
			}
			return resolved;
		}
		else {
			IVirtualFile virtualFile = ComponentCore.createFile(project, new Path(reference));
			if (virtualFile != null && virtualFile.exists()) {
				return virtualFile.getUnderlyingFile().getFullPath();
			}
		}
		return null;
	}
	private static IPath resolveInReferenced(IProject project, IPath runtimeReference) {
		IVirtualReference[] references = ComponentCore.createComponent(project).getReferences();
		if (references != null) {
			for (int i = 0; i < references.length; i++) {
				IVirtualComponent referencedComponent = references[i].getReferencedComponent();
				if (referencedComponent == null)
					continue;
				IVirtualComponent component = referencedComponent.getComponent();
				if (component == null)
					continue;
				IVirtualFolder rootFolder = component.getRootFolder();
				if (rootFolder == null)
					continue;
				// overlay?
				IVirtualResource member = rootFolder.findMember(runtimeReference);
				if (member != null) {
					return member.getWorkspaceRelativePath();
				}
			}
		}
		return null;
	}
}
