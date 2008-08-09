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
package org.eclipse.wst.html.tests.encoding.html;

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

import org.eclipse.wst.html.core.internal.contenttype.HTMLResourceEncodingDetector;
import org.eclipse.wst.html.tests.encoding.HTMLEncodingTestsPlugin;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;

public class HTMLEncodingTests extends TestCase {

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
	private final String fileDir = "html/";
	private final String fileHome = "testfiles/";
	private final String fileLocation = fileHome + fileDir;
	private int READ_BUFFER_SIZE = 8000;

	public HTMLEncodingTests(String name) {
		super(name);
	}

	private void doTestFileStream(String filename, String expectedIANAEncoding, IResourceCharsetDetector detector) throws IOException {
		File file = HTMLEncodingTestsPlugin.getTestFile(filename);
		if (!file.exists())
			throw new IllegalArgumentException(filename + " was not found");
		InputStream inputStream = new FileInputStream(file);
		// InputStream inStream = getClass().getResourceAsStream(filename);
		InputStream istream = getMarkSupportedStream(inputStream);
		try {
			detector.set(istream);
			EncodingMemento encodingMemento = ((HTMLResourceEncodingDetector)detector).getEncodingMemento();
			String foundIANAEncoding = null;
			if (encodingMemento != null) {
				foundIANAEncoding = encodingMemento.getJavaCharsetName();
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
				expectedIANAResult = (expectedIANAEncoding == foundIANAEncoding);
			}
			else {
				expectedIANAResult = expectedIANAEncoding.equalsIgnoreCase(foundIANAEncoding);
			}

			assertTrue("encoding test file " + filename + " expected: " + expectedIANAEncoding + " found: " + foundIANAEncoding, expectedIANAResult);
			// a very simple read test ... will cause JUnit error (not fail)
			// if
			// throws exception.
			if (expectedIANAEncoding != null) {
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

	public void testEmptyFile() throws IOException {
		String filename = fileLocation + "EmptyFile.html";
		// HTML has no spec default encoding. Will use platform default encoding.
		doTestFileStream(filename, System.getProperty("file.encoding"), new HTMLResourceEncodingDetector());
	}

	public void testIllformedNormalNonDefault() throws IOException {
		String filename = fileLocation + "IllformedNormalNonDefault.html";
		String ianaInFile = "UTF-8";
		doTestFileStream(filename, ianaInFile, new HTMLResourceEncodingDetector());
	}

	public void testMultiNonDefault() throws IOException {
		String filename = fileLocation + "MultiNonDefault.html";
		doTestFileStream(filename, "ISO-8859-6", new HTMLResourceEncodingDetector());
	}

	public void testNoEncoding() throws IOException {
		String filename = fileLocation + "NoEncoding.html";
		// HTML has no spec default encoding. Will use platform default encoding.
		doTestFileStream(filename, System.getProperty("file.encoding"), new HTMLResourceEncodingDetector());
	}

	public void testnoquotes() throws IOException {
		String filename = fileLocation + "noquotes.html";
		doTestFileStream(filename, "UTF-8", new HTMLResourceEncodingDetector());

	}

	public void testNormalNonDefault() throws IOException {
		String filename = fileLocation + "NormalNonDefault.html";
		String ianaInFile = "UTF-8";
		doTestFileStream(filename, ianaInFile, new HTMLResourceEncodingDetector());
	}
	
	public void testUTF16BE() throws IOException {
		String filename = fileLocation + "utf16be.html";
		doTestFileStream(filename, "UTF-16BE", new HTMLResourceEncodingDetector());
	}
	
	public void testUTF16LE() throws IOException {
		String filename = fileLocation + "utf16le.html";
		doTestFileStream(filename, "UTF-16LE", new HTMLResourceEncodingDetector());
	}
	
	public void testUTF16LENoQuotes() throws IOException {
		String filename = fileLocation + "noquotesUTF16le.html";
		doTestFileStream(filename, "UTF-16LE", new HTMLResourceEncodingDetector());
	}
	
	public void testUTF16BOM() throws IOException {
		String filename = this.fileLocation + "utf16BOM.html";
		doTestFileStream(filename, "UTF-16", new HTMLResourceEncodingDetector());
	}

}
