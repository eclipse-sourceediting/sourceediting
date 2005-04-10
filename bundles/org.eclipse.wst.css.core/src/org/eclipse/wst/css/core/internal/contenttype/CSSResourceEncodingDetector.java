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
package org.eclipse.wst.css.core.internal.contenttype;

import java.io.IOException;

import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;
import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;



public class CSSResourceEncodingDetector extends AbstractResourceEncodingDetector implements IResourceCharsetDetector {

	private CSSHeadTokenizer fTokenizer;

	/**
	 * There is no spec defined encoding for CSS, so Null is returned.
	 */
	public String getSpecDefaultEncoding() {
		// should match what's in plugin.xml (or look it up from there).
		return null;
	}

	private boolean canHandleAsUnicodeStream(String tokenType) {
		boolean canHandleAsUnicodeStream = false;
		if (tokenType == EncodingParserConstants.UTF83ByteBOM) {
			canHandleAsUnicodeStream = true;
			String enc = "UTF-8"; //$NON-NLS-1$
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
			fEncodingMemento.setUTF83ByteBOMUsed(true);
		}
		else if (tokenType == EncodingParserConstants.UTF16BE) {
			canHandleAsUnicodeStream = true;
			String enc = "UTF-16BE"; //$NON-NLS-1$
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
		}
		else if (tokenType == EncodingParserConstants.UTF16LE) {
			canHandleAsUnicodeStream = true;
			String enc = "UTF-16"; //$NON-NLS-1$
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
		}
		return canHandleAsUnicodeStream;
	}

	/**
	 * @return Returns the tokenizer.
	 */
	private CSSHeadTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new CSSHeadTokenizer();
		}
		return fTokenizer;
	}

	private boolean isLegalString(String valueTokenType) {
		if (valueTokenType == null)
			return false;
		else
			return valueTokenType.equals(EncodingParserConstants.StringValue) || valueTokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
	}

	protected void parseInput() throws IOException {
		checkInContents();
		if (fEncodingMemento == null) {
			checkHeuristics();
		}
	}

	private void checkInContents() throws IOException {
		CSSHeadTokenizer tokenizer = getTokenizer();
		tokenizer.reset(fReader);
		HeadParserToken token = null;
		String tokenType = null;
		do {
			token = tokenizer.getNextToken();
			tokenType = token.getType();
			if (canHandleAsUnicodeStream(tokenType)) {
				// side effect of canHandle is to create appropriate memento
			}
			else if (tokenType == CSSHeadTokenizerConstants.CHARSET_RULE) {
				if (tokenizer.hasMoreTokens()) {
					HeadParserToken valueToken = tokenizer.getNextToken();
					String valueTokenType = valueToken.getType();
					if (isLegalString(valueTokenType)) {
						createEncodingMemento(valueToken.getText(), EncodingMemento.FOUND_ENCODING_IN_CONTENT);

					}
				}
			}

		}
		while (tokenizer.hasMoreTokens());
	}

	/**
	 * 
	 */
	private void checkHeuristics() throws IOException {
		boolean noHeuristic = false;
		String heuristicEncoding = null;
		try {
			fReader.reset();
			byte[] bytes = new byte[3];
			int nRead = 0;
			for (int i = 0; i < bytes.length; i++) {
				if (fReader.ready()) {
					int oneByte = fReader.read();
					nRead++;
					if (oneByte <= 0xFF) {
						bytes[i] = (byte) oneByte;
					}
					else {
						noHeuristic = true;
					}
				}
				else {
					noHeuristic = true;
					break;
				}
			}
			if (!noHeuristic && nRead == 3) {
				heuristicEncoding = EncodingGuesser.guessEncoding(bytes, 3);
			}
		}
		catch (IOException e) {
			// if any IO exception, then not a heuristic case
		}
		finally {
			fReader.reset();
		}
		if (heuristicEncoding != null) {
			createEncodingMemento(heuristicEncoding, EncodingMemento.GUESSED_ENCODING_FROM_STREAM);
		}

	}

}