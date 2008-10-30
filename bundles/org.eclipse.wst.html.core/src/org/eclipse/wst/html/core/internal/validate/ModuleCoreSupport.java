/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * This class encapsulates any used Module Core APIs along with fallbacks for
 * use on non-compliant projects and when those services are not available at
 * runtime.
 * 
 * Because ModuleCore API calls can result in locks needing to be acquired,
 * none of these methods should be called while other thread locks have
 * already been acquired.
 */
public final class ModuleCoreSupport {
	static final boolean _dump_NCDFE = false;
	private static final String WEB_INF = "WEB-INF"; //$NON-NLS-1$
	private static final IPath WEB_INF_PATH = new Path(WEB_INF);

	/**
	 * @param project
	 * @return the computed IPath to the "root" of the web contents, either
	 *         from facet knowledge or hueristics, or null if one can not be
	 *         determined
	 */
	public static IPath computeWebContentRootPath(IPath path) {
		IPath root = null;
		try {
			root = ModuleCoreSupportDelegate.getWebContentRootPath(ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0)));
		}
		catch (NoClassDefFoundError e) {
			if (_dump_NCDFE)
				e.printStackTrace();
		}
		if (root == null) {
			/*
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=213245
			 * 
			 * NPE in JSPTaglibDirectiveContentAssistProcessor with
			 * non-faceted project
			 */
			root = getLocalRoot(path);
		}
		return root;
	}

	/**
	 * @param project
	 * @return the IPath to the "root" of the web contents
	 */
	public static IPath getWebContentRootPath(IProject project) {
		if (project == null)
			return null;

		IPath path = null;
		try {
			path = ModuleCoreSupportDelegate.getWebContentRootPath(project);
		}
		catch (NoClassDefFoundError e) {
			if (_dump_NCDFE)
				e.printStackTrace();
		}
		return path;
	}

	/**
	 * @param path
	 *            - the full path to a resource within the workspace
	 * @return - the runtime path of the resource if one exists, null
	 *         otherwise
	 */
	public static IPath getRuntimePath(IPath path) {
		IPath result = null;
		try {
			result = ModuleCoreSupportDelegate.getRuntimePath(path);
		}
		catch (NoClassDefFoundError e) {
			if (_dump_NCDFE)
				e.printStackTrace();
		}
		if (result == null) {
			IPath root = getLocalRoot(path);
			result = path.removeFirstSegments(root.segmentCount()).makeAbsolute();
		}
		return result;
	}

	/**
	 * @param basePath
	 *            - the full path to a resource within the workspace
	 * @param reference
	 *            - the reference string to resolve
	 * @return - the full path within the workspace that corresponds to the
	 *         given reference according to the virtual pathing support
	 */
	public static IPath resolve(IPath basePath, String reference) {
		IPath resolvedPath = null;
		try {
			resolvedPath = ModuleCoreSupportDelegate.resolve(basePath, reference);
		}
		catch (NoClassDefFoundError e) {
			if (_dump_NCDFE)
				e.printStackTrace();
		}

		if (resolvedPath == null) {
			IPath rootPath = getLocalRoot(basePath);
			if (reference.startsWith(Path.ROOT.toString())) {
				resolvedPath = rootPath.append(reference);
			}
			else {
				resolvedPath = basePath.removeLastSegments(1).append(reference);
			}
		}

		return resolvedPath;
	}

	/**
	 * @param basePath
	 * @return the applicable Web context root path, if one exists
	 */
	private static IPath getLocalRoot(IPath basePath) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		// existing workspace resources - this is the 93% case
		IResource file = FileBuffers.getWorkspaceFileAtLocation(basePath);

		// Try the base path as a folder first
		if (file == null && basePath.segmentCount() > 1) {
			file = workspaceRoot.getFolder(basePath);
		}
		// If not a folder, then try base path as a file
		if (file != null && !file.exists() && basePath.segmentCount() > 1) {
			file = workspaceRoot.getFile(basePath);
		}

		if (file == null && basePath.segmentCount() == 1) {
			file = workspaceRoot.getProject(basePath.segment(0));
		}

		if (file == null) {
			/*
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=116529
			 * 
			 * This method produces a less accurate result, but doesn't
			 * require that the file exist yet.
			 */
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(basePath);
			if (files.length > 0)
				file = files[0];
		}

		while (file != null) {
			/**
			 * Treat any parent folder with a WEB-INF subfolder as a web-app
			 * root
			 */
			IContainer folder = null;
			if ((file.getType() & IResource.FOLDER) != 0) {
				folder = (IContainer) file;
			}
			else {
				folder = file.getParent();
			}
			// getFolder on a workspace root must use a full path, skip
			if (folder != null && (folder.getType() & IResource.ROOT) == 0) {
				IFolder webinf = folder.getFolder(WEB_INF_PATH);
				if (webinf != null && webinf.exists()) {
					return folder.getFullPath();
				}
			}
			file = file.getParent();
		}

		return basePath.uptoSegment(1);
	}


}
