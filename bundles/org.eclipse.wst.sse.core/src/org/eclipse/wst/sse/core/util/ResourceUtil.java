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
package org.eclipse.wst.sse.core.util;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.IStructuredModel;


/**
 * @deprecated - incorrect and not updated for M3 changes
 */
public class ResourceUtil {

	/**
	 * Obtain IFile from IStructuredModel
	 */
	public static IFile getFileFor(IStructuredModel model) {
		if (model == null)
			return null;
		String path = model.getBaseLocation();
		if (path == null || path.length() == 0) {
			Object id = model.getId();
			if (id == null)
				return null;
			path = id.toString();
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = root.getFileForLocation(new Path(path));
		return file;
	}

	/**
	 * Obtain IFiles from IStructuredModel (includes linkedResources)
	 * 
	 * @return the corresponding files in the workspace, or an empty array if
	 *         none
	 */
	public static IFile[] getFilesFor(IStructuredModel model) {
		IFile[] files = new IFile[0];

		if (model != null) {
			String path = model.getBaseLocation();
			if (path == null || path.length() == 0) {
				Object id = model.getId();
				if (id == null)
					return files;
				path = id.toString();
			}
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			files = root.findFilesForLocation(new Path(path));
		}
		return files;
	}
}
