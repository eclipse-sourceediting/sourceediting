/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - Initial API and implementation, based on a patch
 *                           provided by Nik Matyushev in bug 195262.
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.doubleclick;

import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.doubleclick.XMLDoubleClickStrategy;

/**
 * XSLDoubleClickStrategy extends the XMLDoubleclickStrategy to take into
 * account those areas that may be involved in XPath Expressions.
 * 
 * 
 * @author dcarver
 * 
 */
public class XSLDoubleClickStrategy extends XMLDoubleClickStrategy {
	protected static final char[] XML_DELIMITERS = { ' ', '\'', '\"', '[', ']',
			'|', '(', ')', '{', '}', '=', '!' };
	protected static final char[] XML_PARENTHESIS = { '[', ']', '(', ')', '{',
			'}' };

	@Override
	protected Point getWord(String string, int cursor) {
		if (string == null) {
			return null;
		}

		int wordStart = 0;
		int wordEnd = string.length();

		wordStart = startOfWord(string, cursor, wordStart);
		wordEnd = endOfWord(string, cursor, wordEnd);
		wordEnd = checkXPathExpression(string, wordEnd);

		if ((wordStart == wordEnd) && !isQuoted(string)) {
			wordStart = 0;
			wordEnd = string.length();
		}

		return new Point(wordStart, wordEnd);
	}

	private int checkXPathExpression(String string, int wordEnd) {
		if (wordEnd < string.length() - 1) {
			// check paranthesis
			int[] flags = new int[XML_PARENTHESIS.length / 2];
			boolean found = false;
			int pos = wordEnd;
			do {
				char cur = string.charAt(pos);
				for (int i = 0; i < XML_PARENTHESIS.length; i++) {
					if (cur == XML_PARENTHESIS[i]) {
						flags[i / 2] += (i % 2 == 0 ? 1 : -1);
						found = true;
					}
				}
				boolean stop = true;
				boolean unbalanced = false;
				for (int i = 0; i < flags.length; i++) {
					stop = stop && flags[i] == 0;
					unbalanced |= flags[i] < 0;
				}

				if (!unbalanced) {
					pos++;
				}
				if (stop | unbalanced) {
					break;
				}
			} while (pos < string.length());
			
			if (found) {
				wordEnd = Math.min(string.length() - 1, pos);
			}
		}
		return wordEnd;
	}

	private int endOfWord(String string, int cursor, int wordEnd) {
		for (int i = 0; i < XML_DELIMITERS.length; i++) {
			char delim = XML_DELIMITERS[i];
			int end = string.indexOf(delim, cursor);
			wordEnd = Math.min(wordEnd, end == -1 ? string.length() : end);
		}

		if (wordEnd == string.length()) {
			wordEnd = cursor;
		}
		return wordEnd;
	}

	protected int startOfWord(String string, int cursor, int wordStart) {
		for (int i = 0; i < XML_DELIMITERS.length; i++) {
			char delim = XML_DELIMITERS[i];
			wordStart = Math.max(wordStart, string.lastIndexOf(delim,
					cursor - 1));
		}

		if (wordStart == -1) {
			wordStart = cursor;
		} else {
			wordStart++;
		}
		return wordStart;
	}

	protected boolean isQuoted(String string) {
		if ((string == null) || (string.length() < 2)) {
			return false;
		}

		int lastIndex = string.length() - 1;
		char firstChar = string.charAt(0);
		char lastChar = string.charAt(lastIndex);

		return (((firstChar == SINGLE_QUOTE) && (lastChar == SINGLE_QUOTE)) || ((firstChar == DOUBLE_QUOTE) && (lastChar == DOUBLE_QUOTE)));
	}

	protected void processElementAttrEqualsDoubleClicked2Times() {
		int prevRegionOffset = fStructuredDocumentRegion
				.getStartOffset(fTextRegion) - 1;
		ITextRegion prevRegion = fStructuredDocumentRegion
				.getRegionAtCharacterOffset(prevRegionOffset);
		int nextRegionOffset = fStructuredDocumentRegion
				.getEndOffset(fTextRegion);
		ITextRegion nextRegion = fStructuredDocumentRegion
				.getRegionAtCharacterOffset(nextRegionOffset);

		if ((prevRegion != null)
				&& (prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)
				&& (nextRegion != null)
				&& (nextRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
			fStructuredTextViewer.setSelectedRange(fStructuredDocumentRegion
					.getStartOffset(prevRegion), nextRegion.getTextEnd()
					- prevRegion.getStart());
		}
	}

	protected void processElementAttrNameDoubleClicked2Times() {
		int nextRegionOffset = fStructuredDocumentRegion
				.getEndOffset(fTextRegion);
		ITextRegion nextRegion = fStructuredDocumentRegion
				.getRegionAtCharacterOffset(nextRegionOffset);

		if (nextRegion != null) {
			nextRegionOffset = fStructuredDocumentRegion
					.getEndOffset(nextRegion);
			nextRegion = fStructuredDocumentRegion
					.getRegionAtCharacterOffset(nextRegionOffset);
			if ((nextRegion != null)
					&& (nextRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStartOffset(fTextRegion),
						nextRegion.getTextEnd() - fTextRegion.getStart());
			} else {
				// attribute has no value
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStart(),
						fStructuredDocumentRegion.getLength());
				fDoubleClickCount = 0;
			}
		}
	}

	protected void processElementAttrValueDoubleClicked() {
		String regionText = fStructuredDocumentRegion.getText(fTextRegion);

		if (fDoubleClickCount == 1) {
			Point word = getWord(regionText, fCaretPosition
					- fStructuredDocumentRegion.getStartOffset(fTextRegion));
			if (word.x == word.y) { // no word found; select whole region
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStartOffset(fTextRegion),
						regionText.length());
				fDoubleClickCount++;
			} else {
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStartOffset(fTextRegion)
								+ word.x, word.y - word.x);
			}
		} else if (fDoubleClickCount == 2) {
			if (isQuoted(regionText)) {
				// ==> // Point word = getWord(regionText, fCaretPosition -
				// fStructuredDocumentRegion.getStartOffset(fTextRegion));
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStartOffset(fTextRegion),
						regionText.length());
			} else {
				processElementAttrValueDoubleClicked2Times();
			}
		} else if (fDoubleClickCount == 3) {
			if (isQuoted(regionText)) {
				processElementAttrValueDoubleClicked2Times();
			} else {
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStart(),
						fStructuredDocumentRegion.getLength());
				fDoubleClickCount = 0;
			}
		} else { // fDoubleClickCount == 4
			fStructuredTextViewer.setSelectedRange(fStructuredDocumentRegion
					.getStart(), fStructuredDocumentRegion.getLength());
			fDoubleClickCount = 0;
		}
	}

	protected void processElementAttrValueDoubleClicked2Times() {
		int prevRegionOffset = fStructuredDocumentRegion
				.getStartOffset(fTextRegion) - 1;
		ITextRegion prevRegion = fStructuredDocumentRegion
				.getRegionAtCharacterOffset(prevRegionOffset);

		if (prevRegion != null) {
			prevRegionOffset = fStructuredDocumentRegion
					.getStartOffset(prevRegion) - 1;
			prevRegion = fStructuredDocumentRegion
					.getRegionAtCharacterOffset(prevRegionOffset);
			if ((prevRegion != null)
					&& (prevRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStartOffset(prevRegion),
						fTextRegion.getTextEnd() - prevRegion.getStart());
			}
		}
	}

	protected void processElementDoubleClicked() {
		if (fTextRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			processElementAttrValueDoubleClicked(); // special handling for
		} else {
			if (fDoubleClickCount == 1) {
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStart()
								+ fTextRegion.getStart(), fTextRegion
								.getTextLength());
			} else if (fDoubleClickCount == 2) {
				if (fTextRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
					processElementAttrNameDoubleClicked2Times();
				} else if (fTextRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
					processElementAttrEqualsDoubleClicked2Times();
				} else {
					fStructuredTextViewer.setSelectedRange(
							fStructuredDocumentRegion.getStart(),
							fStructuredDocumentRegion.getLength());
					fDoubleClickCount = 0;
				}
			} else { // fDoubleClickCount == 3
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStart(),
						fStructuredDocumentRegion.getLength());
				fDoubleClickCount = 0;
			}
		}
	}

	protected void processTextDoubleClicked() {
		if (fDoubleClickCount == 1) {
			super.doubleClicked(fStructuredTextViewer);

			Point selectedRange = fStructuredTextViewer.getSelectedRange();
			if ((selectedRange.x == fStructuredDocumentRegion
					.getStartOffset(fTextRegion))
					&& (selectedRange.y == fTextRegion.getTextLength())) {
				// only one word in region, skip one level of double click
				// selection
				fDoubleClickCount++;
			}
		} else if (fDoubleClickCount == 2) {
			if (fTextRegion.getType() == DOMRegionContext.UNDEFINED) {
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStart(),
						fStructuredDocumentRegion.getLength());
				fDoubleClickCount = 0;
			} else {
				if (isQuoted(fStructuredDocumentRegion.getFullText(fTextRegion))) {
					fStructuredTextViewer.setSelectedRange(
							fStructuredDocumentRegion
									.getStartOffset(fTextRegion) + 1,
							fTextRegion.getTextLength() - 2);
				} else {
					fStructuredTextViewer.setSelectedRange(
							fStructuredDocumentRegion
									.getStartOffset(fTextRegion), fTextRegion
									.getTextLength());
				}
			}
		} else {
			if ((fDoubleClickCount == 3)
					&& isQuoted(fStructuredDocumentRegion
							.getFullText(fTextRegion))) {
				fStructuredTextViewer.setSelectedRange(
						fStructuredDocumentRegion.getStartOffset(fTextRegion),
						fTextRegion.getTextLength());
			} else {
				if ((fDoubleClickCount == 3)
						&& isQuoted(fStructuredDocumentRegionText)) {
					fStructuredTextViewer.setSelectedRange(
							fStructuredDocumentRegion.getStart() + 1,
							fStructuredDocumentRegion.getLength() - 2);
				} else {
					fStructuredTextViewer.setSelectedRange(
							fStructuredDocumentRegion.getStart(),
							fStructuredDocumentRegion.getLength());
					fDoubleClickCount = 0;
				}
			}
		}
	}

	public void setModel(IStructuredModel structuredModel) {
		fStructuredModel = structuredModel;
	}

	protected void updateDoubleClickCount(int caretPosition) {
		if (fCaretPosition == caretPosition) {
			if (fStructuredDocumentRegion != null) {
				fDoubleClickCount++;
			} else {
				fDoubleClickCount = 1;
			}
		} else {
			fCaretPosition = caretPosition;
			fDoubleClickCount = 1;
		}
	}

	protected void updateStructuredDocumentRegion() {
		fStructuredDocumentRegion = fStructuredModel.getStructuredDocument()
				.getRegionAtCharacterOffset(fCaretPosition);
		if (fStructuredDocumentRegion != null) {
			fStructuredDocumentRegionText = fStructuredDocumentRegion.getText();
		} else {
			fStructuredDocumentRegionText = ""; //$NON-NLS-1$
		}
	}

	protected void updateTextRegion() {
		if (fStructuredDocumentRegion != null) {
			fTextRegion = fStructuredDocumentRegion
					.getRegionAtCharacterOffset(fCaretPosition);
			// if fTextRegion is null, it means we are at just past the last
			// fStructuredDocumentRegion,
			// at the very end of the document, so we'll use the last text
			// region in the document
			if (fTextRegion == null) {
				fTextRegion = fStructuredDocumentRegion
						.getRegionAtCharacterOffset(fCaretPosition - 1);
			}
		} else {
			fTextRegion = null;
		}
	}
}
