/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.internal.contentassist;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.xml.ui.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.templates.TemplateContextTypeIdsXML;

/**
 * <p>Proposal computer used to computer XML template content assist
 * proposals</p>
 */
public class XMLTemplatesCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	/** <p>The template processor used to create the proposals</p> */
	private XMLTemplateCompletionProcessor fTemplateProcessor = null;

	/**
	 * Create the computer
	 */
	public XMLTemplatesCompletionProposalComputer() {
		super();
		fTemplateProcessor = new XMLTemplateCompletionProcessor();
	}

	/**
	 * @see org.eclipse.wst.xml.ui.contentassist.DefaultXMLCompletionProposalComputer#addAttributeNameProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		addTemplates(contentAssistRequest, TemplateContextTypeIdsXML.ATTRIBUTE, context);
	}

	/**
	 * @see org.eclipse.wst.xml.ui.contentassist.DefaultXMLCompletionProposalComputer#addAttributeValueProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		addTemplates(contentAssistRequest, TemplateContextTypeIdsXML.ATTRIBUTE_VALUE, context);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.contentassist.DefaultXMLCompletionProposalComputer#addEmptyDocumentProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addEmptyDocumentProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		addTemplates(contentAssistRequest, TemplateContextTypeIdsXML.NEW, context);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.contentassist.DefaultXMLCompletionProposalComputer#addTagInsertionProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, int, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		
		addTemplates(contentAssistRequest, TemplateContextTypeIdsXML.TAG, context);
	}
	
	/**
	 * <p>Adds templates to the list of proposals</p>
	 * 
	 * @param contentAssistRequest
	 * @param templateContext
	 * @param context
	 */
	private void addTemplates(ContentAssistRequest contentAssistRequest, String templateContext,
			CompletionProposalInvocationContext context) {
		
		if (contentAssistRequest != null) {

			boolean useProposalList = !contentAssistRequest.shouldSeparate();
	
			if (fTemplateProcessor != null) {
				fTemplateProcessor.setContextType(templateContext);
				ICompletionProposal[] proposals =
					fTemplateProcessor.computeCompletionProposals(
							context.getViewer(), context.getInvocationOffset());
				for (int i = 0; i < proposals.length; ++i) {
					if (useProposalList) {
						contentAssistRequest.addProposal(proposals[i]);
					}
					else {
						contentAssistRequest.addMacro(proposals[i]);
					}
				}
			}
		}
	}
}
