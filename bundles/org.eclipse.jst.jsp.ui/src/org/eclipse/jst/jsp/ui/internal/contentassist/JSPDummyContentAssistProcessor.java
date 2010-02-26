/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.DefaultXMLCompletionProposalComputer;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentModelGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is a "null" version of AbstractContentAssistProcessor
 * 
 * @deprecated This class is no longer used locally and will be removed in the future
 * @see DefaultXMLCompletionProposalComputer
 */
public class JSPDummyContentAssistProcessor extends AbstractContentAssistProcessor {
	protected void addAttributeNameProposals(ContentAssistRequest contentAssistRequest) {
		super.addAttributeNameProposals(contentAssistRequest);
	}

	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		super.addAttributeValueProposals(contentAssistRequest);
	}


	protected void addCommentProposal(ContentAssistRequest contentAssistRequest) {
		super.addCommentProposal(contentAssistRequest);
	}


	protected void addContent(List contentList, CMContent content) {
		super.addContent(contentList, content);
	}


	protected void addDocTypeProposal(ContentAssistRequest contentAssistRequest) {
		super.addDocTypeProposal(contentAssistRequest);
	}


	protected void addEmptyDocumentProposals(ContentAssistRequest contentAssistRequest) {
		super.addEmptyDocumentProposals(contentAssistRequest);
	}


	protected void addEndTagNameProposals(ContentAssistRequest contentAssistRequest) {
		super.addEndTagNameProposals(contentAssistRequest);
	}


	protected void addEndTagProposals(ContentAssistRequest contentAssistRequest) {
		super.addEndTagProposals(contentAssistRequest);
	}


	protected void addEntityProposals(ContentAssistRequest contentAssistRequest, int documentPosition, ITextRegion completionRegion, IDOMNode treeNode) {
		super.addEntityProposals(contentAssistRequest, documentPosition, completionRegion, treeNode);
	}


	protected void addEntityProposals(Vector proposals, Properties map, String key, int nodeOffset, IStructuredDocumentRegion parent, ITextRegion completionRegion) {
		super.addEntityProposals(proposals, map, key, nodeOffset, parent, completionRegion);
	}


	protected void addPCDATAProposal(String nodeName, ContentAssistRequest contentAssistRequest) {
		super.addPCDATAProposal(nodeName, contentAssistRequest);
	}


	protected void addStartDocumentProposals(ContentAssistRequest contentAssistRequest) {
		super.addStartDocumentProposals(contentAssistRequest);
	}


	protected void addTagCloseProposals(ContentAssistRequest contentAssistRequest) {
		super.addTagCloseProposals(contentAssistRequest);
	}


	protected void addTagInsertionProposals(ContentAssistRequest contentAssistRequest, int childPosition) {
		super.addTagInsertionProposals(contentAssistRequest, childPosition);
	}


	protected void addTagNameProposals(ContentAssistRequest contentAssistRequest, int childPosition) {
		super.addTagNameProposals(contentAssistRequest, childPosition);
	}

	protected boolean attributeInList(IDOMNode node, Node parent, CMNode cmnode) {
		return super.attributeInList(node, parent, cmnode);
	}


	protected boolean beginsWith(String aString, String prefix) {
		return super.beginsWith(aString, prefix);
	}


	protected ContentAssistRequest computeAttributeProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeAttributeProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}


	protected ContentAssistRequest computeAttributeValueProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeAttributeValueProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}


	protected ContentAssistRequest computeCompletionProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode treeNode, IDOMNode xmlnode) {
		return super.computeCompletionProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
	}


	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		return super.computeCompletionProposals(viewer, documentOffset);
	}


	protected ContentAssistRequest computeContentProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeContentProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}


	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return super.computeContextInformation(viewer, documentOffset);
	}

	protected ContentAssistRequest computeEndTagOpenProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeEndTagOpenProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}


	protected ICompletionProposal[] computeEntityReferenceProposals(int documentPosition, ITextRegion completionRegion, IDOMNode treeNode) {
		return super.computeEntityReferenceProposals(documentPosition, completionRegion, treeNode);
	}

	protected ContentAssistRequest computeEqualsProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeEqualsProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	protected ContentAssistRequest computeStartDocumentProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeStartDocumentProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	protected ContentAssistRequest computeTagCloseProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeTagCloseProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	protected ContentAssistRequest computeTagNameProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeTagNameProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	protected ContentAssistRequest computeTagOpenProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeTagOpenProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	protected String getAdditionalInfo(CMNode parentOrOwner, CMNode cmnode) {
		return super.getAdditionalInfo(parentOrOwner, cmnode);
	}

	protected List getAvailableChildrenAtIndex(Element parent, int index, int validityChecking) {
		return super.getAvailableChildrenAtIndex(parent, index, validityChecking);
	}

	protected List getAvailableRootChildren(Document document, int childIndex) {
		return super.getAvailableRootChildren(document, childIndex);
	}

	protected CMElementDeclaration getCMElementDeclaration(Node node) {
		return super.getCMElementDeclaration(node);
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return super.getCompletionProposalAutoActivationCharacters();
	}

	protected ITextRegion getCompletionRegion(int offset, IStructuredDocumentRegion flatNode) {
		return super.getCompletionRegion(offset, flatNode);
	}

	protected ITextRegion getCompletionRegion(int documentPosition, Node domnode) {
		return super.getCompletionRegion(documentPosition, domnode);
	}

	public XMLContentModelGenerator getContentGenerator() {
		return super.getContentGenerator();
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return super.getContextInformationAutoActivationCharacters();
	}

	public IContextInformationValidator getContextInformationValidator() {
		return super.getContextInformationValidator();
	}

	protected int getElementPosition(Node child) {
		return super.getElementPosition(child);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return super.getErrorMessage();
	}

	protected String getMatchString(IStructuredDocumentRegion parent, ITextRegion aRegion, int offset) {
		return super.getMatchString(parent, aRegion, offset);
	}

	protected ITextRegion getNameRegion(IStructuredDocumentRegion flatNode) {
		return super.getNameRegion(flatNode);
	}


	protected List getPossibleDataTypeValues(Node node, CMAttributeDeclaration ad) {
		return super.getPossibleDataTypeValues(node, ad);
	}


	protected String getRequiredName(Node parentOrOwner, CMNode cmnode) {
		return super.getRequiredName(parentOrOwner, cmnode);
	}

	protected String getRequiredText(Node parentOrOwner, CMAttributeDeclaration attrDecl) {
		return super.getRequiredText(parentOrOwner, attrDecl);
	}

	protected String getRequiredText(Node parentOrOwner, CMElementDeclaration elementDecl) {
		return super.getRequiredText(parentOrOwner, elementDecl);
	}

	protected List getValidChildElementDeclarations(Element parent, int childPosition, int kindOfAction) {
		return super.getValidChildElementDeclarations(parent, childPosition, kindOfAction);
	}

	protected void init() {
		super.init();
	}

	protected boolean isCloseRegion(ITextRegion region) {
		return super.isCloseRegion(region);
	}

	protected boolean isNameRegion(ITextRegion region) {
		return super.isNameRegion(region);
	}

	protected boolean isQuote(String string) {
		return super.isQuote(string);
	}

	protected Properties mapToProperties(CMNamedNodeMap map) {
		return super.mapToProperties(map);
	}

	public void setErrorMessage(String errorMessage) {
		super.setErrorMessage(errorMessage);
	}

	protected void setErrorMessage(String errorMessage, String append) {
		super.setErrorMessage(errorMessage, append);
	}

	protected void setErrorMessage(String errorMessage, String prepend, String append) {
		super.setErrorMessage(errorMessage, prepend, append);
	}

	protected boolean stringsEqual(String a, String b) {
		return super.stringsEqual(a, b);
	}

}
