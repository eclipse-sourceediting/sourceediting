/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.format;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLGenerator;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.ssemodelquery.ModelQueryAdapter;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class ElementNodeFormatter extends DocumentNodeFormatter {
	static protected final char DOUBLE_QUOTE = '"';//$NON-NLS-1$
	static protected final String DOUBLE_QUOTES = "\"\"";//$NON-NLS-1$
	static protected final char EQUAL_CHAR = '='; // equal sign$NON-NLS-1$
	static protected final String PRESERVE = "preserve";//$NON-NLS-1$
	static protected final String PRESERVE_QUOTED = "\"preserve\"";//$NON-NLS-1$
	static protected final char SINGLE_QUOTE = '\'';//$NON-NLS-1$
	static protected final String XML_SPACE = "xml:space";//$NON-NLS-1$

	protected void formatEndTag(XMLNode node, IStructuredFormatContraints formatContraints) {
		if (!isEndTagMissing(node)) {
			// end tag exists

			IStructuredDocument structuredDocument = node.getModel().getStructuredDocument();
			String lineDelimiter = structuredDocument.getLineDelimiter();
			String nodeIndentation = getNodeIndent(node);
			XMLNode lastChild = (XMLNode) node.getLastChild();
			if (lastChild != null && lastChild.getNodeType() != Node.TEXT_NODE) {
				if (isEndTagMissing(lastChild)) {
					// find deepest child
					XMLNode deepestChild = (XMLNode) lastChild.getLastChild();
					while (deepestChild != null && deepestChild.getLastChild() != null && isEndTagMissing(deepestChild)) {
						lastChild = deepestChild;
						deepestChild = (XMLNode) deepestChild.getLastChild();
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
						} else
							insertAfterNode(lastChild, lineDelimiter + nodeIndentation);
					}
				} else
					// indent end tag
					insertAfterNode(lastChild, lineDelimiter + nodeIndentation);
			} else if (lastChild == null && firstStructuredDocumentRegionContainsLineDelimiters(node)) {
				// indent end tag
				replace(structuredDocument, node.getFirstStructuredDocumentRegion().getEndOffset(), 0, lineDelimiter + nodeIndentation);
			}

			// format end tag name
			IStructuredDocumentRegion endTagStructuredDocumentRegion = node.getLastStructuredDocumentRegion();
			if (endTagStructuredDocumentRegion.getRegions().size() >= 3) {
				ITextRegion endTagNameRegion = endTagStructuredDocumentRegion.getRegions().get(1);
				removeRegionSpaces(node, endTagStructuredDocumentRegion, endTagNameRegion);
			}
		}
	}

	protected void formatNode(XMLNode node, IStructuredFormatContraints formatContraints) {
		if (node != null) {
			// format indentation before node
			formatIndentationBeforeNode(node, formatContraints);

			// format start tag
			XMLNode newNode = node;
			int startTagStartOffset = node.getStartOffset();
			XMLModel structuredModel = node.getModel();

			formatStartTag(node, formatContraints);
			// save new node
			newNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset);

			IStructuredDocumentRegion flatNode = newNode.getFirstStructuredDocumentRegion();
			if (flatNode != null) {
				ITextRegionList regions = flatNode.getRegions();
				ITextRegion lastRegion = regions.get(regions.size() - 1);
				// format children and end tag if not empty start tag
				if (lastRegion.getType() != XMLRegionContext.XML_EMPTY_TAG_CLOSE) {
					// format children
					formatChildren(newNode, formatContraints);

					// save new node
					newNode = (XMLNode) structuredModel.getIndexedRegion(startTagStartOffset);

					// format end tag
					formatEndTag(newNode, formatContraints);
				}
			}

			// format indentation after node
			formatIndentationAfterNode(newNode, formatContraints);
		}
	}

	/**
	 * This method formats the start tag name, and formats the attributes if
	 * available.
	 */
	protected void formatStartTag(XMLNode node, IStructuredFormatContraints formatContraints) {
		String singleIndent = getFormatPreferences().getIndent();
		String lineIndent = formatContraints.getCurrentIndent();
		String attrIndent = lineIndent + singleIndent;
		boolean splitMultiAttrs = ((IStructuredFormatPreferencesXML) fFormatPreferences).getSplitMultiAttrs();
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
				currentAvailableLineWidth = getFormatPreferences().getLineWidth() - usedWidth;
			} catch (BadLocationException exception) {
				throw new SourceEditingRuntimeException(exception);
			}

			StringBuffer stringBuffer = new StringBuffer();
			String lineDelimiter = node.getModel().getStructuredDocument().getLineDelimiter();
			int attrLength = attributes.getLength();
			int lastUndefinedRegionOffset = 0;
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
						ModelQueryAdapter adapter = (ModelQueryAdapter) ((XMLDocument) node.getOwnerDocument()).getAdapterFor(ModelQueryAdapter.class);
						CMElementDeclaration elementDeclaration = (CMElementDeclaration) adapter.getModelQuery().getCMNode(node);
						if (elementDeclaration == null)
							// CMElementDeclaration not found, default to
							// PRESERVE
							formatContraints.setClearAllBlankLines(false);
						else {
							CMAttributeDeclaration attributeDeclaration = (CMAttributeDeclaration) elementDeclaration.getAttributes().getNamedItem(XML_SPACE);
							if (attributeDeclaration == null)
								// CMAttributeDeclaration not found, default
								// to PRESERVE
								formatContraints.setClearAllBlankLines(false);
							else {
								String defaultValue = attributeDeclaration.getAttrType().getImpliedValue();

								if (defaultValue.compareTo(PRESERVE) == 0)
									formatContraints.setClearAllBlankLines(false);
								else
									formatContraints.setClearAllBlankLines(getFormatPreferences().getClearAllBlankLines());
							}
						}
					} else {
						XMLGenerator generator = node.getModel().getGenerator();
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
							formatContraints.setClearAllBlankLines(false);
						else
							formatContraints.setClearAllBlankLines(getFormatPreferences().getClearAllBlankLines());
					}
				}

				if (splitMultiAttrs && attrLength > 1) {
					stringBuffer.append(lineDelimiter + attrIndent);
					stringBuffer.append(flatNode.getText(nameRegion));
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
				} else {
					if (valueRegion != null) {
						int textLength = 1 + flatNode.getText(nameRegion).length() + 1 + flatNode.getText(valueRegion).length();
						if (i == attrLength - 1) {
							if (flatNode != null) {
								ITextRegionList regions = flatNode.getRegions();
								ITextRegion lastRegion = regions.get(regions.size() - 1);
								if (lastRegion.getType() != XMLRegionContext.XML_EMPTY_TAG_CLOSE)
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
						} else {
							stringBuffer.append(lineDelimiter + attrIndent);
							currentAvailableLineWidth = getFormatPreferences().getLineWidth() - attrIndent.length();
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
					} else {
						if (currentAvailableLineWidth >= 1 + flatNode.getText(nameRegion).length()) {
							stringBuffer.append(SPACE_CHAR);
							currentAvailableLineWidth--;
						} else {
							stringBuffer.append(lineDelimiter + attrIndent);
							currentAvailableLineWidth = getFormatPreferences().getLineWidth() - attrIndent.length();
						}

						stringBuffer.append(flatNode.getText(nameRegion));

						currentAvailableLineWidth -= flatNode.getText(nameRegion).length();
					}
				}
			}

			// append undefined regions
			String undefinedRegion = getUndefinedRegions(node, lastUndefinedRegionOffset, node.getEndOffset() - lastUndefinedRegionOffset);
			stringBuffer.append(undefinedRegion);

			XMLModel structuredModel = node.getModel();
			IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
			// 1 is for "<"
			int offset = node.getStartOffset() + 1 + node.getNodeName().length();
			// 1 is for "<"
			int length = node.getFirstStructuredDocumentRegion().getTextLength() - 1 - node.getNodeName().length();

			if (flatNode != null) {
				ITextRegionList regions = flatNode.getRegions();
				ITextRegion firstRegion = regions.get(0);
				ITextRegion lastRegion = regions.get(regions.size() - 1);

				if (firstRegion.getType() == XMLRegionContext.XML_END_TAG_OPEN)
					// skip formatting for end tags in this format: </tagName>
					return;
				else {
					if (lastRegion.getType() == XMLRegionContext.XML_TAG_CLOSE || lastRegion.getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE)
						length = length - lastRegion.getLength();

					if (lastRegion.getType() == XMLRegionContext.XML_EMPTY_TAG_CLOSE)
						// leave space before XML_EMPTY_TAG_CLOSE: <tagName />
						stringBuffer.append(SPACE_CHAR);
				}
			}

			replace(structuredDocument, offset, length, stringBuffer.toString());
		}
	}

	protected String getUndefinedRegions(XMLNode node, int startOffset, int length) {
		String result = new String();

		IStructuredDocumentRegion flatNode = node.getFirstStructuredDocumentRegion();
		ITextRegionList regions = flatNode.getRegions();
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion region = regions.get(i);
			String regionType = region.getType();
			int regionStartOffset = flatNode.getStartOffset(region);

			if (regionType.compareTo(XMLRegionContext.UNDEFINED) == 0 && regionStartOffset >= startOffset && regionStartOffset < startOffset + length)
				result = result + flatNode.getFullText(region);
		}

		if (result.length() > 0)
			return SPACE + result.trim();
		else
			return result;
	}
}
