/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;


class WorkspaceFileHyperlink implements IHyperlink {
	// copies of this class exist in:
	// org.eclipse.wst.xml.ui.internal.hyperlink
	// org.eclipse.wst.html.ui.internal.hyperlink
	// org.eclipse.wst.jsdt.web.ui.internal.hyperlink
	private IFile fFile;
	private IRegion fHighlightRange;
	private IRegion fRegion;
	
	public WorkspaceFileHyperlink(IRegion region, IFile file) {
		fRegion = region;
		fFile = file;
	}
	
	public WorkspaceFileHyperlink(IRegion region, IFile file, IRegion range) {
		fRegion = region;
		fFile = file;
		fHighlightRange = range;
	}
	
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String path = fFile.getFullPath().toString();
		if (path.length() > 60) {
			path = path.substring(0, 25) + "..." + path.substring(path.length() - 25, path.length());
		}
		return NLS.bind(HTMLUIMessages.Open, path);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void open() {
		if (fFile != null && fFile.exists()) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart editor = IDE.openEditor(page, fFile, true);
				// highlight range in editor if possible
				if (fHighlightRange != null && editor != null) { 
					ITextEditor textEditor = null;
					if (editor instanceof ITextEditor) {
						textEditor = (ITextEditor) editor;
					} else {
						textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
					}
					if (textEditor != null) {
						textEditor.selectAndReveal(fHighlightRange.getOffset(), fHighlightRange.getLength());
					}
				}
			} catch (PartInitException pie) {
				Logger.log(Logger.WARNING_DEBUG, pie.getMessage(), pie);
			}
		}
	}
}
