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
package org.eclipse.wst.xml.tests.encoding.properties;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.encoding.CodedIO;

public class TestOverrides extends TestCase {

	private static final boolean DEBUG = false;

	public String doTestOverride(String stringToCheck) {

		String charset = CodedIO.getAppropriateJavaCharset(stringToCheck);
		assertNotNull("override test failed for " + stringToCheck, charset);
		return charset;
	}

	public void testISO88598I() {
		String result = doTestOverride("ISO-8859-8-I");
		assertEquals("mapping override not correct for ISO-8859-8-I", "ISO-8859-8", result);
		if (DEBUG) {
			System.out.println(result);
		}
	}

	public void testXSJIS() {
		String result = doTestOverride("X-SJIS");
		assertEquals("mapping override not correct for X-SJIS", "Shift_JIS", result);
		if (DEBUG) {
			System.out.println(result);
		}
	}

}
