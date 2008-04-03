/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
	static private final String CR = "\r"; //$NON-NLS-1$
	static private final String DELIMITERS = " \t\n\r\f"; //$NON-NLS-1$
	static private final String EMPTY_STRING = ""; //$NON-NLS-1$
	static private final String FF = "\f"; //$NON-NLS-1$
	static private final String LF = "\n"; //$NON-NLS-1$
	static private final String SPACE = " "; //$NON-NLS-1$
	static private final String TAB = "\t"; //$NON-NLS-1$

	private String compressSpaces(String string, IStructuredFormatContraints formatContraints) {
		/*
		 * Note that the StructuredTextEditor supports mixed new line
		 * characters (CR, LF, CRLF) in one file. We have to handle that when
		 * we try to preserve blank lines.
		 */
		String[] stringArray = null;
		boolean clearAllBlankLines = formatContraints.getClearAllBlankLines();

		if (clearAllBlankLines)
			stringArray = StringUtils.asArray(string);
		else
			stringArray = StringUtils.asArray(string, DELIMITERS, true);

		StringBuffer compressedString = new StringBuffer();
		if (stringArray.length > 0) {
			boolean cr = false, lf = false, cr2 = false, nonSpace = true;

			if (stringArray[0].compareTo(CR) == 0)
				cr = true;
			else if (stringArray[0].compareTo(LF) == 0)
				lf = true;
			else if ((stringArray[0].compareTo(SPACE) != 0) && (stringArray[0].compareTo(TAB) != 0) && (stringArray[0].compareTo(FF) != 0)) {
				compressedString.append(stringArray[0]);
				nonSpace = true;
			}

			for (int i = 1; i < stringArray.length; i++) {
				if (stringArray[i].compareTo(CR) == 0) {
					if (cr && lf) {
						if (nonSpace) {
							compressedString.append(CR + LF);
							nonSpace = false;
						}
						compressedString.append(stringArray[i]);
						cr2 = true;
					}
					else if (cr) {
						if (nonSpace) {
							compressedString.append(CR);
							nonSpace = false;
						}
						compressedString.append(stringArray[i]);
						cr2 = true;
					}
					else
						cr = true;
				}
				else if (stringArray[i].compareTo(LF) == 0) {
					if (cr && lf && cr2) {
						compressedString.append(stringArray[i]);
					}
					else if (lf) {
						if (nonSpace) {
							compressedString.append(LF);
							nonSpace = false;
						}
						compressedString.append(stringArray[i]);
					}
					else
						lf = true;
				}
				else if ((stringArray[i].compareTo(SPACE) != 0) && (stringArray[i].compareTo(TAB) != 0) && (stringArray[i].compareTo(FF) != 0)) {
					if (compressedString.length() > 0)
						compressedString.append(SPACE);
					compressedString.append(stringArray[i]);

					cr = false;
					lf = false;
					cr2 = false;
					nonSpace = true;
				}
			}
		}

		return compressedString.toString();
	}

	protected void formatNode(IDOMNode node, IStructuredFormatContraints formatContraints) {
		// [111674] If inside xml:space="preserve" element, we bail
		if (formatContraints.getInPreserveSpaceElement())
			return;
		if (node != null) {
			IStructuredDocument doc = node.getStructuredDocument();

			int lineWidth = getFormatPreferences().getLineWidth();
			int currentAvailableLineWidth = computeAvailableLineWidth(doc, node.getStartOffset(), lineWidth);

			String nodeText = getNodeText(node);
			String compressedText = compressSpaces(nodeText, formatContraints);

			IDOMNode parentNode = (IDOMNode) node.getParentNode();

			if (((enoughSpace(parentNode, currentAvailableLineWidth, compressedText)) && (noSiblingsAndNoFollowingComment(node)) && !firstStructuredDocumentRegionContainsLineDelimiters(parentNode)) || node.getStartOffset() == 0) {
				handleNoReflow(node, doc, compressedText, parentNode);
			}
			else {
				// not enough space, need to reflow text
				String nodeIndentation = formatContraints.getCurrentIndent();
				currentAvailableLineWidth = lineWidth - getIndentationLength(nodeIndentation);
				List vector = reflowText(compressedText, currentAvailableLineWidth);
				int vectorSize = vector.size();
				StringBuffer reflowedTextBuffer = new StringBuffer();
				String lineDelimiter = getLineDelimiter(doc, node.getStartOffset());
				// handle first line specially to check for allowWhitespace
				if (vectorSize > 0) {
					// determines whether or not to allow whitespace if there
					// is an entity or cdata before it
					boolean allowWhitespace = true;
					// [206072] StringIndexOutOfBoundsException
					if (nodeText.length() == 0 || !Character.isWhitespace(nodeText.charAt(0))) {
						Node previousSibling = node.getPreviousSibling();
						if (previousSibling != null && (previousSibling.getNodeType() == Node.ENTITY_REFERENCE_NODE || previousSibling.getNodeType() == Node.CDATA_SECTION_NODE))
							allowWhitespace = false;
					}
					String theString = (String) vector.get(0);
					if (allowWhitespace) {
						reflowedTextBuffer.append(lineDelimiter);
						if (theString.trim().length() > 0)
							reflowedTextBuffer.append(nodeIndentation).append(theString);
					}
					else {
						reflowedTextBuffer.append(theString);
					}
				}
				// do the rest of the lines
				for (int i = 1; i < vectorSize; i++) {
					String theString = (String) vector.get(i);
					if (theString.trim().length() > 0)
						reflowedTextBuffer.append(lineDelimiter).append(nodeIndentation).append(theString);
					else
						reflowedTextBuffer.append(lineDelimiter);
				}
				String reflowedText = reflowedTextBuffer.toString();
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
						Node nextSibling = node.getNextSibling();
						if (nextSibling.getNodeType() == Node.COMMENT_NODE) {
							// add indentation to end if
							// currentTextEndsWithLineDelimiter
							// or followed by multiLineComment

							int indexOfLastLineDelimiter = StringUtils.indexOfLastLineDelimiter(nodeText);
							boolean currentTextEndsWithLineDelimiter = indexOfLastLineDelimiter != -1;
							if (currentTextEndsWithLineDelimiter) {
								// no more non blank character after the last
								// line delimiter
								currentTextEndsWithLineDelimiter = StringUtils.indexOfNonblank(nodeText, indexOfLastLineDelimiter) == -1;
							}

							String nodeValue = nextSibling.getNodeValue();
							boolean multiLineComment = StringUtils.containsLineDelimiter(nodeValue);

							if (currentTextEndsWithLineDelimiter || multiLineComment) {
								reflowedText = StringUtils.appendIfNotEndWith(reflowedText, lineDelimiter);
								reflowedText = StringUtils.appendIfNotEndWith(reflowedText, nodeIndentation);
							}
						}
						else if (nextSibling.getNodeType() == Node.ENTITY_REFERENCE_NODE || nextSibling.getNodeType() == Node.CDATA_SECTION_NODE) {
							int textLength = nodeText.length();
							if (textLength > 0 && Character.isWhitespace(nodeText.charAt(textLength - 1))) {
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

	/**
	 * Keeps text inline with its parent (no reflow necessary)
	 * 
	 * @param node
	 * @param doc
	 * @param compressedText
	 * @param parentNode
	 */
	private void handleNoReflow(IDOMNode node, IStructuredDocument doc, String compressedText, IDOMNode parentNode) {
		String nodeIndentation;
		// enough space and text has no line delimiters and (node has no
		// siblings or followed by inline comment) and
		// parentFirstStructuredDocumentRegionContainsLineDelimiters

		if (isEndTagMissing(parentNode)) {
			parentNode = (IDOMNode) parentNode.getParentNode();
			while (isEndTagMissing(parentNode))
				parentNode = (IDOMNode) parentNode.getParentNode();

			// add parent's indentation to end
			nodeIndentation = getNodeIndent(parentNode);
			String lineDelimiter = getLineDelimiter(doc, node.getStartOffset());
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

	/**
	 * Calculates if there is enough space on the current line for
	 * compressedText (and for its parent end tag)
	 * 
	 * @param parentNode
	 * @param currentAvailableLineWidth
	 * @param compressedText
	 * @return
	 */
	private boolean enoughSpace(IDOMNode parentNode, int currentAvailableLineWidth, String compressedText) {
		int parentEndTagLength = parentNode.getNodeName().length() + 3;
		return compressedText.length() <= (currentAvailableLineWidth - parentEndTagLength) && !StringUtils.containsLineDelimiter(compressedText);
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

	private String getLineDelimiter(IStructuredDocument doc, int nodeOffset) {
		int line = doc.getLineOfOffset(nodeOffset);
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

	private int computeAvailableLineWidth(IStructuredDocument doc, int nodeOffset, int lineWidth) {
		// compute current available line width
		int currentAvailableLineWidth = 0;
		try {
			int lineOffset = doc.getLineInformationOfOffset(nodeOffset).getOffset();
			String text = doc.get(lineOffset, nodeOffset - lineOffset);
			int usedWidth = getIndentationLength(text);
			currentAvailableLineWidth = lineWidth - usedWidth;
		}
		catch (BadLocationException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
		return currentAvailableLineWidth;
	}

}
