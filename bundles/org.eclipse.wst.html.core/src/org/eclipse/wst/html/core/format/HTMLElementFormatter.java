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
package org.eclipse.wst.html.core.format;

import java.util.Iterator;

import org.eclipse.wst.css.core.adapters.IStyleDeclarationAdapter;
import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.format.CSSSourceFormatter;
import org.eclipse.wst.html.core.HTMLFormatContraints;
import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.format.IStructuredFormatPreferencesXML;
import org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

// nakamori_TODO: check and remove CSS formatting

public class HTMLElementFormatter extends HTMLFormatter implements XMLRegionContext, XMLJSPRegionContexts {

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
	private void formatEndTag(XMLElement element, HTMLFormatContraints contraints) {
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
			if (regionType == XML_TAG_NAME || regionType == JSP_ROOT_TAG_NAME || regionType == JSP_DIRECTIVE_NAME) {
				if (prevRegion != null && prevRegion.getType() == XML_END_TAG_OPEN) {
					removeTailingSpaces(endStructuredDocumentRegion, prevRegion);
				}
			}
			else if (regionType == XML_TAG_CLOSE) {
				if (prevRegion != null && (prevRegion.getType() == XML_TAG_NAME || prevRegion.getType() == JSP_ROOT_TAG_NAME)) {
					removeTailingSpaces(endStructuredDocumentRegion, prevRegion);
				}
			}
			prevRegion = region;
		}
		if (prevRegion != null && (prevRegion.getType() == XML_TAG_NAME || prevRegion.getType() == JSP_ROOT_TAG_NAME)) {
			removeTailingSpaces(endStructuredDocumentRegion, prevRegion);
		}

		String newEndTag = endStructuredDocumentRegion.getText();
		if (newEndTag != null && newEndTag.length() > 0) {
			setWidth(contraints, newEndTag);
		}
	}

	/**
	 */
	protected void formatNode(XMLNode node, HTMLFormatContraints contraints) {
		if (node == null)
			return;
		XMLElement element = (XMLElement) node;

		formatStartTag(element, contraints);

		formatChildNodes(element, contraints);

		formatEndTag(element, contraints);
	}

	/**
	 */
	private void formatStartTag(XMLElement element, HTMLFormatContraints contraints) {
		IStructuredDocumentRegion startStructuredDocumentRegion = element.getStartStructuredDocumentRegion();
		if (startStructuredDocumentRegion == null)
			return;

		// We should format attributes in JSPTag?
		//if (element.isJSPTag() || element.isCommentTag()) {
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
		insertBreak = ((IStructuredFormatPreferencesXML) getFormatPreferences()).getSplitMultiAttrs();

		if (insertBreak) {
			NamedNodeMap attributes = element.getAttributes();
			if (attributes == null || attributes.getLength() < 2)
				insertBreak = false;
		}
		String breakSpaces = getBreakSpaces(element);
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
			if (regionType == XML_TAG_NAME || regionType == JSP_ROOT_TAG_NAME || regionType == JSP_DIRECTIVE_NAME) {
				if (prevRegion != null && prevRegion.getType() == XML_TAG_OPEN) {
					removeTailingSpaces(startStructuredDocumentRegion, prevRegion);
				}
				breakRegion = region;
			}
			else if (regionType == XML_TAG_ATTRIBUTE_NAME) {
				if (prevRegion != null && (prevRegion.getType() == XML_TAG_ATTRIBUTE_NAME || prevRegion.getType() == XML_TAG_ATTRIBUTE_EQUALS)) {
					// attribute name without value
					breakRegion = prevRegion;
				}
			}
			else if (regionType == XML_TAG_ATTRIBUTE_EQUALS) {
				if (prevRegion != null && prevRegion.getType() == XML_TAG_ATTRIBUTE_NAME) {
					removeTailingSpaces(startStructuredDocumentRegion, prevRegion);
				}
			}
			else if (regionType == XML_TAG_ATTRIBUTE_VALUE) {
				if (prevRegion != null && prevRegion.getType() == XML_TAG_ATTRIBUTE_EQUALS) {
					removeTailingSpaces(startStructuredDocumentRegion, prevRegion);
				}
				breakRegion = region;
			}
			else if (regionType == XML_TAG_CLOSE || regionType == XML_EMPTY_TAG_CLOSE) {
				if (prevRegion != null && (prevRegion.getType() == XML_TAG_ATTRIBUTE_NAME || prevRegion.getType() == XML_TAG_ATTRIBUTE_EQUALS)) {
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
		if (prevRegion != null && (prevRegion.getType() == XML_TAG_ATTRIBUTE_NAME || prevRegion.getType() == XML_TAG_ATTRIBUTE_EQUALS)) {
			// attribute name without value
			int end = prevRegion.getTextEnd();
			if (lastBreakRegion != null) {
				int offset = lastBreakRegion.getEnd();
				int count = end - offset;
				if (insertBreak || !isWidthAvailable(contraints, count + 1)) {
					replaceTailingSpaces(startStructuredDocumentRegion, lastBreakRegion, breakSpaces);
					setWidth(contraints, breakSpaces);
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
			if (prevRegion != null && prevRegion.getType() == XML_EMPTY_TAG_CLOSE) {
				compressTailingSpaces(startStructuredDocumentRegion, lastBreakRegion);
				count++;
			}
			else
				removeTailingSpaces(startStructuredDocumentRegion, lastBreakRegion);
			addWidth(contraints, count);
		}
		else {
			addWidth(contraints, startStructuredDocumentRegion.getLength());
		}
	}

	/**
	 */
	private void formatStyleAttr(Attr attr) {
		if (attr == null)
			return;
		String value = getCSSValue(attr);
		if (value == null)
			return;
		String oldValue = ((XMLNode) attr).getValueSource();
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
		INodeAdapter adapter = notifier.getAdapterFor(CSSSourceFormatter.class);
		if (adapter == null)
			return null;
		CSSSourceFormatter formatter = (CSSSourceFormatter) adapter;
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