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
	static final char[] XML_DELIMITERS = { ' ', '\'', '\"', '[', ']',
			'|', '(', ')', '{', '}', '=', '!' };
	static final char[] XML_PARENTHESIS = { '[', ']', '(', ')', '{',
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

}
