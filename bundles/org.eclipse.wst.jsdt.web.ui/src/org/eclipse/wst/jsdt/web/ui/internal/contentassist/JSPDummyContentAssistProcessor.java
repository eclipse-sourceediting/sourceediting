/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

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
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentModelGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is a "null" version of AbstractContentAssistProcessor
 * 
 * @plannedfor 1.0
 */
public class JSPDummyContentAssistProcessor extends
		AbstractContentAssistProcessor {
	@Override
	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest) {
		super.addAttributeNameProposals(contentAssistRequest);
	}

	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest) {
		super.addAttributeValueProposals(contentAssistRequest);
	}

	@Override
	protected void addCommentProposal(ContentAssistRequest contentAssistRequest) {
		super.addCommentProposal(contentAssistRequest);
	}

	@Override
	protected void addContent(List contentList, CMContent content) {
		super.addContent(contentList, content);
	}

	@Override
	protected void addDocTypeProposal(ContentAssistRequest contentAssistRequest) {
		super.addDocTypeProposal(contentAssistRequest);
	}

	@Override
	protected void addEmptyDocumentProposals(
			ContentAssistRequest contentAssistRequest) {
		super.addEmptyDocumentProposals(contentAssistRequest);
	}

	@Override
	protected void addEndTagNameProposals(
			ContentAssistRequest contentAssistRequest) {
		super.addEndTagNameProposals(contentAssistRequest);
	}

	@Override
	protected void addEndTagProposals(ContentAssistRequest contentAssistRequest) {
		super.addEndTagProposals(contentAssistRequest);
	}

	@Override
	protected void addEntityProposals(
			ContentAssistRequest contentAssistRequest, int documentPosition,
			ITextRegion completionRegion, IDOMNode treeNode) {
		super.addEntityProposals(contentAssistRequest, documentPosition,
				completionRegion, treeNode);
	}

	@Override
	protected void addEntityProposals(Vector proposals, Properties map,
			String key, int nodeOffset, IStructuredDocumentRegion parent,
			ITextRegion completionRegion) {
		super.addEntityProposals(proposals, map, key, nodeOffset, parent,
				completionRegion);
	}

	@Override
	protected void addPCDATAProposal(String nodeName,
			ContentAssistRequest contentAssistRequest) {
		super.addPCDATAProposal(nodeName, contentAssistRequest);
	}

	@Override
	protected void addStartDocumentProposals(
			ContentAssistRequest contentAssistRequest) {
		super.addStartDocumentProposals(contentAssistRequest);
	}

	@Override
	protected void addTagCloseProposals(
			ContentAssistRequest contentAssistRequest) {
		super.addTagCloseProposals(contentAssistRequest);
	}

	@Override
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition) {
		super.addTagInsertionProposals(contentAssistRequest, childPosition);
	}

	@Override
	protected void addTagNameProposals(
			ContentAssistRequest contentAssistRequest, int childPosition) {
		super.addTagNameProposals(contentAssistRequest, childPosition);
	}

	@Override
	protected boolean attributeInList(IDOMNode node, Node parent, CMNode cmnode) {
		return super.attributeInList(node, parent, cmnode);
	}

	@Override
	protected boolean beginsWith(String aString, String prefix) {
		return super.beginsWith(aString, prefix);
	}

	@Override
	protected ContentAssistRequest computeAttributeProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeAttributeProposals(documentPosition, matchString,
				completionRegion, nodeAtOffset, node);
	}

	@Override
	protected ContentAssistRequest computeAttributeValueProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeAttributeValueProposals(documentPosition,
				matchString, completionRegion, nodeAtOffset, node);
	}

	@Override
	protected ContentAssistRequest computeCompletionProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode treeNode, IDOMNode xmlnode) {
		return super.computeCompletionProposals(documentPosition, matchString,
				completionRegion, treeNode, xmlnode);
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int documentOffset) {
		return super.computeCompletionProposals(viewer, documentOffset);
	}

	@Override
	protected ContentAssistRequest computeContentProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeContentProposals(documentPosition, matchString,
				completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int documentOffset) {
		return super.computeContextInformation(viewer, documentOffset);
	}

	@Override
	protected ContentAssistRequest computeEndTagOpenProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeEndTagOpenProposals(documentPosition, matchString,
				completionRegion, nodeAtOffset, node);
	}

	@Override
	protected ICompletionProposal[] computeEntityReferenceProposals(
			int documentPosition, ITextRegion completionRegion,
			IDOMNode treeNode) {
		return super.computeEntityReferenceProposals(documentPosition,
				completionRegion, treeNode);
	}

	@Override
	protected ContentAssistRequest computeEqualsProposals(int documentPosition,
			String matchString, ITextRegion completionRegion,
			IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeEqualsProposals(documentPosition, matchString,
				completionRegion, nodeAtOffset, node);
	}

	@Override
	protected ContentAssistRequest computeStartDocumentProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeStartDocumentProposals(documentPosition,
				matchString, completionRegion, nodeAtOffset, node);
	}

	@Override
	protected ContentAssistRequest computeTagCloseProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeTagCloseProposals(documentPosition, matchString,
				completionRegion, nodeAtOffset, node);
	}

	@Override
	protected ContentAssistRequest computeTagNameProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeTagNameProposals(documentPosition, matchString,
				completionRegion, nodeAtOffset, node);
	}

	@Override
	protected ContentAssistRequest computeTagOpenProposals(
			int documentPosition, String matchString,
			ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		return super.computeTagOpenProposals(documentPosition, matchString,
				completionRegion, nodeAtOffset, node);
	}

	@Override
	protected String getAdditionalInfo(CMNode parentOrOwner, CMNode cmnode) {
		return super.getAdditionalInfo(parentOrOwner, cmnode);
	}

	@Override
	protected List getAvailableChildrenAtIndex(Element parent, int index,
			int validityChecking) {
		return super.getAvailableChildrenAtIndex(parent, index,
				validityChecking);
	}

	@Override
	protected List getAvailableRootChildren(Document document, int childIndex) {
		return super.getAvailableRootChildren(document, childIndex);
	}

	@Override
	protected CMElementDeclaration getCMElementDeclaration(Node node) {
		return super.getCMElementDeclaration(node);
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return super.getCompletionProposalAutoActivationCharacters();
	}

	@Override
	protected ITextRegion getCompletionRegion(int offset,
			IStructuredDocumentRegion flatNode) {
		return super.getCompletionRegion(offset, flatNode);
	}

	@Override
	protected ITextRegion getCompletionRegion(int documentPosition, Node domnode) {
		return super.getCompletionRegion(documentPosition, domnode);
	}

	@Override
	public XMLContentModelGenerator getContentGenerator() {
		return super.getContentGenerator();
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return super.getContextInformationAutoActivationCharacters();
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return super.getContextInformationValidator();
	}

	@Override
	protected int getElementPosition(Node child) {
		return super.getElementPosition(child);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return super.getErrorMessage();
	}

	@Override
	protected String getMatchString(IStructuredDocumentRegion parent,
			ITextRegion aRegion, int offset) {
		return super.getMatchString(parent, aRegion, offset);
	}

	@Override
	protected ITextRegion getNameRegion(IStructuredDocumentRegion flatNode) {
		return super.getNameRegion(flatNode);
	}

	@Override
	protected List getPossibleDataTypeValues(Node node,
			CMAttributeDeclaration ad) {
		return super.getPossibleDataTypeValues(node, ad);
	}

	@Override
	protected String getRequiredName(Node parentOrOwner, CMNode cmnode) {
		return super.getRequiredName(parentOrOwner, cmnode);
	}

	@Override
	protected String getRequiredText(Node parentOrOwner,
			CMAttributeDeclaration attrDecl) {
		return super.getRequiredText(parentOrOwner, attrDecl);
	}

	@Override
	protected String getRequiredText(Node parentOrOwner,
			CMElementDeclaration elementDecl) {
		return super.getRequiredText(parentOrOwner, elementDecl);
	}

	@Override
	protected List getValidChildElementDeclarations(Element parent,
			int childPosition, int kindOfAction) {
		return super.getValidChildElementDeclarations(parent, childPosition,
				kindOfAction);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected boolean isCloseRegion(ITextRegion region) {
		return super.isCloseRegion(region);
	}

	@Override
	protected boolean isNameRegion(ITextRegion region) {
		return super.isNameRegion(region);
	}

	@Override
	protected boolean isQuote(String string) {
		return super.isQuote(string);
	}

	@Override
	protected Properties mapToProperties(CMNamedNodeMap map) {
		return super.mapToProperties(map);
	}

	@Override
	public void setErrorMessage(String errorMessage) {
		super.setErrorMessage(errorMessage);
	}

	@Override
	protected void setErrorMessage(String errorMessage, String append) {
		super.setErrorMessage(errorMessage, append);
	}

	@Override
	protected void setErrorMessage(String errorMessage, String prepend,
			String append) {
		super.setErrorMessage(errorMessage, prepend, append);
	}

	@Override
	protected boolean stringsEqual(String a, String b) {
		return super.stringsEqual(a, b);
	}

}