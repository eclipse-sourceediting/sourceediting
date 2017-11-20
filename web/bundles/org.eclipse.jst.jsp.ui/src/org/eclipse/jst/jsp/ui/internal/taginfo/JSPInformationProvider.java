/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.taginfo;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;

/**
 * Provides context help for JSP tags (Show tooltip description)
 * 
 * @deprecated StructuredTextViewerConfiguration creates the appropriate
 *             information provider
 */
public class JSPInformationProvider implements IInformationProvider, IInformationProviderExtension {
	private ITextHover fTextHover = null;

	public JSPInformationProvider() {
		fTextHover = SSEUIPlugin.getDefault().getTextHoverManager().createBestMatchHover(new JSPTagInfoHoverProcessor());
	}

	public IRegion getSubject(ITextViewer textViewer, int offset) {
		return fTextHover.getHoverRegion(textViewer, offset);
	}

	public String getInformation(ITextViewer textViewer, IRegion subject) {
		return (String) getInformation2(textViewer, subject);
	}

	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		return fTextHover.getHoverInfo(textViewer, subject);
	}
}
