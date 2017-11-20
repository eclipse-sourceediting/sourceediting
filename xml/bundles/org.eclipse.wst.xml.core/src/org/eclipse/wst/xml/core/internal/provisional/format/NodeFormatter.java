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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.sse.core.internal.format.StructuredFormatContraints;
import org.eclipse.wst.sse.core.internal.parser.ContextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.document.CDATASectionImpl;
import org.eclipse.wst.xml.core.internal.document.CharacterDataImpl;
import org.eclipse.wst.xml.core.internal.document.CommentImpl;
import org.eclipse.wst.xml.core.internal.parser.regions.TagNameRegion;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Node;

public class NodeFormatter implements IStructuredFormatter {
	static protected final String EMPTY_STRING = ""; //$NON-NLS-1$
	static private final char SPACE_CHAR = ' '; //$NON-NLS-1$
	static private final char TAB_CHAR = '\t'; //$NON-NLS-1$
	static private final String TAB = "\t"; //$NON-NLS-1$
	protected IStructuredFormatContraints fFormatContraints = null;
	protected IStructuredFormatPreferences fFormatPreferences = null;
	protected IProgressMonitor fProgressMonitor = null;

	protected boolean firstStructuredDocumentRegionContainsLineDelimiters(IDOMNode node) {
		boolean result = false;

		if (node != null) {
			IStructuredDocumentRegion firstStructuredDocumentRegion = node.getFirstStructuredDocumentRegion();
			if (firstStructuredDocumentRegion != null) {
				String firstStructuredDocumentRegionText = firstStructuredDocumentRegion.getText();
				result = StringUtils.containsLineDelimiter(firstStructuredDocumentRegionText);
			}
		}

		return result;
	}

	public void format(Node node) {
		IStructuredFormatContraints formatContraints = getFormatContraints();

		format(node, formatContraints);
	}

	public void format(Node node, IStructuredFormatContraints formatContraints) {
		if (formatContraints.getFormatWithSiblingIndent())
			formatContraints.setCurrentIndent(getSiblingIndent(node));

		if (node instanceof IDOMNode)
			formatNode((IDOMNode) node, formatContraints);
	}

	protected void formatIndentationAfterNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		// [111674] If inside xml:space="preserve" element, we bail
		if (formatContraints.getInPreserveSpaceElement())
			return;
		if (node != null) {
			IDOMNode nextSibling = (IDOMNode) node.getNextSibling();
			IStructuredDocument doc = node.getModel().getStructuredDocument();
			String lineDelimiter = getLineDelimiter(node, doc);

			if (node.getParentNode() != null) {
				if (node.getParentNode().getNodeType() == Node.DOCUMENT_NODE)
					if (nextSibling != null)
						if (nextSibling.getNodeType() == Node.TEXT_NODE)
							getFormatter(nextSibling).format(nextSibling, formatContraints);
						else if (nextSibling.getNodeType() == Node.COMMENT_NODE) {
							// do nothing
						}
						else {
							String lineIndent = formatContraints.getCurrentIndent();
							insertAfterNode(node, lineDelimiter + lineIndent);
						}
					else {
					}

				else if (nextSibling != null)
					if (nextSibling.getNodeType() == Node.TEXT_NODE)
						getFormatter(nextSibling).format(nextSibling, formatContraints);
					else if (nextSibling.getNodeType() == Node.COMMENT_NODE) {
						// do nothing
					}
					else {
						String lineIndent = formatContraints.getCurrentIndent();
						insertAfterNode(node, lineDelimiter + lineIndent);
					}
				else {
					IDOMNode indentNode = getParentIndentNode(node);
					String lineIndent = getNodeIndent(indentNode);
					IDOMNode lastChild = getDeepestChildNode(node);
					boolean clearAllBlankLines = formatContraints.getClearAllBlankLines();

					if (lastChild != null) {
						if ((lastChild.getNodeType() == Node.TEXT_NODE) && (lastChild.getNodeValue().endsWith(lineDelimiter + lineIndent))) {
							// this text node already ends with the requested
							// indentation
						}

						else if ((lastChild.getNodeType() == Node.TEXT_NODE) && (lastChild.getNodeValue() != null && lastChild.getNodeValue().endsWith(lineDelimiter)))
							if (clearAllBlankLines) {
								replaceNodeValue(lastChild, lineDelimiter + lineIndent);
							}
							else {
								// append indentation
								insertAfterNode(lastChild, lineIndent);
							}
						else if (lastChild.getNodeType() == Node.TEXT_NODE)
							if (lastChild.getNodeValue().length() == 0) {
								// replace
								replaceNodeValue(lastChild, lineDelimiter + lineIndent);
							}
							else {
								// append indentation
								insertAfterNode(lastChild, lineDelimiter + lineIndent);
							}
						else {
							// as long as not at the end of the document
							IStructuredDocumentRegion endRegion = node.getLastStructuredDocumentRegion();
							if (endRegion != null && endRegion.getNext() != null)
								// append indentation
								insertAfterNode(lastChild, lineDelimiter + lineIndent);
						}
					}
				}
			}
		}
	}

	protected void formatIndentationBeforeNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		// [111674] If inside xml:space="preserve" element, we bail
		if (formatContraints.getInPreserveSpaceElement())
			return;
		if (node != null) {
			IDOMNode previousSibling = (IDOMNode) node.getPreviousSibling();
			IStructuredDocument doc = node.getModel().getStructuredDocument();
			String lineDelimiter = getLineDelimiter(node, doc);

			String lineIndent = formatContraints.getCurrentIndent();

			if (node.getParentNode() != null) {
				if (node.getParentNode().getNodeType() == Node.DOCUMENT_NODE) {
					if (previousSibling != null)
						if (previousSibling.getNodeType() == Node.TEXT_NODE)
							getFormatter(previousSibling).format(previousSibling, formatContraints);
						else {
							insertBeforeNode(node, lineDelimiter + lineIndent);
						}
				}
				else {
					if (previousSibling == null || previousSibling.getNodeType() != Node.TEXT_NODE) {
						// 261968 - formatting tag without closing bracket:
						// <t1><t1
						// 265673 - Null ptr in formatIndentationBeforeNode
						int prevEndNodeOffset = -1;
						int prevEndRegionOffset = -1;
						if (previousSibling != null) {
							prevEndNodeOffset = previousSibling.getEndOffset();
							IStructuredDocumentRegion endRegion = previousSibling.getEndStructuredDocumentRegion();
							if (endRegion != null) {
								prevEndRegionOffset = endRegion.getTextEndOffset();
							}
						}
						if ((previousSibling == null) || (prevEndNodeOffset != -1 && prevEndNodeOffset == prevEndRegionOffset)) {
							insertBeforeNode(node, lineDelimiter + lineIndent);
						}

					}
					else {
						if (previousSibling.getNodeValue().length() == 0) {
							// replace
							replaceNodeValue(previousSibling, lineDelimiter + lineIndent);
						}
						else {
							// append indentation
							if (!previousSibling.getNodeValue().endsWith(lineDelimiter + lineIndent)) {
								if (previousSibling.getNodeValue().endsWith(lineDelimiter)) {
									insertAfterNode(previousSibling, lineIndent);
								}
								else
									getFormatter(previousSibling).format(previousSibling, formatContraints);
							}
						}
					}
				}
			}
		}
	}

	private String getLineDelimiter(IDOMNode node, IStructuredDocument doc) {
		int line = doc.getLineOfOffset(node.getStartOffset());
		String lineDelimiter = doc.getLineDelimiter();
		try {
			if (line > 0) {
				lineDelimiter = doc.getLineDelimiter(line - 1);
			}
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		// BUG115716: if cannot get line delimiter from current line, just
		// use default line delimiter
		if (lineDelimiter == null)
			lineDelimiter = doc.getLineDelimiter();
		return lineDelimiter;
	}

	protected void formatNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		if (node != null && (fProgressMonitor == null || !fProgressMonitor.isCanceled())) {
			// format indentation before node
			formatIndentationBeforeNode(node, formatContraints);

			// format indentation after node
			formatIndentationAfterNode(node, formatContraints);
		}
	}

	/**
	 * This method will compute the correct indentation after this node
	 * depending on the indentations of its sibling nodes and parent node. Not
	 * needed anymore?
	 */
	protected void formatTrailingText(IDOMNode node, IStructuredFormatContraints formatContraints) {
		// [111674] If inside xml:space="preserve" element, we bail
		if (formatContraints.getInPreserveSpaceElement())
			return;

		String lineDelimiter = node.getModel().getStructuredDocument().getLineDelimiter();
		String lineIndent = formatContraints.getCurrentIndent();
		String parentLineIndent = getNodeIndent(node.getParentNode());
		boolean clearAllBlankLines = formatContraints.getClearAllBlankLines();

		if ((node != null) && (node.getNodeType() != Node.DOCUMENT_NODE)) {
			IDOMNode nextSibling = (IDOMNode) node.getNextSibling();
			if ((nextSibling != null) && (nextSibling.getNodeType() == Node.TEXT_NODE)) {
				String nextSiblingText = nextSibling.getNodeValue();
				if (nextSibling.getNextSibling() == null)
					if ((nextSibling.getParentNode().getNodeType() == Node.DOCUMENT_NODE) && (nextSiblingText.trim().length() == 0))
						// delete spaces at the end of the document
						replaceNodeValue(nextSibling, EMPTY_STRING);
					else
						// replace the text node with parent indentation
						replaceNodeValue(nextSibling, lineDelimiter + parentLineIndent);
				else
					// replace the text node with indentation
					replaceNodeValue(nextSibling, lineDelimiter + lineIndent);
			}
			else {
				if (nextSibling == null) {
					lineIndent = parentLineIndent;

					if (node.getParentNode().getNodeType() != Node.DOCUMENT_NODE)
						if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue().endsWith(lineDelimiter + lineIndent))) {
							// this text node already ends with the requested
							// indentation
						}

						else if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue().endsWith(lineDelimiter)))
							if (clearAllBlankLines)
								replaceNodeValue(node, lineDelimiter + lineIndent);
							else
								// append indentation
								insertAfterNode(node, lineIndent);
						else if (node.getNodeType() == Node.TEXT_NODE)
							if (node.getNodeValue().length() == 0)
								// replace
								replaceNodeValue(node, lineDelimiter + lineIndent);
							else
							// append indentation
							if (!node.getNodeValue().endsWith(lineDelimiter + lineIndent))
								if (node.getNodeValue().endsWith(lineDelimiter))
									insertAfterNode(node, lineIndent);
								else
									insertAfterNode(node, lineDelimiter + lineIndent);
							else
								replaceNodeValue(node, lineDelimiter + lineIndent);
				}
				else {
					if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue().endsWith(lineDelimiter + lineIndent))) {
						// this text node already ends with the requested
						// indentation
					}

					else if ((node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue().endsWith(lineDelimiter)))
						if (clearAllBlankLines)
							replaceNodeValue(node, lineDelimiter + lineIndent);
						else
							// append indentation
							insertAfterNode(node, lineIndent);
					else if (node.getNodeType() == Node.TEXT_NODE)
						if (node.getNodeValue().length() == 0)
							// replace
							replaceNodeValue(node, lineDelimiter + lineIndent);
						else
							// append indentation
							insertAfterNode(node, lineDelimiter + lineIndent);
					else
						// append indentation
						insertAfterNode(node, lineDelimiter + lineIndent);
				}
			}
		}
	}

	protected IDOMNode getDeepestChildNode(IDOMNode node) {
		IDOMNode result = null;
		IDOMNode lastChild = (IDOMNode) node.getLastChild();

		if (lastChild == null)
			result = node;
		else {
			result = getDeepestChildNode(lastChild);

			if ((result.getNodeType() == Node.TEXT_NODE || result.getNodeType() == Node.COMMENT_NODE) && !isEndTagMissing(node))
				result = node;
		}

		return result;
	}

	public IStructuredFormatContraints getFormatContraints() {
		if (fFormatContraints == null) {
			fFormatContraints = new StructuredFormatContraints();

			fFormatContraints.setClearAllBlankLines(getFormatPreferences().getClearAllBlankLines());
		}

		return fFormatContraints;
	}

	public IStructuredFormatPreferences getFormatPreferences() {
		if (fFormatPreferences == null) {
			fFormatPreferences = new StructuredFormatPreferencesXML();

			Preferences preferences = getModelPreferences();
			if (preferences != null) {
				fFormatPreferences.setLineWidth(preferences.getInt(XMLCorePreferenceNames.LINE_WIDTH));
				((StructuredFormatPreferencesXML) fFormatPreferences).setSplitMultiAttrs(preferences.getBoolean(XMLCorePreferenceNames.SPLIT_MULTI_ATTRS));
				((StructuredFormatPreferencesXML) fFormatPreferences).setAlignEndBracket(preferences.getBoolean(XMLCorePreferenceNames.ALIGN_END_BRACKET));
				((StructuredFormatPreferencesXML) fFormatPreferences).setPreservePCDATAContent(preferences.getBoolean(XMLCorePreferenceNames.PRESERVE_CDATACONTENT));
				fFormatPreferences.setClearAllBlankLines(preferences.getBoolean(XMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES));

				char indentChar = ' ';
				String indentCharPref = preferences.getString(XMLCorePreferenceNames.INDENTATION_CHAR);
				if (XMLCorePreferenceNames.TAB.equals(indentCharPref)) {
					indentChar = '\t';
				}
				int indentationWidth = preferences.getInt(XMLCorePreferenceNames.INDENTATION_SIZE);

				StringBuffer indent = new StringBuffer();
				for (int i = 0; i < indentationWidth; i++) {
					indent.append(indentChar);
				}
				fFormatPreferences.setIndent(indent.toString());
			}
		}

		return fFormatPreferences;
	}

	protected IStructuredFormatter getFormatter(IDOMNode node) {
		// 262135 - NPE during format of empty document
		if (node == null)
			return null;

		short nodeType = ((Node) node).getNodeType();
		IStructuredFormatter formatter = null;
		switch (nodeType) {
			case Node.ELEMENT_NODE : {
				formatter = new ElementNodeFormatter();
				break;
			}
			case Node.TEXT_NODE : {
				formatter = new TextNodeFormatter();
				break;
			}
			case Node.CDATA_SECTION_NODE : {
				formatter = new NoMoveFormatter();
				break;
			}
			case Node.COMMENT_NODE : {
				formatter = new CommentNodeFormatter();
				break;
			}
			case Node.PROCESSING_INSTRUCTION_NODE : {
				formatter = new NodeFormatter();
				break;
			}
			case Node.DOCUMENT_NODE : {
				formatter = new DocumentNodeFormatter();
				break;
			}
			case Node.ENTITY_REFERENCE_NODE : {
				formatter = new NoMoveFormatter();
				break;
			}
			default : {
				formatter = new NodeFormatter();
			}
		}

		// init fomatter
		formatter.setFormatPreferences(getFormatPreferences());
		formatter.setProgressMonitor(fProgressMonitor);

		return formatter;
	}

	protected int getIndentationLength(String indent) {
		// TODO Kit : The calculation of IndentationLength is not correct
		// here.
		// nodeIndentation may contain tabs. Multiply by 4 temporarily to get
		// approx. width.
		// Need to re-work.

		int indentationLength = 0;

		for (int i = 0; i < indent.length(); i++) {
			if (indent.substring(i, i + 1).compareTo(TAB) == 0)
				indentationLength += 4;
			else
				indentationLength++;
		}

		return indentationLength;
	}

	protected Preferences getModelPreferences() {
		return XMLCorePlugin.getDefault().getPluginPreferences();
	}

	/**
	 * This method will find the indentation for this node. It will search
	 * backwards starting from the beginning of the node until a character
	 * other than a space or a tab is found. If this node is null or it's a
	 * document node or it's a first level node (node's parent is a document
	 * node) the default empty string will be returned as the indentation.
	 */
	protected String getNodeIndent(Node node) {
		String result = EMPTY_STRING;

		if ((node != null) && (node.getNodeType() != Node.DOCUMENT_NODE) && (node.getParentNode() != null) && (node.getParentNode().getNodeType() != Node.DOCUMENT_NODE)) {
			IDOMNode siblingTextNode = (IDOMNode) node.getPreviousSibling();
			if ((siblingTextNode != null) && (siblingTextNode.getNodeType() == Node.TEXT_NODE)) {
				// find the indentation
				String siblingText = siblingTextNode.getNodeValue();
				int siblingTextLength = siblingText.length();
				if ((siblingText != null) && (siblingTextLength > 0) && ((siblingText.charAt(siblingTextLength - 1) == SPACE_CHAR) || (siblingText.charAt(siblingTextLength - 1) == TAB_CHAR))) {
					int searchIndex = siblingTextLength - 1;
					while ((searchIndex >= 0) && ((siblingText.charAt(searchIndex) == SPACE_CHAR) || (siblingText.charAt(searchIndex) == TAB_CHAR)))
						searchIndex--;

					if (searchIndex < siblingTextLength)
						result = siblingText.substring(searchIndex + 1, siblingTextLength);
				}
			}
		}

		return result;
	}

	protected String getNodeName(IDOMNode node) {
		return node.getNodeName();
	}

	protected String getNodeText(IDOMNode node) {
		String text = null;

		if ((node instanceof CharacterDataImpl) && !(node instanceof CommentImpl) && !(node instanceof CDATASectionImpl) && !isJSPTag(node))
			text = ((CharacterDataImpl) node).getSource();
		else
			text = node.getFirstStructuredDocumentRegion().getText();

		return text;
	}

	protected IDOMNode getParentIndentNode(IDOMNode node) {
		IDOMNode result = null;
		IDOMNode parentNode = (IDOMNode) node.getParentNode();

		if (parentNode.getNodeType() == Node.DOCUMENT_NODE)
			result = parentNode;
		else {
			ITextRegion region = parentNode.getLastStructuredDocumentRegion().getFirstRegion();
			if (region.getType() == DOMRegionContext.XML_END_TAG_OPEN)
				result = parentNode;
			else
				result = getParentIndentNode(parentNode);
		}

		return result;
	}

	/**
	 * This method will find the indentation for a node sibling to this node.
	 * It will try to find a sibling node before this node first. If there is
	 * no sibling node before this node, it will try to find a sibling node
	 * after this node. If still not found, we will check if this node is
	 * already indented from its parent. If yes, this node's indentation will
	 * be used. Otherwise, the parent node's indentation plus one indentation
	 * will be used. If this node is null or it's a document node or it's a
	 * first level node (node's parent is a document node) the default empty
	 * string will be returned as the indentation.
	 */
	protected String getSiblingIndent(Node node) {
		String result = EMPTY_STRING;

		if ((node != null) && (node.getNodeType() != Node.DOCUMENT_NODE) && (node.getParentNode() != null) && (node.getParentNode().getNodeType() != Node.DOCUMENT_NODE)) {
			// find the text node before the previous non-text sibling
			// if that's not found, we will try the text node before the next
			// non-text sibling
			IDOMNode sibling = (IDOMNode) node.getPreviousSibling();
			while ((sibling != null) && (sibling.getNodeType() == Node.TEXT_NODE || sibling.getNodeType() == Node.COMMENT_NODE)) {
				if (sibling.getNodeType() == Node.COMMENT_NODE && sibling.getPreviousSibling() != null && sibling.getPreviousSibling().getNodeType() == Node.TEXT_NODE && StringUtils.containsLineDelimiter(sibling.getPreviousSibling().getNodeValue()))
					break;
				sibling = (IDOMNode) sibling.getPreviousSibling();
			}
			if (sibling == null) {
				sibling = (IDOMNode) node.getNextSibling();
				while ((sibling != null) && (sibling.getNodeType() == Node.TEXT_NODE))
					sibling = (IDOMNode) sibling.getNextSibling();
			}
			String singleIndent = getFormatPreferences().getIndent();
			String parentLineIndent = getNodeIndent(node.getParentNode());

			if (sibling != null) {
				String siblingIndent = getNodeIndent(sibling);
				if (siblingIndent.length() > 0)
					result = siblingIndent;
				else {
					String nodeIndent = getNodeIndent(node);
					if (nodeIndent.length() > parentLineIndent.length())
						// this node is indented from its parent, its
						// indentation will be used
						result = nodeIndent;
					else
						result = parentLineIndent + singleIndent;
				}
			}
			else {
				String nodeIndent = getNodeIndent(node);
				if (nodeIndent.length() > parentLineIndent.length())
					// this node is indented from its parent, its indentation
					// will be used
					result = nodeIndent;
				else
					result = parentLineIndent + singleIndent;
			}
		}

		return result;
	}

	protected void insertAfterNode(IDOMNode node, String string) {
		IDOMModel structuredModel = node.getModel();
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

		int offset = node.getEndOffset();
		int length = 0;

		// 261968 - formatting tag without closing bracket: <t1><t1
		if (node.getEndStructuredDocumentRegion() != null) {
			offset = node.getEndStructuredDocumentRegion().getTextEndOffset();
			length = node.getEndOffset() - offset;
		}
		replace(structuredDocument, offset, length, string);
	}

	protected void insertBeforeNode(IDOMNode node, String string) {
		IDOMModel structuredModel = node.getModel();
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

		replace(structuredDocument, node.getStartOffset(), 0, string);
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return type.equals(IStructuredFormatter.class);
	}

	protected boolean isEndTagMissing(IDOMNode node) {
		boolean result = false;

		if ((node != null) && (node.getNodeType() != Node.DOCUMENT_NODE) && !isJSPTag(node)) {
			IStructuredDocumentRegion startTagStructuredDocumentRegion = node.getFirstStructuredDocumentRegion();
			IStructuredDocumentRegion endTagStructuredDocumentRegion = node.getLastStructuredDocumentRegion();

			ITextRegion startTagNameRegion = null;
			if (startTagStructuredDocumentRegion.getRegions().size() > 1)
				startTagNameRegion = startTagStructuredDocumentRegion.getRegions().get(1);
			ITextRegion endTagNameRegion = null;
			if (endTagStructuredDocumentRegion.getRegions().size() > 1)
				endTagNameRegion = endTagStructuredDocumentRegion.getRegions().get(1);

			ITextRegionList startTagRegions = startTagStructuredDocumentRegion.getRegions();
			if (startTagNameRegion == endTagNameRegion && startTagNameRegion != null && (startTagRegions.get(0)).getType() != DOMRegionContext.XML_END_TAG_OPEN && (startTagRegions.get(startTagRegions.size() - 1).getType()) != DOMRegionContext.XML_EMPTY_TAG_CLOSE)
				// end tag missing
				result = true;
		}

		return result;
	}

	protected boolean nodeHasSiblings(IDOMNode node) {
		return (node.getPreviousSibling() != null) || (node.getNextSibling() != null);
	}

	/**
	 * Node changed. No format should be performed automatically.
	 */
	public void notifyChanged(org.eclipse.wst.sse.core.internal.provisional.INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	protected void removeRegionSpaces(IDOMNode node, IStructuredDocumentRegion flatNode, ITextRegion region) {
		if ((region != null) && (region instanceof ContextRegion || region instanceof TagNameRegion) && (flatNode.getEndOffset(region) > flatNode.getTextEndOffset(region))) {
			IDOMModel structuredModel = node.getModel();
			IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();

			replace(structuredDocument, flatNode.getTextEndOffset(region), flatNode.getEndOffset(region) - flatNode.getTextEndOffset(region), EMPTY_STRING);
		}
	}

	/**
	 * This method will replace the string at offset and length with a new
	 * string. If the string to be replaced is the same as the new string, the
	 * string will not be replaced.
	 */
	protected void replace(IStructuredDocument structuredDocument, int offset, int length, String string) {
		try {
			String structuredDocumentString = structuredDocument.get(offset, length);
			if (structuredDocumentString.compareTo(string) != 0)
				structuredDocument.replaceText(structuredDocument, offset, length, string);
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
	}

	/**
	 * This method will replace the node value with a new string. If the node
	 * value to be replaced is the same as the new string, the node value will
	 * not be replaced.
	 */
	protected void replaceNodeValue(IDOMNode node, String string) {
		IDOMModel structuredModel = node.getModel();
		IStructuredDocument structuredDocument = structuredModel.getStructuredDocument();
		int offset = node.getStartOffset();
		int length = node.getEndOffset() - node.getStartOffset();

		try {
			String structuredDocumentString = structuredDocument.get(offset, length);
			if (structuredDocumentString.compareTo(string) != 0)
				replace(structuredDocument, offset, length, string);
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
	}

	public void setFormatPreferences(IStructuredFormatPreferences formatPreferences) {
		fFormatPreferences = formatPreferences;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.format.IStructuredFormatter#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
	}

	/**
	 * ISSUE: this is a bit of hidden JSP knowledge that was implemented this
	 * way for expedency. Should be evolved in future to depend on
	 * "nestedContext".
	 */
	private boolean isJSPTag(Node node) {

		final String JSP_CLOSE = "JSP_CLOSE"; //$NON-NLS-1$
		// final String JSP_COMMENT_CLOSE = "JSP_COMMENT_CLOSE"; //$NON-NLS-1$

		// final String JSP_COMMENT_OPEN = "JSP_COMMENT_OPEN"; //$NON-NLS-1$
		// final String JSP_COMMENT_TEXT = "JSP_COMMENT_TEXT"; //$NON-NLS-1$

		final String JSP_CONTENT = "JSP_CONTENT"; //$NON-NLS-1$
		final String JSP_DECLARATION_OPEN = "JSP_DECLARATION_OPEN"; //$NON-NLS-1$
		final String JSP_DIRECTIVE_CLOSE = "JSP_DIRECTIVE_CLOSE"; //$NON-NLS-1$
		final String JSP_DIRECTIVE_NAME = "JSP_DIRECTIVE_NAME"; //$NON-NLS-1$

		final String JSP_DIRECTIVE_OPEN = "JSP_DIRECTIVE_OPEN"; //$NON-NLS-1$
		final String JSP_EXPRESSION_OPEN = "JSP_EXPRESSION_OPEN"; //$NON-NLS-1$

		// final String JSP_ROOT_TAG_NAME = "JSP_ROOT_TAG_NAME"; //$NON-NLS-1$

		final String JSP_SCRIPTLET_OPEN = "JSP_SCRIPTLET_OPEN"; //$NON-NLS-1$

		boolean result = false;

		if (node instanceof IDOMNode) {
			IStructuredDocumentRegion flatNode = ((IDOMNode) node).getFirstStructuredDocumentRegion();
			// in some cases, the nodes exists, but hasn't been associated
			// with
			// a flatnode yet (the screen updates can be initiated on a
			// different thread,
			// so the request for a flatnode can come in before the node is
			// fully formed.
			// if the flatnode is null, we'll just allow the defaults to
			// apply.
			if (flatNode != null) {
				String flatNodeType = flatNode.getType();
				// should not be null, but just to be sure
				if (flatNodeType != null) {
					if ((flatNodeType.equals(JSP_CONTENT)) || (flatNodeType.equals(JSP_EXPRESSION_OPEN)) || (flatNodeType.equals(JSP_SCRIPTLET_OPEN)) || (flatNodeType.equals(JSP_DECLARATION_OPEN)) || (flatNodeType.equals(JSP_DIRECTIVE_CLOSE)) || (flatNodeType.equals(JSP_DIRECTIVE_NAME)) || (flatNodeType.equals(JSP_DIRECTIVE_OPEN)) || (flatNodeType.equals(JSP_CLOSE))) {
						result = true;
					}
				}
			}
		}

		return result;
	}
}
