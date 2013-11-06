/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.handlers;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PageImport {

	private String name;

	public PageImport(String name) {
		this.name = name;
	}

	/**
	 * Adds the page import to the document
	 * @param document the JSP document to have the import added to
	 * @return the length of text that was inserted
	 */
	public int add(IDocument document) {
		int offset = -1;
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
		try {
			if (model instanceof IDOMModel) {
				final IDOMModel dom = (IDOMModel) model;
				final boolean isXML = isXMLDocument(dom);
				final int insertPosition = getInsertPosition(dom, isXML);
				offset = insertImportDeclaration(document, insertPosition, isXML);
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return offset;
	}

	/**
	 * Determines if the document is an XML-style JSP
	 * 
	 * @param model the JSP's dom model
	 * @return true is the document is an xml-style JSP
	 */
	private boolean isXMLDocument(IDOMModel model) {
		final IDOMDocument documentNode = model.getDocument();
		final Element docElement = documentNode.getDocumentElement();
		return docElement != null && ((docElement.getNodeName().equals("jsp:root")) || docElement.getAttributeNode("xmlns:jsp") != null || ((((IDOMNode) docElement).getStartStructuredDocumentRegion() == null && ((IDOMNode) docElement).getEndStructuredDocumentRegion() == null))); //$NON-NLS-1$  //$NON-NLS-2$
	}

	private int getInsertPosition(IDOMModel model, boolean isXML) {
		int pos = 0;
		IDOMDocument documentNode = model.getDocument();
		/*
		 * document element must be sole Element child of Document
		 * to remain valid
		 */
		Node targetElement = null;
		if (isXML) {
			targetElement = documentNode.getDocumentElement();
		}
		if (targetElement == null)
			targetElement = getInsertNode(documentNode);
		if (targetElement != null) {
			IStructuredDocumentRegion sdRegion = ((IDOMNode) targetElement).getFirstStructuredDocumentRegion();
			if (isXML) {
				/*
				 * document Element must be sole Element child of
				 * Document to remain valid, so insert after
				 */
				pos = sdRegion.getEndOffset();
				try {
					final IStructuredDocument doc = model.getStructuredDocument();
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
		return pos;
	}

	private Node getInsertNode(IDOMDocument documentNode) {
		NodeList childNodes = documentNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
				return childNodes.item(i);
		}
		return documentNode.getFirstChild();
	}

	private int insertImportDeclaration(IDocument document, int position, boolean isXML) {
		String delim = (document instanceof IStructuredDocument) ? ((IStructuredDocument) document).getLineDelimiter() : TextUtilities.getDefaultLineDelimiter(document);
		boolean isCustomTag = isCustomTagDocument(document);
		final String opening;
		final String closing;
		if (isCustomTag) {
			if (isXML) {
				opening = "<jsp:directive.tag import=\""; //$NON-NLS-1$
				closing = "\"/>"; //$NON-NLS-1$
			}
			else {
				opening = "<%@tag import=\""; //$NON-NLS-1$
				closing = "\"%>"; //$NON-NLS-1$
			}
		}
		else {
			if (isXML) {
				opening = "<jsp:directive.page import=\""; //$NON-NLS-1$
				closing = "\"/>"; //$NON-NLS-1$
			}
			else {
				opening = "<%@page import=\""; //$NON-NLS-1$
				closing = "\"%>"; //$NON-NLS-1$
			}
		}
		final String declaration = opening + name + closing + delim;
		final InsertEdit edit = new InsertEdit(position, declaration);
		try {
			edit.apply(document);
			return declaration.length();
		}
		catch (BadLocationException e) {
		}
		catch (MalformedTreeException e) {
		}
		return -1;
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
	
}
