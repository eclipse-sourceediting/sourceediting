/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.tests.internal;

import java.io.IOException;
import java.io.StringReader;

/**
 * @see org.eclipse.wst.xml.core.tests.util.StringCompareUtil
 * @see org.eclipse.wst.sse.core.utils.StringUtils
 */
public class StringUtils {

	/**
	 * Replace matching literal portions of a string with another string
	 */
	public static String replace(String aString, String source, String target) {
		if (aString == null)
			return null;
		String normalString = ""; //$NON-NLS-1$
		int length = aString.length();
		int position = 0;
		int previous = 0;
		int spacer = source.length();
		StringBuffer sb = new StringBuffer(normalString);
		while (position + spacer - 1 < length && aString.indexOf(source, position) > -1) {
			position = aString.indexOf(source, previous);
			sb.append(normalString);
			sb.append(aString.substring(previous, position));
			sb.append(target);
			position += spacer;
			previous = position;
		}
		sb.append(aString.substring(position, aString.length()));
		normalString = sb.toString();

		return normalString;
	}
	
	public static boolean equalsIgnoreLineSeperator(String string1, String string2) {

		if (string1 == null)
			return false;
		if (string2 == null)
			return false;

		StringReader s1Reader = new StringReader(string1);
		StringReader s2Reader = new StringReader(string2);

		// assume true unless find evidence to the contrary
		boolean result = true;
		int s1Char = -1;
		int s2Char = -1;
		do {

			s1Char = getNextChar(s1Reader);

			s2Char = getNextChar(s2Reader);

			if (s1Char != s2Char) {
				result = false;
				break;
			}
		}
		while (s1Char != -1 && s2Char != -1);

		return result;
	}

	/**
	 * Method getNextChar.
	 * @param s1Reader
	 * @return char
	 */
	private static int getNextChar(StringReader reader) {
		int nextChar = -1;
		try {
			nextChar = reader.read();
			while (isEOL(nextChar)) {
				nextChar = reader.read();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return nextChar;
	}

	private static boolean isEOL(int aChar) {
		return (aChar == '\n' || aChar == '\r');
	}

}
