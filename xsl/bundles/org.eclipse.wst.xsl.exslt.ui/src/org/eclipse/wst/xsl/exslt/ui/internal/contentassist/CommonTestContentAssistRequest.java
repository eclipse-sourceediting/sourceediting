/*******************************************************************************
 * Copyright (c) 2009, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.exslt.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CommonTestContentAssistRequest extends
		CommonSelectContentAssistRequest {
	
	private String ATTR_TEST = "test";  //$NON-NLS-1$
	
	public CommonTestContentAssistRequest(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length, filter, textViewer);
	}
	
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		proposals.clear();
		
		adjustXPathStart(ATTR_TEST);

		int offset = getReplacementBeginPosition();
		IDOMAttr attrNode = getAttribute(ATTR_TEST);

		this.matchString = extractXPathMatchString(attrNode, getRegion(),
				getReplacementBeginPosition());

		addSelectProposals((Element) getNode().getParentNode(), offset);

		return getAllCompletionProposals();
	}
	

}
