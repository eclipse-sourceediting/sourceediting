/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.util;



/**
 * Collection of text functions.
 */
// This class was originally copied from org.eclipse.jface.text, and made public
public class TextUtilities {

	/**
	 * Determines which one of fgDelimiters appears first in the text. If none of them the
	 * hint is returned.
	 * @deprecated - no longer needed, at least not here
	 */
	public static String determineLineDelimiter(StringBuffer textBuffer, String[] possibles, String hint) {
		try {
			// TODO: avoid use of String instance
			String text = textBuffer.toString();
			int[] info = indexOf(possibles, text, 0);
			return possibles[info[1]];
		}
		catch (ArrayIndexOutOfBoundsException x) {
		}
		return hint;
	}

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
	 * Returns the position in the string greater than offset
	 * of the longest matching search string.
	 */
	public static int[] indexOf(String[] searchStrings, String text, int offset) {

		int[] result = {-1, -1};

		for (int i = 0; i < searchStrings.length; i++) {
			int index = text.indexOf(searchStrings[i], offset);
			if (index >= 0) {

				if (result[0] == -1) {
					result[0] = index;
					result[1] = i;
				}
				else if (index < result[0]) {
					result[0] = index;
					result[1] = i;
				}
				else if (index == result[0] && searchStrings[i].length() > searchStrings[result[1]].length()) {
					result[0] = index;
					result[1] = i;
				}
			}
		}

		return result;

	}
}
