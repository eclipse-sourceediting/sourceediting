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
package org.eclipse.jst.jsp.tests.encoding.jsp;

import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.contenttype.HeadParserToken;
import org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizer;
import org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizerConstants;
import org.eclipse.jst.jsp.tests.encoding.JSPEncodingTestsPlugin;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;
import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizerConstants;

public class JSPHeadTokenizerTester extends TestCase {
	boolean DEBUG = false;
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
		try {
			doTestFile(JSPEncodingTestsPlugin.getTestReader(filename), expectedName, finalTokenType, expectedContentType);
		}
		catch (IOException e) {
			System.out.println("Error opening file \"" + filename +"\"");
		}
	}

	private void doTestFile(Reader fileReader, String expectedName, String finalTokenType, String expectedContentType) throws Exception {
		JSPHeadTokenizer tokenizer = null;
		tokenizer = new JSPHeadTokenizer(fileReader);
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
		/*
		 * Based partially on
		 * org.eclipse.jst.jsp.core.internal.document.PageDirectiveAdapterImpl
		 * .getMimeTypeFromContentTypeValue(String) , divides the full value
		 * into segments according to ';', assumes the first specifies the
		 * content type itself if it has no '=', and that the remainder are
		 * parameters which may specify a charset
		 */
		
		String cleanContentTypeValue = StringUtils.stripNonLetterDigits(contentType);
		/* Break the mime header into the main value and its parameters, separated by ';' */
		StringTokenizer tokenizer = new StringTokenizer(cleanContentTypeValue, ";"); //$NON-NLS-1$
		int tLen = tokenizer.countTokens();
		if (tLen == 0)
			return;
		String[] tokens = new String[tLen];
		int j = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens[j] = tokenizer.nextToken();
			j++;
		}
		
		int firstParameter = 0;
		if (tokens[0].indexOf('=') == -1) {
			/*
			 * no equal sign in the first segment, so assume it indicates a
			 * content type properly
			 */
			fContentType = tokens[0].trim();
			firstParameter = 1;
		}
		/*
		 * now handle parameters as name=value pairs, looking for "charset"
		 * specifically
		 */
		Pattern equalPattern = Pattern.compile("\\s*=\\s*"); //$NON-NLS-1$
		for (int i = firstParameter; i < tokens.length; i++) {
			String[] pair = equalPattern.split(tokens[i]);
			if (pair.length < 2)
				continue;
			if (pair[0].trim().equals("charset")) { //$NON-NLS-1$
				fCharset = pair[1].trim();
			}
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
		fContentTypeValue = null;
		HeadParserToken token = null;
		HeadParserToken finalToken = null;
		do {
			token = tokenizer.getNextToken();
			String tokenType = token.getType();

			if(canHandleAsUnicodeStream(tokenType)) {
				
			}
			else if (tokenType == XMLHeadTokenizerConstants.XMLDelEncoding) {
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
	
	public void testUTF16le() throws Exception {
		String filename = fileLocation + "utf16le.jsp";
		doTestFile(filename, "UTF-16LE", null, "text/html");
	}
	
	public void testUTF16be() throws Exception {
		String filename = fileLocation + "utf16be.jsp";
		doTestFile(filename, "UTF-16BE", null, "text/html");
	}
	
	/*
		sun.io.MalformedInputException
		at sun.io.ByteToCharUTF8.convert(ByteToCharUTF8.java:262)
		at sun.nio.cs.StreamDecoder$ConverterSD.convertInto(StreamDecoder.java:314)
		at sun.nio.cs.StreamDecoder$ConverterSD.implRead(StreamDecoder.java:364)
		at sun.nio.cs.StreamDecoder.read(StreamDecoder.java:250)
		at java.io.InputStreamReader.read(InputStreamReader.java:212)
		at org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizer.zzRefill(JSPHeadTokenizer.java:359)
		at org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizer.primGetNextToken(JSPHeadTokenizer.java:598)
		at org.eclipse.jst.jsp.core.internal.contenttype.JSPHeadTokenizer.getNextToken(JSPHeadTokenizer.java:254)
		at org.eclipse.jst.jsp.tests.encoding.jsp.JSPHeadTokenizerTester.parseHeader(JSPHeadTokenizerTester.java:182)
		at org.eclipse.jst.jsp.tests.encoding.jsp.JSPHeadTokenizerTester.doTestFile(JSPHeadTokenizerTester.java:58)
		at org.eclipse.jst.jsp.tests.encoding.jsp.JSPHeadTokenizerTester.testUTF16BOM(JSPHeadTokenizerTester.java:324)
	*/

	public void testUTF16BOM() throws Exception {
		/*String filename = fileLocation + "utf16BOM.jsp";
		ZippedTest test = new ZippedTest();
		test.setUp();
		IFile file = test.getFile(filename);
		assertNotNull(file);
		Reader fileReader = new FileReader(file.getLocationURI().getPath());
		doTestFile(fileReader, "UTF-16", null, null);
		test.shutDown();*/
	}
	
	public void testUTF16leXmlStyle() throws Exception {
		String filename = fileLocation + "utf16le_xmlStyle.jsp";
		doTestFile(filename, "UTF-16LE", null, null);
	}

	public String getLanguage() {
		return fLanguage;
	}
}
