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
package org.eclipse.wst.css.tests.encoding.css;

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

import org.eclipse.wst.css.core.internal.contenttype.CSSResourceEncodingDetector;
import org.eclipse.wst.css.tests.encoding.CSSEncodingTestsPlugin;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;

public class CSSEncodingTester extends TestCase {

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
	private final String fileDir = "css/";
	// private final String pluginLocation =
	// TestsPlugin.getInstallLocation().toString();
	private final String fileHome = "testfiles/";
	private final String fileLocation = fileHome + fileDir;
	private int READ_BUFFER_SIZE = 8000;

	private void doTestFileStream(String filename, String expectedIANAEncoding, IResourceCharsetDetector detector) throws IOException {
		File file = CSSEncodingTestsPlugin.getTestFile(filename);
		if (!file.exists())
			throw new IllegalArgumentException(filename + " was not found");
		InputStream inputStream = new FileInputStream(file);
		// InputStream inStream = getClass().getResourceAsStream(filename);
		InputStream istream = getMarkSupportedStream(inputStream);
		try {
			detector.set(istream);
			EncodingMemento encodingMemento = ((CSSResourceEncodingDetector)detector).getEncodingMemento();

			String foundIANAEncoding = null;

			if (encodingMemento != null) {
				foundIANAEncoding = encodingMemento.getDetectedCharsetName();
			}
			// I changed many "equals" to "equalsIgnoreCase" on 11/4/2002,
			// since
			// some issues with SHIFT_JIS vs. Shift_JIS were causing failures.
			// We do want to be tolerant on input, and accept either, but I
			// think
			// that SupportedJavaEncodings needs to be changed to "recommend"
			// Shift_JIS.
			boolean expectedIANAResult = false;
			if (expectedIANAEncoding == null) {
				expectedIANAResult = expectedIANAEncoding == foundIANAEncoding;
			}
			else {
				expectedIANAResult = expectedIANAEncoding.equalsIgnoreCase(foundIANAEncoding);
			}

			assertTrue("encoding test file " + filename + " expected: " + expectedIANAEncoding + " found: " + foundIANAEncoding, expectedIANAResult);
			// a very simple read test ... will cause JUnit error (not fail)
			// if
			// throws exception.
			if (foundIANAEncoding != null) {
				ensureCanRead(filename, foundIANAEncoding, istream);
			}
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
	private void ensureCanRead(String filename, String encoding, InputStream inputStream) throws IOException {
		Charset charset = Charset.forName(encoding);
		CharsetDecoder charsetDecoder = charset.newDecoder();
		charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
		charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		InputStreamReader reader = new InputStreamReader(inputStream, charsetDecoder);
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

	public void testBestCase() throws IOException {
		String filename = fileLocation + "nonStandard.css";
		doTestFileStream(filename, "ISO-8859-6", new CSSResourceEncodingDetector());

	}

	public void testEmptyFile() throws IOException {
		String filename = fileLocation + "emptyFile.css";
		doTestFileStream(filename, null, new CSSResourceEncodingDetector());
	}

	public void testEUCJP() throws IOException {
		String filename = fileLocation + "encoding_test_eucjp.css";
		doTestFileStream(filename, "EUC-JP", new CSSResourceEncodingDetector());
	}

	public void testJIS() throws IOException {
		String filename = fileLocation + "encoding_test_jis.css";
		doTestFileStream(filename, "ISO-2022-JP", new CSSResourceEncodingDetector());
	}

	public void testNoEncoding() throws IOException {
		String filename = fileLocation + "noEncoding.css";
		doTestFileStream(filename, null, new CSSResourceEncodingDetector());
	}

	public void testnonStandardIllFormed() throws IOException {
		String filename = fileLocation + "nonStandardIllFormed.css";
		doTestFileStream(filename, "ISO-8859-6", new CSSResourceEncodingDetector());
	}

	public void testnonStandardIllFormed2() throws IOException {
		String filename = fileLocation + "nonStandardIllFormed2.css";
		doTestFileStream(filename, "ISO-8859-6", new CSSResourceEncodingDetector());
	}

	public void testShiftJIS() throws IOException {
		String filename = fileLocation + "encoding_test_sjis.css";
		doTestFileStream(filename, "SHIFT_JIS", new CSSResourceEncodingDetector());
	}
	
	public void testUTF16be() throws IOException {
		String filename = fileLocation + "utf16be.css";
		doTestFileStream(filename, "UTF-16BE", new CSSResourceEncodingDetector());
	}
	
	public void testUTF16le() throws IOException {
		String filename = fileLocation + "utf16le.css";
		doTestFileStream(filename, "UTF-16LE", new CSSResourceEncodingDetector());
	}
	
	public void testUTF16beMalformed() throws IOException {
		String filename = fileLocation + "utf16beMalformed.css";
		doTestFileStream(filename, "UTF-16BE", new CSSResourceEncodingDetector());
	}
	
	public void testUTF16leMalformed() throws IOException {
		String filename = fileLocation + "utf16leMalformed.css";
		doTestFileStream(filename, "UTF-16LE", new CSSResourceEncodingDetector());
	}
	
	public void testUTF16BOM() throws IOException {
		String filename = fileLocation + "utf16BOM.css";
		doTestFileStream(filename, "UTF-16", new CSSResourceEncodingDetector());
	}

}
