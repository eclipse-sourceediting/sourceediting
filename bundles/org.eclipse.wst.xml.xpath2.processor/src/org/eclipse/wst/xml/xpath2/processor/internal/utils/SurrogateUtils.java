/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (STAR) - bug 282096 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.internal.utils;

public class SurrogateUtils {

	/**
	 * This class will decode a surrogate entity into it's string representation
	 * @param str
	 * @return The decoded string
	 * @since 1.1
	 */
	public static String decodeXML(String str) {
		String decodeString = str;
		decodeString = decode(decodeString, "&#x1");
		decodeString = decode(decodeString, "&#x2");
		return decodeString;
	}
	
	private static String decode(String decodeStr, String key) {
		while (decodeStr.contains(key)) {
			int startpos = decodeStr.indexOf(key);
			String starthex = decodeStr.substring(startpos);
			int semipos = starthex.indexOf(';');
			String hexValue = starthex.substring(4, semipos);
			int i = Integer.parseInt(hexValue, 16);
			char c = (char)i;
			decodeStr = decodeStr.replaceAll(key + hexValue + ";", "" + c);
		}
		return decodeStr;
	}
}
