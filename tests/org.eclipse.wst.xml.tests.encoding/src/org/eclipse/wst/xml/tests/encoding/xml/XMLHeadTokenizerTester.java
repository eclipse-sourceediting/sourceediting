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
package org.eclipse.wst.xml.tests.encoding.xml;

import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;
import org.eclipse.wst.xml.core.internal.contenttype.HeadParserToken;
import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizer;
import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizerConstants;
import org.eclipse.wst.xml.tests.encoding.TestsPlugin;


public class XMLHeadTokenizerTester extends TestCase {

	private boolean DEBUG = false;
	private final String fileDir = "xml/";
	private final String fileHome = "testfiles/";
	private final String fileLocation = fileHome + fileDir;
	private String fEncoding = null;
	private HeadParserToken fFinalToken;

	private void doTestFile(String filename, String expectedName) {
		doTestFile(filename, expectedName, null);
	}

	private void doTestFile(String filename, String expectedName, String expectedFinalTokenType) {

		XMLHeadTokenizer tokenizer = null;
		Reader fileReader = null;
		try {
			if (DEBUG) {
				System.out.println();
				System.out.println("       " + filename);
				System.out.println();
			}
			fileReader = TestsPlugin.getByteReader(filename);
			tokenizer = new XMLHeadTokenizer(fileReader);
		}
		catch (IOException e) {
			System.out.println("Error opening file \"" + filename + "\"");
		}

		String resultValue = null;
		try {
			parse(tokenizer);
			resultValue = fEncoding;
			if (DEBUG) {
				System.out.println("XML Head Tokenizer Found Encoding: " + resultValue);
			}
			fileReader.close();
		}
		catch (java.io.IOException e) {
			System.out.println("An I/O error occured while scanning :");
			System.out.println(e);
		}

		if (expectedFinalTokenType != null) {
			assertTrue("did not end as expected. found:  " + fFinalToken.getType(), expectedFinalTokenType.equals(fFinalToken.getType()));
		}
		else {
			if (expectedName == null) {
				// TODO: this test branch needs to be improved ... doesn't
				// fail
				// as it should
				// (such as when tokenizer changed to return early when
				// Unicode
				// stream found).
				assertTrue("expected no encoding, but found: " + resultValue, resultValue == null);
			}
			else {
				assertTrue("expected " + expectedName + " but found " + resultValue, expectedName.equals(resultValue));
			}
		}

	}

	private void parse(XMLHeadTokenizer tokenizer) throws IOException {
		HeadParserToken token = null;
		String tokenType = null;
		do {
			token = tokenizer.getNextToken();
			tokenType = token.getType();
			// normally "parsing" the tokens should be done by parser
			// @see, XMLResourceEncodoingDetector
			// but we'll
			// do it here for a little
			// more independent test.
			if (tokenType == EncodingParserConstants.UTF16BE) {
				fEncoding = "UTF16BEInStream";
			}
			if (tokenType == EncodingParserConstants.UTF16LE) {
				fEncoding = "UTF16LEInStream";
			}
			if (tokenType == EncodingParserConstants.UTF83ByteBOM) {
				fEncoding = "UTF83ByteBOMInStream";
			}
			if (tokenType == XMLHeadTokenizerConstants.XMLDelEncoding) {
				if (tokenizer.hasMoreTokens()) {
					token = tokenizer.getNextToken();
					tokenType = token.getType();
					if (isLegalString(tokenType)) {
						fEncoding = token.getText();
					}
				}
			}
		}
		while (tokenizer.hasMoreTokens());
		// for testing
		fFinalToken = token;
	}

	private boolean isLegalString(String tokenType) {
		boolean result = false;
		if (tokenType != null) {
			result = tokenType.equals(EncodingParserConstants.StringValue) || tokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
		}
		return result;
	}

	/**
	 * Normal XMLDeclaration with default encoding specified (UTF-8)
	 * 
	 */
	public void testBestCase() {
		String filename = fileLocation + "testNormalCase.xml";
		doTestFile(filename, "UTF-8");

	}

	/**
	 * This is a UTF-16 file (Unicode bytes in BOM). So, the tokenizer by
	 * itself can't read correctly. Returns null in "pure" tokenizer test, but
	 * encoding detector case should still handle since looks for bytes first.
	 */
	public void testUTF16() {
		String filename = fileLocation + "testUTF16.xml";
		doTestFile(filename, "UTF16BEInStream");
	}

	/**
	 * Just to make sure we don't choke on empty file.
	 * 
	 */
	public void testEmptyFile() {
		String filename = fileLocation + "EmptyFile.xml";
		doTestFile(filename, null);
	}

	/**
	 * Testing as a result of CMVC defect 217720
	 */
	public void testEUCJP() {
		String filename = fileLocation + "eucjp.xml";
		doTestFile(filename, "EUC-JP");
	}

	/**
	 * Extended XML Declaration that contains 'standalone' attribute
	 * 
	 */
	public void testExtraAttrCase() {
		String filename = fileLocation + "testExtraValidStuff.xml";
		doTestFile(filename, "UTF-8");

	}

	/**
	 * A case with a valid encoding, but extra attributes which are not
	 * valid/meaningful.
	 * 
	 */
	public void testExtraJunkCase() {
		String filename = fileLocation + "testExtraJunk.xml";
		doTestFile(filename, "ISO-8859-1");
	}

	/**
	 * Missing 2 quotes, one and end of version value and one at beginning of
	 * encoding value. In this case, tokenizer handles as undelimite string,
	 * but if we ever modifiy to also look for 'version', then would not work
	 * the same.
	 * 
	 */
	public void testIllFormed() {
		String filename = fileLocation + "testIllFormed.xml";
		doTestFile(filename, null);
	}

	/**
	 * Missing XMLDecl end tag ... we should be able to safely guess.
	 * 
	 */
	public void testIllFormed2() {
		String filename = fileLocation + "testIllFormed2.xml";
		doTestFile(filename, "UTF-8");
	}

	/**
	 * Missing end quote on UTF-8 attribute, so picks up following attribte
	 * too.
	 * 
	 */
	public void testIllFormed3() {
		String filename = fileLocation + "testIllFormed3.xml";
		doTestFile(filename, "UTF-8 standalone=");
	}

	/**
	 * Missing end quote on UTF-8 attribute, but then XMLDeclEnds, so should
	 * be able to handle
	 * 
	 */
	public void testIllFormed4() {
		String filename = fileLocation + "testIllFormed4.xml";
		doTestFile(filename, "UTF-8");
	}

	/**
	 * Test of missing end quote on encoding value.
	 * 
	 */
	public void testIllformedNormalNonDefault() {
		String filename = fileLocation + "IllformedNormalNonDefault.xml";
		doTestFile(filename, "ISO-8859-1");
	}

	/**
	 * Empty string as encoding value; (And, malformed input, for UTF-8 ...
	 * should not effect results of this level of test).
	 * 
	 */
	public void testMalformedNoEncoding() {
		String filename = fileLocation + "MalformedNoEncoding.xml";
		doTestFile(filename, "");
	}

	/**
	 * Empty string as encoding value; (And, malformed input, for UTF-8 ...
	 * should not effect results of this level of test).
	 * 
	 */
	public void testMalformedNoEncodingXSL() {
		String filename = fileLocation + "MalformedNoEncoding.xsl";
		doTestFile(filename, "");
	}

	/**
	 * XMLDeclaration not all on same line
	 * 
	 */
	public void testMultiLineCase() {
		String filename = fileLocation + "testMultiLine.xml";
		doTestFile(filename, "ISO-8859-1");

	}

	/**
	 * No encoding in XMLDeclaration
	 * 
	 */
	public void testNoEncoding() {
		String filename = fileLocation + "NoEncoding.xml";
		doTestFile(filename, null);
	}

	/**
	 * ?Is this a dup?
	 * 
	 */
	public void testNoEncodingCase() {
		String filename = fileLocation + "testNoEncodingValue.xml";
		doTestFile(filename, null);
	}

	/**
	 * Normal XMLDeclaration with ISO-1 specified
	 * 
	 */
	public void testNormalNonDefault() {
		String filename = fileLocation + "NormalNonDefault.xml";
		doTestFile(filename, "ISO-8859-1");
	}

	/**
	 * No XMLDeclaration at all. (Invalid, but should still be able to parse).
	 * 
	 */
	public void testNoXMLDecl() {
		String filename = fileLocation + "testNoXMLDecl.xml";
		doTestFile(filename, null);
	}

	/**
	 * Hard to handle safely (may appear in comment, for example).
	 * 
	 */
	public void testNoXMLDeclAtFirst() {
		String filename = fileLocation + "testNoXMLDeclAtFirst.xml";
		doTestFile(filename, null);
	}

	/**
	 * This test is just to make sure the scanning ends before end of file is
	 * reached.
	 * 
	 */
	public void testNoXMLDeclInLargeFile() {
		String filename = fileLocation + "testNoXMLDeclInLargeFile.xml";
		doTestFile(filename, null, EncodingParserConstants.MAX_CHARS_REACHED);
	}

	/**
	 * Testing as a result of CMVC defect 217720
	 */
	public void testshiftjis() {
		String filename = fileLocation + "shiftjis.xml";
		doTestFile(filename, "Shift_JIS");
	}

	/**
	 * Testing as a result of CMVC defect 217720
	 */
	public void testUTF16LEWithJapaneseChars() {
		String filename = fileLocation + "utf16UnicodeStreamWithNoEncodingInHeader2.xml";
		doTestFile(filename, "UTF16LEInStream");
	}

	/**
	 * Testing as a result of CMVC defect 217720
	 */
	public void testUTF16BEWithJapaneseChars() {
		String filename = fileLocation + "utf16UnicodeStreamWithNoEncodingInHeaderBE.xml";
		doTestFile(filename, "UTF16BEInStream");
	}

	/**
	 * A common case.
	 * 
	 */
	 public void testUTF8With3ByteBOM() {
		String filename = fileLocation + "UTF8With3ByteBOM.xml";
		doTestFile(filename, "UTF83ByteBOMInStream");
	}
	public void UTF16LEAtStartOfLargeFile() {
		String filename = fileLocation + "UTF16LEAtStartOfLargeFile.xml";
		doTestFile(filename, "UTF16LEInStream");
	}
	
	public void testUTF16LE() {
		String filename = fileLocation + "utf16le.xml";
		doTestFile(filename, "UTF-16LE");
	}
	
	public void testUTF16BE() {
		String filename = fileLocation + "utf16be.xml";
		doTestFile(filename, "UTF-16BE");
	}
	
	public void testUTF16BEMalformed() {
		String filename = fileLocation + "utf16beMalformed.xml";
		doTestFile(filename, "UTF-16BE");
	}
	
	public void testUTF16LEMalformed() {
		String filename = fileLocation + "utf16leMalformed.xml";
		doTestFile(filename, "UTF-16LE");
	}
}
