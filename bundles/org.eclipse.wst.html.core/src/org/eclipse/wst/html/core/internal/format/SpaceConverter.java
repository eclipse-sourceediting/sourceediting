/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.format;



final class SpaceConverter {

	private String source = null;
	private int length = 0;
	private int startOffset = 0;
	private int endOffset = 0;
	private int spaceCount = 0;
	private int wordCount = 0;
	private StringBuffer buffer = null;
	private int lastOffset = 0;
	private boolean keepBlankLines = false;

	/**
	 */
	public SpaceConverter(String source) {
		super();

		if (source == null) {
			this.source = new String();
		}
		else {
			this.source = source;
			this.length = source.length();
		}
	}

	/**
	 */
	public SpaceConverter(String source, boolean keepBlankLines) {
		super();

		if (source == null) {
			this.source = new String();
		}
		else {
			this.source = source;
			this.length = source.length();
		}
		this.keepBlankLines = keepBlankLines;
	}

	/**
	 */
	public void compressSpaces() {
		if (this.spaceCount == 0)
			return;
		if (this.spaceCount == 1 && this.source.charAt(this.startOffset) == ' ')
			return;

		if (this.buffer == null)
			this.buffer = new StringBuffer();
		if (this.startOffset > this.lastOffset) {
			this.buffer.append(this.source.substring(this.lastOffset, this.startOffset));
		}

		this.buffer.append(' ');

		this.lastOffset = this.startOffset + this.spaceCount;
	}

	/**
	 */
	public String getSource() {
		if (this.buffer == null)
			this.buffer = new StringBuffer();
		if (this.length > this.lastOffset) {
			this.buffer.append(this.source.substring(this.lastOffset, this.length));
		}
		return this.buffer.toString();
	}

	/**
	 */
	public int getSpaceCount() {
		return this.spaceCount;
	}

	/**
	 */
	public boolean hasSpaces() {
		return (this.spaceCount > 0);
	}

	/**
	 */
	public boolean isModified() {
		return (this.buffer != null);
	}

	/**
	 * Add number of the old blank lines to new space string
	 */
	private static String mergeBlankLines(String newSpaces, String oldSpaces) {
		if (newSpaces == null || newSpaces.length() == 0)
			return newSpaces;
		if (oldSpaces == null)
			return newSpaces;

		// count old new lines
		int newLineCount = 0;
		int oldLength = oldSpaces.length();
		for (int i = 0; i < oldLength; i++) {
			char c = oldSpaces.charAt(i);
			if (c == '\r') {
				newLineCount++;
				if (i + 1 < oldLength) {
					c = oldSpaces.charAt(i + 1);
					if (c == '\n')
						i++;
				}
			}
			else {
				if (c == '\n')
					newLineCount++;
			}
		}
		if (newLineCount < 2)
			return newSpaces; // no blank line

		// here assuming newSpaces starts with a new line if any
		String delim = null;
		char d = newSpaces.charAt(0);
		if (d == '\r') {
			if (newSpaces.length() > 1 && newSpaces.charAt(1) == '\n')
				delim = "\r\n";//$NON-NLS-1$
			else
				delim = "\r";//$NON-NLS-1$
		}
		else {
			if (d == '\n')
				delim = "\n";//$NON-NLS-1$
			else
				return newSpaces; // no new line
		}

		newLineCount--;
		StringBuffer buffer = new StringBuffer(newSpaces.length() + newLineCount * 2);
		while (newLineCount > 0) {
			buffer.append(delim);
			newLineCount--;
		}
		buffer.append(newSpaces);
		return buffer.toString();
	}

	/**
	 */
	public int nextWord() {
		if (this.endOffset == this.length) {
			this.startOffset = this.endOffset;
			this.spaceCount = 0;
			this.wordCount = 0;
			return 0;
		}

		this.startOffset = this.endOffset;
		int i = this.startOffset;
		for (; i < this.length; i++) {
			if (!Character.isWhitespace(this.source.charAt(i)))
				break;
		}
		this.spaceCount = i - this.startOffset;

		int wordOffset = i;
		for (; i < this.length; i++) {
			if (Character.isWhitespace(this.source.charAt(i)))
				break;
		}
		this.wordCount = i - wordOffset;
		this.endOffset = i;

		return this.wordCount;
	}

	/**
	 */
	public void replaceSpaces(String spaces) {
		int spaceLength = (spaces != null ? spaces.length() : 0);
		String oldSpaces = null;
		if (spaceLength == this.spaceCount) {
			if (spaceLength == 0)
				return;
			if (this.startOffset == 0) {
				if (this.source.startsWith(spaces))
					return;
			}
			else if (this.endOffset == this.length) {
				if (this.source.endsWith(spaces))
					return;
			}
			else {
				int textOffset = this.startOffset + this.spaceCount;
				oldSpaces = this.source.substring(this.startOffset, textOffset);
				if (oldSpaces.equals(spaces))
					return;
			}
		}
		if (this.keepBlankLines && this.spaceCount > 0) {
			if (oldSpaces == null) {
				int textOffset = this.startOffset + this.spaceCount;
				oldSpaces = this.source.substring(this.startOffset, textOffset);
			}
			if (oldSpaces != null) {
				spaces = mergeBlankLines(spaces, oldSpaces);
				if (oldSpaces.equals(spaces))
					return;
			}
		}

		if (this.buffer == null)
			this.buffer = new StringBuffer();
		if (this.startOffset > this.lastOffset) {
			this.buffer.append(this.source.substring(this.lastOffset, this.startOffset));
		}

		if (spaceLength > 0)
			this.buffer.append(spaces);

		this.lastOffset = this.startOffset + this.spaceCount;
	}
}