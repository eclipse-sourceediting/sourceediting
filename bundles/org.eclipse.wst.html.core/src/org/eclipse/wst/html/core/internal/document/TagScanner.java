/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.document;



/**
 */
class TagScanner {

	private String tag = null;
	private int offset = 0;
	private int length = 0;
	private boolean oneLine = false;

	/**
	 */
	TagScanner(String tag, int offset) {
		super();

		this.tag = tag;
		this.offset = offset;
		if (tag != null)
			this.length = tag.length();
	}

	/**
	 */
	TagScanner(String tag, int offset, boolean oneLine) {
		this(tag, offset);

		this.oneLine = oneLine;
	}

	/**
	 */
	int getNextOffset() {
		for (; this.offset < this.length; this.offset++) {
			char c = this.tag.charAt(this.offset);
			if (!isEnd(c))
				break;
		}
		return this.offset;
	}

	/**
	 */
	int getOffset() {
		return this.offset;
	}

	/**
	 */
	private final boolean isEnd(char c) {
		return (this.oneLine && (c == '\r' || c == '\n'));
	}

	/**
	 */
	private static boolean isEqual(char c) {
		return (c == '=');
	}

	/**
	 */
	private static boolean isQuote(char c) {
		return (c == '"' || c == '\'');
	}

	/**
	 */
	private static boolean isSpace(char c) {
		return Character.isWhitespace(c);
	}

	/**
	 */
	private char nextChar() {
		for (; this.offset < this.length; this.offset++) {
			char c = this.tag.charAt(this.offset);
			if (isEnd(c))
				break;
			if (!isSpace(c))
				return c;
		}
		return 0;
	}

	/**
	 */
	String nextName() {
		if (this.tag == null)
			return null;
		if (this.offset >= this.length)
			return null;

		if (nextChar() == 0)
			return null;

		int nameOffset = this.offset;
		for (; this.offset < this.length; this.offset++) {
			char c = this.tag.charAt(this.offset);
			if (isEnd(c) || isSpace(c))
				break;
			if (isEqual(c) && this.offset > nameOffset)
				break;
		}
		if (this.offset == nameOffset)
			return null;

		return this.tag.substring(nameOffset, this.offset);
	}

	/**
	 */
	String nextValue() {
		if (this.tag == null)
			return null;
		if (this.offset >= this.length)
			return null;

		char seperator = nextChar();
		if (!isEqual(seperator))
			return null;
		this.offset++; // skip '='
		char quote = nextChar();
		if (quote == 0)
			return null;
		if (isQuote(quote))
			this.offset++;
		else
			quote = 0;

		int valueOffset = this.offset;
		for (; this.offset < this.length; this.offset++) {
			char c = this.tag.charAt(this.offset);
			if (isEnd(c)) {
				quote = 0;
				break;
			}
			if (quote == 0) {
				if (isSpace(c))
					break;
			}
			else {
				if (c == quote)
					break;
			}
		}
		int valueEnd = this.offset;
		if (quote != 0 && this.offset < this.length)
			this.offset++;
		if (valueEnd == valueOffset)
			return null;

		return this.tag.substring(valueOffset, valueEnd);
	}
}
