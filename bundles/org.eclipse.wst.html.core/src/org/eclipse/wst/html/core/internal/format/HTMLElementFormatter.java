/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.format;

import java.util.Iterator;

import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatter;
import org.eclipse.wst.css.core.internal.formatter.CSSSourceFormatterFactory;
import org.eclipse.wst.css.core.internal.provisional.adapters.IStyleDeclarationAdapter;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.html.core.internal.provisional.HTMLFormatContraints;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.StructuredFormatPreferencesXML;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

// nakamori_TODO: check and remove CSS formatting

public class HTMLElementFormatter extends HTMLFormatter {

	/**
	 */
	protected HTMLElementFormatter() {
		super();
	}

	/**
	 */
	private void compressTailingSpaces(IStructuredDocumentRegion flatNode, ITextRegion region) {
		int offset = region.getTextEnd();
		int count = region.getEnd() - offset;
		if (count == 1) {
			String source = flatNode.getFullText(region);
			int start = region.getStart();
			if (source != null && source.charAt(offset - start) == ' ') {
				// nothing to do
				return;
			}
		}
		replaceSource(flatNode, offset, count, " ");//$NON-NLS-1$
	}

	/**
	 */
	private void formatEndTag(IDOMElement element, HTMLFormatContraints contraints) {
		Node lastChild = element.getLastChild();

		if (lastChild != null && lastChild instanceof IDOMElement && lastChild.getNodeName().equals("jsp:scriptlet")) { //$NON-NLS-1$
			insertBreakAfter((IDOMElement) lastChild, contraints);
			return;
		}


		IStructuredDocumentRegion endStructuredDocumentRegion = element.getEndStructuredDocumentRegion();
		if (endStructuredDocumentRegion == null)
			return;

		if (element.isJSPTag() || element.isCommentTag()) {
			String endTag = endStructuredDocumentRegion.getText();
			if (endTag != null && endTag.length() > 0) {
				setWidth(contraints, endTag);
			}
			return;
		}

		ITextRegion prevRegion = null;
		ITextRegionList regions = endStructuredDocumentRegion.getRegions();
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			if (region == null)
				continue;
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_TAG_NAME || isNestedTag(regionType)) {
				if (prevRegion != null && prevRegion.getType() == DOMRegionContext.XML_END_TAG_OPEN) {
					removeTailingSpaces(endStructuredDocumentRegion, prevRegion);
				}
			}
			else if (regionType == DOMRegionContext.XML_TAG_CLOSE) {
				if (prevRegion != null && (prevRegion.getType() == DOMRegionContext.XML_TAG_NAME || isNestedRootTag(prevRegion.getType()))) {
					removeTailingSpaces(endStructuredDocumentRegion, prevRegion);
				}
			}
			prevRegion = region;
		}
		if (prevRegion != null && (prevRegion.getType() == DOMRegionContext.XML_TAG_NAME || isNestedRootTag(prevRegion.getType()))) {
			removeTailingSpaces(endStructuredDocumentRegion, prevRegion);
		}

		// BUG123890 (end tag length was already prefactored into
		// formatStartTag so no need to do it here)
		// String newEndTag = endStructuredDocumentRegion.getText();
		// if (newEndTag != null && newEndTag.length() > 0) {
		// setWidth(contraints, newEndTag);
		// }
	}

	/**
	 */
	protected void formatNode(IDOMNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;
		IDOMElement element = (IDOMElement) node;

		formatStartTag(element, contraints);

		formatChildNodes(element, contraints);

		formatEndTag(element, contraints);
	}

	/**
	 */
	private void formatStartTag(IDOMElement element, HTMLFormatContraints contraints) {

		if (element.getNodeName().equals("jsp:scriptlet")) { //$NON-NLS-1$
			insertBreakBefore(element, contraints);
			return;
		}

		IStructuredDocumentRegion startStructuredDocumentRegion = element.getStartStructuredDocumentRegion();
		if (startStructuredDocumentRegion == null)
			return;

		// We should format attributes in JSPTag?
		// if (element.isJSPTag() || element.isCommentTag()) {
		if (element.isCommentTag()) {
			String startTag = startStructuredDocumentRegion.getText();
			if (startTag != null && startTag.length() > 0) {
				setWidth(contraints, startTag);
			}
			return;
		}

		// first process style attribute
		if (element.isGlobalTag()) {
			Attr attr = element.getAttributeNode("style");//$NON-NLS-1$
			if (attr != null)
				formatStyleAttr(attr);
		}
		boolean insertBreak = false;
		insertBreak = ((StructuredFormatPreferencesXML) getFormatPreferences()).getSplitMultiAttrs();
		boolean alignEndBracket = ((StructuredFormatPreferencesXML) getFormatPreferences()).isAlignEndBracket();
		boolean attributesSplitted = false;

		if (insertBreak) {
			NamedNodeMap attributes = element.getAttributes();
			if (attributes == null || attributes.getLength() < 2)
				insertBreak = false;
		}
		String breakSpaces = getBreakSpaces(element);
		String originalBreakSpaces = breakSpaces;
		String indent = getIndent();
		if (indent != null && indent.length() > 0) {
			breakSpaces += indent;
		}
		ITextRegion lastBreakRegion = null;

		ITextRegion prevRegion = null;
		ITextRegionList regions = startStructuredDocumentRegion.getRegions();
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			if (region == null)
				continue;

			ITextRegion breakRegion = null;

			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_TAG_NAME || isNestedTag(regionType)) {
				if (prevRegion != null && prevRegion.getType() == DOMRegionContext.XML_TAG_OPEN) {
					removeTailingSpaces(startStructuredDocumentRegion, prevRegion);
				}
				breakRegion = region;
			}
			else if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				if (prevRegion != null && (prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME || prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)) {
					// attribute name without value
					breakRegion = prevRegion;
				}
			}
			else if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				if (prevRegion != null && prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
					removeTailingSpaces(startStructuredDocumentRegion, prevRegion);
				}
			}
			else if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				if (prevRegion != null && prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
					removeTailingSpaces(startStructuredDocumentRegion, prevRegion);
				}
				breakRegion = region;
			}
			else if (regionType == DOMRegionContext.XML_TAG_CLOSE || regionType == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
				if (prevRegion != null && (prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME || prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)) {
					// attribute name without value
					breakRegion = prevRegion;
				}
			}

			if (breakRegion != null) {
				int end = breakRegion.getTextEnd();
				if (lastBreakRegion != null) {
					int offset = lastBreakRegion.getEnd();
					int count = end - offset;
					if (insertBreak || !isWidthAvailable(contraints, count + 1)) {
						replaceTailingSpaces(startStructuredDocumentRegion, lastBreakRegion, breakSpaces);
						setWidth(contraints, breakSpaces);
						attributesSplitted = true;
					}
					else {
						compressTailingSpaces(startStructuredDocumentRegion, lastBreakRegion);
						addWidth(contraints, 1);
					}
					addWidth(contraints, count);
				}
				else {
					addWidth(contraints, end);
				}
				lastBreakRegion = breakRegion;
			}

			prevRegion = region;
		}
		if (prevRegion != null && (prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME || prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)) {
			// attribute name without value
			int end = prevRegion.getTextEnd();
			if (lastBreakRegion != null) {
				int offset = lastBreakRegion.getEnd();
				int count = end - offset;
				if (insertBreak || !isWidthAvailable(contraints, count + 1)) {
					replaceTailingSpaces(startStructuredDocumentRegion, lastBreakRegion, breakSpaces);
					setWidth(contraints, breakSpaces);
					attributesSplitted = true;
				}
				else {
					compressTailingSpaces(startStructuredDocumentRegion, lastBreakRegion);
					addWidth(contraints, 1);
				}
				addWidth(contraints, count);
			}
			else {
				addWidth(contraints, end);
			}
			lastBreakRegion = prevRegion;
		}

		if (lastBreakRegion != null) {
			int offset = lastBreakRegion.getTextEnd();
			int count = startStructuredDocumentRegion.getLength() - offset;
			if (prevRegion != null && prevRegion.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
				compressTailingSpaces(startStructuredDocumentRegion, lastBreakRegion);
				count++;
			}
			else {
				removeTailingSpaces(startStructuredDocumentRegion, lastBreakRegion);
				// BUG123890 (pre-factor in end tag)
				count += element.getTagName().length() + 3;
			}
			addWidth(contraints, count);
		}
		else {
			addWidth(contraints, startStructuredDocumentRegion.getLength());
		}
		// BUG113584 - align last bracket
		if (alignEndBracket && attributesSplitted) {
			removeTailingSpaces(startStructuredDocumentRegion, lastBreakRegion);
			replaceTailingSpaces(startStructuredDocumentRegion, lastBreakRegion, originalBreakSpaces);
			contraints.setAvailableLineWidth(getLineWidth() - originalBreakSpaces.length() - 1);
		}
	}

	/**
	 * ISSUE: this is a bit of hidden JSP knowledge that was implemented this
	 * way for expedency. Should be evolved in future to depend on
	 * "nestedContext".
	 */
	private boolean isNestedTag(String regionType) {
		final String JSP_ROOT_TAG_NAME = "JSP_ROOT_TAG_NAME"; //$NON-NLS-1$
		final String JSP_DIRECTIVE_NAME = "JSP_DIRECTIVE_NAME"; //$NON-NLS-1$
		boolean result = regionType.equals(JSP_ROOT_TAG_NAME) || regionType.equals(JSP_DIRECTIVE_NAME);
		return result;
	}

	/**
	 * ISSUE: this is a bit of hidden JSP knowledge that was implemented this
	 * way for expedency. Should be evolved in future to depend on
	 * "nestedContext".
	 */
	private boolean isNestedRootTag(String regionType) {
		final String JSP_ROOT_TAG_NAME = "JSP_ROOT_TAG_NAME"; //$NON-NLS-1$
		boolean result = regionType.equals(JSP_ROOT_TAG_NAME);
		return result;
	}


	/**
	 */
	private void formatStyleAttr(Attr attr) {
		if (attr == null)
			return;
		// if someone's made it a container somehow, CSS can't format it
		if (((IDOMNode) attr).getValueRegion() instanceof ITextRegionContainer)
			return;
		String value = getCSSValue(attr);
		if (value == null)
			return;
		String oldValue = ((IDOMNode) attr).getValueSource();
		if (oldValue != null && value.equals(oldValue))
			return;
		attr.setValue(value);
	}

	/**
	 */
	private ICSSModel getCSSModel(Attr attr) {
		if (attr == null)
			return null;
		INodeNotifier notifier = (INodeNotifier) attr.getOwnerElement();
		if (notifier == null)
			return null;
		INodeAdapter adapter = notifier.getAdapterFor(IStyleDeclarationAdapter.class);
		if (adapter == null)
			return null;
		if (!(adapter instanceof IStyleDeclarationAdapter))
			return null;
		IStyleDeclarationAdapter styleAdapter = (IStyleDeclarationAdapter) adapter;
		return styleAdapter.getModel();
	}

	/**
	 */
	private String getCSSValue(Attr attr) {
		ICSSModel model = getCSSModel(attr);
		if (model == null)
			return null;
		ICSSNode document = model.getDocument();
		if (document == null)
			return null;
		INodeNotifier notifier = (INodeNotifier) document;
		CSSSourceFormatter formatter = (CSSSourceFormatter) notifier.getAdapterFor(CSSSourceFormatter.class);
		// try another way to get formatter
		if (formatter == null)
			formatter = CSSSourceFormatterFactory.getInstance().getSourceFormatter(notifier);
		if (formatter == null)
			return null;
		StringBuffer buffer = formatter.format(document);
		if (buffer == null)
			return null;
		return buffer.toString();
	}

	/**
	 */
	private void removeTailingSpaces(IStructuredDocumentRegion flatNode, ITextRegion region) {
		int offset = region.getTextEnd();
		int count = region.getEnd() - offset;
		if (count <= 0)
			return;
		replaceSource(flatNode, offset, count, null);
	}

	/**
	 */
	private void replaceTailingSpaces(IStructuredDocumentRegion flatNode, ITextRegion region, String spaces) {
		int offset = region.getTextEnd();
		int count = region.getEnd() - offset;
		if (count == spaces.length()) {
			String source = flatNode.getFullText(region);
			if (source != null && source.endsWith(spaces)) {
				// nothing to do
				return;
			}
		}
		replaceSource(flatNode, offset, count, spaces);
	}
}