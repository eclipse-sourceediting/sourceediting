/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;


/**
 * @deprecated - makes assumptions on behalf of the requester
 */
public class ResourceUtil {

	/**
	 * Obtains the IFile for a model
	 * 
	 * @param model
	 *            the model to use
	 * @return the IFile used to create the model, if it came from an IFile,
	 *         null otherwise
	 */
	public static IFile getFileFor(IStructuredModel model) {
		if (model == null)
			return null;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = null;
		IPath location = new Path(model.getBaseLocation());
		// if the path is not a path in the file system and there are at least
		// 2 segments, it might be in the workspace
		IFile[] files = root.findFilesForLocation(location);
		if (files.length > 0) {
			file = files[0];
		}
		else if (location.segmentCount() > 1) {
			// remember, this IFile isn't guaranteed to exist
			file = root.getFile(location);
		}
		return file;
	}

	/**
	 * Obtain IFiles from IStructuredModel (includes linkedResources)
	 * 
	 * @return the corresponding files in the workspace, or an empty array if
	 *         none
	 */
	public static IFile[] getFilesFor(IStructuredModel model) {
		if (model == null)
			return null;

		IFile[] files = null;
		IPath location = new Path(model.getBaseLocation());
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		// if the path is not a path in the file system and there are at least
		// 2 segments, it might be in the workspace
		if (!location.toFile().exists() && location.segmentCount() > 1) {
			// remember, this IFile isn't guaranteed to exist
			files = new IFile[]{root.getFile(location)};
		}
		else {
			files = root.findFilesForLocation(location);
		}
		return files;
	}
}
