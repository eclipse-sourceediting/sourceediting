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
package org.eclipse.jst.jsp.ui.internal.contentassist;



import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.IResourceDependentProcessor;
import org.eclipse.wst.xml.core.document.DOMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.ui.contentassist.AbstractContentAssistProcessor;
import org.eclipse.wst.xml.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.contentassist.XMLContentModelGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author pavery
 *
 * This class is a "null" version of AbstractContentAssistProcessor
 */
public class JSPDummyContentAssistProcessor extends AbstractContentAssistProcessor implements IResourceDependentProcessor {

	IResource fResource = null;

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addAttributeNameProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addAttributeNameProposals(ContentAssistRequest contentAssistRequest) {
		super.addAttributeNameProposals(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addAttributeValueProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addAttributeValueProposals(ContentAssistRequest contentAssistRequest) {
		super.addAttributeValueProposals(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addCommentProposal(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addCommentProposal(ContentAssistRequest contentAssistRequest) {
		super.addCommentProposal(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addContent(java.util.List, org.eclipse.wst.xml.core.internal.contentmodel.CMContent)
	 */
	protected void addContent(List contentList, CMContent content) {
		super.addContent(contentList, content);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addDocTypeProposal(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addDocTypeProposal(ContentAssistRequest contentAssistRequest) {
		super.addDocTypeProposal(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addEmptyDocumentProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addEmptyDocumentProposals(ContentAssistRequest contentAssistRequest) {
		super.addEmptyDocumentProposals(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addEndTagNameProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addEndTagNameProposals(ContentAssistRequest contentAssistRequest) {
		super.addEndTagNameProposals(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addEndTagProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addEndTagProposals(ContentAssistRequest contentAssistRequest) {
		super.addEndTagProposals(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addEntityProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest, int, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode)
	 */
	protected void addEntityProposals(ContentAssistRequest contentAssistRequest, int documentPosition, ITextRegion completionRegion, DOMNode treeNode) {
		super.addEntityProposals(contentAssistRequest, documentPosition, completionRegion, treeNode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addEntityProposals(java.util.Vector, java.util.Properties, java.lang.String, int, com.ibm.sed.structuredDocument.ITextRegion)
	 */
	protected void addEntityProposals(Vector proposals, Properties map, String key, int nodeOffset, IStructuredDocumentRegion parent, ITextRegion completionRegion) {
		super.addEntityProposals(proposals, map, key, nodeOffset, parent, completionRegion);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addPCDATAProposal(java.lang.String, com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addPCDATAProposal(String nodeName, ContentAssistRequest contentAssistRequest) {
		super.addPCDATAProposal(nodeName, contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addStartDocumentProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addStartDocumentProposals(ContentAssistRequest contentAssistRequest) {
		super.addStartDocumentProposals(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addTagCloseProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addTagCloseProposals(ContentAssistRequest contentAssistRequest) {
		super.addTagCloseProposals(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addTagInsertionProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest, int)
	 */
	protected void addTagInsertionProposals(ContentAssistRequest contentAssistRequest, int childPosition) {
		super.addTagInsertionProposals(contentAssistRequest, childPosition);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addTagNameProposals(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest, int)
	 */
	protected void addTagNameProposals(ContentAssistRequest contentAssistRequest, int childPosition) {
		super.addTagNameProposals(contentAssistRequest, childPosition);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#addXMLProposal(com.ibm.sed.structured.contentassist.xml.ContentAssistRequest)
	 */
	protected void addXMLProposal(ContentAssistRequest contentAssistRequest) {
		super.addXMLProposal(contentAssistRequest);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#attributeInList(com.ibm.sed.model.xml.DOMNode, org.w3c.dom.Node, org.eclipse.wst.xml.core.internal.contentmodel.CMNode)
	 */
	protected boolean attributeInList(DOMNode node, Node parent, CMNode cmnode) {
		return super.attributeInList(node, parent, cmnode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#beginsWith(java.lang.String, java.lang.String)
	 */
	protected boolean beginsWith(String aString, String prefix) {
		return super.beginsWith(aString, prefix);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeAttributeProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeAttributeProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeAttributeProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeAttributeValueProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeAttributeValueProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeAttributeValueProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeCompletionProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeCompletionProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode treeNode, DOMNode xmlnode) {
		return super.computeCompletionProposals(documentPosition, matchString, completionRegion, treeNode, xmlnode);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		return super.computeCompletionProposals(viewer, documentOffset);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeContentProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeContentProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeContentProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return super.computeContextInformation(viewer, documentOffset);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeEndTagOpenProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeEndTagOpenProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeEndTagOpenProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeEntityReferenceProposals(int, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ICompletionProposal[] computeEntityReferenceProposals(int documentPosition, ITextRegion completionRegion, DOMNode treeNode) {
		return super.computeEntityReferenceProposals(documentPosition, completionRegion, treeNode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeEqualsProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeEqualsProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeEqualsProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeStartDocumentProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeStartDocumentProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeStartDocumentProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeTagCloseProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeTagCloseProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeTagCloseProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeTagNameProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeTagNameProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeTagNameProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#computeTagOpenProposals(int, java.lang.String, com.ibm.sed.structuredDocument.ITextRegion, com.ibm.sed.model.xml.DOMNode, com.ibm.sed.model.xml.DOMNode)
	 */
	protected ContentAssistRequest computeTagOpenProposals(int documentPosition, String matchString, ITextRegion completionRegion, DOMNode nodeAtOffset, DOMNode node) {
		return super.computeTagOpenProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getAdditionalInfo(org.eclipse.wst.xml.core.internal.contentmodel.CMNode, org.eclipse.wst.xml.core.internal.contentmodel.CMNode)
	 */
	protected String getAdditionalInfo(CMNode parentOrOwner, CMNode cmnode) {
		return super.getAdditionalInfo(parentOrOwner, cmnode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getAvailableChildrenAtIndex(org.w3c.dom.Element, int)
	 */
	protected List getAvailableChildrenAtIndex(Element parent, int index) {
		return super.getAvailableChildrenAtIndex(parent, index);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getAvailableRootChildren(org.w3c.dom.Document, int)
	 */
	protected List getAvailableRootChildren(Document document, int childIndex) {
		return super.getAvailableRootChildren(document, childIndex);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getCMElementDeclaration(org.w3c.dom.Node)
	 */
	protected CMElementDeclaration getCMElementDeclaration(Node node) {
		return super.getCMElementDeclaration(node);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return super.getCompletionProposalAutoActivationCharacters();
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getCompletionRegion(int, com.ibm.sed.structuredDocument.core.IStructuredDocumentRegion)
	 */
	protected ITextRegion getCompletionRegion(int offset, IStructuredDocumentRegion flatNode) {
		return super.getCompletionRegion(offset, flatNode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getCompletionRegion(int, org.w3c.dom.Node)
	 */
	protected ITextRegion getCompletionRegion(int documentPosition, Node domnode) {
		return super.getCompletionRegion(documentPosition, domnode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getContentGenerator()
	 */
	public XMLContentModelGenerator getContentGenerator() {
		return super.getContentGenerator();
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return super.getContextInformationAutoActivationCharacters();
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return super.getContextInformationValidator();
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getElementPosition(org.w3c.dom.Node)
	 */
	protected int getElementPosition(Node child) {
		return super.getElementPosition(child);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return super.getErrorMessage();
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getMatchString(com.ibm.sed.structuredDocument.ITextRegion, int)
	 */
	protected String getMatchString(IStructuredDocumentRegion parent, ITextRegion aRegion, int offset) {
		return super.getMatchString(parent, aRegion, offset);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getNameRegion(com.ibm.sed.structuredDocument.IStructuredDocumentRegion)
	 */
	protected ITextRegion getNameRegion(IStructuredDocumentRegion flatNode) {
		return super.getNameRegion(flatNode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getPossibleDataTypeValues(org.w3c.dom.Node, org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration)
	 */
	protected List getPossibleDataTypeValues(Node node, CMAttributeDeclaration ad) {
		return super.getPossibleDataTypeValues(node, ad);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getRequiredName(org.w3c.dom.Node, org.eclipse.wst.xml.core.internal.contentmodel.CMNode)
	 */
	protected String getRequiredName(Node parentOrOwner, CMNode cmnode) {
		return super.getRequiredName(parentOrOwner, cmnode);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getRequiredText(org.w3c.dom.Node, org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration)
	 */
	protected String getRequiredText(Node parentOrOwner, CMAttributeDeclaration attrDecl) {
		return super.getRequiredText(parentOrOwner, attrDecl);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getRequiredText(org.w3c.dom.Node, org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration)
	 */
	protected String getRequiredText(Node parentOrOwner, CMElementDeclaration elementDecl) {
		return super.getRequiredText(parentOrOwner, elementDecl);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#getValidCMNodes(int, int, java.util.List)
	 */
	protected List getValidCMNodes(int childPosition, int kindOfAction, List modelQueryActions) {
		return super.getValidCMNodes(childPosition, kindOfAction, modelQueryActions);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#init()
	 */
	protected void init() {
		super.init();
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#initialize(org.eclipse.core.resources.IResource)
	 */
	public void initialize(IResource iResource) {
		fResource = iResource;
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#isCloseRegion(com.ibm.sed.structuredDocument.ITextRegion)
	 */
	protected boolean isCloseRegion(ITextRegion region) {
		return super.isCloseRegion(region);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#isNameRegion(com.ibm.sed.structuredDocument.ITextRegion)
	 */
	protected boolean isNameRegion(ITextRegion region) {
		return super.isNameRegion(region);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#isQuote(java.lang.String)
	 */
	protected boolean isQuote(String string) {
		return super.isQuote(string);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#mapToProperties(org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap)
	 */
	protected Properties mapToProperties(CMNamedNodeMap map) {
		return super.mapToProperties(map);
	}

	//	/**
	//	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#newContentAssistRequest(org.w3c.dom.Node, org.w3c.dom.Node, com.ibm.sed.structuredDocument.ITextRegion, int, int, java.lang.String)
	//	 */
	//	protected ContentAssistRequest newContentAssistRequest(Node node, Node possibleParent, ITextRegion completionRegion, int begin, int length, String filter) {
	//		return super.newContentAssistRequest(node, possibleParent, completionRegion, begin, length, filter);
	//	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#release()
	 */
	public void release() {
		super.release();
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#setErrorMessage(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void setErrorMessage(String errorMessage, String prepend, String append) {
		super.setErrorMessage(errorMessage, prepend, append);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#setErrorMessage(java.lang.String, java.lang.String)
	 */
	protected void setErrorMessage(String errorMessage, String append) {
		super.setErrorMessage(errorMessage, append);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#setErrorMessage(java.lang.String)
	 */
	public void setErrorMessage(String errorMessage) {
		super.setErrorMessage(errorMessage);
	}

	/**
	 * @see com.ibm.sed.structured.contentassist.xml.AbstractContentAssistProcessor#stringsEqual(java.lang.String, java.lang.String)
	 */
	protected boolean stringsEqual(String a, String b) {
		return super.stringsEqual(a, b);
	}

}