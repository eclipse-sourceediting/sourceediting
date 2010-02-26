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

import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>Computes JSP specific proposals</p>
 * 
 * @base org.eclipse.jst.jsp.ui.internal.contentassist.JSPContentAssistProcessor
 */
public class JSPCompletionProposalComputer extends DefaultXMLCompletionProposalComputer {
	/**
	 * <p>Create the computer</p>
	 */
	public JSPCompletionProposalComputer() {
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLCompletionProposalComputer#computeCompletionProposals(java.lang.String, org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion, org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode, org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected ContentAssistRequest computeCompletionProposals(String matchString, ITextRegion completionRegion,
			IDOMNode treeNode, IDOMNode xmlnode, CompletionProposalInvocationContext context) {

		//be sure to get the super proposals
		ContentAssistRequest request = super.computeCompletionProposals(matchString, completionRegion, treeNode, xmlnode, context);
		
		//calculate JSP specific proposals
		int documentPosition = context.getInvocationOffset();
		IStructuredDocumentRegion sdRegion = ContentAssistUtils.getStructuredDocumentRegion(
				context.getViewer(), documentPosition);

		Document doc = null;
		if (xmlnode != null) {
			if (xmlnode.getNodeType() == Node.DOCUMENT_NODE) {
				doc = (Document) xmlnode;
			} else {
				doc = xmlnode.getOwnerDocument();
			}
		}
		String[] directiveNames = {"page", "include", "taglib"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		ITextRegion prevTextRegion = null;
		try {
			int offset = sdRegion.getStartOffset(completionRegion);
			if(offset > 0) {
				offset--;
			}
			ITypedRegion prevRegion = context.getDocument().getPartition(offset);
			prevTextRegion = sdRegion.getRegionAtCharacterOffset(prevRegion.getOffset());
		} catch(BadLocationException e) {
			//this should never happen
			Logger.logException(e);
		}
		
		// suggest JSP Expression inside of XML comments
		if (completionRegion.getType() == DOMRegionContext.XML_COMMENT_TEXT && !isXMLFormat(doc)) {
			if (request == null) {
				request = new ContentAssistRequest(treeNode, xmlnode, sdRegion,
						completionRegion, documentPosition, 0, ""); //$NON-NLS-1$
			}
			request.addProposal(new CustomCompletionProposal("<%=  %>",//$NON-NLS-1$
					documentPosition, 0, 4,
					JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP),
					"jsp:expression", null, "&lt;%= %&gt;", XMLRelevanceConstants.R_JSP)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		/* handle proposals in and around JSP_DIRECTIVE_OPEN,
		 * JSP_DIRECTIVE_CLOSE (preceded by OPEN) and JSP_DIRECTIVE_NAME
		 */
		else if ((completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN &&
				documentPosition >= sdRegion.getTextEndOffset(completionRegion)) ||
				(completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME &&
						documentPosition <= sdRegion.getTextEndOffset(completionRegion)) ||
				(completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE &&
						prevTextRegion != null &&
						prevTextRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN &&
						documentPosition <= sdRegion.getTextEndOffset(completionRegion))) {

			if (completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN ||
					completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE) {
				
				if (request == null) {
					request = new ContentAssistRequest(xmlnode, xmlnode, sdRegion,
							completionRegion, documentPosition, 0, matchString);
				}
				
				//determine if there is any part of a directive name already existing
				Iterator regions = sdRegion.getRegions().iterator();
				String nameString = null;
				int begin = request.getReplacementBeginPosition();
				int length = request.getReplacementLength();
				while (regions.hasNext()) {
					ITextRegion region = (ITextRegion) regions.next();
					if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
						nameString = sdRegion.getText(region);
						begin = sdRegion.getStartOffset(region);
						length = region.getTextLength();
						break;
					}
				}
				if (nameString == null) {
					nameString = ""; //$NON-NLS-1$
				}
				
				/* Suggest the directive names that have been determined to be
				 * appropriate based on existing content
				 */
				for (int i = 0; i < directiveNames.length; i++) {
					if (directiveNames[i].startsWith(nameString) || documentPosition <= begin) {
						request.addProposal(new CustomCompletionProposal(directiveNames[i], begin,
								length, directiveNames[i].length(),
								JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP),
								directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
					}
				}
			}
			// by default, JSP_DIRECTIVE_NAME
			else { 
				if (request == null) {
					request = new ContentAssistRequest(xmlnode, xmlnode, sdRegion,
							completionRegion, sdRegion.getStartOffset(completionRegion),
							completionRegion.getTextLength(), matchString);
				}
				//add each of the directive names as a proposal
				for (int i = 0; i < directiveNames.length; i++) {
					if (directiveNames[i].startsWith(matchString)) {
						request.addProposal(new CustomCompletionProposal(
								directiveNames[i], request.getReplacementBeginPosition(),
								request.getReplacementLength(), directiveNames[i].length(),
								JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP),
								directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
					}
				}
			}
		}
		else if ((completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME &&
				documentPosition > sdRegion.getTextEndOffset(completionRegion)) ||
				(completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE &&
						documentPosition <= sdRegion.getStartOffset(completionRegion))) {
			
			if (request == null) {
				request = computeAttributeProposals(matchString, completionRegion,
						treeNode, xmlnode, context);
			}
		}
		// no name?: <%@ %>
		else if (completionRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_CLOSE &&
				documentPosition <= sdRegion.getStartOffset(completionRegion)) {
			if (request != null) {
				request = computeAttributeProposals(matchString, completionRegion, treeNode,
						xmlnode, context);
			}
			Iterator regions = sdRegion.getRegions().iterator();
			String nameString = null;
			while (regions.hasNext()) {
				ITextRegion region = (ITextRegion) regions.next();
				if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
					nameString = sdRegion.getText(region);
					break;
				}
			}
			if (nameString == null) {
				for (int i = 0; i < directiveNames.length; i++) {
					request.addProposal(new CustomCompletionProposal(
							directiveNames[i], request.getReplacementBeginPosition(),
							request.getReplacementLength(), directiveNames[i].length(),
							JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP),
							directiveNames[i], null, null, XMLRelevanceConstants.R_JSP));
				}
			}
		}

		return request;
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addAttributeValueProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		
		//nothing to suggest here
	}

	/**
	 * @param doc determine if this doc is XML format
	 * @return <code>true</code> if the given doc is of XML format, <code>false</code> otherwise.
	 */
	private boolean isXMLFormat(Document doc) {
		if (doc == null)
			return false;
		Element docElement = doc.getDocumentElement();
		return docElement != null && ((docElement.getNodeName().equals("jsp:root")) ||
				((((IDOMNode) docElement).getStartStructuredDocumentRegion() == null &&
						((IDOMNode) docElement).getEndStructuredDocumentRegion() == null))); //$NON-NLS-1$
	}
}
