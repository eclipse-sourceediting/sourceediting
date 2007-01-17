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

import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatContraints;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;


public class TextNodeFormatter extends NodeFormatter {
	protected void formatNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		// [111674] If inside xml:space="preserve" element, we bail
		if (formatContraints.getInPreserveSpaceElement())
			return;
		if (node != null) {
			IStructuredDocument doc = node.getModel().getStructuredDocument();
			int line = doc.getLineOfOffset(node.getStartOffset());
			String lineDelimiter = doc.getLineDelimiter();
			try {
				lineDelimiter = doc.getLineDelimiter(line);
			}
			catch (BadLocationException e) {
				// log for now, unless we find reason not to
				Logger.log(Logger.INFO, e.getMessage());
			}
			// BUG166441: if cannot get line delimiter from current line, just
			// use default line delimiter
			if (lineDelimiter == null)
				lineDelimiter = doc.getLineDelimiter();
			int lineWidth = getFormatPreferences().getLineWidth();
			IDOMNode parentNode = (IDOMNode) node.getParentNode();
			String nodeIndentation = formatContraints.getCurrentIndent();

			// compute current available line width
			int currentAvailableLineWidth = 0;
			try {
				int nodeNameOffset = node.getStartOffset();
				int lineOffset = node.getStructuredDocument().getLineInformationOfOffset(nodeNameOffset).getOffset();
				String text = node.getStructuredDocument().get(lineOffset, nodeNameOffset - lineOffset);
				int usedWidth = getIndentationLength(text);
				currentAvailableLineWidth = getFormatPreferences().getLineWidth() - usedWidth;
			}
			catch (BadLocationException e) {
				// log for now, unless we find reason not to
				Logger.log(Logger.INFO, e.getMessage());
			}

			String compressedText = getCompressedNodeText(node, formatContraints);

			if (((enoughSpace(parentNode, currentAvailableLineWidth, compressedText)) && (noSiblingsAndNoFollowingComment(node)) && !firstStructuredDocumentRegionContainsLineDelimiters(parentNode)) || node.getStartOffset() == 0) {
				// enough space
				// and text has no line delimiters
				// and (node has no siblings or followed by inline comment)
				// and
				// parentFirstStructuredDocumentRegionContainsLineDelimiters

				if (isEndTagMissing(parentNode)) {
					parentNode = (IDOMNode) parentNode.getParentNode();
					while (isEndTagMissing(parentNode))
						parentNode = (IDOMNode) parentNode.getParentNode();

					// add parent's indentation to end
					nodeIndentation = getNodeIndent(parentNode);

					if (!compressedText.endsWith(lineDelimiter + nodeIndentation)) {
						compressedText = StringUtils.appendIfNotEndWith(compressedText, lineDelimiter);
						compressedText = StringUtils.appendIfNotEndWith(compressedText, nodeIndentation);
					}
				}

				if ((parentNode != null) && (parentNode.getNodeType() == Node.DOCUMENT_NODE) && (node.getNodeValue().length() > 0) && (node.getNodeValue().trim().length() == 0) && ((node.getPreviousSibling() == null) || (node.getNextSibling() == null)))
					// delete spaces at the beginning or end of the document
					compressedText = EMPTY_STRING;

				replaceNodeValue(node, compressedText);
			}
			else {
				// not enough space, need to reflow text

				currentAvailableLineWidth = lineWidth - getIndentationLength(nodeIndentation);
				List vector = reflowText(compressedText, currentAvailableLineWidth);
				int vectorSize = vector.size();
				String reflowedText = new String();

				for (int i = 0; i < vectorSize; i++) {
					if (((String) vector.get(i)).trim().length() > 0)
						reflowedText = reflowedText + lineDelimiter + nodeIndentation + (String) vector.get(i);
					else
						reflowedText = reflowedText + lineDelimiter;
				}

				if (node.getNextSibling() == null) {
					if (isEndTagMissing(parentNode)) {
						// don't add indentation to end if parent end tag is
						// missing
					}

					else {
						// add parent's indentation to end
						nodeIndentation = getNodeIndent(parentNode);

						if (!reflowedText.endsWith(lineDelimiter + nodeIndentation)) {
							reflowedText = StringUtils.appendIfNotEndWith(reflowedText, lineDelimiter);
							reflowedText = StringUtils.appendIfNotEndWith(reflowedText, nodeIndentation);
						}
					}
				}
				else {
					if (!reflowedText.endsWith(lineDelimiter + nodeIndentation)) {
						// not already ended with the expected indentation

						if (node.getNextSibling().getNodeType() == Node.COMMENT_NODE) {
							// add indentation to end if
							// currentTextEndsWithLineDelimiter
							// or followed by multiLineComment

							String nodeText = getNodeText(node);
							int indexOfLastLineDelimiter = StringUtils.indexOfLastLineDelimiter(nodeText);
							boolean currentTextEndsWithLineDelimiter = indexOfLastLineDelimiter != -1;
							if (currentTextEndsWithLineDelimiter) {
								// no more non blank character after the last
								// line delimiter
								currentTextEndsWithLineDelimiter = StringUtils.indexOfNonblank(nodeText, indexOfLastLineDelimiter) == -1;
							}

							String nodeValue = node.getNextSibling().getNodeValue();
							boolean multiLineComment = StringUtils.containsLineDelimiter(nodeValue);

							if (currentTextEndsWithLineDelimiter || multiLineComment) {
								reflowedText = StringUtils.appendIfNotEndWith(reflowedText, lineDelimiter);
								reflowedText = StringUtils.appendIfNotEndWith(reflowedText, nodeIndentation);
							}
						}
						else {
							// not a comment, just add add indentation to end
							reflowedText = StringUtils.appendIfNotEndWith(reflowedText, lineDelimiter);
							reflowedText = StringUtils.appendIfNotEndWith(reflowedText, nodeIndentation);
						}
					}
				}

				replaceNodeValue(node, reflowedText);
			}

		}
	}

	private boolean noSiblingsAndNoFollowingComment(IDOMNode node) {
		IDOMNode nextSibling = (IDOMNode) node.getNextSibling();
		return !nodeHasSiblings(node) || (noLineDelimiter(node) && isComment(nextSibling) && noLineDelimiter(nextSibling));
	}

	private boolean isComment(IDOMNode node) {
		boolean result = false;
		if (node != null) {
			result = node.getNodeType() == Node.COMMENT_NODE;
		}
		return result;
	}

	private boolean noLineDelimiter(IDOMNode node) {
		boolean result = false;
		if (node != null) {
			result = !StringUtils.containsLineDelimiter(node.getNodeValue());
		}
		return result;
	}

	private boolean enoughSpace(IDOMNode parentNode, int currentAvailableLineWidth, String compressedText) {
		return compressedText.length() <= (currentAvailableLineWidth - parentNode.getNodeName().length() - 3) && !StringUtils.containsLineDelimiter(compressedText);
	}

	protected Vector reflowText(String text, int availableWidth) {
		String[] stringArray = null;
		boolean clearAllBlankLines = getFormatPreferences().getClearAllBlankLines();

		if (clearAllBlankLines)
			stringArray = StringUtils.asArray(text);
		else
			stringArray = StringUtils.asArray(text, DELIMITERS, true);

		Vector output = new Vector();
		if ((stringArray != null) && (stringArray.length > 0)) {
			StringBuffer buffer = new StringBuffer();
			if (stringArray[0].compareTo(CR) != 0)
				buffer.append(stringArray[0]);
			int bufferLength = stringArray[0].toString().length();
			boolean cr = stringArray[0].compareTo(CR) == 0;

			for (int i = 1; i < stringArray.length; i++) {
				String eachString = stringArray[i];
				if ((eachString.compareTo(SPACE) != 0) && (eachString.compareTo(TAB) != 0) && (eachString.compareTo(FF) != 0)) {
					if ((bufferLength + 1 + eachString.length() > availableWidth) || (eachString.compareTo(CR) == 0) || (eachString.compareTo(LF) == 0)) {
						if ((eachString.compareTo(LF) == 0) && cr) {
							// do nothing
						}
						else {
							output.add(buffer.toString());
							buffer = new StringBuffer();
							bufferLength = 0;
						}
						cr = eachString.compareTo(CR) == 0;
					}
					else if (buffer.toString().trim().length() > 0) {
						buffer.append(SPACE);
						bufferLength++;
					}
					if ((eachString.compareTo(CR) != 0) && (eachString.compareTo(LF) != 0)) {
						buffer.append(eachString);
						bufferLength = bufferLength + eachString.length();
					}
				}
			}
			output.add(buffer.toString());
		}
		else
			output.add(text);

		return output;
	}
}
