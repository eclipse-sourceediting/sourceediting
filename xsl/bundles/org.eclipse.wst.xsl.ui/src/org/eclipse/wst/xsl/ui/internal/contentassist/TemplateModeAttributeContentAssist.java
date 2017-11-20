/*******************************************************************************
 *Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 243578  - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistRequest;
import org.w3c.dom.Node;

/**
 * TemplateModeAttributeContentAssist provides content assistance proposals for
 * <xsl:templates> with a mode attribute.   It looks at all the modes defined
 * within the xsl model, and pulls out any modes that have been defined.
 * @author dcarver
 * @since 1.0
 */
public class TemplateModeAttributeContentAssist extends
		AbstractXSLContentAssistRequest {

	private static final String MODE_ATTRIBUTE = "mode"; //$NON-NLS-1$

	/**
	 * Constructor for creating the TemplateMode Content Assistance class.
	 * 
	 * @param node
	 * @param parent
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public TemplateModeAttributeContentAssist(Node node,
			IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length,
				filter, textViewer);
	}

	/**
	 * The main method that returns an array of proposals. Returns the available
	 * modes that have been defined in the {@link StylesheetModel}.  If no proposals
	 * are found it returns a NULL value.
	 * @return ICompletionPropsal[] 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest#getCompletionProposals()
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		proposals.clear();
		
		StylesheetModel model = getStylesheetModel();

		addModeProposals(model);
		return getAllCompletionProposals();
	}

	/**
	 * @param model
	 */
	protected void addModeProposals(StylesheetModel model) {
		List<Template> templates = model.getTemplates();
		ArrayList<String> modes = new ArrayList<String>();
		
		for (Template template : templates) {
			XSLAttribute attribute = template.getAttribute(MODE_ATTRIBUTE);
			IDOMNode xmlNode = (IDOMNode)node;

			if (attribute != null && xmlNode.getStartOffset() != template.getOffset()) {
				CustomCompletionProposal proposal = new CustomCompletionProposal(
						attribute.getValue(), getStartOffset() + 1, 0,
						attribute.getValue().length(), XSLPluginImageHelper
								.getInstance().getImage(
										XSLPluginImages.IMG_MODE), attribute
								.getValue(), null, null, 0);
				if (modes.indexOf(attribute.getValue()) == -1) {
					proposals.add(proposal);
					modes.add(attribute.getValue());
				}
			}
		}
		modes.clear();
	}

}
