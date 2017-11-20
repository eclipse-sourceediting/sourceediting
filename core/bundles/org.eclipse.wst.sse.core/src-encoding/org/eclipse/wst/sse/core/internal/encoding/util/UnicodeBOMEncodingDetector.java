/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.encoding.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;


/**
 * This is a "common function" class to decide if an input stream, is a
 * unicode stream.
 */
public class UnicodeBOMEncodingDetector implements IResourceCharsetDetector {

	//private static final String UTF_16_CHARSET_NAME = "UTF-16";
	// //$NON-NLS-1$

	public static class NotEnoughInputForBOMException extends IOException {

		/**
		 * Default <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;

		public NotEnoughInputForBOMException() {
			super();
		}

		public NotEnoughInputForBOMException(String s) {
			super(s);
		}

	}

	private final static byte BB = (byte) 0xBB;
	private final static byte BF = (byte) 0xBF;
	private final static byte EF = (byte) 0xEF;
	private final static byte FE = (byte) -2;

	private final static byte FF = (byte) -1;
	private static final String UTF_16BE_CHARSET_NAME = "UTF-16BE"; //$NON-NLS-1$
	private static final String UTF_16LE_CHARSET_NAME = "UTF-16LE"; //$NON-NLS-1$

	private static final String UTF_8_CHARSET_NAME = "UTF-8"; //$NON-NLS-1$

	private InputStream fInputStream = null;
	private boolean fNoBOMPossible;

	private EncodingMemento checkForBOM(InputStream inputStream) {
		EncodingMemento result = null;

		try {
			byte b1 = getNextByte(inputStream);
			byte b2 = getNextByte(inputStream);
			if (b1 == FE && b2 == FF) {
				result = createEncodingMemento(UTF_16BE_CHARSET_NAME);
				result.setUnicodeStream(true);
			} else {
				if (b1 == FF && b2 == FE) {
					result = createEncodingMemento(UTF_16LE_CHARSET_NAME);
					result.setUnicodeStream(true);
				} else {
					byte b3 = getNextByte((inputStream));
					if (b1 == EF && b2 == BB && b3 == BF) {
						result = createEncodingMemento(UTF_8_CHARSET_NAME);
						result.setUTF83ByteBOMUsed(true);
					}
				}
			}
		} catch (NotEnoughInputForBOMException e) {
			// This is sort of unexpected for normal cases, but can occur for
			// empty
			// streams. And, this can occur "normally" for non-BOM streams
			// that
			// have only two
			// bytes, and for which those two bytes match the first two bytes
			// of UTF-8
			// BOM In any case, we'll simply return null;
			result = null;
		} catch (IOException e) {
			// other errors should be impossible
			throw new Error(e);
		}

		return result;
	}

	private EncodingMemento createEncodingMemento(String javaEncodingName) {
		EncodingMemento encodingMemento = new EncodingMemento();
		encodingMemento.setJavaCharsetName(javaEncodingName);
		String ianaName = Charset.forName(javaEncodingName).name();
		encodingMemento.setDetectedCharsetName(ianaName);
		if (javaEncodingName.equals(UTF_8_CHARSET_NAME)) {
			encodingMemento.setUTF83ByteBOMUsed(true);
		}
		return encodingMemento;
	}

	public String getEncoding() throws IOException {

		return getEncodingMemento().getDetectedCharsetName();
	}

	/**
	 * Returns IANA encoding name if BOM detected in stream. If a BOM is
	 * detected, the stream is left positioned after readying the BOM. If a
	 * BOM is not detected, the steam is reset.
	 * 
	 * 0xFEFF UTF-16, big-endian 0xFFFE UTF-16, little-endian 0xEFBBBF UTF-8
	 * (BOM is optional)
	 * 
	 * @param inputStream -
	 *            must be a resetable (mark supported) stream so it can be
	 *            reset, if not BOM encoded stream
	 * @return String - IANA encodingname (may not work well on 1.3, but 1.4
	 *         seems to have good support for IANA names)
	 */
	public EncodingMemento getEncodingMemento() {

		EncodingMemento result = null;
		if (!fNoBOMPossible) {

			if (fInputStream == null)
				throw new IllegalStateException("input must be set before use"); //$NON-NLS-1$

			if (!fInputStream.markSupported()) {
				throw new IllegalArgumentException("inputStream must be resetable"); //$NON-NLS-1$
			}

			result = checkForBOM(fInputStream);
		}

		return result;

	}

	private byte getNextByte(InputStream inputStream) throws IOException {

		int byteCharAsInt = -1;
		// be sure we won't block
		if (inputStream.available() > 0) {
			byteCharAsInt = inputStream.read();
			byteCharAsInt = byteCharAsInt & 0XFF;
		}
		// to avoid confustion over meaning of returned byte,
		// throw exception if EOF reached.
		if (byteCharAsInt == -1)
			throw new NotEnoughInputForBOMException("typically not an error"); //$NON-NLS-1$
		return (byte) byteCharAsInt;
	}

	/**
	 *  
	 */

	public String getSpecDefaultEncoding() {
		// There is no default for this case
		return null;
	}

	/**
	 *  
	 */
	private void resetAll() {
		fNoBOMPossible = false;
		fInputStream = null;

	}

	/**
	 *  
	 */

	public void set(InputStream inputStream) {
		resetAll();
		fInputStream = inputStream;
	}

	public void set(IStorage iStorage) throws CoreException {
		set(new BufferedInputStream(iStorage.getContents(), CodedIO.MAX_BUF_SIZE));

	}

	public void set(Reader reader) {
		if (reader instanceof ByteReader) {
			ByteReader byteReader = (ByteReader) reader;
			fInputStream = byteReader.fInputStream;
		} else {
			fNoBOMPossible = true;
		}

	}

}
