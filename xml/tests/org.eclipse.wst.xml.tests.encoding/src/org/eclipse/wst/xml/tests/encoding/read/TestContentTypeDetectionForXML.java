/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.xml.tests.encoding.read;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

public class TestContentTypeDetectionForXML extends TestContentTypeDetection {
	private static final String expectedCustomXMLContentType = "org.eclipse.wst.xml.core.xmlsource";
	
//	private static final String expectedXSLContentType = "org.eclipse.wst.xml.core.xslsource";
	
	private static final String expectedXMLContentType = "org.eclipse.core.runtime.xml";

	public void testFile103() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/EmptyFile.xml", null);
	}

	public void testFile103P() throws CoreException, IOException {
		doTestForParent(expectedXMLContentType, "testfiles/xml/EmptyFile.xml", null);
	}

	public void testFile104() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/eucjp.xml", null);
	}

	public void testFile104b() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/eucjp.xml", null);
	}

	public void testFile105() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/IllformedNormalNonDefault.xml", null);
	}
//  [251786] - No encoding should try to be picked up by the org.eclipse.wst.xml.core.xmlsource
//	public void testFile106() throws CoreException, IOException {
//		doTest(expectedXMLContentType, "testfiles/xml/MalformedNoEncoding.xml", null);
//	}

	/**
	 * This file is illformed in its specified charset
	 * and characters. 
	 * @throws CoreException
	 * @throws IOException
	 */
//  [251786] - No encoding should try to be picked up by the org.eclipse.wst.xml.core.xmlsource
//	public void testFile107() throws CoreException, IOException {
//		doTest(expectedXSLContentType, "testfiles/xml/MalformedNoEncoding.xsl", null);
//	}
//  [251786] - No encoding should try to be picked up by the org.eclipse.wst.xml.core.xmlsource
//	public void testFile107P() throws CoreException, IOException {
//		doTestForParent(expectedXMLContentType, "testfiles/xml/MalformedNoEncoding.xsl", null);
//	}

	public void testFile108() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/NoEncoding.xml", null);
	}

	public void testFile109() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/NormalNonDefault.xml", null);
	}


	public void testFile110() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/shiftjis.xml", null);
	}

	public void testFile111() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testExtraJunk.xml", null);
	}

	public void testFile112() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testExtraValidStuff.xml", null);
	}
	// [250392] the version number is illformed and should not be parsed correctly by the XMLContentDescriber.
	// Might be able to fix when [251786] is corrected
//	public void testFile113() throws CoreException, IOException {
//		doTest(expectedXMLContentType, "testfiles/xml/testIllFormed.xml", null);
//	}

	public void testFile114() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testIllFormed2.xml", null);
	}

	// [250392] the version number is illformed and should not be parsed correctly by the XMLContentDescriber.
	// Might be able to fix when [251786] is corrected
//	public void testFile115() throws CoreException, IOException {
//		doTest(expectedXMLContentType, "testfiles/xml/testIllFormed3.xml", java.nio.charset.IllegalCharsetNameException.class);
//	}

	public void testFile116() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testIllFormed4.xml", null);
	}

	public void testFile117() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testMultiLine.xml", null);
	}

	public void testFile118() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testNoEncodingValue.xml", null);
	}

	public void testFile119() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testNormalCase.xml", null);
	}

	public void testFile120() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testNoXMLDecl.xml", null);
	}

	public void testFile120WS() throws CoreException, IOException {
		// whitespace (CRLF) before xml declaration
		doTest(expectedCustomXMLContentType, "testfiles/xml/testWSBeforeXMLDecl.xml", null);
	}

	public void testFile120WS2() throws CoreException, IOException {
		// whitespace (space only) before xml declaration
		doTest(expectedCustomXMLContentType, "testfiles/xml/testWSBeforeXMLDecl2.xml", null);
	}

	public void testFile120WS3() throws CoreException, IOException {
		// whitespace (space, tabs, and CR only) before xml declaration
		doTest(expectedCustomXMLContentType, "testfiles/xml/testWSBeforeXMLDecl3.xml", null);
	}

	public void testFile120P() throws CoreException, IOException {
		doTestForParent(expectedXMLContentType, "testfiles/xml/testNoXMLDecl.xml", null);
	}

	public void testFile121() throws CoreException, IOException {
		// tag (not just white space) before xml declaration.
		doTest(expectedXMLContentType, "testfiles/xml/testNoXMLDeclAtFirst.xml", null);
	}

	public void testFile121P() throws CoreException, IOException {
		doTestForParent(expectedXMLContentType, "testfiles/xml/testNoXMLDeclAtFirst.xml", null);
	}

	public void testFile122() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testNoXMLDeclInLargeFile.xml", null);
	}

	public void testFile122P() throws CoreException, IOException {
		doTestForParent(expectedXMLContentType, "testfiles/xml/testNoXMLDeclInLargeFile.xml", null);
	}

	public void testFile123() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/testUTF16.xml", null);
	}

	public void testFile124() throws CoreException, IOException {
		// large, utf16, but no xmlDecl
		doTest(expectedXMLContentType, "testfiles/xml/UTF16LEAtStartOfLargeFile.xml", null);
	}

	public void testFile124P() throws CoreException, IOException {
		doTestForParent(expectedXMLContentType, "testfiles/xml/UTF16LEAtStartOfLargeFile.xml", null);
	}

	public void testFile125() throws CoreException, IOException {
		// illformed, is in utf16, but not in header, not in encoding= spec.
		doTest(expectedXMLContentType, "testfiles/xml/utf16UnicodeStreamWithNoEncodingInHeader2.xml", null);
	}

	public void testFile125P() throws CoreException, IOException {
		doTestForParent(expectedXMLContentType, "testfiles/xml/utf16UnicodeStreamWithNoEncodingInHeader2.xml", null);
	}

	public void testFile126() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/utf16UnicodeStreamWithNoEncodingInHeaderBE.xml", null);
	}

	public void testFile127() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/utf16WithJapaneseChars.xml", null);
	}

	public void testFile128() throws CoreException, IOException {
		doTest(expectedXMLContentType, "testfiles/xml/UTF8With3ByteBOM.xml", null);
	}
	
	public void testFile129() throws CoreException, IOException {
		doTest(expectedCustomXMLContentType, "testfiles/xml/utf16be.xml", null);
	}
	
	public void testFile130() throws CoreException, IOException {
		doTest(expectedCustomXMLContentType, "testfiles/xml/utf16le.xml", null);
	}
	
	public void testFile131() throws CoreException, IOException {
		doTest(expectedCustomXMLContentType, "testfiles/xml/utf16beMalformed.xml", null);
	}
	
	public void testFile132() throws CoreException, IOException {
		doTest(expectedCustomXMLContentType, "testfiles/xml/utf16leMalformed.xml", null);
	}

}
