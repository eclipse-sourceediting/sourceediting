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

/**
 * This is a convience or utility class that allows you to make a copy of a
 * part of a larger text store, but have the copy behave as if it was the
 * larger text store.
 * 
 * In other words, it provides a subset of a larger document, that "looks like"
 * the orginal document. That is, "looks like" in terms of offsets and lengths.
 * Valid access can only be made to data between the orignal offsets, even
 * though those offsets are in the same units at the original, and even though
 * the length appears to be long.
 * 
 * For example, if a subsettext store is created for the def part of abcdefgh,
 * then get(3,5) is valid, getLength is 8. Any other access, such as
 * getChar(2), would be invalid.
 */
import org.eclipse.jface.text.ITextStore;

/**
 * Similar to basics of IDocument, but the offsets are mapped from coordinates
 * of underlying storage to a "virtual" document.
 */
public class SubSetTextStore implements ITextStore {
	private int pseudoBeginOffset; // maps to "zero" postion of new text
	//private int pseudoEndOffset;
	private int pseudoLength; // length of old/original document
	private StringBuffer stringBuffer = new StringBuffer();

	/**
	 * SubSetTextStore constructor comment.
	 * 
	 * @param initialContent
	 *            java.lang.String
	 */
	public SubSetTextStore(String initialContent, int beginOffset, int endOffset, int originalDocumentLength) {
		super();
		pseudoBeginOffset = beginOffset;
		//pseudoEndOffset = endOffset;
		// used to be originalDocument.getLength ... not sure if used, or
		// which
		// is right
		pseudoLength = originalDocumentLength;
		stringBuffer = new StringBuffer(initialContent);
		//set(initialContent);
	}

	// this is our "private" get, which methods in this class should
	// use to get using "real" coordinates of underlying representation.
	private String _get(int begin, int length) {
		char[] chars = new char[length];
		int srcEnd = begin + length;
		stringBuffer.getChars(begin, srcEnd, chars, 0);
		return new String(chars);
	}

	public char get(int offset) {
		return stringBuffer.charAt(offset - pseudoBeginOffset);
	}

	/**
	 * @return java.lang.String
	 * @param begin
	 *            int
	 * @param end
	 *            int
	 */
	public String get(int begin, int length) {
		// remap the begin and end to "appear" to be in the
		// same coordinates of the original parentDocument
		return _get(begin - pseudoBeginOffset, length);
	}

	/**
	 * @return java.lang.String
	 * @param begin
	 *            int
	 * @param end
	 *            int
	 */
	public char getChar(int pos) {
		// remap the begin and end to "appear" to be in the
		// same coordinates of the original parentDocument
		return get(pos - pseudoBeginOffset);
	}

	/**
	 * We redefine getLength so its not the true length of this sub-set
	 * document, but the length of the original. This is needed, as a simple
	 * example, if you want to see if the pseudo end is equal the last
	 * position of the original document.
	 */
	public int getLength() {
		return pseudoLength;
	}

	/**
	 * Returns the length as if considered a true, standalone document
	 */
	public int getTrueLength() {
		return stringBuffer.length();
	}

	public void replace(int begin, int length, String changes) {
		// remap the begin and end to "appear" to be in the
		// same coordinates of the original parentDocument
		int end = begin + length;
		stringBuffer.replace(begin - pseudoBeginOffset, end, changes);
	}

	public void set(String text) {
		stringBuffer.setLength(0);
		stringBuffer.append(text);
	}
}
