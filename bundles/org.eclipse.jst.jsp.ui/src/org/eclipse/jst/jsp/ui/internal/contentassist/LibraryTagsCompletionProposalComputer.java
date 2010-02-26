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
import java.util.List;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDElementDeclaration;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.core.internal.contentmodel.HTMLPropertyDeclaration;
import org.eclipse.wst.html.core.internal.contentmodel.JSPCMDocument;
import org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMNodeWrapper;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentModelGenerator;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.eclipse.wst.xml.ui.internal.editor.CMImageUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>Computes tags provided by tag libraries completion proposals.</p>
 * 
 * <p>Extends the {@link HTMLTagsCompletionProposalComputer} to benefit from
 * its work for determining the correct {@link XMLContentModelGenerator} to use</p>
 */
public class LibraryTagsCompletionProposalComputer extends
	HTMLTagsCompletionProposalComputer {
	
	private int fDepthCount;

	/**
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer#validModelQueryNode(org.eclipse.wst.xml.core.internal.contentmodel.CMNode)
	 */
	protected boolean validModelQueryNode(CMNode node) {
		boolean isValid = false;
		
		//unwrap
		if(node instanceof CMNodeWrapper) {
			node = ((CMNodeWrapper)node).getOriginNode();
		}
		
		//determine if is valid
		if(node instanceof HTMLPropertyDeclaration) {
			HTMLPropertyDeclaration propDec = (HTMLPropertyDeclaration)node;
			isValid = propDec.isJSP();
		} else if(node.supports(TLDElementDeclaration.IS_LIBRARY_TAG)){
			Boolean isLibraryTag = (Boolean)node.getProperty(TLDElementDeclaration.IS_LIBRARY_TAG);
			isValid = isLibraryTag != null && isLibraryTag.booleanValue();
		}
		
		return isValid;
	}
	
	/**
	 * <p>JSP has none.  This overrides the default behavior to add in doctype proposals which
	 * should really be contributed by the HTML tag computer</p>
	 * 
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer#addStartDocumentProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addStartDocumentProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		//jsp has none
	}
	
	/**
	 * @see org.eclipse.wst.html.ui.internal.contentassist.HTMLTagsCompletionProposalComputer#addEmptyDocumentProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addEmptyDocumentProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		//jsp has none
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer#addAttributeValueProposals(org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest, org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext)
	 */
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {

		if(!this.isXHTML) {
			IDOMNode node = (IDOMNode) contentAssistRequest.getNode();
	
			ModelQuery mq = ModelQueryUtil.getModelQuery(node.getOwnerDocument());
			if (mq != null) {
				CMDocument doc = mq.getCorrespondingCMDocument(node);
				// this shouldn't have to have the prefix coded in
				if (doc instanceof JSPCMDocument || doc instanceof CMNodeWrapper ||
						node.getNodeName().startsWith("jsp:")) { //$NON-NLS-1$
					return;
				}
			}
	
			// Find the attribute name for which this position should have a value
			IStructuredDocumentRegion open = node.getFirstStructuredDocumentRegion();
			ITextRegionList openRegions = open.getRegions();
			int i = openRegions.indexOf(contentAssistRequest.getRegion());
			if (i < 0) {
				return;
			}
			ITextRegion nameRegion = null;
			while (i >= 0) {
				nameRegion = openRegions.get(i--);
				if (nameRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
					break;
				}
			}
			
			// on an empty value, add all the JSP and taglib tags
			CMElementDeclaration elementDecl =
				AbstractXMLModelQueryCompletionProposalComputer.getCMElementDeclaration(node);
			if (nameRegion != null && elementDecl != null) {
				String attributeName = open.getText(nameRegion);
				if (attributeName != null) {
					Node parent = contentAssistRequest.getParent();
					
					//ignore start quote in match string
					String matchString = contentAssistRequest.getMatchString().trim();
					if(matchString.startsWith("'") || matchString.startsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
						matchString = matchString.substring(1);
					}
					
					//get all the proposals
					List additionalElements = ModelQueryUtil.getModelQuery(node.getOwnerDocument()).getAvailableContent(
							(Element) node, elementDecl, ModelQuery.INCLUDE_ALL);
					Iterator nodeIterator = additionalElements.iterator();
					
					//check each suggestion
					while (nodeIterator.hasNext()) {
						CMNode additionalElementDecl = (CMNode) nodeIterator.next();
						if (additionalElementDecl != null && additionalElementDecl instanceof CMElementDeclaration &&
								validModelQueryNode(additionalElementDecl)) {
							CMElementDeclaration ed = (CMElementDeclaration) additionalElementDecl;
							// https://bugs.eclipse.org/bugs/show_bug.cgi?id=89811
							StringBuffer sb = new StringBuffer();
							getContentGenerator().generateTag(parent, ed, sb);
	
							String proposedText = sb.toString();
	
							//filter out any proposals that dont match matchString
							if (beginsWith(proposedText, matchString)) {
								//wrap with ' because JSP attributes are warped with "
								proposedText = "'" + proposedText; //$NON-NLS-1$
								
								//if its a container its possible the closing quote is already there
								//don't want to risk injecting an extra
								if(!(contentAssistRequest.getRegion() instanceof ITextRegionContainer)) {
									proposedText += "'"; //$NON-NLS-1$
								}
								
								//get the image
								Image image = CMImageUtil.getImage(elementDecl);
								if (image == null) {
									image = this.getGenericTagImage();
								}
								
								//create the proposal
								int cursorAdjustment = getCursorPositionForProposedText(proposedText);
								String proposedInfo = AbstractXMLModelQueryCompletionProposalComputer.getAdditionalInfo(
										AbstractXMLModelQueryCompletionProposalComputer.getCMElementDeclaration(parent), elementDecl);
								String tagname = getContentGenerator().getRequiredName(node, ed);
								CustomCompletionProposal proposal = new CustomCompletionProposal(
										proposedText, contentAssistRequest.getReplacementBeginPosition(),
										contentAssistRequest.getReplacementLength(), cursorAdjustment, image, tagname, null, proposedInfo,
										XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE);
								contentAssistRequest.addProposal(proposal);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLCompletionProposalComputer#setErrorMessage(java.lang.String)
	 */
	public void setErrorMessage(String errorMessage) {
		if (fDepthCount == 0) {
			super.setErrorMessage(errorMessage);
		}
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#getGenericTagImage()
	 */
	protected Image getGenericTagImage() {
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#getDeemphasizedTagImage()
	 */
	protected Image getDeemphasizedTagImage() {
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}
	
	/**
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.AbstractXMLModelQueryCompletionProposalComputer#getEmphasizedTagImage()
	 */
	protected Image getEmphasizedTagImage() {
		return JSPEditorPluginImageHelper.getInstance().getImage(JSPEditorPluginImages.IMG_OBJ_TAG_JSP);
	}

}