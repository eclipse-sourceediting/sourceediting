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

import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;
import org.eclipse.wst.xml.core.internal.contenttype.XMLResourceEncodingDetector;
import org.eclipse.wst.xml.tests.encoding.TestsPlugin;


public class XMLEncodingTests extends TestCase {
	private int READ_BUFFER_SIZE = 8000;
	private boolean DEBUG = false;
	private final String fileRoot = "testfiles/";
	private final String fileDir = "xml/";
	private final String fileLocation = fileRoot + fileDir;

	public XMLEncodingTests(String name) {
		super(name);
	}

	private void doTestFileStream(String filename, String expectedIANAEncoding, IResourceCharsetDetector detector) throws IOException {
		File file = TestsPlugin.getTestFile(filename);
		if (!file.exists())
			throw new IllegalArgumentException(filename + " was not found");
		InputStream inputStream = new FileInputStream(file);
		//InputStream inStream = getClass().getResourceAsStream(filename);
		InputStream istream = getMarkSupportedStream(inputStream);
		try {
			detector.set(istream);
			EncodingMemento encodingMemento = ((XMLResourceEncodingDetector)detector).getEncodingMemento();
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
			// a very simple read test ... will cause JUnit error (not fail) if throws exception.
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

	public void testXMLEmptyFile() throws IOException {
		String filename = fileLocation + "EmptyFile.xml";
		doTestFileStream(filename, "UTF-8", new XMLResourceEncodingDetector());
	}

	public void testXMLIllformedNormalNonDefault() throws IOException {
		String filename = fileLocation + "IllformedNormalNonDefault.xml";
		String ianaInFile = "ISO-8859-1";
		doTestFileStream(filename, ianaInFile, new XMLResourceEncodingDetector());
	}

	public void testXMLNormalNonDefault() throws IOException {
		String filename = fileLocation + "NormalNonDefault.xml";
		String ianaInFile = "ISO-8859-1";
		doTestFileStream(filename, ianaInFile, new XMLResourceEncodingDetector());
	}

	public void testXMLNoEncoding() throws IOException {
		String filename = fileLocation + "NoEncoding.xml";
		doTestFileStream(filename, "UTF-8", new XMLResourceEncodingDetector());
	}

	/**
	 * Caution, when this file prints out in console (when debug set to true, it appears 
	 * incorrect (due to font problems in console).
	 */
	public void testUTF16() throws IOException {
		String filename = fileLocation + "utf16WithJapaneseChars.xml";
		doTestFileStream(filename, "UTF-16", new XMLResourceEncodingDetector());
	}

	/**
	 * This test shows unicode BOM should take priority over settings/defaults
	 */
	public void testUtf16UnicodeStreamWithNoEncodingInHeader() throws IOException {
		String filename = fileLocation + "utf16UnicodeStreamWithNoEncodingInHeader2.xml";
		doTestFileStream(filename, "UTF-16", new XMLResourceEncodingDetector());
	}

	/**
	 * This test shows unicode BOM should take priority over settings/defaults
	 * Note: UTF-16 == UTF-16BE
	 */
	public void testUtf16UnicodeStreamWithNoEncodingInHeaderBE() throws IOException {
		String filename = fileLocation + "utf16UnicodeStreamWithNoEncodingInHeaderBE.xml";
		doTestFileStream(filename, "UTF-16", new XMLResourceEncodingDetector());
	}

	public void testUTF83ByteBOM() throws IOException {
		String filename = fileLocation + "UTF8With3ByteBOM.xml";
		doTestFileStream(filename, "UTF-8", new XMLResourceEncodingDetector());
	}
	
	public void testUTF16BE() throws IOException {
		String filename = fileLocation + "utf16be.xml";
		doTestFileStream(filename, "UTF-16BE", new XMLResourceEncodingDetector());
	}
	
	public void testUTF16LE() throws IOException {
		String filename = fileLocation + "utf16le.xml";
		doTestFileStream(filename, "UTF-16LE", new XMLResourceEncodingDetector());
	}
	
	public void testUTF16LEMalformed() throws IOException {
		String filename = fileLocation + "utf16leMalformed.xml";
		doTestFileStream(filename, "UTF-16LE", new XMLResourceEncodingDetector());
	}
	
	public void testUTF16BEMalformed() throws IOException {
		String filename = fileLocation + "utf16beMalformed.xml";
		doTestFileStream(filename, "UTF-16BE", new XMLResourceEncodingDetector());
	}

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

	private StringBuffer readInputStream(Reader reader) throws IOException {

		StringBuffer buffer = new StringBuffer();
		int numRead = 0;
		char tBuff[] = new char[READ_BUFFER_SIZE];
		while ((numRead = reader.read(tBuff, 0, tBuff.length)) != -1) {
			buffer.append(tBuff, 0, numRead);
		}
		return buffer;
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

}
