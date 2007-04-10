/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.commentelement.util;



/**
 */
public class TagScanner {

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

	private int length = 0;
	private int memOffset = 0;
	private int offset = 0;
	private boolean oneLine = false;

	private String tag = null;

	/**
	 */
	public TagScanner(String tag, int offset) {
		super();

		this.tag = tag;
		this.offset = offset;
		this.memOffset = -1;
		if (tag != null)
			this.length = tag.length();
	}

	/**
	 */
	public TagScanner(String tag, int offset, boolean oneLine) {
		this(tag, offset);

		this.oneLine = oneLine;
	}

	/**
	 */
	public int getNextOffset() {
		int i;
		char c;
		for (i = offset; i < length; i++) {
			c = tag.charAt(i);
			if (isEnd(c))
				break;
			if (isQuote(c)) {
				i++;
				break;
			}
			if (!isSpace(c) && !isEqual(c))
				break;
		}
		return i;
	}

	/**
	 */
	public int getOffset() {
		return this.memOffset;
	}

	/**
	 */
	private final boolean isEnd(char c) {
		return (this.oneLine && (c == '\r' || c == '\n'));
	}

	/**
	 */
	public boolean isNewLine() {
		if (oneLine)
			return false;
		char c;
		for (int i = memOffset - 1; 0 <= i; i--) {
			c = tag.charAt(i);
			if (c == '\r' || c == '\n')
				return true;
			if (!isSpace(c))
				return false;
		}
		return false;
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
	public String nextName() {
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

		this.memOffset = nameOffset;
		return this.tag.substring(nameOffset, this.offset);
	}

	/**
	 */
	public String nextValue() {
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
			} else {
				if (c == quote)
					break;
			}
		}
		int valueEnd = this.offset;
		if (quote != 0 && this.offset < this.length)
			this.offset++;
		if (valueEnd == valueOffset)
			return null;

		this.memOffset = valueOffset;
		return this.tag.substring(valueOffset, valueEnd);
	}
}
