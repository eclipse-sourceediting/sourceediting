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
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.ui.internal.templates.TemplateContextTypeIdsJSP;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;

/**
 * <p>Computes JSP template completion proposals</p>
 */
public class JSPTemplatesCompletionProposalComputer extends
		DefaultXMLCompletionProposalComputer {

	/** Template completion processor used to create template proposals */
	private JSPTemplateCompletionProcessor fTemplateProcessor = null;
	
	/**
	 * Create the computer
	 */
	public JSPTemplatesCompletionProposalComputer() {
		this.fTemplateProcessor = new JSPTemplateCompletionProcessor();
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLCompletionProposalComputer#computeCompletionProposals(org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(
			CompletionProposalInvocationContext context,
			IProgressMonitor monitor) {
		
		//get the templates specific to the context
		List proposals = new ArrayList(super.computeCompletionProposals(context, monitor));
		
		//get templates not specific to the context
		proposals.addAll(this.getTemplateProposals(TemplateContextTypeIdsJSP.ALL, context));
	
		return proposals;
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addAttributeNameProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.ATTRIBUTE, context);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addAttributeValueProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.ATTRIBUTE_VALUE, context);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addTagInsertionProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, int, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		
		addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.TAG, context);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addEmptyDocumentProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addEmptyDocumentProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		if (ContentTypeIdForJSP.ContentTypeID_JSPTAG.equals(
				((IDOMNode) contentAssistRequest.getNode()).getModel().getContentTypeIdentifier())) {
			
			addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.NEW_TAG, context);
		} else {
			addTemplates(contentAssistRequest, TemplateContextTypeIdsJSP.NEW, context);
		}
	}
	
	/**
	 * <p>Get the template proposals from the template processor</p>
	 * 
	 * @param templateContext
	 * @param context
	 * @return
	 */
	private List getTemplateProposals(String templateContext,
			CompletionProposalInvocationContext context) {
		
		List templateProposals = new ArrayList();
		
		if (fTemplateProcessor != null) {
			fTemplateProcessor.setContextType(templateContext);
			ICompletionProposal[] proposals =
				fTemplateProcessor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset());
		
			templateProposals.addAll(Arrays.asList(proposals));
		}
		
		return templateProposals;
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
			List proposals = this.getTemplateProposals(templateContext, context);
	
			for (int i = 0; i < proposals.size(); ++i) {
				if (useProposalList) {
					contentAssistRequest.addProposal((ICompletionProposal)proposals.get(i));
				}
				else {
					contentAssistRequest.addMacro((ICompletionProposal)proposals.get(i));
				}
			}
		}
	}
}
