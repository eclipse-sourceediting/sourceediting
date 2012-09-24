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
package org.eclipse.wst.sse.core.internal.document;


/**
 * Collection of text functions.
 * 
 * @deprecated - marked as deprecated to remind us to phase this out (and/or
 *             move to "finished" version).
 */
public class TextUtilities {

	/**
	 * @deprecated if possible, its best to use
	 *             IDocument.getLegalLineDelimiters()
	 */
	public final static String[] fgDelimiters = new String[]{"\n", "\r", "\r\n"};//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$

	/**
	 * Determines which one of fgDelimiters appears first in the text. If none
	 * of them the hint is returned.
	 */
	public static String determineLineDelimiter(StringBuffer textBuffer, String[] possibles, String hint) {
		try {
			// TODO: avoid use of String instance
			String text = textBuffer.toString();
			int[] info = indexOf(possibles, text, 0);
			return possibles[info[1]];
		} catch (ArrayIndexOutOfBoundsException x) {
		}
		return hint;
	}

	/**
	 * Returns the position in the string greater than offset of the longest
	 * matching search string.
	 */
	private static int[] indexOf(String[] searchStrings, String text, int offset) {

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
