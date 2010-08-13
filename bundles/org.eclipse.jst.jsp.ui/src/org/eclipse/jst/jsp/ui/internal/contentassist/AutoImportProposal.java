/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Genuitec, LLC - Fix for bug 203303
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP20TLDNames;
import org.eclipse.jst.jsp.core.internal.provisional.JSP11Namespace;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AutoImportProposal extends JSPCompletionProposal {
	
	// the import string, no quotes or colons
	String fImportDeclaration;
	IImportContainer fImportContainer;

	public AutoImportProposal(String importDeclaration, String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance, boolean updateReplacementLengthOnValidate) {
		super(replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo, relevance, updateReplacementLengthOnValidate);
		setImportDeclaration(importDeclaration);
	}

	public AutoImportProposal(String importDeclaration, IImportContainer importContainer ,String replacementString, int replacementOffset, int replacementLength, int cursorPosition, Image image, String displayString, IContextInformation contextInformation, String additionalProposalInfo, int relevance, boolean updateReplacementLengthOnValidate) {
		this(importDeclaration, replacementString, replacementOffset, replacementLength, cursorPosition, image, displayString, contextInformation, additionalProposalInfo, relevance, updateReplacementLengthOnValidate);
		fImportContainer = importContainer;
	}

	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		super.apply(viewer, trigger, stateMask, offset);
		// if the import doesn't exist, add it
		String importDecl = getImportDeclaration().replaceAll(";", "");  //$NON-NLS-1$//$NON-NLS-2$
		if (fImportContainer == null || !(fImportContainer.getImport(importDecl).exists() || isImportPageDirective(viewer, offset)))
			addImportDeclaration(viewer);
	}
	
	private boolean isImportPageDirective(ITextViewer viewer, int offset){
		Node node = (Node) ContentAssistUtils.getNodeAt(viewer, offset);
		
		while ((node != null) && (node.getNodeType() == Node.TEXT_NODE) && (node.getParentNode() != null)) {
			node = node.getParentNode();
		}
		if (node.getNodeName().equalsIgnoreCase(JSP11Namespace.ElementName.DIRECTIVE_PAGE)){
			NamedNodeMap nodeMap = node.getAttributes();
			if (nodeMap != null)
				return nodeMap.getNamedItem(JSP20TLDNames.IMPORT) != null;
		}
		
		return false ;
	}
	/**
	 * adds the import declaration to the document in the viewer in the appropriate position
	 * @param viewer
	 */
	private void addImportDeclaration(ITextViewer viewer) {
		IDocument doc = viewer.getDocument();
		
		// calculate once and pass along
		boolean isXml = isXmlFormat(doc);
		
		int insertPosition = getInsertPosition(doc, isXml);
		String insertText = createImportDeclaration(doc, isXml);
		InsertEdit insert = new InsertEdit(insertPosition, insertText);
		try {
			insert.apply(doc);
		}
		catch (MalformedTreeException e) {
			Logger.logException(e);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		
		// make sure the cursor position after is correct
		setCursorPosition(getCursorPosition() + insertText.length());
	}
	
 	private Node getInsertNode(IDOMDocument documentNode) {
		NodeList childNodes = documentNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
				return childNodes.item(i);
		}
		return documentNode.getFirstChild();
	}
	
	/**
	 * 
	 * @param doc
	 * @param isXml
	 * @return position after <jsp:root> if xml, otherwise right before the document element
	 */
	private int getInsertPosition(IDocument doc, boolean isXml) {
		int pos = 0;
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
		try {
			if (sModel != null) {
				if (sModel instanceof IDOMModel) {
					IDOMDocument documentNode = ((IDOMModel) sModel).getDocument();
					/*
					 * document element must be sole Element child of Document
					 * to remain valid
					 */
					Node targetElement = null;
					if (isXml) {
						targetElement = documentNode.getDocumentElement();
					}
					if (targetElement == null)
						targetElement = getInsertNode(documentNode);
					if (targetElement != null) {
						IStructuredDocumentRegion sdRegion = ((IDOMNode) targetElement).getFirstStructuredDocumentRegion();
						if (isXml) {
							/*
							 * document Element must be sole Element child of
							 * Document to remain valid, so insert after
							 */
							pos = sdRegion.getEndOffset();
							try {
								while (pos < doc.getLength() && (doc.getChar(pos) == '\r' || doc.getChar(pos) == '\n')) {
									pos++;
								}
							}
							catch (BadLocationException e) {
								// not important, use pos as determined earlier
							}
						}
						else {
							// insert before target element
							pos = sdRegion.getStartOffset();
						}
					}
					else {
						pos = 0;
					}
				}
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return pos;
	}
	
	// Genuitec bug #6227,
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=203303
	private boolean isCustomTagDocument(IDocument doc) {
		boolean isTag = false;
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
		try {
			if (sModel instanceof IDOMModel) {
				String contentType = ((IDOMModel) sModel).getContentTypeIdentifier();
				if (contentType != null) {
					IContentType modelCT = Platform.getContentTypeManager().getContentType(contentType);
					IContentType tagCT = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPTAG);
					if (modelCT != null && tagCT != null) {
						isTag = modelCT.isKindOf(tagCT);
					}
				}
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return isTag;
	}
		 
	/**
	 * 
	 * @param doc
	 * @return true if this document is xml-jsp syntax, otherwise false
	 */
	private boolean isXmlFormat(IDocument doc) {
		boolean isXml = false;
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(doc);
		try {
			if (sModel != null) {
				if (!isXml) {
					if (sModel instanceof IDOMModel) {
						IDOMDocument documentNode = ((IDOMModel) sModel).getDocument();
						Element docElement = documentNode.getDocumentElement();
						isXml = docElement != null && ((docElement.getNodeName().equals("jsp:root")) || docElement.getAttributeNode("xmlns:jsp") != null || ((((IDOMNode) docElement).getStartStructuredDocumentRegion() == null && ((IDOMNode) docElement).getEndStructuredDocumentRegion() == null))); //$NON-NLS-1$  //$NON-NLS-2$
					}
				}
			}
		}
		finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}
		return isXml;
	}
	/**
	 * 
	 * @param doc
	 * @param isXml
	 * @return appropriate import declaration string depending if document is xml or not
	 */
	private String createImportDeclaration(IDocument doc, boolean isXml) {
		String delim = (doc instanceof IStructuredDocument) ? ((IStructuredDocument) doc).getLineDelimiter() : TextUtilities.getDefaultLineDelimiter(doc);
		boolean isCustomTag = isCustomTagDocument(doc);
		final String opening;
		final String closing;
		if (isCustomTag) {
			if (isXml) {
				opening = "<jsp:directive.tag import=\""; //$NON-NLS-1$
				closing = "\"/>"; //$NON-NLS-1$
			}
			else {
				opening = "<%@tag import=\""; //$NON-NLS-1$
				closing = "\"%>"; //$NON-NLS-1$
			}
		}
		else {
			if (isXml) {
				opening = "<jsp:directive.page import=\""; //$NON-NLS-1$
				closing = "\"/>"; //$NON-NLS-1$
			}
			else {
				opening = "<%@page import=\""; //$NON-NLS-1$
				closing = "\"%>"; //$NON-NLS-1$
			}
		}
		return opening + getImportDeclaration() + closing + delim;
	}

	public String getImportDeclaration() {
		return fImportDeclaration;
	}
	public void setImportDeclaration(String importDeclaration) {
		fImportDeclaration = importDeclaration;
	}
}
