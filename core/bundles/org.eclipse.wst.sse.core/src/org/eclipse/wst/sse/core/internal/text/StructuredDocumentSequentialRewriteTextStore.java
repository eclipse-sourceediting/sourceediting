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
package org.eclipse.wst.sse.core.internal.text;

import org.eclipse.jface.text.ITextStore;
import org.eclipse.jface.text.SequentialRewriteTextStore;

public class StructuredDocumentSequentialRewriteTextStore extends SequentialRewriteTextStore implements CharSequence, IRegionComparible {

	/**
	 * @param source
	 */
	public StructuredDocumentSequentialRewriteTextStore(ITextStore source) {
		super(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#charAt(int)
	 */
	public char charAt(int index) {
		return get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#length()
	 */
	public int length() {
		return getLength();
	}

	/**
	 * @param c
	 * @param d
	 * @return
	 */
	private boolean matchesIgnoreCase(char c1, char c2) {
		// we check both case conversions to handle those few cases,
		// in languages such as Turkish, which have some characters
		// which sort of have 3 cases.
		boolean result = false;
		if (Character.toUpperCase(c1) == Character.toUpperCase(c2))
			result = true;
		else if (Character.toLowerCase(c1) == Character.toLowerCase(c2))
			result = true;
		return result;
	}

	public boolean regionMatches(int offset, int length, String stringToCompare) {
		boolean result = false;
		int compareLength = stringToCompare.length();
		if (compareLength == length) {
			int endOffset = offset + length;
			if (endOffset <= length()) {
				result = regionMatches(offset, stringToCompare);
			}
		}

		return result;
	}

	/**
	 * This method assumes all lengths have been checked and fall withint
	 * exceptable limits
	 * 
	 * @param offset
	 * @param stringToCompare
	 * @return
	 */
	private boolean regionMatches(int offset, String stringToCompare) {
		boolean result = true;
		int stringOffset = 0;
		int len = stringToCompare.length();
		for (int i = offset; i < len; i++) {
			if (charAt(i) != stringToCompare.charAt(stringOffset++)) {
				result = false;
				break;
			}
		}
		return result;
	}

	public boolean regionMatchesIgnoreCase(int offset, int length, String stringToCompare) {
		boolean result = false;
		int compareLength = stringToCompare.length();
		if (compareLength == length) {
			int endOffset = offset + length;
			if (endOffset <= length()) {
				result = regionMatchesIgnoreCase(offset, stringToCompare);
			}
		}

		return result;
	}

	private boolean regionMatchesIgnoreCase(int offset, String stringToCompare) {
		boolean result = true;
		int stringOffset = 0;
		int len = stringToCompare.length();
		for (int i = offset; i < len; i++) {
			if (!matchesIgnoreCase(charAt(i), stringToCompare.charAt(stringOffset++))) {
				result = false;
				break;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#subSequence(int, int)
	 */
	public CharSequence subSequence(int start, int end) {

		return get(start, end - start);
	}

}
