/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;

class CharOperation {

	private CharOperation() {
	}

	static int indexOf(char[] array, char c) {
		return indexOf(array, c, 0);
	}

	static int indexOf(char[] array, char c, int start) {
		for (int i = start; i < array.length; i++) {
			if (array[i] == c)
				return i;
		}
		return -1;
	}


	/**
	 * note: This method taken from org.eclipse.jdt.core.compiler.CharOperation
	 * 
	 * Answers true if the two arrays are identical character by character, otherwise false.
	 * The equality is case sensitive.
	 * <br>
	 * <br>
	 * For example:
	 * <ol>
	 * <li><pre>
	 *    first = null
	 *    second = null
	 *    result => true
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    first = { }
	 *    second = null
	 *    result => false
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    first = { 'a' }
	 *    second = { 'a' }
	 *    result => true
	 * </pre>
	 * </li>
	 * <li><pre>
	 *    first = { 'a' }
	 *    second = { 'A' }
	 *    result => false
	 * </pre>
	 * </li>
	 * </ol>
	 * @param first the first array
	 * @param second the second array
	 * @return true if the two arrays are identical character by character, otherwise false
	 */
	public static final boolean equals(char[] first, char[] second, boolean ignoreCase) {
		if (first == second)
			return true;
		if (first == null || second == null)
			return false;
		if (first.length != second.length)
			return false;

		for (int i = first.length; --i >= 0;) {
			if (ignoreCase) {
				if (Character.toUpperCase(first[i]) != Character.toUpperCase(second[i])) {
					return false;
				}
			}
			else {
				if (first[i] != second[i]) {
					return false;
				}
			}
		}
		return true;
	}
}
