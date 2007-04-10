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
package org.eclipse.wst.xml.tests.encoding.write;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.encoding.CodedStreamCreator;
import org.eclipse.wst.sse.core.internal.encoding.EncodingRule;

public class TestCodedWrite extends TestCase {
	/** A table of hex digits */
	private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	private static StringBuffer sBuff = new StringBuffer(2);

	private static String byteToHex(byte bytechar) {
		sBuff.setLength(0);
		int low = bytechar & 0xF;
		int hi = (bytechar >> 4) & 0xF;
		sBuff.append(toHex(hi));
		sBuff.append(toHex(low));
		return sBuff.toString();
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

	private boolean DEBUG = false;

	/**
	 *  
	 */
	public TestCodedWrite() {
		super();
	}

	/**
	 * @param name
	 */
	public TestCodedWrite(String name) {
		super(name);
	}

	public void testSimple() throws UnsupportedEncodingException, CoreException, IOException {
		String jsp = "<%@ page contentType=\"text/html; charset=ISO-8859-3\"%>";
		CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
		codedStreamCreator.set("dummy.jsp", jsp);
		ByteArrayOutputStream outputStream = codedStreamCreator.getCodedByteArrayOutputStream(EncodingRule.CONTENT_BASED);
		if (DEBUG) {
			debugPrint("testSimple", jsp, "ISO-8859-3", outputStream);
		}
		assertNotNull(outputStream);

	}

	private void debugPrint(String testname, String originalString, String encoding, ByteArrayOutputStream outputStream) throws UnsupportedEncodingException {
		System.out.println();
		System.out.println(testname);
		byte[] bytes = outputStream.toByteArray();
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(byteToHex(bytes[i]));
		}
		System.out.println();
		if (encoding == null) {
			System.out.println(new String(bytes));

		}
		else {
			System.out.println(new String(bytes, encoding));
		}
	}

	public void testSimpleUTF16BE() throws UnsupportedEncodingException, CoreException, IOException {
		String jsp = "<%@ page contentType=\"text/html; charset=UTF-16BE\"%>";
		CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
		codedStreamCreator.set("dummy.jsp", jsp);
		ByteArrayOutputStream outputStream = codedStreamCreator.getCodedByteArrayOutputStream(EncodingRule.CONTENT_BASED);
		if (DEBUG) {
			debugPrint("testSimpleUTF16BE", jsp, "UTF-16BE", outputStream);
		}
		assertNotNull(outputStream);

	}

	public void testSimpler() throws UnsupportedEncodingException, CoreException, IOException {
		CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-16\"?><tag>test text</tag>";
		codedStreamCreator.set("dummy.xml", xmlString);
		ByteArrayOutputStream outputStream = codedStreamCreator.getCodedByteArrayOutputStream(EncodingRule.CONTENT_BASED);
		if (DEBUG) {
			debugPrint("testSimpler", xmlString, "UTF-16", outputStream);
		}
		assertNotNull(outputStream);

	}

	public void testSimplest() throws UnsupportedEncodingException, CoreException, IOException {
		CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
		String text = "test text";
		codedStreamCreator.set("dummy.xml", text);
		ByteArrayOutputStream outputStream = codedStreamCreator.getCodedByteArrayOutputStream(EncodingRule.CONTENT_BASED);
		if (DEBUG) {
			debugPrint("testSimplest", text, null, outputStream);
		}
		assertNotNull(outputStream);

	}
}
