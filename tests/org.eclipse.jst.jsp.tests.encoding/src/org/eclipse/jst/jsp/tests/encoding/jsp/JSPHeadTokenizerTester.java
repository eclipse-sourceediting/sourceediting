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
package org.eclipse.jst.jsp.tests.encoding.jsp;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.contenttype.HeadParserToken;
import org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizer;
import org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizerConstants;
import org.eclipse.jst.jsp.tests.encoding.JSPEncodingTestsPlugin;
import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;
import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizerConstants;

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
	private String fLanguage;

	private void doTestFile(String filename, String expectedName) throws Exception {
		doTestFile(filename, expectedName, null, null);
	}

	private void doTestFile(String filename, String expectedName, String finalTokenType, String expectedContentType) throws Exception {
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

		HeadParserToken token = parseHeader(tokenizer);
		String resultValue = getAppropriateEncoding();
		fileReader.close();
		if (finalTokenType != null) {
			assertTrue("did not end as expected. found:  " + token.getType(), finalTokenType.equals(token.getType()));
		}

		if (expectedName == null) {
			assertTrue("expected no encoding, but found: " + resultValue, resultValue == null);
		}
		else {
			assertTrue("expected " + expectedName + " but found " + resultValue, expectedName.equals(resultValue));
		}

		String foundContentType = getContentType();
		if (expectedContentType == null) {
			assertTrue("expected no contentType, but found: " + foundContentType, foundContentType == null);
		}
		else {
			assertTrue("expected " + expectedContentType + " but found " + foundContentType, expectedContentType.equals(foundContentType));
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
	public void testNormalNonDefault() throws Exception {
		String filename = fileLocation + "NormalNonDefault.jsp";
		doTestFile(filename, "ISO-8859-8");
	}

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

	private String getContentType() {
		return fContentType;
	}

	private boolean isLegalString(String tokenType) {
		boolean result = false;
		if (tokenType != null) {
			result = tokenType.equals(EncodingParserConstants.StringValue) || tokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || tokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
		}
		return result;
	}

	/**
	 * This method should be exactly the same as what is in
	 * JSPResourceEncodingDetector
	 * 
	 * @param contentType
	 */
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
					else {
						fContentType = parts[0];
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
	private HeadParserToken parseHeader(JSPHeadTokenizer tokenizer) throws Exception {
		fPageEncodingValue = null;
		fCharset = null;
		fContentType = null;
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
			else if (tokenType == JSPHeadTokenizerConstants.PageLanguage) {
				if (tokenizer.hasMoreTokens()) {
					HeadParserToken valueToken = tokenizer.getNextToken();
					String valueTokenType = valueToken.getType();
					if (isLegalString(valueTokenType)) {
						fLanguage = valueToken.getText();
					}
				}
			}
		}
		while (tokenizer.hasMoreTokens());
		if (fContentTypeValue != null) {
			parseContentTypeValue(fContentTypeValue);
		}
		finalToken = token;
		return finalToken;
	}

	public void testBestCase() throws Exception {
		String filename = fileLocation + "nomalDirectiveCase.jsp";
		doTestFile(filename, "ISO-8859-2", null, "text/html");
	}

	public void testMinimalPageDirective() throws Exception {
		String filename = fileLocation + "minimalPageDirective.jsp";
		doTestFile(filename, null, null, "text/html");
	}

	public void testIllFormed() throws Exception {
		String filename = fileLocation + "testIllFormed.jsp";
		doTestFile(filename, null);
	}

	public void testIllFormed2() throws Exception {
		String filename = fileLocation + "testIllFormed2.jsp";
		doTestFile(filename, "UTF-8");
	}

	public void testIllformedNormalNonDefault() throws Exception {
		String filename = fileLocation + "IllformedNormalNonDefault.jsp";
		doTestFile(filename, "ISO-8859-8", null, "text/html");
	}

	public void testEmptyFile() throws Exception {
		String filename = fileLocation + "EmptyFile.jsp";
		doTestFile(filename, null);
	}

	public void testNomalDirectiveCaseUsingXMLSyntax() throws Exception {
		String filename = fileLocation + "nomalDirectiveCaseUsingXMLSyntax.jsp";
		doTestFile(filename, "ISO-8859-2", null, "text/html");
	}

	public void testNoPageDirective() throws Exception {
		String filename = fileLocation + "testNoPageDirective.jsp";
		doTestFile(filename, null);
	}

	public void testNormalPageDirectiveWithXMLDecl() throws Exception {
		String filename = fileLocation + "nomalDirectiveCasewithXMLDecl.jsp";
		doTestFile(filename, "ISO-8859-1", null, "text/html");
	}


	public void testNoPageDirectiveAtFirst() throws Exception {
		String filename = fileLocation + "testNoPageDirectiveAtFirst.jsp";
		doTestFile(filename, "ISO-8859-2", null, "text/html");
	}

	public void testNoPageDirectiveInLargeFile() throws Exception {
		String filename = fileLocation + "testNoPageDirectiveInLargeFile.jsp";
		doTestFile(filename, null, EncodingParserConstants.MAX_CHARS_REACHED, null);
	}

	public void testNormalCaseWithNeither() throws Exception {
		String filename = fileLocation + "nomalDirectiveCaseNoEncoding.jsp";
		doTestFile(filename, null);
	}

	public void testNormalCharset() throws Exception {
		String filename = fileLocation + "nomalDirectiveCaseUsingCharset.jsp";
		doTestFile(filename, "ISO-8859-3", null, "text/html");
	}

	public String getLanguage() {
		return fLanguage;
	}
}
