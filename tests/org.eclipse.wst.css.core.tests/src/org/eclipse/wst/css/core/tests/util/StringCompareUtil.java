/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.util;

import java.io.IOException;
import java.io.StringReader;

public class StringCompareUtil {

	public boolean equalsIgnoreLineSeperator(String string1, String string2) {

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
	private int getNextChar(StringReader reader) {
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

	private boolean isEOL(int aChar) {
		return (aChar == '\n' || aChar == '\r');
	}

}