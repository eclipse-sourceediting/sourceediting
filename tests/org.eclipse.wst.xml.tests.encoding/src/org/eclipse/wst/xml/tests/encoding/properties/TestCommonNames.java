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

import org.eclipse.wst.sse.core.internal.encoding.CommonCharsetNames;

public class TestCommonNames extends TestCase {

	private static final boolean DEBUG = false;

	public void testCommonNames() {
		String[] names = CommonCharsetNames.getCommonCharsetNames();
		assertTrue("common charset names could not be loaded", names != null && names.length > 0);
		if (DEBUG) {
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				String displayName = CommonCharsetNames.getDisplayString(name);
				System.out.println( name + "    " + displayName);
			}
		}
	}
	
	public void doTestDefaultIanaNames(String stringToCheck, String defaultName, String expected) {
		String actual = CommonCharsetNames.getPreferredDefaultIanaName(stringToCheck, defaultName);
		assertEquals("default IANA name test failed for " + stringToCheck, expected, actual);
	}

	public void testASCII() {
		doTestDefaultIanaNames("ASCII", "UTF-8", "US-ASCII");
	}
	
	public void testCp1252() {
		doTestDefaultIanaNames("Cp1252", "UTF-8", "ISO-8859-1");
	}
	
	public void testMS950() {
		doTestDefaultIanaNames("MS950", "UTF-8", "BIG5");
	}
	
	public void testCp1256() {
		doTestDefaultIanaNames("Cp1256", "UTF-8", "windows-1256");
	}
	
	public void testMS949() {
		doTestDefaultIanaNames("MS949", "UTF-8", "EUC-KR");
	}
	
	public void testEUC_JP() {
		doTestDefaultIanaNames("EUC-JP", "UTF-8", "EUC-JP");
	}
	
	public void testTotallyFake() {
		doTestDefaultIanaNames("totallyFake", "UTF-8", "UTF-8");
	}
	
	public void testSystemEncoding() {
		String systemEnc = System.getProperty("file.encoding");
		if (systemEnc != null) {
			String actual = CommonCharsetNames.getPreferredDefaultIanaName(systemEnc, "UTF-8");
			assertNotNull("default IANA name test failed for system encoding " + systemEnc, actual);
		}
	}
}
