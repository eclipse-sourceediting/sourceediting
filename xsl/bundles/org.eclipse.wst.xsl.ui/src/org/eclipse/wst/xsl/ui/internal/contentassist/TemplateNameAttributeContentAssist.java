/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) bug 243577 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xsl.core.model.CallTemplate;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistRequest;
import org.w3c.dom.Node;

/**
 * Provides content assistance for XSLT Named Templates.   It provides a list
 * of available call-template names that have already been used with in the
 * stylesheet.
 * 
 * @author David Carver
 * @since 1.0
 */
public class TemplateNameAttributeContentAssist extends AbstractXSLContentAssistRequest {

	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	/**
	 * Provides content assistance for XSLT Named Templates.  Will provide a list
	 * of proposals based on the available named-templates if any are defined in
	 * the XSL Stylesheet model.   Only provides a list of named templates that haven't already
	 * been added as a proposal.  Does not check to see if it's already is defined in the current stylesheet.
	 * @param node
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public TemplateNameAttributeContentAssist(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length, filter, textViewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.xsl.ui.internal.contentassist.AbstractXSLContentAssistRequest#getCompletionProposals()
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		proposals.clear();
		
		StylesheetModel model = getStylesheetModel();
		List<CallTemplate> templates = model.getCallTemplates();
		
		for (CallTemplate template : templates) {
			CustomCompletionProposal proposal = createProposal(template);
			addUniqueProposal(proposal);
		}
		
		return getAllCompletionProposals();
	}

	private void addUniqueProposal(CustomCompletionProposal proposal) {
		if (proposals.lastIndexOf(proposal) == -1) {
			if (matchString.length() > 0) {
				if (proposal.getDisplayString().startsWith(matchString)) {
					addProposal(proposal);
				}
			} else {
				addProposal(proposal);
			}
		}
	}

	private CustomCompletionProposal createProposal(CallTemplate template) {
		CustomCompletionProposal proposal = new CustomCompletionProposal(
				template.getAttributeValue(ATTR_NAME), getStartOffset() + 1, 0,
				template.getAttributeValue(ATTR_NAME).length(), XSLPluginImageHelper
						.getInstance().getImage(
								XSLPluginImages.IMG_TEMPLATE), template
						.getAttributeValue(ATTR_NAME), null, null, 0);
		return proposal;
	}

}
