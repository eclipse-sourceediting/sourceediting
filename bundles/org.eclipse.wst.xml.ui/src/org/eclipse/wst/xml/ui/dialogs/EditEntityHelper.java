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


package org.eclipse.wst.xml.ui.dialogs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xml.ui.util.XMLCommonResources;
import org.eclipse.wst.xml.uriresolver.XMLCatalogEntry;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;



public class EditEntityHelper {

	public void performBrowseForPublicId(Shell parentShell, Text publicIdField) {
		performBrowseForPublicId(parentShell, publicIdField, null);
	}

	public void performBrowseForPublicId(Shell parentShell, Text publicIdField, Text systemIdField) {
		String[] extensions = {"dtd", "txt"}; //$NON-NLS-1$ //$NON-NLS-2$
		SelectXMLCatalogIdDialog dialog = new SelectXMLCatalogIdDialog(parentShell, extensions);
		dialog.create();
		dialog.getShell().setText(XMLCommonResources.getInstance().getString("_UI_LABEL_SELECT_XML_CATALOG_ENTRY")); //$NON-NLS-1$
		dialog.setBlockOnOpen(true);
		dialog.open();
		if (dialog.getReturnCode() == Window.OK) {
			String id = dialog.getId();
			if (id != null) {
				publicIdField.setText(id);
				if (systemIdField != null && dialog.getSystemId() != null) {
					systemIdField.setText(dialog.getSystemId());
				}
			}
		}
	}

	public void performBrowseForSystemId(Shell parentShell, Text systemIdField, IPath resourceLocation) {
		String[] extensions = {"dtd"}; //$NON-NLS-1$
		SelectFileOrXMLCatalogIdDialog dialog = new SelectFileOrXMLCatalogIdDialog(parentShell, extensions, XMLCatalogEntry.SYSTEM);
		dialog.create();
		dialog.getShell().setText(XMLCommonResources.getInstance().getString("_UI_LABEL_SPECIFY_SYSTEM_ID")); //$NON-NLS-1$
		dialog.setBlockOnOpen(true);
		dialog.open();
		if (dialog.getReturnCode() == Window.OK) {
			String id = dialog.getId();
			IFile file = dialog.getFile();
			if (id != null) {
				systemIdField.setText(id);
			} else if (file != null) {
				String uri = null;
				if (resourceLocation != null) {
					uri = URIHelper.getRelativeURI(file.getLocation(), resourceLocation);
				} else {
					uri = file.getLocation().toOSString();
				}
				systemIdField.setText(uri);
			}
		}
	}
}
