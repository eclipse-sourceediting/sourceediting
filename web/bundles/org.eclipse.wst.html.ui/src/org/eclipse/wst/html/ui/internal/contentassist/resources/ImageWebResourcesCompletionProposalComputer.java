/**
 *  Copyright (c) 2013, 2019 Angelo ZERR and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Mickael Istria (Red Hat Inc.) - Extracted, refactored and moved to org.eclipse
 *  Nitin Dahyabhai (IBM Corporation) - improve performance finding matching resources
 */
package org.eclipse.wst.html.ui.internal.contentassist.resources;

import java.net.URL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.w3c.dom.Node;

public class ImageWebResourcesCompletionProposalComputer extends
		AbstractWebResourcesCompletionProposalComputer {

	ContentTypeSpecs fileMatcher = ContentTypeSpecs.createFor("org.eclipse.ui.content-type.images");

	@Override
	protected ICompletionProposal createCompletionProposal(ContentAssistRequest request, IPath referencePath, IPath runtimeReferencePath, IPath proposalPath, IPath runtimeProposalPath, String relativeProposal) {
		String replacementString = '"' + relativeProposal + '"';
		int cursorPosition = replacementString.length();
		Image image = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(proposalPath.lastSegment()).createImage();
		if (image != null) {
			super.images.add(image);
		}
		final int replacementLength = request.getRegion().getTextLength();
		final int replacementOffset = request.getStartOffset();
		URL previewURL = null;
		String previewErrorInfo = null;
		try {
			previewURL =  new URL("platform://resource" + proposalPath);
		} catch (Exception ex) {
			previewErrorInfo = NLS.bind(HTMLUIMessages.cannotGenerateImagePreview, ex.getMessage());
		}
		
		if (previewURL != null) {
			return new ImageCompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition, image, relativeProposal, previewURL, 0);
		} else {
			return new CustomCompletionProposal(replacementString, replacementOffset, replacementLength, cursorPosition, image, relativeProposal, null, previewErrorInfo, 0);
		}
	}

	@Override
	ContentTypeSpecs createFilenameMatcher() {
		return fileMatcher;
	}

	@Override
	boolean matchRequest(ContentAssistRequest contentAssistRequest) {
		Node node = contentAssistRequest.getNode();
		return
			"img".equals(node.getLocalName()) &&
			"src".equals(getCurrentAttributeName(contentAssistRequest));
	}
}
