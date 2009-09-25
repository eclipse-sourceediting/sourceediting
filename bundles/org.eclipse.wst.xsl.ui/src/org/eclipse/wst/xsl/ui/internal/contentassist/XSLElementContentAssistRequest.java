/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 244978 - initial API and implementation
 *                          bug 263843 - Fix null pointer exception on region
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xsl.ui.internal.contentassist.contentmodel.XSLContentModelGenerator;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXMLElementContentAssistRequest;
import org.w3c.dom.Node;
 

/**
 * This class provides content assistance proposals outside of the XSL namespace.  Normal
 * XML editor content assistance only provides proposals for items within the same namespace
 * or if an element has children elements.   This class extends this functionality by checking
 * for the first XSL ancestor and uses that to determine what proposals should be
 * provided in the way of xsl elements.
 * 
 * @author David Carver
 * @since 1.0
 */
public class XSLElementContentAssistRequest extends
		AbstractXMLElementContentAssistRequest {

	/**
	 * @param node
	 * @param parent
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public XSLElementContentAssistRequest(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length,
				filter, textViewer);
		contentModel = new XSLContentModelGenerator();
	}


	/**
	 * Calculate proposals for open content regions.
	 */
	protected void computeTagOpenProposals() {

		if (replacementBeginPosition == documentRegion.getStartOffset(region)) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				// at the start of an existing tag, right before the '<'
				computeTagNameProposals();
			}
		} else {
			// within the white space
			ITextRegion name = getNameRegion(((IDOMNode) node)
					.getStartStructuredDocumentRegion());
			if ((name != null)
					&& ((documentRegion.getStartOffset(name) <= replacementBeginPosition) && (documentRegion
							.getEndOffset(name) >= replacementBeginPosition))) {
				// replace the existing name
				replacementBeginPosition = documentRegion.getStartOffset(name);
				replacementLength = name.getTextLength();
			} else {
				// insert a valid new name, or possibly an end tag
				// addEndTagProposals(contentAssistRequest);
				setReplacementLength(0);
			}
			addTagNameProposals(getElementPosition(node));
		}
	}

	/**
	 * Provides a list of possible proposals for the XSL Elements within the current
	 * scope.
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		if (region == null) {
			return new ArrayList<ICompletionProposal>();
		}
		
		if (region.getType() == DOMRegionContext.XML_TAG_OPEN) {
			computeTagOpenProposals();
		} else if (region.getType() == DOMRegionContext.XML_TAG_NAME) {
			computeTagNameProposals();
		}
		return getAllCompletionProposals();
	}
	
	
	/**
	 * Calculates the proposals for the XML Tag Name Region.
	 */
	protected void computeTagNameProposals() {
		// completing the *first* tag in "<tagname1 |<tagname2"
		
		// Ignore attributes
		if (inAttributeRegion()) {
			return;
		}
		
		addTagNameProposals(this.getElementPosition(node));
		// addEndTagNameProposals();

	}

}
