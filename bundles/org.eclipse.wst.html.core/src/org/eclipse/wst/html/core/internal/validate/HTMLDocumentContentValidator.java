/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;


import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HTMLDocumentContentValidator extends PrimeValidator {


	private final static class Division {
		private Vector explicitHtmls = new Vector();
		private Vector rest = new Vector();

		public Division(Document document, NodeList children) {
			String rootTagName = getRootTagName(document);
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (isHtmlTag(child, rootTagName)) {
					explicitHtmls.add(child);
				}
				else {
					rest.add(child);
				}
			}
		}

		public boolean hasExplicitHtmls() {
			return explicitHtmls.size() > 0;
		}

		public List getExplicitHtmls() {
			return explicitHtmls;
		}

		public Iterator getRestNodes() {
			return rest.iterator();
		}

		/* utilities */
		private static boolean isHtmlTag(Node node, String tagName) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				return false;
			return ((Element) node).getTagName().equalsIgnoreCase(tagName);
		}

		private static String getRootTagName(Document document) {
			DocumentTypeAdapter adapter = (DocumentTypeAdapter) ((IDOMDocument) document).getAdapterFor(DocumentTypeAdapter.class);
			if (adapter != null) {
				DocumentType docType = adapter.getDocumentType();
				if (docType != null) {
					return docType.getName();
				}
			}

			return HTML40Namespace.ElementName.HTML;
		}
	}

	/**
	 * HTMLDocumentContentValidator constructor comment.
	 */
	public HTMLDocumentContentValidator() {
		super();
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return ((type == HTMLDocumentContentValidator.class) || super.isAdapterForType(type));
	}

	/**
	 */
	public void validate(IndexedRegion node) {
		// isFragment check should be more intelligent.
		boolean isFragment = true;

		Document target = (Document) node;
		NodeList children = target.getChildNodes();
		if (children == null)
			return;

		Division division = new Division(target, children);
		if (division.hasExplicitHtmls()) {
			isFragment = false;

			List explicits = division.getExplicitHtmls();
			if (explicits.size() > 1) {
				for (int i = 1; i < explicits.size(); i++) {
					Element html = (Element) explicits.get(i);
					// report error (duplicate)
					Segment errorSeg = FMUtil.getSegment((IDOMNode) html, FMUtil.SEG_START_TAG);
					if (errorSeg != null)
						reporter.report(new ErrorInfoImpl(ErrorState.DUPLICATE_ERROR, errorSeg, html));
				}
			}
		}
		validateContent(division.getRestNodes(), isFragment);
	}

	/*
	 * This methods validate nodes other than HTML elements.
	 */
	private void validateContent(Iterator children, boolean isFragment) {
		boolean foundDoctype = false;
		while (children.hasNext()) {
			IDOMNode child = (IDOMNode) children.next();

			int error = ErrorState.NONE_ERROR;
			int segType = FMUtil.SEG_WHOLE_TAG;

			switch (child.getNodeType()) {
				case Node.ELEMENT_NODE :
					if (!isFragment) {
						Element childElem = (Element) child;
						CMElementDeclaration ced = CMUtil.getDeclaration(childElem);
						// Undefined element is valid.
						if (ced == null)
							continue;
						// JSP (includes custom tags) and SSI are valid.
						if (CMUtil.isForeign(childElem) || CMUtil.isSSI(ced))
							continue; // Defect 186774

						// report error (invalid content)
						error = ErrorState.INVALID_CONTENT_ERROR;
						// mark the whole start tag as error.
						segType = FMUtil.SEG_START_TAG;
					}
					break;
				case Node.TEXT_NODE :
					if (!isFragment) {
						// TEXT node is valid when it contains white space
						// characters only.
						// Otherwise, it is invalid content.
						if (((IDOMText) child).isElementContentWhitespace())
							continue;
						error = ErrorState.INVALID_CONTENT_ERROR;
						segType = FMUtil.SEG_WHOLE_TAG;
					}
					break;
				case Node.DOCUMENT_TYPE_NODE :
					// DOCTYPE is also valid when it appears once and only
					// once.
					if (!foundDoctype) {
						foundDoctype = true;
						continue;
					}
					error = ErrorState.DUPLICATE_ERROR;
					segType = FMUtil.SEG_WHOLE_TAG;
					break;
				case Node.COMMENT_NODE :
				// always valid.
				case Node.PROCESSING_INSTRUCTION_NODE :
					continue;
				default :
					if (!isFragment) {
						error = ErrorState.INVALID_CONTENT_ERROR;
						segType = FMUtil.SEG_WHOLE_TAG;
					}
					break;
			}
			if (error != ErrorState.NONE_ERROR) {
				Segment errorSeg = FMUtil.getSegment(child, segType);
				if (errorSeg != null)
					reporter.report(new ErrorInfoImpl(error, errorSeg, child));
			}
		}
	}

}
