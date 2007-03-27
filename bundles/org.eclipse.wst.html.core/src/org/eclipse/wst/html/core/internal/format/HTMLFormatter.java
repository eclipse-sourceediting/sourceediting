/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.format;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.html.core.internal.provisional.HTMLFormatContraints;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.StructuredFormatPreferencesXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTMLFormatter implements IStructuredFormatter {

	private static final String HTML_NAME = "html";//$NON-NLS-1$
	private static final String BODY_NAME = "BODY";//$NON-NLS-1$

	/**
	 */
	protected void addWidth(HTMLFormatContraints contraints, int width) {
		if (contraints == null)
			return;
		if (!splitLines() || getLineWidth() < 0)
			return;

		int availableWidth = contraints.getAvailableLineWidth() - width;
		if (availableWidth < 0)
			availableWidth = 0;
		contraints.setAvailableLineWidth(availableWidth);
	}

	/**
	 */
	protected boolean canFormatChild(Node node) {
		while (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				return true;
			CMElementDeclaration decl = getElementDeclaration((Element) node);
			if (decl != null) {
				if (decl.getContentType() == CMElementDeclaration.CDATA)
					return false;
				if (decl.supports(HTMLCMProperties.SHOULD_KEEP_SPACE)) {
					boolean shouldKeepSpace = ((Boolean) decl.getProperty(HTMLCMProperties.SHOULD_KEEP_SPACE)).booleanValue();
					if (shouldKeepSpace)
						return false;
				}
			}
			node = node.getParentNode();
		}
		return false;
	}

	/**
	 */
	protected boolean canInsertBreakAfter(CMElementDeclaration decl) {
		if (decl == null)
			return false;
		if (!decl.supports(HTMLCMProperties.LINE_BREAK_HINT))
			return false;
		String hint = (String) decl.getProperty(HTMLCMProperties.LINE_BREAK_HINT);
		if (hint == null)
			return false;
		return (hint.equals(HTMLCMProperties.Values.BREAK_BEFORE_START_AND_AFTER_END) || hint.equals(HTMLCMProperties.Values.BREAK_AFTER_START));
	}

	/**
	 */
	protected boolean canInsertBreakAfter(Node node) {
		if (node == null)
			return false;
		Node parent = node.getParentNode();
		if (parent == null)
			return false;
		Node next = node.getNextSibling();

		if (parent.getNodeType() == Node.DOCUMENT_NODE) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				// do not insert break after unclosed tag
				if (!((IDOMElement) node).isClosed())
					return false;
			}
			return true;
		}
		else if (parent.getNodeType() == Node.ELEMENT_NODE) {
			IDOMElement element = (IDOMElement) parent;
			// do not insert break before missing end tag
			if (next == null && element.getEndStructuredDocumentRegion() == null)
				return false;

			// insert line break under non-HTML elements including JSP elements
			if (element.getPrefix() != null)
				return true;

			CMElementDeclaration decl = getElementDeclaration(element);
			if (decl != null) {
				if (decl.getContentType() == CMElementDeclaration.ELEMENT)
					return true;
				String tagName = element.getTagName();
				// special for direct children under BODY
				if (tagName != null && tagName.equalsIgnoreCase(BODY_NAME))
					return true;
			}
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			IDOMElement element = (IDOMElement) node;
			CMElementDeclaration decl = getElementDeclaration(element);
			if (canInsertBreakAfter(decl)) {
				// spcial for BR
				return canFormatChild(parent);
			}
		}
		if (next != null && next.getNodeType() == Node.ELEMENT_NODE) {
			CMElementDeclaration decl = getElementDeclaration((Element) next);
			if (canInsertBreakBefore(decl))
				return true;
		}
		return false;
	}

	/**
	 */
	protected boolean canInsertBreakBefore(CMElementDeclaration decl) {
		if (decl == null)
			return false;
		if (!decl.supports(HTMLCMProperties.LINE_BREAK_HINT))
			return false;
		String hint = (String) decl.getProperty(HTMLCMProperties.LINE_BREAK_HINT);
		if (hint == null)
			return false;
		return hint.equals(HTMLCMProperties.Values.BREAK_BEFORE_START_AND_AFTER_END);
	}

	/**
	 */
	protected boolean canInsertBreakBefore(Node node) {
		if (node == null)
			return false;
		Node parent = node.getParentNode();
		if (parent == null)
			return false;
		Node prev = node.getPreviousSibling();

		if (parent.getNodeType() == Node.DOCUMENT_NODE) {
			if (prev == null)
				return false;
			return true;
		}
		else if (parent.getNodeType() == Node.ELEMENT_NODE) {
			IDOMElement element = (IDOMElement) parent;
			// do not insert break after missing start tag
			if (prev == null && element.getStartStructuredDocumentRegion() == null)
				return false;

			// insert line break under non-HTML elements including JSP elements
			if (element.getPrefix() != null)
				return true;

			CMElementDeclaration decl = getElementDeclaration(element);
			if (decl != null) {
				if (decl.getContentType() == CMElementDeclaration.ELEMENT)
					return true;
				String tagName = element.getTagName();
				// special for direct children under BODY
				if (tagName != null && tagName.equalsIgnoreCase(BODY_NAME))
					return true;
			}
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			CMElementDeclaration decl = getElementDeclaration((Element) node);
			if (canInsertBreakBefore(decl))
				return true;
		}
		if (prev != null && prev.getNodeType() == Node.ELEMENT_NODE) {
			CMElementDeclaration decl = getElementDeclaration((Element) prev);
			if (canInsertBreakAfter(decl)) {
				// spcial for BR
				return canFormatChild(parent);
			}
		}
		return false;
	}

	/**
	 */
	public void format(Node node) {
		format(node, getFormatContraints());
	}

	/**
	 */
	public void format(Node node, IStructuredFormatContraints contraints) {
		if (node instanceof IDOMNode && contraints instanceof HTMLFormatContraints)
			format((IDOMNode) node, (HTMLFormatContraints) contraints);
	}

	public void format(IDOMNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;
		if (node.getParentNode() == null)
			return; // do not format removed node

		setWidth(contraints, node);

		if (canInsertBreakBefore(node))
			insertBreakBefore(node, contraints);

		formatNode(node, contraints);

		if (canInsertBreakAfter(node))
			insertBreakAfter(node, contraints);
	}

	/**
	 */
	protected void formatChildNodes(IDOMNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;
		if (!node.hasChildNodes())
			return;

		// concat adjacent texts
		node.normalize();

		// disable sibling indent during formatting all the children
		boolean indent = false;
		if (contraints != null) {
			indent = contraints.getFormatWithSiblingIndent();
			contraints.setFormatWithSiblingIndent(false);
		}

		boolean insertBreak = true;
		IDOMNode child = (IDOMNode) node.getFirstChild();
		while (child != null) {
			if (child.getParentNode() != node)
				break;
			IDOMNode next = (IDOMNode) child.getNextSibling();

			if (insertBreak && canInsertBreakBefore(child)) {
				insertBreakBefore(child, contraints);
			}

			IStructuredFormatter formatter = HTMLFormatterFactory.getInstance().createFormatter(child, getFormatPreferences());
			if (formatter != null) {
				if (formatter instanceof HTMLFormatter) {
					HTMLFormatter htmlFormatter = (HTMLFormatter) formatter;
					htmlFormatter.formatNode(child, contraints);
				}
				else {
					formatter.format(child);
				}
			}

			if (canInsertBreakAfter(child)) {
				insertBreakAfter(child, contraints);
				insertBreak = false; // not to insert twice
			}
			else {
				insertBreak = true;
			}

			child = next;
		}

		if (contraints != null)
			contraints.setFormatWithSiblingIndent(indent);
	}

	/**
	 */
	protected void formatNode(IDOMNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;

		if (node.hasChildNodes()) { // container
			formatChildNodes(node, contraints);
		}
		else { // leaf
			IStructuredDocumentRegion flatNode = node.getStartStructuredDocumentRegion();
			if (flatNode != null) {
				String source = flatNode.getText();
				if (source != null && source.length() > 0) {
					setWidth(contraints, source);
				}
			}
		}
	}

	/**
	 */
	protected String getBreakSpaces(Node node) {
		if (node == null)
			return null;
		StringBuffer buffer = new StringBuffer();

		String delim = ((IDOMNode) node).getModel().getStructuredDocument().getLineDelimiter();
		if (delim != null && delim.length() > 0)
			buffer.append(delim);

		String indent = getIndent();
		if (indent != null && indent.length() > 0) {
			for (Node parent = node.getParentNode(); parent != null; parent = parent.getParentNode()) {
				if (parent.getNodeType() != Node.ELEMENT_NODE)
					break;
				// ignore omitted tag
				if (((IDOMNode) parent).getStartStructuredDocumentRegion() == null)
					continue;

				IDOMElement element = (IDOMElement) parent;
				if (element.getPrefix() != null) {
					String localName = element.getLocalName();
					// special for html:html
					if (localName != null && !localName.equals(HTML_NAME)) {
						buffer.append(indent);
					}
					continue;
				}

				CMElementDeclaration decl = getElementDeclaration(element);
				if (decl != null && decl.supports(HTMLCMProperties.SHOULD_INDENT_CHILD_SOURCE)) {
					boolean shouldIndent = ((Boolean) decl.getProperty(HTMLCMProperties.SHOULD_INDENT_CHILD_SOURCE)).booleanValue();
					if (shouldIndent)
						buffer.append(indent);
				}

			}
		}

		return buffer.toString();
	}

	/**
	 */
	protected String getIndent() {
		return getFormatPreferences().getIndent();
	}

	/**
	 */
	protected int getLineWidth() {
		return getFormatPreferences().getLineWidth();
	}

	/**
	 */
	protected CMElementDeclaration getElementDeclaration(Element element) {
		if (element == null)
			return null;
		Document document = element.getOwnerDocument();
		if (document == null)
			return null;
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(document);
		if (modelQuery == null)
			return null;
		return modelQuery.getCMElementDeclaration(element);
	}

	/**
	 */
	protected void insertBreakAfter(IDOMNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;
		if (node.getNodeType() == Node.TEXT_NODE)
			return;
		Node parent = node.getParentNode();
		if (parent == null)
			return;
		Node next = node.getNextSibling();

		String spaces = null;
		if (next == null) { // last spaces
			// use parent indent for the end tag
			spaces = getBreakSpaces(parent);
		}
		else if (next.getNodeType() == Node.TEXT_NODE) {
			if (contraints != null && contraints.getFormatWithSiblingIndent()) {
				IDOMNode text = (IDOMNode) next;
				IStructuredFormatter formatter = HTMLFormatterFactory.getInstance().createFormatter(text, getFormatPreferences());
				if (formatter instanceof HTMLTextFormatter) {
					HTMLTextFormatter textFormatter = (HTMLTextFormatter) formatter;
					textFormatter.formatText(text, contraints, HTMLTextFormatter.FORMAT_HEAD);
				}
			}
			return;
		}
		else {
			spaces = getBreakSpaces(node);
		}
		if (spaces == null || spaces.length() == 0)
			return;

		replaceSource(node.getModel(), node.getEndOffset(), 0, spaces);
		setWidth(contraints, spaces);
	}

	/**
	 */
	protected void insertBreakBefore(IDOMNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;
		if (node.getNodeType() == Node.TEXT_NODE)
			return;
		Node parent = node.getParentNode();
		if (parent == null)
			return;
		Node prev = node.getPreviousSibling();

		String spaces = null;
		if (prev != null && prev.getNodeType() == Node.TEXT_NODE) {
			if (contraints != null && contraints.getFormatWithSiblingIndent()) {
				IDOMNode text = (IDOMNode) prev;
				IStructuredFormatter formatter = HTMLFormatterFactory.getInstance().createFormatter(text, getFormatPreferences());
				if (formatter instanceof HTMLTextFormatter) {
					HTMLTextFormatter textFormatter = (HTMLTextFormatter) formatter;
					textFormatter.formatText(text, contraints, HTMLTextFormatter.FORMAT_TAIL);
				}
			}
			return;
		}
		else {
			spaces = getBreakSpaces(node);
		}
		if (spaces == null || spaces.length() == 0)
			return;

		replaceSource(node.getModel(), node.getStartOffset(), 0, spaces);
		setWidth(contraints, spaces);
	}

	/**
	 */
	protected boolean isWidthAvailable(HTMLFormatContraints contraints, int width) {
		if (contraints == null)
			return true;
		if (!splitLines() || getLineWidth() < 0)
			return true;
		return (contraints.getAvailableLineWidth() >= width);
	}

	/**
	 */
	protected boolean keepBlankLines(HTMLFormatContraints contraints) {
		if (contraints == null)
			return true;
		return (!contraints.getClearAllBlankLines());
	}

	/**
	 */
	protected void replaceSource(IStructuredDocumentRegion flatNode, int offset, int length, String source) {
		if (flatNode == null)
			return;
		IStructuredDocument structuredDocument = flatNode.getParentDocument();
		if (structuredDocument == null)
			return;
		if (source == null)
			source = new String();
		int startOffset = flatNode.getStartOffset();
		if (structuredDocument.containsReadOnly(startOffset + offset, length))
			return;
		// We use 'structuredDocument' as the requester object just so this and the other
		// format-related 'repalceText' (in replaceSource) can use the same requester.
		// Otherwise, if requester is not identical, 
		// the undo group gets "broken" into multiple pieces based
		// on the requesters being different. Technically, any unique, common
		// requester object would work.
		structuredDocument.replaceText(structuredDocument, startOffset + offset, length, source);
	}

	/**
	 */
	protected void replaceSource(IDOMModel model, int offset, int length, String source) {
		if (model == null)
			return;
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		if (structuredDocument == null)
			return;
		if (source == null)
			source = new String();
		if (structuredDocument.containsReadOnly(offset, length))
			return;
		// We use 'structuredDocument' as the requester object just so this and the other
		// format-related 'repalceText' (in replaceSource) can use the same requester.
		// Otherwise, if requester is not identical, 
		// the undo group gets "broken" into multiple pieces based
		// on the requesters being different. Technically, any unique, common
		// requester object would work. 
		structuredDocument.replaceText(structuredDocument, offset, length, source);
	}

	/**
	 */
	protected void setWidth(HTMLFormatContraints contraints, String source) {
		if (contraints == null)
			return;
		if (source == null)
			return;
		int length = source.length();
		if (length == 0)
			return;

		if (!splitLines())
			return;
		int lineWidth = getLineWidth();
		if (lineWidth < 0)
			return;

		int offset = source.lastIndexOf('\n');
		int offset2 = source.lastIndexOf('\r');
		if (offset2 > offset)
			offset = offset2;
		if (offset >= 0)
			offset++;

		int availableWidth = 0;
		if (offset >= 0) {
			availableWidth = lineWidth - (length - offset);
		}
		else {
			availableWidth = contraints.getAvailableLineWidth() - length;
		}
		if (availableWidth < 0)
			availableWidth = 0;
		contraints.setAvailableLineWidth(availableWidth);
	}

	/**
	 */
	protected void setWidth(HTMLFormatContraints contraints, Node node) {
		if (contraints == null)
			return;
		if (node == null)
			return;
		IStructuredDocument structuredDocument = ((IDOMNode) node).getStructuredDocument();
		if (structuredDocument == null)
			return; // error

		if (!splitLines())
			return;
		int lineWidth = getLineWidth();
		if (lineWidth < 0)
			return;

		int offset = ((IDOMNode) node).getStartOffset();
		int line = structuredDocument.getLineOfOffset(offset);
		int lineOffset = 0;
		try {
			lineOffset = structuredDocument.getLineOffset(line);
		}
		catch (BadLocationException ex) {
			return; // error
		}
		if (lineOffset > offset)
			return; // error

		int availableWidth = lineWidth - (offset - lineOffset);
		if (availableWidth < 0)
			availableWidth = 0;

		contraints.setAvailableLineWidth(availableWidth);
	}

	/**
	 */
	protected boolean splitLines() {
		return true;//getFormatPreferences().getSplitLines();
	}

	protected IStructuredFormatPreferences fFormatPreferences = null;
	protected HTMLFormatContraints fFormatContraints = null;
	protected IProgressMonitor fProgressMonitor = null;

	//public void format(XMLNode node, FormatContraints formatContraints) {
	//	if (formatContraints.getFormatWithSiblingIndent())
	//		formatContraints.setCurrentIndent(getSiblingIndent(node));
	//
	//	formatNode(node, formatContraints);
	//}

	public void setFormatPreferences(IStructuredFormatPreferences formatPreferences) {
		fFormatPreferences = formatPreferences;
	}

	public IStructuredFormatPreferences getFormatPreferences() {
		if (fFormatPreferences == null) {
			fFormatPreferences = new StructuredFormatPreferencesXML();

			Preferences preferences = HTMLCorePlugin.getDefault().getPluginPreferences();
			if (preferences != null) {
				fFormatPreferences.setLineWidth(preferences.getInt(HTMLCorePreferenceNames.LINE_WIDTH));
				((StructuredFormatPreferencesXML) fFormatPreferences).setSplitMultiAttrs(preferences.getBoolean(HTMLCorePreferenceNames.SPLIT_MULTI_ATTRS));
				fFormatPreferences.setClearAllBlankLines(preferences.getBoolean(HTMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES));

				char indentChar = ' ';
				String indentCharPref = preferences.getString(HTMLCorePreferenceNames.INDENTATION_CHAR);
				if (HTMLCorePreferenceNames.TAB.equals(indentCharPref)) {
					indentChar = '\t';
				}
				int indentationWidth = preferences.getInt(HTMLCorePreferenceNames.INDENTATION_SIZE);

				StringBuffer indent = new StringBuffer();
				for (int i = 0; i < indentationWidth; i++) {
					indent.append(indentChar);
				}
				fFormatPreferences.setIndent(indent.toString());
			}
		}

		return fFormatPreferences;
	}

	public IStructuredFormatContraints getFormatContraints() {
		if (fFormatContraints == null) {
			fFormatContraints = new HTMLFormatContraintsImpl();

			fFormatContraints.setAvailableLineWidth(getFormatPreferences().getLineWidth());
			fFormatContraints.setClearAllBlankLines(getFormatPreferences().getClearAllBlankLines());
		}

		return fFormatContraints;
	}

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		fProgressMonitor = progressMonitor;
	}
}