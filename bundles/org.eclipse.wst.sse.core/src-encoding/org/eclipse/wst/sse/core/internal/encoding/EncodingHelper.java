/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;


/**
 * EncodingHelper is used to determine the IANA tag / java encoding from the
 * processing instruction. From this processing instruction: <?xml
 * version="1.0" encoding="UTF-8"?>getEncodingTag() would return "UTF-8" and
 * getEncoding() would return UTF8. The processing instruction is searched for
 * via an input stream, or a byte array, depending on the constructor used.
 * 
 * @deprecated - users of this class should move to base support: see
 *             iFile.getCharset (and related functions with bom detection in
 *             IContentDescription
 * 
 */
public class EncodingHelper {

	protected static final int encodingNameSearchLimit = 1000;
	protected static final org.eclipse.wst.sse.core.internal.encoding.util.SupportedJavaEncoding javaEncodingConverterHelper = new org.eclipse.wst.sse.core.internal.encoding.util.SupportedJavaEncoding();

	/**
	 * @deprecated Encoding preferences are stored on a per content type
	 *             basis. See the EncodingHelper.java class in b2butil for an
	 *             example of how to extract the default encoding for a given
	 *             content type.
	 */
	static public String getDefaultEncoding() {
		return javaEncodingConverterHelper.getJavaConverterName(getDefaultEncodingTag());
	}

	/**
	 * @deprecated Encoding preferences are stored on a per content type
	 *             basis. See the EncodingHelper.java class in b2butil for an
	 *             example of how to extract the default encoding for a given
	 *             content type.
	 */
	static public String getDefaultEncodingTag() {
		return "UTF-8"; //$NON-NLS-1$
	}

	private String encodingName = null;
	private String encodingTag;

	/**
	 * Method EncodingHelper. Determine the encoding based on the byte array
	 * passed in.
	 * 
	 * @param headerBytes
	 * @param length
	 */
	public EncodingHelper(byte[] headerBytes, int length) {
		determineEncoding(headerBytes, length);
	}

	/**
	 * Method EncodingHelper. Determine the encoding based on the input
	 * stream. Only the first 1000 bytes will be searched
	 * 
	 * @param inStream
	 */
	public EncodingHelper(InputStream inStream) {
		determineEncoding(inStream);
	}

	private void determineEncoding(byte[] headerBytes, int length) {
		try {
			if (headerBytes.length >= 4) {
				if ((headerBytes[0] == -2) && (headerBytes[1] == -1)) // Byte
				// Order
				// Mark
				// ==
				// 0xFEFF
				{
					// UTF-16, big-endian
					encodingName = "UnicodeBig"; //$NON-NLS-1$
					// encodingTag = "UTF-16";//$NON-NLS-1$
				} else if ((headerBytes[0] == -1) && (headerBytes[1] == -2)) // Byte
				// Order
				// Mark
				// ==
				// 0xFFFE
				{
					// UTF-16, little-endian
					encodingName = "UnicodeLittle"; //$NON-NLS-1$
					// encodingTag = "UTF-16";//$NON-NLS-1$
				} else if ((headerBytes[0] == -17) && (headerBytes[1] == -69) && (headerBytes[2] == -65)) // Byte
				// Order
				// Mark
				// ==
				// 0xEF
				// BB
				// BF
				{
					// UTF-8
					encodingName = "UTF8"; //$NON-NLS-1$
				}

				// // Otherwise, we need to check the document's content.
				// // ( UTF-8, US-ASCII, ISO-8859, Shift-JIS, EUC, ... )
				// if ((encodingName != null) && (encodingName.length() == 0))
				// // special
				// {
				// encodingName = getEncodingNameByAuto(smallBuffer,
				// smallBuffer.length);
				// }
				// else
				// XMLEncodingPlugin.getPlugin().getMsgLogger().write("encoding
				// name from BOM = " + encodingName);
				// System.out.println("encoding name from BOM = " +
				// encodingName);
				// Now determine the encoding tag
				encodingTag = getEncodingName(headerBytes, headerBytes.length);
				if (encodingName == null) {
					if (encodingTag != null) {
						encodingName = javaEncodingConverterHelper.getJavaConverterName(encodingTag);
					} else {
						encodingName = "UTF8"; //$NON-NLS-1$
					}
				}

				if (encodingTag == null || encodingTag.trim().equals("")) { //$NON-NLS-1$
					encodingTag = javaEncodingConverterHelper.getIANAEncodingName(encodingName);
				}
				// System.out.println("encodingTag = " + encodingTag);
			}
		} catch (UnsupportedEncodingException ex) {
			// if this classn't deprecated, this should not be ignored
		}
	}

	protected void determineEncoding(InputStream inStream) {
		try {
			// try and get at least first four bytes for auto encoding
			// detection
			byte[] headerBytes = getBytes(inStream, encodingNameSearchLimit);
			determineEncoding(headerBytes, encodingNameSearchLimit);
		} catch (IOException exception) {
			// if exception, no bytes}
		}
	}

	private byte[] getBytes(InputStream inputStream, int max) throws IOException {
		byte[] allHeaderBytes = new byte[max];
		int nRead = inputStream.read(allHeaderBytes, 0, max);

		byte[] headerBytes = null;
		if (nRead != max) {
			headerBytes = new byte[nRead];
			System.arraycopy(allHeaderBytes, 0, headerBytes, 0, nRead);
		} else {
			headerBytes = allHeaderBytes;
		}
		return headerBytes;
	}

	/**
	 * <code>getEncoding</code> Retrieve the java converter name from the
	 * document.
	 * 
	 * @return a <code>String</code> value for the java converter
	 */
	public String getEncoding() {
		return encodingName;
	}

	/**
	 * Use the encoding information in the document.
	 */
	protected String getEncodingName(byte[] string, int length) throws UnsupportedEncodingException {
		String enc = null;
		enc = getEncodingNameInDocument(string, length);
		return (enc);
	}

	protected String getEncodingNameInDocument(byte[] string, int length) throws UnsupportedEncodingException {
		String encoding = null;
		final String content;
		if (encodingName != null) {
			content = new String(string, encodingName);
		} else {
			content = new String(string); // use default Java encoder
		}

		String prologTag = "<?xml"; //$NON-NLS-1$

		String encodingKeyword = "encoding"; //$NON-NLS-1$

		int indexStart = content.indexOf(prologTag);
		if (indexStart != -1) {
			int indexEnd = content.indexOf(">", indexStart); //$NON-NLS-1$
			if (indexEnd != -1) {
				String prolog = content.substring(indexStart, indexEnd);
				int encodingStart = prolog.indexOf(encodingKeyword);
				if (encodingStart != -1) {
					String encodingString = prolog.substring(encodingStart + encodingKeyword.length());
					String delimiter = encodingString.indexOf("'") != -1 ? "'" : "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					StringTokenizer tokenizer = new StringTokenizer(encodingString, delimiter);
					String equalSign = "="; //$NON-NLS-1$
					while (tokenizer.hasMoreElements()) {
						String currToken = tokenizer.nextToken();
						if (currToken.trim().equals(equalSign)) {
							if (tokenizer.hasMoreElements()) {
								encoding = tokenizer.nextToken();
							}
							break;
						}
					}
				}

			}
		}

		if (encoding != null) {
			final int len = encoding.length();
			if (len > 0) {
				if ((encoding.charAt(0) == '"') && (encoding.charAt(len - 1) == '"')) {
					encoding = encoding.substring(1, len - 1);
				} else if ((encoding.charAt(0) == '\'') && (encoding.charAt(len - 1) == '\'')) {
					encoding = encoding.substring(1, len - 1);
				}
			}
		}
		return encoding;
	}

	/**
	 * <code>getEncodingTag</code> Retrieve the encoding tag from the
	 * document.
	 * 
	 * @return a <code>String</code> value for the encoding tag
	 */
	public String getEncodingTag() {
		return encodingTag;
	}

	public boolean isSameEncoding(String oldEncoding) {
		if (oldEncoding == null) {
			return false;
		}

		if (oldEncoding.equals(getEncoding())) {
			return true;
		}

		return false;
	}
}
