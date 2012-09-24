/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.internal.contentproperties;



import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
* @deprecated This is package protected so no one cares anyways.
*/
class ContentSettingsFileHandler extends AbstractContentSettingsHandler {



	private Map properties;


	private void getProperties(IResource file) {
		properties = getContentSettings().getProperties(file);
	}

	/*
	 * @see AbstractContentSettingsHandler#handleAdded()
	 */
	protected void handleAdded() {
		super.handleAdded();

		if (super.getDelta().getFlags() == 0) {
			// pulldown memu->copy->create file without override, new file,
			// import without override
			// copy,new,import has same kind(=1) and flag(=0).

		} else if ((getDelta().getFlags() & IResourceDelta.MOVED_FROM) != 0) {
			// pulldown menu-> rename without override,pulldown menu->move
			// without override
			// instead of this method,handleRemoved() works for this delta

		}

	}


	/*
	 * @see AbstractContentSettingsHandler#handleChanged()
	 */
	protected void handleChanged() {
		// edit
		if (getDelta().getFlags() == IResourceDelta.CONTENT && (getDelta().getFlags() & IResourceDelta.REPLACED) == 0) {
			super.handleChanged();

		} else if (getDelta().getFlags() == IResourceDelta.CONTENT && (getDelta().getFlags() & IResourceDelta.REPLACED) != 0) {
			// override as a result of copy or import
			// in Web project, copy with override doesn't happen

			// override as move or rename
			// handleRemoved() works for this delta
			super.handleChanged();

		}


	}

	/*
	 * @see AbstractContentSettingsHandler#handleRemoved()
	 */
	protected void handleRemoved() {
		super.handleRemoved();
		IFile deletedFile = null;

		// if entry exists then remove it.
		if (getDelta().getFlags() == 0) {
			// pulldown menu->delete
			deletedFile = (IFile) getDelta().getResource();

			if (deletedFile == null)
				return;
			getContentSettings().deleteAllProperties(deletedFile);

			getContentSettings().releaseCache();
		}

		else if ((getDelta().getFlags() & IResourceDelta.MOVED_TO) != 0) {

			// pulldown menu-> rename, pulldown menu->move
			deletedFile = (IFile) getDelta().getResource();
			getProperties(deletedFile);

			// get destination IResource
			IPath targetPath = getDelta().getMovedToPath();
			IWorkspaceRoot iwr = ResourcesPlugin.getWorkspace().getRoot();
			IResource targetFile = iwr.getFile(targetPath);//iwr.findMember(targetPath);

			// set property of destination file
			getContentSettings().deleteAllProperties(targetFile);
			setProperties(targetFile);
			if (properties != null)
				properties.clear();
			properties = null;
		}

		if (deletedFile == null)
			return;
		getContentSettings().deleteAllProperties(deletedFile);

		getContentSettings().releaseCache();

	}

	private void setProperties(IResource file) {
		if (file.getFileExtension() == null)
			return;
		if (!(file.getFileExtension().equalsIgnoreCase("shtml")//$NON-NLS-1$
					|| file.getFileExtension().equalsIgnoreCase("htm")//$NON-NLS-1$
					|| file.getFileExtension().equalsIgnoreCase("html")//$NON-NLS-1$
					|| file.getFileExtension().equalsIgnoreCase("jhtml")//$NON-NLS-1$
					|| file.getFileExtension().equalsIgnoreCase("xhtml")//$NON-NLS-1$
					|| file.getFileExtension().equalsIgnoreCase("jsp")//$NON-NLS-1$
					|| file.getFileExtension().equalsIgnoreCase("css")//$NON-NLS-1$
					|| file.getFileExtension().equalsIgnoreCase("jsf")//$NON-NLS-1$
		|| file.getFileExtension().equalsIgnoreCase("jspf")))//$NON-NLS-1$
			return;
		if (properties == null || properties.isEmpty())
			return;
		getContentSettings().setProperties(file, properties);
	}


}
