/**
 *  Copyright (c) 2016, 2019 Red Hat Inc. and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Mickael Istria (Red Hat Inc.)
 */
package org.eclipse.wst.html.ui.internal.contentassist.resources;

import java.net.URL;

import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;

public class ImageCompletionProposal extends CustomCompletionProposal implements ICompletionProposalExtension3 {

	private AbstractReusableInformationControlCreator controlCreator;

	public ImageCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
			int cursorPosition, Image image, String resourcePath, URL previewUrl, int i) {
		super(replacementString, replacementOffset, replacementLength, cursorPosition, image, resourcePath, null, "<img src='" + previewUrl.toString() +"'/>", i);
		
	}

	@Override
	public IInformationControlCreator getInformationControlCreator() {
		if (this.controlCreator == null) {
			this.controlCreator = new AbstractReusableInformationControlCreator() {
				@Override
				protected IInformationControl doCreateInformationControl(Shell parent) {
					return new BrowserInformationControl(parent, JFaceResources.DIALOG_FONT, false);
				}
			};
		}
		return this.controlCreator;
	}

	@Override
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		return null;
	}

	@Override
	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return 0;
	}

}
