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
package org.eclipse.wst.sse.core.internal.util;



/**
 * Collection of text functions.
 * @deprecated - the JFace class is public in 3.1
 */
// This class was originally copied from org.eclipse.jface.text, and made
// public
public class TextUtilities {
	/**
	 * Returns whether the text ends with one of the given search strings.
	 */
	public static boolean endsWith(String[] searchStrings, String text) {
		for (int i = 0; i < searchStrings.length; i++) {
			if (text.endsWith(searchStrings[i]))
				return true;
		}
		return false;
	}

	/**
	 * Returns the position in the string greater than offset of the longest
	 * matching search string.
	 */
	public static int[] indexOf(String[] searchStrings, String text, int offset) {

		int[] result = {-1, -1};

		for (int i = 0; i < searchStrings.length; i++) {
			int index = text.indexOf(searchStrings[i], offset);
			if (index >= 0) {

				if (result[0] == -1) {
					result[0] = index;
					result[1] = i;
				} else if (index < result[0]) {
					result[0] = index;
					result[1] = i;
				} else if (index == result[0] && searchStrings[i].length() > searchStrings[result[1]].length()) {
					result[0] = index;
					result[1] = i;
				}
			}
		}

		return result;

	}
}
