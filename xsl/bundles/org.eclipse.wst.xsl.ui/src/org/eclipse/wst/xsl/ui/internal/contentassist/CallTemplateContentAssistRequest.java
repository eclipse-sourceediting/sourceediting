/*******************************************************************************
 *Copyright (c) 2008 Standards in Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - bug 243575 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.StylesheetModel;
import org.eclipse.wst.xsl.core.model.Template;
import org.eclipse.wst.xsl.core.model.XSLAttribute;
import org.eclipse.wst.xsl.ui.internal.Messages;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistRequest;
import org.w3c.dom.Node;

/**
 * CallTemplateContentAssistance provides content assist proposals for
 * <xsl:call-with> templates.   It will provide a list of all available
 * named templates.
 * 
 * @author David Carver
 *
 */
public class CallTemplateContentAssistRequest extends
		AbstractXSLContentAssistRequest {
	
	private static final String ATTR_MODE = "mode"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

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
	public CallTemplateContentAssistRequest(Node node, IStructuredDocumentRegion documentRegion,
			ITextRegion completionRegion, int begin, int length, String filter,
			ITextViewer textViewer) {
		super(node, documentRegion, completionRegion, begin, length,
				filter, textViewer);
		// TODO Auto-generated constructor stub
	}
	
	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest#getCompletionProposals()
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals() {
		proposals.clear();
		
		IFile editorFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getLocation()));
		
		StylesheetModel model = XSLCore.getInstance().getStylesheet(editorFile);

		List<Template> templates = model.getTemplates();
		
		for (Template template : templates) {
			XSLAttribute attribute = template.getAttribute(ATTR_NAME);
			if (attribute != null) {
				String proposalInfo = getAdditionalInfo(template);
				CustomCompletionProposal proposal = new CustomCompletionProposal(
						attribute.getValue(), getStartOffset() + 1, 0,
						attribute.getValue().length(), XSLPluginImageHelper
								.getInstance().getImage(
										XSLPluginImages.IMG_TEMPLATE), attribute
								.getValue(), null, proposalInfo, 0);
				addProposal(proposal);
			}
		}
		return getAllCompletionProposals();
	}
	
	protected String getAdditionalInfo(Template template) {
		XSLAttribute nameAttribute = template.getAttribute(ATTR_NAME);
		
		String proposalInfo = Messages.CallTemplateContentAssistTemplateName + nameAttribute.getValue() +
		                      "\r\n" + Messages.CallTemplateContentAssistTemplateNameFile + template.getStylesheet().getFile().getName(); //$NON-NLS-1$
		
		return proposalInfo;
	}

}
