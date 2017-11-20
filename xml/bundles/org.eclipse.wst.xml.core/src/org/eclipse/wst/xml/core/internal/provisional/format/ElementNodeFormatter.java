/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Møller - xml:space='preserve' support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.provisional.format;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class ElementNodeFormatter extends DocumentNodeFormatter {
	static private final char DOUBLE_QUOTE = '"';//$NON-NLS-1$
	static private final String DOUBLE_QUOTES = "\"\"";//$NON-NLS-1$
	static private final char EQUAL_CHAR = '='; // equal sign$NON-NLS-1$
	static private final String PRESERVE = "preserve";//$NON-NLS-1$
	static private final String PRESERVE_QUOTED = "\"preserve\"";//$NON-NLS-1$
	static private final char SINGLE_QUOTE = '\'';//$NON-NLS-1$
	static private final String XML_SPACE = "xml:space";//$NON-NLS-1$
	static private final char SPACE_CHAR = ' '; //$NON-NLS-1$
	static private final String XSL_NAMESPACE = "http://www.w3.org/1999/XSL/Transform"; //$NON-NLS-1$
	static private final String XSL_ATTRIBUTE = "attribute"; //$NON-NLS-1$
	static private final String XSL_TEXT = "text"; //$NON-NLS-1$

	protected void formatEndTag(IDOMNode node, IStructuredFormatContraints formatContraints) {
		if (!isEndTagMissing(node)) {
			// end tag exists

			IStructuredDocument structuredDocument = node.getModel().getStructuredDocument();
			String lineDelimiter = structuredDocument.getLineDelimiter();
			String nodeIndentation = getNodeIndent(node);
			IDOMNode lastChild = (IDOMNode) node.getLastChild();
			if (lastChild != null && lastChild.getNodeType() != Node.TEXT_NODE) {
				if (isEndTagMissing(lastChild)) {
					// find deepest child
					IDOMNode deepestChild = (IDOMNode) lastChild.getLastChild();
					while (deepestChild != null && deepestChild.getLastChild() != null && isEndTagMissing(deepestChild)) {
						lastChild = deepestChild;
						deepestChild = (IDOMNode) deepestChild.getLastChild();
					}

					if (deepestChild != null) {
						if (deepestChild.getNodeType() == Node.TEXT_NODE) {
							// Special indentation handling if lastChild's end
							// tag is missing and deepestChild is a text node.
							String nodeText = deepestChild.getNodeValue();

							if (!nodeText.endsWith(lineDelimiter + nodeIndentation)) {
								nodeText = StringUtils.appendIfNotEndWith(nodeText, lineDelimiter);
								nodeText = StringUtils.appendIfNotEndWith(nodeText, nodeIndentation);
							}

							replaceNodeValue(deepestChild, nodeText);
						}
						else
							insertAfterNode(lastChild, lineDelimiter + nodeIndentation);
					}
				}
				else
					// indent end tag
					insertAfterNode(lastChild, lineDelimiter + nodeIndentation);
			}
			else if (lastChild == null && firstStructuredDocumentRegionContainsLineDelimiters(node)) {
				// BUG174243 do not indent end tag if node has empty content
				// (otherwise new text node would be introduced)
				ModelQueryAdapter adapter = (ModelQueryAdapter) ((IDOMDocument) node.getOwnerDocument()).getAdapterFor(ModelQueryAdapter.class);
				CMElementDeclaration elementDeclaration = (CMElementDeclaration) adapter.getModelQuery().getCMNode(node);
				if ((elementDeclaration == null) || (elementDeclaration.getContentType() != CMElementDeclaration.EMPTY)) {
					// indent end tag
					replace(structuredDocument, node.getFirstStructuredDocumentRegion().getEndOffset(), 0, lineDelimiter + nodeIndentation);
				}
			}

			// format end tag name
			IStructuredDocumentRegion endTagStructuredDocumentRegion = node.getLastStructuredDocumentRegion();
			if (endTagStructuredDocumentRegion.getRegions().size() >= 3) {
				ITextRegion endTagNameRegion = endTagStructuredDocumentRegion.getRegions().get(1);
				removeRegionSpaces(node, endTagStructuredDocumentRegion, endTagNameRegion);
			}
		}
	}

	protected void formatNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		if (node != null) {
			// format indentation before node
			formatIndentationBeforeNode(node, formatContraints);

			// format start tag
			IDOMNode newNode = node;
			int startTagStartOffset = node.getStartOffset();
			IDOMModel structuredModel = node.getModel();

			boolean currentlyInXmlSpacePreserve = formatContraints.getInPreserveSpaceElement();
			formatStartTag(node, formatContraints);
			// save new node
			newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset);

			IStructuredDocumentRegion flatNode = newNode.getFirstStructuredDocumentRegion();
			if (flatNode != null) {
				ITextRegionList regions = flatNode.getRegions();
				ITextRegion lastRegion = regions.get(regions.size() - 1);
				// format children and end tag if not empty start tag
				if (lastRegion.getType() != DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
					// format children
					formatChildren(newNode, formatContraints);

					// save new node
					newNode = (IDOMNode) structuredModel.getIndexedRegion(startTagStartOffset);

					// format end tag
					formatEndTag(newNode, formatContraints);
				}
			}

			formatContraints.setInPreserveSpaceElement(currentlyInXmlSpacePreserve);
			// only indent if not at last node
			if (newNode != null && newNode.getNextSibling() != null)
				// format indentation after node
				formatIndentationAfterNode(newNode, formatContraints);
		}
	}

	/**
	 * This method formats the start tag name, and formats the attributes if
	 * available.
	 */
	protected void formatStartTag(IDOMNode node, IStructuredFormatContraints formatContraints) {
		StructuredFormatPreferencesXML preferences = (StructuredFormatPreferencesXML) getFormatPreferences();
		String singleIndent = preferences.getIndent();
		String lineIndent = formatContraints.getCurrentIndent();
		String attrIndent = lineIndent + singleIndent;
		boolean splitMultiAttrs = preferences.getSplitMultiAttrs();
		boolean alignEndBracket = preferences.isAlignEndBracket();
		boolean sawXmlSpace = false;

		IStructuredDocumentRegion flatNode = node.getFirstStructuredDocumentRegion();
		NamedNodeMap attributes = node.getAttributes();

		// Note: attributes should not be null even if the node has no
		// attributes. However, attributes.getLength() will be 0. But, check
		// for null just in case.
		if (attributes != null) {
			// compute current available line width
			int currentAvailableLineWidth = 0;
			try {
				// 1 is for "<"
				int nodeNameOffset = node.getStartOffset() + 1 + node.getNodeName().length();
				int lineOffset = node.getStructuredDocument().getLineInformationOfOffset(nodeNameOffset).getOffset();
				String text = node.getStructuredDocument().get(lineOffset, nodeNameOffset - lineOffset);
				int usedWidth = getIndentationLength(text);
				currentAvailableLineWidth = preferences.getLineWidth() - usedWidth;
			}
			catch (BadLocationException e) {
				// log for now, unless we find reason not to
				Logger.log(Logger.INFO, e.getMessage());
			}

			StringBuffer stringBuffer = new StringBuffer();
			String lineDelimiter = node.getModel().getStructuredDocument().getLineDelimiter();
			int attrLength = attributes.getLength();
			int lastUndefinedRegionOffset = 0;
			boolean startTagSpansOver1Line = false;

			for (int i = 0; i < attrLength; i++) {
				AttrImpl attr = (AttrImpl) attributes.item(i);
				ITextRegion nameRegion = attr.getNameRegion();
				ITextRegion equalRegion = attr.getEqualRegion();
				ITextRegion valueRegion = attr.getValueRegion();

				// append undefined regions
				String undefinedRegion = getUndefinedRegions(node, lastUndefinedRegionOffset, attr.getStartOffset() - lastUndefinedRegionOffset);
				stringBuffer.append(undefinedRegion);
				lastUndefinedRegionOffset = attr.getStartOffset();

				// check for xml:space attribute
				if (flatNode.getText(nameRegion).compareTo(XML_SPACE) == 0) {
					if (valueRegion == null) {
						// [111674] If nothing has been written yet, treat as
						// preserve, but only as hint
						formatContraints.setInPreserveSpaceElement(true);
						// Note we don't set 'sawXmlSpace', so that default or
						// fixed DTD/XSD values may override.
					}
					else {
						ISourceGenerator generator = node.getModel().getGenerator();
						String newAttrValue = generator.generateAttrValue(attr);

						// There is a problem in
						// StructuredDocumentRegionUtil.getAttrValue(ITextRegion)
						// when the region is instanceof ContextRegion.
						// Workaround for now.
						if (flatNode.getText(valueRegion).length() == 1) {
							char firstChar = flatNode.getText(valueRegion).charAt(0);
							if ((firstChar == DOUBLE_QUOTE) || (firstChar == SINGLE_QUOTE))
								newAttrValue = DOUBLE_QUOTES;
						}

						if (newAttrValue.compareTo(PRESERVE_QUOTED) == 0)
							formatContraints.setInPreserveSpaceElement(true);
						else
							formatContraints.setInPreserveSpaceElement(false);
						sawXmlSpace = true;
					}
				}

				if (splitMultiAttrs && attrLength > 1) {
					stringBuffer.append(lineDelimiter + attrIndent);
					stringBuffer.append(flatNode.getText(nameRegion));
					startTagSpansOver1Line = true;
					if (valueRegion != null) {
						// append undefined regions
						undefinedRegion = getUndefinedRegions(node, lastUndefinedRegionOffset, flatNode.getStartOffset(equalRegion) - lastUndefinedRegionOffset);
						stringBuffer.append(undefinedRegion);
						lastUndefinedRegionOffset = flatNode.getStartOffset(equalRegion);

						stringBuffer.append(EQUAL_CHAR);

						// append undefined regions
						undefinedRegion = getUndefinedRegions(node, lastUndefinedRegionOffset, flatNode.getStartOffset(valueRegion) - lastUndefinedRegionOffset);
						stringBuffer.append(undefinedRegion);
						lastUndefinedRegionOffset = flatNode.getStartOffset(valueRegion);

						// Note: trim() should not be needed for
						// valueRegion.getText(). Just a workaround for a
						// problem found in valueRegion for now.
						stringBuffer.append(flatNode.getText(valueRegion).trim());
					}
				}
				else {
					if (valueRegion != null) {
						int textLength = 1 + flatNode.getText(nameRegion).length() + 1 + flatNode.getText(valueRegion).length();
						if (i == attrLength - 1) {
							if (flatNode != null) {
								ITextRegionList regions = flatNode.getRegions();
								ITextRegion lastRegion = regions.get(regions.size() - 1);
								if (lastRegion.getType() != DOMRegionContext.XML_EMPTY_TAG_CLOSE)
									// 3 is for " />"
									textLength += 3;
								else
									// 1 is for ">"
									textLength++;
							}
						}

						if (currentAvailableLineWidth >= textLength) {
							stringBuffer.append(SPACE_CHAR);
							currentAvailableLineWidth--;
						}
						else {
							stringBuffer.append(lineDelimiter + attrIndent);
							startTagSpansOver1Line = true;
							currentAvailableLineWidth = preferences.getLineWidth() - attrIndent.length();
						}

						stringBuffer.append(flatNode.getText(nameRegion));

						// append undefined regions
						undefinedRegion = getUndefinedRegions(node, lastUndefinedRegionOffset, flatNode.getStartOffset(equalRegion) - lastUndefinedRegionOffset);
						stringBuffer.append(undefinedRegion);
						lastUndefinedRegionOffset = flatNode.getStartOffset(equalRegion);

						stringBuffer.append(EQUAL_CHAR);

						// append undefined regions
						undefinedRegion = getUndefinedRegions(node, lastUndefinedRegionOffset, flatNode.getStartOffset(valueRegion) - lastUndefinedRegionOffset);
						stringBuffer.append(undefinedRegion);
						lastUndefinedRegionOffset = flatNode.getStartOffset(valueRegion);

						// Note: trim() should not be needed for
						// valueRegion.getText(). Just a workaround for a
						// problem found in valueRegion for now.
						stringBuffer.append(flatNode.getText(valueRegion).trim());

						currentAvailableLineWidth -= flatNode.getText(nameRegion).length();
						currentAvailableLineWidth--;
						currentAvailableLineWidth -= flatNode.getText(valueRegion).trim().length();
					}
					else {
						if (currentAvailableLineWidth >= 1 + flatNode.getText(nameRegion).length()) {
							stringBuffer.append(SPACE_CHAR);
							currentAvailableLineWidth--;
						}
						else {
							stringBuffer.append(lineDelimiter + attrIndent);
							startTagSpansOver1Line = true;
							currentAvailableLineWidth = preferences.getLineWidth() - attrIndent.length();
						}

						stringBuffer.append(flatNode.getText(nameRegion));

						currentAvailableLineWidth -= flatNode.getText(nameRegion).length();
					}
				}
			}

			// append undefined regions
			String undefinedRegion = getUndefinedRegions(node, lastUndefinedRegionOffset, node.getEndOffset() - lastUndefinedRegionOffset);
			stringBuffer.append(undefinedRegion);

			IDOMModel structuredModel = node.getModel();
			IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
			// 1 is for "<"
			int offset = node.getStartOffset() + 1 + node.getNodeName().length();
			// 1 is for "<"
			int length = node.getFirstStructuredDocumentRegion().getTextLength() - 1 - node.getNodeName().length();

			if (flatNode != null) {
				ITextRegionList regions = flatNode.getRegions();
				ITextRegion firstRegion = regions.get(0);
				ITextRegion lastRegion = regions.get(regions.size() - 1);

				if (firstRegion.getType() == DOMRegionContext.XML_END_TAG_OPEN)
					// skip formatting for end tags in this format: </tagName>
					return;
				else {
					if (lastRegion.getType() == DOMRegionContext.XML_TAG_CLOSE || lastRegion.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE)
						length = length - lastRegion.getLength();

					if (lastRegion.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
						// leave space before XML_EMPTY_TAG_CLOSE: <tagName />
						// unless already going to move end bracket
						if (!startTagSpansOver1Line || !alignEndBracket)
							stringBuffer.append(SPACE_CHAR);
					}
				}
			}

			if (startTagSpansOver1Line && alignEndBracket) {
				stringBuffer.append(lineDelimiter).append(lineIndent);
			}

			replace(structuredDocument, offset, length, stringBuffer.toString());

			// BUG108074 & BUG84688 - preserve whitespace in xsl:text &
			// xsl:attribute
			String nodeNamespaceURI = node.getNamespaceURI();
			if (XSL_NAMESPACE.equals(nodeNamespaceURI)) {
				String nodeName = ((Element) node).getLocalName();
				if (XSL_ATTRIBUTE.equals(nodeName) || XSL_TEXT.equals(nodeName)) {
					sawXmlSpace = true;
					formatContraints.setInPreserveSpaceElement(true);
				}
			}

			// If we didn't see a xml:space attribute above, we'll look for
			// one in the DTD.
			// We do not check for a conflict between a DTD's 'fixed' value
			// and the attribute value found in the instance document, we
			// leave that to the validator.
			if (!sawXmlSpace) {
				ModelQueryAdapter adapter = (ModelQueryAdapter) ((IDOMDocument) node.getOwnerDocument()).getAdapterFor(ModelQueryAdapter.class);
				CMElementDeclaration elementDeclaration = (CMElementDeclaration) adapter.getModelQuery().getCMNode(node);
				if (elementDeclaration != null) {
					int contentType = elementDeclaration.getContentType();
					if (preferences.isPreservePCDATAContent() && contentType == CMElementDeclaration.PCDATA) {
						formatContraints.setInPreserveSpaceElement(true);
					}
					else {
						CMNamedNodeMap cmAttributes = elementDeclaration.getAttributes();
						// Check implied values from the DTD way.
						CMAttributeDeclaration attributeDeclaration = (CMAttributeDeclaration) cmAttributes.getNamedItem(XML_SPACE);
						if (attributeDeclaration != null) {
							// CMAttributeDeclaration found, check it out.
							String defaultValue = attributeDeclaration.getAttrType().getImpliedValue();

							// xml:space="preserve" means preserve space,
							// everything else means back to default.
							if (PRESERVE.compareTo(defaultValue) == 0)
								formatContraints.setInPreserveSpaceElement(true);
							else
								formatContraints.setInPreserveSpaceElement(false);
						}
					}
				}
			}
		}
	}

	protected String getUndefinedRegions(IDOMNode node, int startOffset, int length) {
		String result = NodeFormatter.EMPTY_STRING;

		IStructuredDocumentRegion flatNode = node.getFirstStructuredDocumentRegion();
		ITextRegionList regions = flatNode.getRegions();
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion region = regions.get(i);
			String regionType = region.getType();
			int regionStartOffset = flatNode.getStartOffset(region);

			if (regionType.compareTo(DOMRegionContext.UNDEFINED) == 0 && regionStartOffset >= startOffset && regionStartOffset < startOffset + length)
				result = result + flatNode.getFullText(region);
		}

		if (result.length() > 0)
			return SPACE_CHAR + result.trim();
		else
			return result;
	}
}
