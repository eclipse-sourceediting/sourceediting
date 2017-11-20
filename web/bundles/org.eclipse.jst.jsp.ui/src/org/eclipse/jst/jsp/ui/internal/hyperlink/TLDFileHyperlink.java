/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.hyperlink;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

class TLDFileHyperlink implements IHyperlink {

	private IResource fResource;
	private IRegion fHyperlinkRegion;
	private String fSearchName;
	private int fSearchType;

	public TLDFileHyperlink(IResource resource, int searchType, String searchName, IRegion hyperlinkRegion) {
		super();
		fResource = resource;
		fSearchName = searchName;
		fSearchType = searchType;
		fHyperlinkRegion = hyperlinkRegion;
	}

	private IMarker createMarker(IFile file, IRegion region) throws CoreException {
		// org.eclipse.ui.texteditor.MarkerUtilities.setCharStart(IMarker, int)
		IMarker marker = file.createMarker(JSPUIPlugin.ID + "hyperlink"); //$NON-NLS-1$
		marker.setAttribute(IMarker.TRANSIENT, Boolean.TRUE);
		marker.setAttribute(IMarker.SOURCE_ID, getClass().getName());
		marker.setAttribute(IMarker.CHAR_START, region.getOffset());
		marker.setAttribute(IMarker.CHAR_END, region.getOffset() + region.getLength());
		return marker;
	}

	public IRegion getHyperlinkRegion() {
		return fHyperlinkRegion;
	}

	public String getHyperlinkText() {
		return NLS.bind(JSPUIMessages.Open, fResource.getFullPath().toString());
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		IWorkbenchWindow window = JSPUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (fResource.getType() == IResource.FILE) {
				IFile file = (IFile) fResource;
				IMarker marker = null;
				IDOMModel domModel = null;
				try {
					IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead(file);
					if (model instanceof IDOMModel) {
						domModel = (IDOMModel) model;
						if (domModel != null) {
							IRegion targetRegion = TaglibHyperlinkDetector.findDefinition(domModel, fSearchName, fSearchType);
							if (targetRegion != null) {
								try {
									marker = createMarker(file, targetRegion);
								}
								catch (CoreException e) {
									Logger.logException(e);
								}
							}
							if (marker != null) {
								try {
									IDE.openEditor(page, marker);
								}
								catch (PartInitException e1) {
									Logger.logException(e1);
								}
								finally {
									try {
										marker.delete();
									}
									catch (CoreException e) {
										Logger.logException(e);
									}
								}
							}
							else {
								IDE.openEditor(page, file);
							}
						}
					}
				}
				catch (IOException e2) {
				}
				catch (CoreException e2) {
				}
				finally {
					if (domModel != null) {
						domModel.releaseFromRead();
					}
				}
			}
		}
	}
}
