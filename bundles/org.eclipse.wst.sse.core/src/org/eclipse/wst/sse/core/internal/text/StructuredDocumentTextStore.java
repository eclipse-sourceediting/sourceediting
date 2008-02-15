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
 *     Viacheslav Kabanovich/Exadel 97817 Wrong algoritm in class StructuredDocumentTextStore
 *     			https://bugs.eclipse.org/bugs/show_bug.cgi?id=97817
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.text;

import org.eclipse.jface.text.GapTextStore;
import org.eclipse.jface.text.ITextStore;

public class StructuredDocumentTextStore implements ITextStore, CharSequence, IRegionComparible {

	private GapTextStore fInternalStore;

	/**
	 *  
	 */
	public StructuredDocumentTextStore() {
		this(50, 300);
	}

	/**
	 * @param lowWatermark
	 * @param highWatermark
	 */
	public StructuredDocumentTextStore(int lowWatermark, int highWatermark) {
		super();
		fInternalStore = new GapTextStore(lowWatermark, highWatermark);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#charAt(int)
	 */
	public char charAt(int index) {
		return fInternalStore.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextStore#get(int)
	 */
	public char get(int offset) {

		return fInternalStore.get(offset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextStore#get(int, int)
	 */
	public String get(int offset, int length) {

		return fInternalStore.get(offset, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextStore#getLength()
	 */
	public int getLength() {

		return fInternalStore.getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#length()
	 */
	public int length() {

		return fInternalStore.getLength();
	}

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
		int end = offset + stringToCompare.length();
		for (int i = offset; i < end; i++) {
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
		int end = offset + stringToCompare.length();
		for (int i = offset; i < end; i++) {
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
	 * @see org.eclipse.jface.text.ITextStore#replace(int, int,
	 *      java.lang.String)
	 */
	public void replace(int offset, int length, String text) {
		fInternalStore.replace(offset, length, text);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextStore#set(java.lang.String)
	 */
	public void set(String text) {
		fInternalStore.set(text);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.CharSequence#subSequence(int, int)
	 */
	public CharSequence subSequence(int start, int end) {
		// convert 'end' to 'length'
		return fInternalStore.get(start, end - start);
	}
}
