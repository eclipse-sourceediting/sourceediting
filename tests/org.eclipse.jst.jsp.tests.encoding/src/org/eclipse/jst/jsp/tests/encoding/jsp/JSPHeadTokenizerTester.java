/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.tests.encoding.jsp;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizer;
import org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizerConstants;
import org.eclipse.jst.jsp.tests.encoding.JSPEncodingTestsPlugin;
import org.eclipse.wst.common.encoding.EncodingParserConstants;
import org.eclipse.wst.common.encoding.HeadParserToken;
import org.eclipse.wst.xml.core.contenttype.XMLHeadTokenizerConstants;

public class JSPHeadTokenizerTester extends TestCase {
	private boolean DEBUG = false;
	private String fCharset;
	private String fContentType;
	private String fContentTypeValue;
	private final String fileDir = "jsp/";
	private final String fileHome = "testfiles/";
	private final String fileLocation = fileHome + fileDir;
	private String fPageEncodingValue = null;
	private String fXMLDecEncodingName;

	private void doTestFile(String filename, String expectedName) throws IOException {
		doTestFile(filename, expectedName, null);
	}

	private void doTestFile(String filename, String expectedName, String finalTokenType) throws IOException {
		JSPHeadTokenizer tokenizer = null;
		Reader fileReader = null;
		try {
			if (DEBUG) {
				System.out.println();
				System.out.println("       " + filename);
				System.out.println();
			}
			fileReader = JSPEncodingTestsPlugin.getTestReader(filename);
			tokenizer = new JSPHeadTokenizer(fileReader);
		}
		catch (IOException e) {
			System.out.println("Error opening file \"" + filename + "\"");
		}
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
				assertTrue("expected " + expectedName + " but found " + resultValue, expectedName.equals(resultValue));
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
		if (fXMLDecEncodingName != null)
			result = fXMLDecEncodingName;
		else if (fPageEncodingValue != null)
			result = fPageEncodingValue;
		else if (fCharset != null)
			result = fCharset;
		return result;
	}

	protected String getContentType() {
		return fContentType;
	}

	private boolean isLegalString(String tokenType) {
		if (tokenType == null)
			return false;
		else
			return tokenType.equals(EncodingParserConstants.StringValue) || tokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
	}

	private void parseContentTypeValue(String contentType) {
		Pattern pattern = Pattern.compile(";\\s*charset\\s*=\\s*"); //$NON-NLS-1$
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
							fCharset = parts[0].substring(eqpos);
							fCharset = fCharset.trim();
						}
					}
				}
			}
			else {
				fContentType = parts[0];
			}
		}
		if (parts.length > 1) {
			fCharset = parts[1];
		}
	}

	/**
	 * Give's priority to encoding value, if found else, looks for contentType
	 * value;
	 */
	private HeadParserToken parseHeader(JSPHeadTokenizer tokenizer) throws IOException {
		fPageEncodingValue = null;
		fCharset = null;
		fContentType = null;
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
			String tokenType = token.getType();
			if (tokenType == XMLHeadTokenizerConstants.XMLDelEncoding) {
				if (tokenizer.hasMoreTokens()) {
					HeadParserToken valueToken = tokenizer.getNextToken();
					String valueTokenType = valueToken.getType();
					if (isLegalString(valueTokenType)) {
						fXMLDecEncodingName = valueToken.getText();
					}
				}
			}
			else if (tokenType == JSPHeadTokenizerConstants.PageEncoding) {
				if (tokenizer.hasMoreTokens()) {
					HeadParserToken valueToken = tokenizer.getNextToken();
					String valueTokenType = valueToken.getType();
					if (isLegalString(valueTokenType)) {
						fPageEncodingValue = valueToken.getText();
					}
				}
			}
			else if (tokenType == JSPHeadTokenizerConstants.PageContentType) {
				if (tokenizer.hasMoreTokens()) {
					HeadParserToken valueToken = tokenizer.getNextToken();
					String valueTokenType = valueToken.getType();
					if (isLegalString(valueTokenType)) {
						fContentTypeValue = valueToken.getText();
					}
				}
			}
			// else if (tokenType == JSPHeadTokenizerConstants.PageLanguage) {
			// if (tokenizer.hasMoreTokens()) {
			// IHeadParserToken valueToken = tokenizer.getNextToken();
			// String valueTokenType = valueToken.getType();
			// if (isLegalString(valueTokenType)) {
			// fLanguage = valueToken.getText();
			// }
			// }
		}
		while (tokenizer.hasMoreTokens());
		if (fContentTypeValue != null) {
			parseContentTypeValue(fContentTypeValue);
		}
		finalToken = token;
		return finalToken;
	}

	public void testBestCase() throws IOException {
		String filename = fileLocation + "nomalDirectiveCase.jsp";
		doTestFile(filename, "ISO-8859-2");
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
		String filename = fileLocation + "EmptyFile.jsp";
		doTestFile(filename, null);
	}

	public void testNomalDirectiveCaseUsingXMLSyntax() throws IOException {
		String filename = fileLocation + "nomalDirectiveCaseUsingXMLSyntax.jsp";
		doTestFile(filename, "ISO-8859-2");
	}

	public void testNoPageDirective() throws IOException {
		String filename = fileLocation + "testNoPageDirective.jsp";
		doTestFile(filename, null);
	}

	public void testNoPageDirectiveAtFirst() throws IOException {
		String filename = fileLocation + "testNoPageDirectiveAtFirst.jsp";
		doTestFile(filename, "ISO-8859-2");
	}

	public void testNoPageDirectiveInLargeFile() throws IOException {
		String filename = fileLocation + "testNoPageDirectiveInLargeFile.jsp";
		doTestFile(filename, null, EncodingParserConstants.MAX_CHARS_REACHED);
	}

	public void testNormalCaseWithNeither() throws IOException {
		String filename = fileLocation + "nomalDirectiveCaseNoEncoding.jsp";
		doTestFile(filename, null);
	}

	public void testNormalCharset() throws IOException {
		String filename = fileLocation + "nomalDirectiveCaseUsingCharset.jsp";
		doTestFile(filename, "ISO-8859-3");
	}
}