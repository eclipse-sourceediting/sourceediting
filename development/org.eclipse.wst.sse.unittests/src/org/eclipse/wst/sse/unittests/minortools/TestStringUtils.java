/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.unittests.minortools;



public class TestStringUtils {

	/**
	 * TestStringUtils constructor comment.
	 */
	private TestStringUtils() {
		super();
	}

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
		while (position + spacer - 1 < length && aString.indexOf(source, position) > -1) {
			position = aString.indexOf(source, previous);
			normalString = normalString + aString.substring(previous, position) + target;
			position += spacer;
			previous = position;
		}
		normalString = normalString + aString.substring(position, aString.length());

		return normalString;
	}

}
