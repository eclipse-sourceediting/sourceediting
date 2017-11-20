/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.tests.encoding.jsp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import junit.framework.TestCase;

import org.eclipse.jst.jsp.core.internal.contenttype.JSPResourceEncodingDetector;
import org.eclipse.jst.jsp.tests.encoding.JSPEncodingTestsPlugin;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;

public class JSPEncodingTests extends TestCase {

	/**
	 * Ensures that an InputStream has mark/reset support.
	 */
	private static InputStream getMarkSupportedStream(InputStream original) {
		if (original == null)
			return null;
		if (original.markSupported())
			return original;
		return new BufferedInputStream(original);
	}
	private boolean DEBUG = false;
	private final String fileDir = "jsp/";
	private final String fileHome = "testfiles/";
	private final String fileLocation = fileHome + fileDir;
	private int READ_BUFFER_SIZE = 8000;

	public JSPEncodingTests(String name) {
		super(name);
	}

	private void doTestFileStream(String filename, String expectedIANAEncoding, IResourceCharsetDetector detector) throws IOException {
		File file = JSPEncodingTestsPlugin.getTestFile(filename);
		if (!file.exists())
			throw new IllegalArgumentException(filename + " was not found");
		InputStream inputStream = new FileInputStream(file);
		// InputStream inStream = getClass().getResourceAsStream(filename);
		InputStream istream = getMarkSupportedStream(inputStream);
		try {
			detector.set(istream);
			EncodingMemento encodingMemento = ((JSPResourceEncodingDetector)detector).getEncodingMemento();
			String foundIANAEncoding = encodingMemento.getJavaCharsetName();
			// I changed many "equals" to "equalsIgnoreCase" on 11/4/2002,
			// since
			// some issues with SHIFT_JIS vs. Shift_JIS were causing failures.
			// We do want to be tolerant on input, and accept either, but I
			// think
			// that SupportedJavaEncodings needs to be changed to "recommend"
			// Shift_JIS.
			boolean expectedIANAResult = false;
			expectedIANAResult = expectedIANAEncoding.equalsIgnoreCase(foundIANAEncoding);

			assertTrue("encoding test file " + filename + " expected: " + expectedIANAEncoding + " found: " + foundIANAEncoding, expectedIANAResult);
			// a very simple read test ... will cause JUnit error (not fail)
			// if throws exception.
			ensureCanRead(filename, foundIANAEncoding, istream);
		}
		finally {
			if (istream != null) {
				istream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	/**
	 * This method just reads to stream, to be sure it can be read per
	 * encoding, without exception.
	 */
	private void ensureCanRead(String filename, String encoding, InputStream inStream) throws IOException {
		Charset charset = Charset.forName(encoding);
		CharsetDecoder charsetDecoder = charset.newDecoder();
		charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
		charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		InputStreamReader reader = new InputStreamReader(inStream, charsetDecoder);
		StringBuffer stringBuffer = readInputStream(reader);
		if (DEBUG) {
			System.out.println();
			System.out.println(filename);
			System.out.println(stringBuffer.toString());
		}
	}

	private StringBuffer readInputStream(Reader reader) throws IOException {

		StringBuffer buffer = new StringBuffer();
		int numRead = 0;
		char tBuff[] = new char[READ_BUFFER_SIZE];
		while ((numRead = reader.read(tBuff, 0, tBuff.length)) != -1) {
			buffer.append(tBuff, 0, numRead);
		}
		return buffer;
	}

	public void testdefect4205wpsjsp() throws IOException {
		String filename = fileLocation + "defect_4205_wps.jsp";
		doTestFileStream(filename, "UTF-8", new JSPResourceEncodingDetector());
	}

	public void testJSPEmptyFile() throws IOException {
		String filename = fileLocation + "EmptyFile.jsp";
		doTestFileStream(filename, "ISO-8859-1", new JSPResourceEncodingDetector());
	}

	/**
	 * Caution, when this file prints out in console (when debug set to true,
	 * it appears incorrect (due to font problems in console).
	 */
	public void testUTF16() throws IOException {
		String filename = fileLocation + "testUTF16.jsp";
		// [228366] Encoding changes: For UTF-16 with BOM, the expected should be UTF-16
		doTestFileStream(filename, "UTF-16", new JSPResourceEncodingDetector());
	}

	/**
	 * This test shows unicode BOM should take priority over settings/defaults
	 */
	public void testUtf16UnicodeStreamWithNoEncodingInHeader() throws IOException {
		String filename = fileLocation + "utf16UnicodeStreamWithNoEncodingInHeader2.jsp";
		doTestFileStream(filename, "UTF-16", new JSPResourceEncodingDetector());
	}

	/**
	 * This test shows unicode BOM should take priority over settings/defaults
	 * Note: UTF-16 == UTF-16BE
	 */
	public void testUtf16UnicodeStreamWithNoEncodingInHeaderBE() throws IOException {
		String filename = fileLocation + "utf16UnicodeStreamWithNoEncodingInHeaderBE.jsp";
		// [228366] Encoding changes: For UTF-16 with BOM, the expected should be UTF-16
		doTestFileStream(filename, "UTF-16", new JSPResourceEncodingDetector());
	}

	public void testUTF16WithJapaneseChars() throws IOException {
		String filename = fileLocation + "utf16WithJapaneseChars.jsp";
		// [228366] Encoding changes: For UTF-16 with BOM, the expected should be UTF-16
		doTestFileStream(filename, "UTF-16", new JSPResourceEncodingDetector());
	}

	public void testUTF83ByteBOM() throws IOException {
		String filename = fileLocation + "UTF8With3ByteBOM.jsp";
		doTestFileStream(filename, "UTF-8", new JSPResourceEncodingDetector());
	}

	public void testXMLIllformedNormalNonDefault() throws IOException {
		String filename = fileLocation + "IllformedNormalNonDefault.jsp";
		String ianaInFile = "ISO-8859-8";
		doTestFileStream(filename, ianaInFile, new JSPResourceEncodingDetector());
	}

	public void testXMLNoEncoding() throws IOException {
		String filename = fileLocation + "noEncoding.jsp";
		doTestFileStream(filename, "ISO-8859-1", new JSPResourceEncodingDetector());
	}

	public void testXMLNormalNonDefault() throws IOException {
		String filename = fileLocation + "NormalNonDefault.jsp";
		String ianaInFile = "ISO-8859-8";
		doTestFileStream(filename, ianaInFile, new JSPResourceEncodingDetector());
	}
	
	public void testUTF16le() throws IOException {
		String filename = fileLocation + "utf16le.jsp";
		doTestFileStream(filename, "UTF-16LE", new JSPResourceEncodingDetector());
	}
	
	public void testUTF16be() throws IOException {
		String filename = fileLocation + "utf16be.jsp";
		doTestFileStream(filename, "UTF-16BE", new JSPResourceEncodingDetector());
	}
	
	public void testUTF16BOM() throws IOException {
		String filename = fileLocation + "utf16BOM.jsp";
		doTestFileStream(filename, "UTF-16", new JSPResourceEncodingDetector());
	}
	
	public void testUTF16leXmlStyle() throws IOException {
		String filename = fileLocation + "utf16le_xmlStyle.jsp";
		doTestFileStream(filename, "UTF-16LE", new JSPResourceEncodingDetector());
	}
}
