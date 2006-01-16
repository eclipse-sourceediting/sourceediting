/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;

/**
 * Information provider used to present the information.
 * 
 * @see org.eclipse.jdt.internal.ui.javaeditor.JavaEditor#InformationDispatchAction
 * @see org.eclipse.wst.sse.ui.StructuredTextEditor#InformationDispatchAction
 */
class InformationProvider implements IInformationProvider, IInformationProviderExtension, IInformationProviderExtension2 {

	private IRegion fHoverRegion;
	private String fHoverInfo;
	private IInformationControlCreator fControlCreator;

	InformationProvider(IRegion hoverRegion, String hoverInfo, IInformationControlCreator controlCreator) {
		fHoverRegion = hoverRegion;
		fHoverInfo = hoverInfo;
		fControlCreator = controlCreator;
	}

	/*
	 * @see org.eclipse.jface.text.information.IInformationProvider#getSubject(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public IRegion getSubject(ITextViewer textViewer, int invocationOffset) {
		return fHoverRegion;
	}

	/*
	 * @see org.eclipse.jface.text.information.IInformationProvider#getInformation(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		return (String) getInformation2(textViewer, subject);
	}

	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		return fHoverInfo;
	}

	/*
	 * @see org.eclipse.jface.text.information.IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return fControlCreator;
	}
}
