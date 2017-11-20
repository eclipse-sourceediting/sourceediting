/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.hyperlink;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Hyperlink for external files.
 */
class ExternalFileHyperlink implements IHyperlink {
	// copies of this class exist in:
	// org.eclipse.wst.xml.ui.internal.hyperlink
	// org.eclipse.wst.html.ui.internal.hyperlink
	// org.eclipse.jst.jsp.ui.internal.hyperlink
	
	private IRegion fHyperlinkRegion;
	private File fHyperlinkFile;

	public ExternalFileHyperlink(IRegion region, File file) {
		fHyperlinkFile = file;
		fHyperlinkRegion = region;
	}
	
	public IRegion getHyperlinkRegion() {
		return fHyperlinkRegion;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		String path = fHyperlinkFile.getPath();
		if (path.length() > 60) {
			path = path.substring(0, 25) + "..." + path.substring(path.length() - 25, path.length());
		}
		return NLS.bind(JSPUIMessages.Open, path);
	}

	public void open() {
		if (fHyperlinkFile != null) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IFileStore store = EFS.getLocalFileSystem().getStore(fHyperlinkFile.toURI());
				IDE.openEditorOnFileStore(page, store) ;
			}
			catch (PartInitException e) {
				Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
			}
		}
	}
}
