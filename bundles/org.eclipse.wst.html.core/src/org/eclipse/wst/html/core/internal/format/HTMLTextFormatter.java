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
package org.eclipse.wst.html.core.internal.format;



import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.html.core.internal.provisional.HTMLFormatContraints;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTMLTextFormatter extends HTMLFormatter {

	public static int FORMAT_ALL = 0;
	public static int FORMAT_HEAD = 1;
	public static int FORMAT_TAIL = 2;

	/**
	 */
	protected HTMLTextFormatter() {
		super();
	}

	/**
	 */
	private boolean canFormatText(IDOMText text) {
		if (text == null)
			return false;

		IStructuredDocumentRegion flatNode = text.getFirstStructuredDocumentRegion();
		if (flatNode != null) {
			String type = flatNode.getType();
			if (isUnparsedRegion(type))
				return false;
		}

		Node parent = text.getParentNode();
		if (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			IDOMElement element = (IDOMElement) parent;
			if (!element.isGlobalTag() && !text.isElementContentWhitespace())
				return false;
		}

		return canFormatChild(parent);
	}

	private boolean isUnparsedRegion(String type) {
		boolean result = isNestedScannedRegion(type) || isBlockScannedRegion(type);
		return result;
	}

	private boolean isBlockScannedRegion(String type) {
		return type == DOMRegionContext.BLOCK_TEXT;
	}

	/**
	 * ISSUE: this is a bit of hidden JSP knowledge that was implemented this
	 * way for expedency. Should be evolved in future to depend on
	 * "nestedContext".
	 */
	private boolean isNestedScannedRegion(String type) {
		final String JSP_CONTENT = "JSP_CONTENT"; //$NON-NLS-1$
		return type.equals(JSP_CONTENT);
	}

	/**
	 */
	private boolean canRemoveHeadingSpaces(IDOMNode node) {
		if (node == null)
			return false;
		if (node.getPreviousSibling() != null)
			return false;
		Node parent = node.getParentNode();
		if (parent == null || parent.getNodeType() != Node.ELEMENT_NODE)
			return false;

		CMElementDeclaration decl = getElementDeclaration((Element) parent);
		if (decl == null || (!decl.supports(HTMLCMProperties.LINE_BREAK_HINT)))
			return false;
		String hint = (String) decl.getProperty(HTMLCMProperties.LINE_BREAK_HINT);
		return hint.equals(HTMLCMProperties.Values.BREAK_BEFORE_START_AND_AFTER_END);
	}

	/**
	 */
	private boolean canRemoveTailingSpaces(IDOMNode node) {
		if (node == null)
			return false;
		if (node.getNextSibling() != null)
			return false;
		Node parent = node.getParentNode();
		if (parent == null || parent.getNodeType() != Node.ELEMENT_NODE)
			return false;

		CMElementDeclaration decl = getElementDeclaration((Element) parent);
		if (decl == null || (!decl.supports(HTMLCMProperties.LINE_BREAK_HINT)))
			return false;
		String hint = (String) decl.getProperty(HTMLCMProperties.LINE_BREAK_HINT);
		return hint.equals(HTMLCMProperties.Values.BREAK_BEFORE_START_AND_AFTER_END);
	}

	/**
	 */
	protected void formatNode(IDOMNode node, HTMLFormatContraints contraints) {
		formatText(node, contraints, FORMAT_ALL); // full format
	}

	/**
	 */
	protected void formatText(IDOMNode node, HTMLFormatContraints contraints, int mode) {
		if (node == null)
			return;
		Node parent = node.getParentNode();
		if (parent == null)
			return;

		IDOMText text = (IDOMText) node;
		String source = text.getSource();

		if (!canFormatText(text)) {
			setWidth(contraints, source);
			return;
		}

		int offset = text.getStartOffset();
		int length = text.getEndOffset() - offset;

		// format adjacent text at once
		if (mode == FORMAT_HEAD) {
			Node next = node.getNextSibling();
			while (next != null && next.getNodeType() == Node.TEXT_NODE) {
				IDOMText nextText = (IDOMText) next;
				length += (nextText.getEndOffset() - nextText.getStartOffset());
				String nextSource = nextText.getSource();
				if (nextSource != null && nextSource.length() > 0) {
					if (source == null)
						source = nextSource;
					else
						source += nextSource;
				}
				next = next.getNextSibling();
			}
		}
		else if (mode == FORMAT_TAIL) {
			Node prev = node.getPreviousSibling();
			while (prev != null && prev.getNodeType() == Node.TEXT_NODE) {
				IDOMText prevText = (IDOMText) prev;
				offset = prevText.getStartOffset();
				length += (prevText.getEndOffset() - offset);
				String prevSource = prevText.getSource();
				if (prevSource != null && prevSource.length() > 0) {
					if (source == null)
						source = prevSource;
					else
						source = prevSource + source;
				}
				prev = prev.getPreviousSibling();
			}
		}

		SpaceConverter converter = new SpaceConverter(source, keepBlankLines(contraints));

		int wordLength = converter.nextWord();
		if (wordLength == 0) { // only spaces
			if (!converter.hasSpaces())
				return; // empty
			boolean removeSpaces = false;
			if (parent.getNodeType() == Node.ELEMENT_NODE) {
				// check if tags are omitted
				IDOMNode element = (IDOMNode) parent;
				if (node.getPreviousSibling() == null && element.getStartStructuredDocumentRegion() == null) {
					removeSpaces = true;
				}
				else if (node.getNextSibling() == null && element.getEndStructuredDocumentRegion() == null) {
					removeSpaces = true;
				}
			}
			if (removeSpaces) {
				converter.replaceSpaces(null);
			}
			else if (!isWidthAvailable(contraints, 2) || canInsertBreakAfter(node) || canInsertBreakBefore(node)) {
				String spaces = null;
				if (node.getNextSibling() == null) { // last spaces
					// use parent indent for the end tag
					spaces = getBreakSpaces(parent);
				}
				else {
					spaces = getBreakSpaces(node);
				}
				converter.replaceSpaces(spaces);
				setWidth(contraints, spaces);
			}
			else if (canRemoveHeadingSpaces(node) || canRemoveTailingSpaces(node)) {
				converter.replaceSpaces(null);
			}
			else {
				converter.compressSpaces();
				addWidth(contraints, 1);
			}
		}
		else {
			String breakSpaces = null;

			// format heading spaces
			boolean hasSpaces = converter.hasSpaces();
			if (mode == FORMAT_TAIL) {
				// keep spaces as is
				addWidth(contraints, converter.getSpaceCount());
			}
			else if ((hasSpaces && !isWidthAvailable(contraints, wordLength + 1)) || canInsertBreakBefore(node)) {
				breakSpaces = getBreakSpaces(node);
				converter.replaceSpaces(breakSpaces);
				setWidth(contraints, breakSpaces);
			}
			else {
				if (hasSpaces) {
					if (canRemoveHeadingSpaces(node)) {
						converter.replaceSpaces(null);
					}
					else {
						converter.compressSpaces();
						addWidth(contraints, 1);
					}
				}
			}
			addWidth(contraints, wordLength);

			// format middle
			wordLength = converter.nextWord();
			while (wordLength > 0) {
				if (mode != FORMAT_ALL) {
					// keep spaces as is
					addWidth(contraints, converter.getSpaceCount());
				}
				else if (!isWidthAvailable(contraints, wordLength + 1)) {
					if (breakSpaces == null)
						breakSpaces = getBreakSpaces(node);
					converter.replaceSpaces(breakSpaces);
					setWidth(contraints, breakSpaces);
				}
				else {
					converter.compressSpaces();
					addWidth(contraints, 1);
				}
				addWidth(contraints, wordLength);
				wordLength = converter.nextWord();
			}

			// format tailing spaces
			hasSpaces = converter.hasSpaces();
			if (mode == FORMAT_HEAD) {
				// keep spaces as is
				addWidth(contraints, converter.getSpaceCount());
			}
			else if ((hasSpaces && !isWidthAvailable(contraints, 2)) || canInsertBreakAfter(node)) {
				if (node.getNextSibling() == null) { // last test
					// use parent indent for the end tag
					breakSpaces = getBreakSpaces(parent);
				}
				else {
					if (breakSpaces == null)
						breakSpaces = getBreakSpaces(node);
				}
				converter.replaceSpaces(breakSpaces);
				setWidth(contraints, breakSpaces);
			}
			else {
				if (hasSpaces) {
					if (canRemoveTailingSpaces(node)) {
						converter.replaceSpaces(null);
					}
					else {
						converter.compressSpaces();
						addWidth(contraints, 1);
					}
				}
			}
		}

		if (converter.isModified()) {
			source = converter.getSource();
			replaceSource(text.getModel(), offset, length, source);
		}
	}
}
