/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.extension;



import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * Action for file drop
 */
public class FileDropAction extends AbstractDropAction {


	/* (non-Javadoc)
	 */
	public boolean run(DropTargetEvent event, IExtendedSimpleEditor targetEditor) {
		String[] strs = (String[]) event.data;
		if (strs == null || strs.length == 0) {
			return false;
		}

		String str = ""; //$NON-NLS-1$
		for (int i = 0; i < strs.length; ++i) {
			IPath path = new Path(strs[i]);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile file = root.getFileForLocation(path);
			if (file != null) {
				path = file.getProjectRelativePath();
			}

			str += "\"" + path.toString() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}

		return insert(str, targetEditor);
	}
}
