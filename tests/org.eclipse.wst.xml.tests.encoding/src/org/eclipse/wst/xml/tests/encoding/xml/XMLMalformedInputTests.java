/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
import java.nio.charset.MalformedInputException;

import junit.framework.TestCase;

import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;
import org.eclipse.wst.sse.core.internal.exceptions.MalformedInputExceptionWithDetail;
import org.eclipse.wst.xml.core.internal.contenttype.XMLResourceEncodingDetector;
import org.eclipse.wst.xml.tests.encoding.TestsPlugin;

/**
 * FIXME: this might be a good starting point to create a "file peeker"? But,
 * its not otherwised used -- delete if not fixed/improved soon
 * XMLMalformedInputTests
 */

public class XMLMalformedInputTests extends TestCase {
	private int READ_BUFFER_SIZE = 8000;
	private boolean DEBUG = false;


	public XMLMalformedInputTests(String name) {
		super(name);
	}

	/**
	 * Tests for a file, filename that should throw a
	 * MalformedInputExceptionWithDetail at character, expectedPosition. This
	 * happens when no encoding is specified, so the default is used, but
	 * there are characters that the default encoding does not recognize
	 */
	void doTestMalformedInput(String filename, IResourceCharsetDetector detector, int expectedPosition) throws IOException {
		Exception foundException = null;
		int badCharPosition = -1;
		File file = TestsPlugin.getTestFile(filename);
		if (!file.exists())
			throw new IllegalArgumentException(filename + " was not found");
		InputStream inputStream = new FileInputStream(file);
		InputStream istream = getMarkSupportedStream(inputStream);
		detector.set(istream);
		// IEncodedDocument doc =
		// detector.createNewStructuredDocument(filename, istream);
		EncodingMemento encodingMemento = ((XMLResourceEncodingDetector) detector).getEncodingMemento();
		String foundIANAEncoding = encodingMemento.getJavaCharsetName();

		Charset charset = Charset.forName(foundIANAEncoding);
		CharsetDecoder charsetDecoder = charset.newDecoder();
		charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
		charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		istream.close();
		inputStream.close();

		// now, try reading as per encoding
		inputStream = new FileInputStream(file);
		// skip BOM for this case
		// System.out.println(inputStream.read());
		// System.out.println(inputStream.read());
		// System.out.println(inputStream.read());
		InputStreamReader reader = new InputStreamReader(inputStream, charsetDecoder);

		try {
			// just try reading ... should throw exception
			// exception)
			readInputStream(reader);
		}
		catch (MalformedInputException e) {
			// as expected, now do detailed checking.
			inputStream.close();
			istream.close();
			inputStream = new FileInputStream(file);
			charsetDecoder = charset.newDecoder();
			charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
			charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);
			reader = new InputStreamReader(inputStream, charsetDecoder);
			istream = getMarkSupportedStream(inputStream);
			try {
				handleMalFormedInput_DetailChecking(reader, foundIANAEncoding);
			}
			catch (MalformedInputExceptionWithDetail se) {
				foundException = se;
				badCharPosition = se.getCharPosition();
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
		// handle adjustments here for VM differnces:
		// for now its either 49 or 49 + 2 BOMs (51)
		// can be smarting later.
		assertTrue("MalformedInputException was not thrown as expected for filename: " + filename + " Exception thrown:" + foundException, foundException instanceof MalformedInputExceptionWithDetail);
		assertTrue("Wrong character position detected in MalformedInputException.  Expected: " + expectedPosition + " Found: " + badCharPosition, (badCharPosition == expectedPosition) || badCharPosition == expectedPosition - 2);
	}

	// public void testXSLMalformedInput() throws IOException {
	// String filename = fileLocation + "MalformedNoEncoding.xsl";
	// doTestMalformedInput(filename, new XMLResourceEncodingDetector(), 211);
	// }

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
			if (DEBUG) {
				System.out.println(tBuff[0]);
			}
			buffer.append(tBuff, 0, numRead);
		}
		return buffer;
	}

	/*
	 * removed for PPC machine with IBM VM
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=126503
	 */
	// public void testXMLMalformedInput() throws IOException {
	// String filename = fileLocation + "MalformedNoEncoding.xml";
	// doTestMalformedInput(filename, new XMLResourceEncodingDetector(), 51);
	// }
	// since above test was only one im this class, put in this no op to avoid
	// a failure due to no tests in class!
	public void testNoOp() {
		assertTrue(true);
	}

	private void handleMalFormedInput_DetailChecking(Reader reader, String encodingName) throws IOException, MalformedInputExceptionWithDetail {
		int charPostion = -1;
		charPostion = getCharPostionOfFailure(reader);
		// all of that just to throw more accurate error
		// note: we do the conversion to ianaName, instead of using the local
		// variable,
		// because this is ultimately only for the user error message (that
		// is,
		// the error occurred
		// in context of javaEncodingName no matter what ianaEncodingName is
		throw new MalformedInputExceptionWithDetail(encodingName, charPostion);
	}

	private int getCharPostionOfFailure(Reader reader) throws IOException {
		int charPosition = 1;
		int charRead = -1;
		int result = -1;
		boolean errorFound = false;
		do {
			try {
				if (reader.ready()) {
					charRead = reader.read();
				}
				if (DEBUG) {
					System.out.println(charPosition + ": " + escape((char) charRead, true));
				}
				charPosition++;
			}
			catch (MalformedInputException e) {
				// this is expected, since we're expecting failure,
				// so no need to do anything.
				errorFound = true;
				break;
			}
		}
		while ((charRead != -1 && !errorFound) && reader.ready());

		if (errorFound)
			result = charPosition;
		else
			result = -1;
		return result;
	}

	private String escape(char aChar, boolean escapeSpace) {

		StringBuffer outBuffer = new StringBuffer();
		switch (aChar) {
			case ' ' :
				if (escapeSpace)
					outBuffer.append('\\');

				outBuffer.append(' ');
				break;
			case '\\' :
				outBuffer.append('\\');
				outBuffer.append('\\');
				break;
			case '\t' :
				outBuffer.append('\\');
				outBuffer.append('t');
				break;
			case '\n' :
				outBuffer.append('\\');
				outBuffer.append('n');
				break;
			case '\r' :
				outBuffer.append('\\');
				outBuffer.append('r');
				break;
			case '\f' :
				outBuffer.append('\\');
				outBuffer.append('f');
				break;
			default :
				if ((aChar < 0x0020) || (aChar > 0x007e)) {
					outBuffer.append('\\');
					outBuffer.append('u');
					outBuffer.append(toHex((aChar >> 12) & 0xF));
					outBuffer.append(toHex((aChar >> 8) & 0xF));
					outBuffer.append(toHex((aChar >> 4) & 0xF));
					outBuffer.append(toHex(aChar & 0xF));
				}
				else {
					if (specialSaveChars.indexOf(aChar) != -1)
						outBuffer.append('\\');
					outBuffer.append(aChar);
				}
		}

		return outBuffer.toString();
	}

	/**
	 * Convert a nibble to a hex character
	 * 
	 * @param nibble
	 *            the nibble to convert.
	 */
	private static char toHex(int nibble) {
		return hexDigit[(nibble & 0xF)];
	}

	/** A table of hex digits */
	private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static final String specialSaveChars = "=: \t\r\n\f#!";

}
