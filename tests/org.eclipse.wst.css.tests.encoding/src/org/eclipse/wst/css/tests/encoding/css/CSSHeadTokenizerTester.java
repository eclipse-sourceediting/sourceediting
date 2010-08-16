/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.tests.encoding.css;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.css.core.internal.contenttype.CSSHeadTokenizer;
import org.eclipse.wst.css.core.internal.contenttype.CSSHeadTokenizerConstants;
import org.eclipse.wst.css.core.internal.contenttype.HeadParserToken;
import org.eclipse.wst.css.tests.encoding.CSSEncodingTestsPlugin;
import org.eclipse.wst.xml.tests.encoding.ZippedTest;

public class CSSHeadTokenizerTester extends TestCase {
	boolean DEBUG = false;
	private String fcharset;
	private final String fileDir = "css/";
	private final String fileHome = "testfiles/";
	private final String fileLocation = this.fileHome + this.fileDir;

	private void doTestFile(String filename, String expectedName) throws IOException {
		doTestFile(filename, expectedName, null);
	}

	private void doTestFile(String filename, String expectedName, String finalTokenType) {
		try {
			doTestFile(CSSEncodingTestsPlugin.getTestReader(filename), expectedName, finalTokenType);
		}
		catch (IOException e) {
			System.out.println("Error opening file \"" + filename +"\"");
		}
	}

	private void doTestFile(Reader fileReader, String expectedName, String finalTokenType) throws IOException {
		CSSHeadTokenizer tokenizer = null;
		tokenizer = new CSSHeadTokenizer(fileReader);

		HeadParserToken resultToken = null;
		HeadParserToken token = parseHeader(tokenizer);
		String resultValue = this.fcharset;
		fileReader.close();
		if (finalTokenType != null) {
			assertTrue("did not end as expected. found:  " + token.getType(), finalTokenType.equals(token.getType()));
		}
		else {
			if (expectedName == null) {
				assertTrue("expected no encoding, but found: " + resultValue, resultToken == null);
			}
			else {
				assertTrue("expected " + expectedName + " but found " + resultValue, expectedName.equals(resultValue));
			}
		}

	}

	private boolean isLegalString(String tokenType) {
		boolean result = false;
		if (tokenType == null) {
			result = false;
		}
		else {
			result = tokenType.equals(EncodingParserConstants.StringValue) || tokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
		}
		return result;
	}

	/**
	 * Give's priority to encoding value, if found else, looks for contentType
	 * value;
	 */
	private HeadParserToken parseHeader(CSSHeadTokenizer tokenizer) throws IOException {
		HeadParserToken token = null;
		HeadParserToken finalToken = null;
		do {
			token = tokenizer.getNextToken();
			String tokenType = token.getType();
			if(canHandleAsUnicodeStream(tokenType)) {

			}
			if (tokenType == CSSHeadTokenizerConstants.CHARSET_RULE) {
				if (tokenizer.hasMoreTokens()) {
					HeadParserToken valueToken = tokenizer.getNextToken();
					String valueTokenType = valueToken.getType();
					if (isLegalString(valueTokenType)) {
						this.fcharset = valueToken.getText();

					}
				}
			}
		}
		while (tokenizer.hasMoreTokens());
		finalToken = token;
		return finalToken;

	}
	
	private boolean canHandleAsUnicodeStream(String tokenType) {
		boolean canHandleAsUnicode = false;
		if (tokenType == EncodingParserConstants.UTF83ByteBOM) {
			canHandleAsUnicode = true;
			this.fcharset = "UTF-8"; //$NON-NLS-1$
		}
		else if (tokenType == EncodingParserConstants.UTF16BE || tokenType == EncodingParserConstants.UTF16LE) {
			canHandleAsUnicode = true;
			this.fcharset = "UTF-16"; //$NON-NLS-1$
		}
		return canHandleAsUnicode;
	}

	public void testBestCase() throws IOException {
		String filename = this.fileLocation + "nonStandard.css";
		doTestFile(filename, "ISO-8859-6");

	}

	public void testEmptyFile() throws IOException {
		String filename = this.fileLocation + "emptyFile.css";
		doTestFile(filename, null);
	}

	public void _testEUCJP() throws IOException {
		String filename = this.fileLocation + "encoding_test_eucjp.css";
		doTestFile(filename, "EUC-JP");
	}

	public void testJIS() throws IOException {
		String filename = this.fileLocation + "encoding_test_jis.css";
		doTestFile(filename, "ISO-2022-JP");
	}

	public void testNoEncoding() throws IOException {
		String filename = this.fileLocation + "noEncoding.css";
		doTestFile(filename, null);
	}

	public void testnonStandardIllFormed() throws IOException {
		String filename = this.fileLocation + "nonStandardIllFormed.css";
		doTestFile(filename, "ISO-8859-6");
	}

	public void testnonStandardIllFormed2() throws IOException {
		String filename = this.fileLocation + "nonStandardIllFormed2.css";
		doTestFile(filename, "ISO-8859-6");
	}

	public void _testShiftJIS() throws IOException {
		String filename = this.fileLocation + "encoding_test_sjis.css";
		doTestFile(filename, "SHIFT_JIS");
	}
	
	public void testUTF16be() throws IOException {
		String filename = fileLocation + "utf16be.css";
		doTestFile(filename, "UTF-16BE");
	}
	
	public void testUTF16le() throws IOException {
		String filename = fileLocation + "utf16le.css";
		doTestFile(filename, "UTF-16LE");
	}
	
	public void testUTF16beMalformed() throws IOException {
		String filename = fileLocation + "utf16beMalformed.css";
		doTestFile(filename, "UTF-16BE");
	}
	
	public void testUTF16leMalformed() throws IOException {
		String filename = fileLocation + "utf16leMalformed.css";
		doTestFile(filename, "UTF-16LE");
	}
	
	/*
		sun.io.MalformedInputException
		at sun.io.ByteToCharUTF8.convert(ByteToCharUTF8.java:262)
		at sun.nio.cs.StreamDecoder$ConverterSD.convertInto(StreamDecoder.java:314)
		at sun.nio.cs.StreamDecoder$ConverterSD.implRead(StreamDecoder.java:364)
		at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:250)
		at java.io.InputStreamReader.read(InputStreamReader.java:212)
		at org.eclipse.wst.css.core.internal.contenttype.CSSHeadTokenizer.yy_advance(CSSHeadTokenizer.java:337)
		at org.eclipse.wst.css.core.internal.contenttype.CSSHeadTokenizer.primGetNextToken(CSSHeadTokenizer.java:470)
		at org.eclipse.wst.css.core.internal.contenttype.CSSHeadTokenizer.getNextToken(CSSHeadTokenizer.java:229)
		at org.eclipse.wst.css.tests.encoding.css.CSSHeadTokenizerTester.parseHeader(CSSHeadTokenizerTester.java:88)
		at org.eclipse.wst.css.tests.encoding.css.CSSHeadTokenizerTester.doTestFile(CSSHeadTokenizerTester.java:52)
		at org.eclipse.wst.css.tests.encoding.css.CSSHeadTokenizerTester.doTestFile(CSSHeadTokenizerTester.java:31)
		at org.eclipse.wst.css.tests.encoding.css.CSSHeadTokenizerTester.testUTF16BOM(CSSHeadTokenizerTester.java:186)
	*/

	public void testUTF16BOM() throws Exception {
		String filename = fileLocation + "utf16BOM.css";
		ZippedTest test = new ZippedTest();
		test.setUp();
		IFile file = test.getFile(filename);
		assertNotNull(file);
		Reader fileReader = new FileReader(file.getLocationURI().getPath());
		doTestFile(fileReader, "UTF-16", null);
		test.shutDown();
	}
}
