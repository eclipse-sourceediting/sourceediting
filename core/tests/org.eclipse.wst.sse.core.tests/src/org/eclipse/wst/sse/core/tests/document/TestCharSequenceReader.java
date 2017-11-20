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
package org.eclipse.wst.sse.core.tests.document;

import java.io.IOException;

import org.eclipse.wst.sse.core.internal.text.CharSequenceReader;

import junit.framework.TestCase;



public class TestCharSequenceReader extends TestCase {


	public TestCharSequenceReader() {
		super();
	}


	public TestCharSequenceReader(String name) {
		super(name);
	}

	String fStandardString = "test123";

	public void testStandardBeginning() {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 0, 3);
		char char1 = (char) reader.read();
		assertTrue(char1 == 't');
		char char2 = (char) reader.read();
		assertTrue(char2 == 'e');
		char char3 = (char) reader.read();
		assertTrue(char3 == 's');
		int eof = reader.read();
		assertTrue(eof == -1);
	}

	public void testStandardMiddle() {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 2, 3);
		char char1 = (char) reader.read();
		assertTrue(char1 == 's');
		char char2 = (char) reader.read();
		assertTrue(char2 == 't');
		char char3 = (char) reader.read();
		assertTrue(char3 == '1');
		int eof = reader.read();
		assertTrue(eof == -1);
	}

	public void testStandardEnd() {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 4, 3);
		char char1 = (char) reader.read();
		assertTrue(char1 == '1');
		char char2 = (char) reader.read();
		assertTrue(char2 == '2');
		char char3 = (char) reader.read();
		assertTrue(char3 == '3');
		int eof = reader.read();
		assertTrue(eof == -1);
	}

	public void testStandardOutOfRange() {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 50, 3);
		int eof = reader.read();
		assertTrue(eof == -1);
	}

	public void testStandardPartiallyOutOfRange() {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 5, 3);
		char char1 = (char) reader.read();
		assertTrue(char1 == '2');
		char char2 = (char) reader.read();
		assertTrue(char2 == '3');
		int eof = reader.read();
		assertTrue(eof == -1);
	}

	public void testBufferBegining() throws IOException {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 0, 3);
		String targetString = "tes";
		
		// intentionally small sized buffer
		char[] charbuffer = new char[2];
		StringBuffer stringBuffer = new StringBuffer();
		int nRead = 0;
		while (nRead != -1) {
			nRead = reader.read(charbuffer);
			if (nRead > 0) {
				stringBuffer.append(charbuffer, 0, nRead);
			}
		}
		String testString = stringBuffer.toString();
		
		assertEquals(targetString, testString);
		
	}
	public void testBufferEnd() throws IOException {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 4, 3);
		String targetString = "123";
		
		// intentionally small sized buffer
		char[] charbuffer = new char[2];
		StringBuffer stringBuffer = new StringBuffer();
		int nRead = 0;
		while (nRead != -1) {
			nRead = reader.read(charbuffer);
			if (nRead > 0) {
				stringBuffer.append(charbuffer, 0, nRead);
			}
		}
		String testString = stringBuffer.toString();
		
		assertEquals(targetString, testString);
		
	}	public void testBufferEndEnlarged() throws IOException {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 4, 3);
		String targetString = "123";
		
		// intentionally small sized buffer
		char[] charbuffer = new char[200];
		StringBuffer stringBuffer = new StringBuffer();
		int nRead = 0;
		while (nRead != -1) {
			nRead = reader.read(charbuffer);
			if (nRead > 0) {
				stringBuffer.append(charbuffer, 0, nRead);
			}
		}
		String testString = stringBuffer.toString();
		
		assertEquals(targetString, testString);
		
	}
	public void testBufferBeginingEnlarged() throws IOException {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 0, 3);
		String targetString = "tes";
		
		// intentionally small sized buffer
		char[] charbuffer = new char[200];
		StringBuffer stringBuffer = new StringBuffer();
		int nRead = 0;
		while (nRead != -1) {
			nRead = reader.read(charbuffer);
			if (nRead > 0) {
				stringBuffer.append(charbuffer, 0, nRead);
			}
		}
		String testString = stringBuffer.toString();
		
		assertEquals(targetString, testString);
		
	}
	public void testBufferBeginingReduced() throws IOException {
		CharSequenceReader reader = new CharSequenceReader(fStandardString, 0, 3);
		String targetString = "te";
		
		// intentionally small sized buffer
		char[] charbuffer = new char[2];
		StringBuffer stringBuffer = new StringBuffer();
		int nRead = 0;
		//while (nRead != -1) {
			nRead = reader.read(charbuffer, 0, 2);
			if (nRead > 0) {
				stringBuffer.append(charbuffer, 0, nRead);
			}
		//}
		String testString = stringBuffer.toString();
		
		assertEquals(targetString, testString);
		/// now continue reading same reader
		targetString = "s";
		stringBuffer = new StringBuffer();
		nRead = 0;
		//while (nRead != -1) {
			nRead = reader.read(charbuffer, 0, 2);
			if (nRead > 0) {
				stringBuffer.append(charbuffer, 0, nRead);
			}
		//}
		testString = stringBuffer.toString();
		assertEquals(targetString, testString);
		
	}

}