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
package org.eclipse.wst.html.tests.encoding.html;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.html.core.internal.contenttype.HTMLHeadTokenizer;
import org.eclipse.wst.html.core.internal.contenttype.HTMLHeadTokenizerConstants;
import org.eclipse.wst.html.core.internal.contenttype.HeadParserToken;
import org.eclipse.wst.html.tests.encoding.HTMLEncodingTestsPlugin;
import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;
import org.eclipse.wst.xml.tests.encoding.ZippedTest;

public class HTMLHeadTokenizerTester extends TestCase {
	boolean DEBUG = false;
	private String fCharset;

	private String fContentTypeValue;
	private final String fileDir = "html/";
	private final String fileHome = "testfiles/";
	private final String fileLocation = this.fileHome + this.fileDir;
	private String fPageEncodingValue = null;
	private String fXMLDecEncodingName;

	private void doTestFile(String filename, String expectedName) throws IOException {
		doTestFile(filename, expectedName, null);
	}

	private void doTestFile(String filename, String expectedName, String finalTokenType) {
		try {
			doTestFile(HTMLEncodingTestsPlugin.getTestReader(filename), expectedName, finalTokenType);
		}
		catch (IOException e) {
			System.out.println("Error opening file \"" + filename +"\"");
		}
	}

	private void doTestFile(Reader fileReader, String expectedName, String finalTokenType) throws IOException {
		HTMLHeadTokenizer tokenizer = null;
		tokenizer = new HTMLHeadTokenizer(fileReader);

		HeadParserToken resultToken = null;
		HeadParserToken token = parseHeader(tokenizer);
		String resultValue = getAppropriateEncoding();
		fileReader.close();
		if (finalTokenType != null) {
			assertTrue("did not end as expected. found:  " + token.getType(), finalTokenType.equals(token.getType()));
		}
		else {
			if (expectedName == null) {
				assertTrue("expected no encoding, but found: " + resultValue, resultToken == null);
			}
			else {
				// TODO: need to work on case issues
				assertTrue("expected " + expectedName + " but found " + resultValue, expectedName.equals(resultValue.toUpperCase()));
			}
		}

	}

	// public void testMalformedNoEncoding() {
	// String filename = fileLocation + "MalformedNoEncoding.jsp";
	// doTestFile(filename);
	// }
	// public void testMalformedNoEncodingXSL() {
	// String filename = fileLocation + "MalformedNoEncodingXSL.jsp";
	// doTestFile(filename);
	// }
	// public void testNoEncoding() {
	// String filename = fileLocation + "NoEncoding.jsp";
	// doTestFile(filename);
	// }
	// public void testNormalNonDefault() {
	// String filename = fileLocation + "NormalNonDefault.jsp";
	// doTestFile(filename);
	// }
	// public void testNormalPageCaseNonDefault() {
	// String filename = fileLocation + "NormalPageCaseNonDefault.jsp";
	// doTestFile(filename);
	// }
	// public void testdefect223365() {
	// String filename = fileLocation + "SelColBeanRow12ResultsForm.jsp";
	// doTestFile(filename);
	// }
	/**
	 * returns encoding according to priority: 1. XML Declaration 2. page
	 * directive pageEncoding name 3. page directive contentType charset name
	 */
	private String getAppropriateEncoding() {
		String result = null;
		if (this.fXMLDecEncodingName != null) {
			result = this.fXMLDecEncodingName;
		}
		else if (this.fPageEncodingValue != null) {
			result = this.fPageEncodingValue;
		}
		else if (this.fCharset != null) {
			result = this.fCharset;
		}
		return result;
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

	private void parseContentTypeValue(String contentType) {
		Pattern pattern = Pattern.compile(";\\s*charset\\s*=\\s*");
		String[] parts = pattern.split(contentType);
		if (parts.length > 0) {
			// if only one item, it can still be charset instead of
			// contentType
			if (parts.length == 1) {
				if (parts[0].length() > 6) {
					String checkForCharset = parts[0].substring(0, 7);
					if (checkForCharset.equalsIgnoreCase("charset")) {
						int eqpos = parts[0].indexOf('=');
						eqpos = eqpos + 1;
						if (eqpos < parts[0].length()) {
							this.fCharset = parts[0].substring(eqpos);
							this.fCharset = this.fCharset.trim();
						}
					}
				}
			}
			else {
				// fContentType = parts[0];
			}

		}
		if (parts.length > 1) {
			this.fCharset = parts[1];
		}
	}

	/**
	 * Give's priority to encoding value, if found else, looks for contentType
	 * value;
	 */
	private HeadParserToken parseHeader(HTMLHeadTokenizer tokenizer) throws IOException {
		this.fPageEncodingValue = null;
		this.fCharset = null;
		/*
		 * if (tokenType == XMLHeadTokenizerConstants.XMLDelEncoding) { if
		 * (tokenizer.hasMoreTokens()) { ITextHeadRegion valueToken =
		 * tokenizer.getNextToken(); String valueTokenType =
		 * valueToken.getType(); if (isLegal(valueTokenType)) { resultValue =
		 * valueToken.getText(); if (DEBUG) { System.out.println("XML Head
		 * Tokenizer Found Encoding: " + resultValue); } } } }
		 */
		HeadParserToken token = null;
		HeadParserToken finalToken = null;
		do {
			token = tokenizer.getNextToken();
			if (this.DEBUG) {
				System.out.println(token);
			}
			String tokenType = token.getType();
			if(canHandleAsUnicodeStream(tokenType)) {
				
			}
			if (tokenType == HTMLHeadTokenizerConstants.MetaTagContentType) {
				if (tokenizer.hasMoreTokens()) {
					HeadParserToken valueToken = tokenizer.getNextToken();
					if (this.DEBUG) {
						System.out.println(valueToken);
					}
					String valueTokenType = valueToken.getType();
					if (isLegalString(valueTokenType)) {
						this.fContentTypeValue = valueToken.getText();

					}
				}
			}

		}
		while (tokenizer.hasMoreTokens());
		if (this.fContentTypeValue != null) {
			parseContentTypeValue(this.fContentTypeValue);
		}
		finalToken = token;
		return finalToken;

	}
	
	private boolean canHandleAsUnicodeStream(String tokenType) {
		boolean canHandleAsUnicode = false;
		if (tokenType == EncodingParserConstants.UTF83ByteBOM) {
			canHandleAsUnicode = true;
			this.fCharset = "UTF-8"; //$NON-NLS-1$
		}
		else if (tokenType == EncodingParserConstants.UTF16BE || tokenType == EncodingParserConstants.UTF16LE) {
			canHandleAsUnicode = true;
			this.fCharset = "UTF-16"; //$NON-NLS-1$
		}
		return canHandleAsUnicode;
	}

	public void testBestCase() throws IOException {
		String filename = this.fileLocation + "NormalNonDefault.html";
		doTestFile(filename, "UTF-8");

	}

	// public void testIllFormed() {
	// String filename = fileLocation + "testIllFormed.jsp";
	// doTestFile(filename);
	// }
	// public void testIllFormed2() {
	// String filename = fileLocation + "testIllFormed2.jsp";
	// doTestFile(filename);
	// }
	// public void testIllformedNormalNonDefault() {
	// String filename = fileLocation + "IllformedNormalNonDefault.jsp";
	// doTestFile(filename);
	// }
	public void testEmptyFile() throws IOException {
		String filename = this.fileLocation + "EmptyFile.html";
		doTestFile(filename, null);
	}

	public void testIllFormedNormalNonDefault() throws IOException {
		String filename = this.fileLocation + "IllformedNormalNonDefault.html";
		doTestFile(filename, "UTF-8");
	}

	public void testLargeCase() throws IOException {
		String filename = this.fileLocation + "LargeNonDefault.html";
		doTestFile(filename, "ISO-8859-1");

	}

	public void testLargeNoEncoding() throws IOException {
		String filename = this.fileLocation + "LargeNoEncoding.html";
		doTestFile(filename, null, EncodingParserConstants.MAX_CHARS_REACHED);

	}

	public void testMultiNonDefault() throws IOException {
		String filename = this.fileLocation + "MultiNonDefault.html";
		doTestFile(filename, "ISO-8859-6");
	}

	public void testNoEncoding() throws IOException {
		String filename = this.fileLocation + "NoEncoding.html";
		doTestFile(filename, null);
	}

	public void testnoquotes() throws IOException {
		String filename = this.fileLocation + "noquotes.html";
		doTestFile(filename, "UTF-8");

	}
	
	public void testnoquotesUTF16le() throws IOException {
		String filename = this.fileLocation + "noquotesUTF16le.html";
		doTestFile(filename, "UTF-16LE");
	}
	
	public void testUTF16le() throws IOException {
		String filename = this.fileLocation + "utf16le.html";
		doTestFile(filename, "UTF-16LE");
	}

	public void testUTF16be() throws IOException {
		String filename = this.fileLocation + "utf16be.html";
		doTestFile(filename, "UTF-16BE");
	}
	
	/*
		sun.io.MalformedInputException
		at sun.io.ByteToCharUTF8.convert(ByteToCharUTF8.java:262)
		at sun.nio.cs.StreamDecoder$ConverterSD.convertInto(StreamDecoder.java:314)
		at sun.nio.cs.StreamDecoder$ConverterSD.implRead(StreamDecoder.java:364)
		at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:250)
		at java.io.InputStreamReader.read(InputStreamReader.java:212)
		at org.eclipse.wst.html.core.internal.contenttype.HTMLHeadTokenizer.yy_advance(HTMLHeadTokenizer.java:340)
		at org.eclipse.wst.html.core.internal.contenttype.HTMLHeadTokenizer.primGetNextToken(HTMLHeadTokenizer.java:477)
		at org.eclipse.wst.html.core.internal.contenttype.HTMLHeadTokenizer.getNextToken(HTMLHeadTokenizer.java:232)
		at org.eclipse.wst.html.tests.encoding.html.HTMLHeadTokenizerTester.parseHeader(HTMLHeadTokenizerTester.java:175)
		at org.eclipse.wst.html.tests.encoding.html.HTMLHeadTokenizerTester.doTestFile(HTMLHeadTokenizerTester.java:57)
		at org.eclipse.wst.html.tests.encoding.html.HTMLHeadTokenizerTester.doTestFile(HTMLHeadTokenizerTester.java:37)
		at org.eclipse.wst.html.tests.encoding.html.HTMLHeadTokenizerTester.testUTF16BOM(HTMLHeadTokenizerTester.java:293)
	*/

	public void testUTF16BOM() throws Exception {
		String filename = this.fileLocation + "utf16BOM.html";
		ZippedTest test = new ZippedTest();
		test.setUp();
		IFile file = test.getFile(filename);
		assertNotNull(file);
		Reader fileReader = new FileReader(file.getLocationURI().getPath());
		doTestFile(fileReader, "UTF-16", null);
		test.shutDown();
	}
}
