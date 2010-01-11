/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (STAR) - bug 297006 - String Comparison
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contenttype;

import java.io.IOException;

import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;


public class XMLResourceEncodingDetector extends AbstractResourceEncodingDetector implements IResourceCharsetDetector {
	private XMLHeadTokenizer fTokenizer;
	private boolean fDeclDetected = false;
	private boolean fInitialWhiteSpace = false;

	private boolean canHandleAsUnicodeStream(String tokenType) {
		boolean canHandleAsUnicodeStream = false;
		if (EncodingParserConstants.UTF83ByteBOM.equals(tokenType)) {
			canHandleAsUnicodeStream = true;
			String enc = "UTF-8"; //$NON-NLS-1$
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
			fEncodingMemento.setUTF83ByteBOMUsed(true);
		}
		else if (EncodingParserConstants.UTF16BE.equals(tokenType) || EncodingParserConstants.UTF16LE.equals(tokenType)) {
			canHandleAsUnicodeStream = true;
			String enc = "UTF-16"; //$NON-NLS-1$
			byte[] bom = (EncodingParserConstants.UTF16BE.equals(tokenType)) ? IContentDescription.BOM_UTF_16BE : IContentDescription.BOM_UTF_16LE;
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
			fEncodingMemento.setUnicodeStream(true);
			fEncodingMemento.setUnicodeBOM(bom);
		}
		return canHandleAsUnicodeStream;
	}

	public String getSpecDefaultEncoding() {
		// by default, UTF-8 as per XML spec
		final String enc = "UTF-8"; //$NON-NLS-1$
		return enc;
	}

	/**
	 * @return Returns the tokenizer.
	 */
	private XMLHeadTokenizer getTokenizer() {
		// TODO: need to work on 'reset' in tokenizer, so new instance isn't
		// always needed
		// if (fTokenizer == null) {
		fTokenizer = new XMLHeadTokenizer();
		// }
		return fTokenizer;
	}

	private boolean isLegalString(String valueTokenType) {
		if (valueTokenType == null)
			return false;
		else
			return valueTokenType.equals(EncodingParserConstants.StringValue) || valueTokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
	}

	protected void parseInput() throws IOException {
		XMLHeadTokenizer tokenizer = getTokenizer();
		tokenizer.reset(fReader);
		HeadParserToken token = null;
		String tokenType = null;
		do {
			token = tokenizer.getNextToken();
			tokenType = token.getType();

			// handle xml content type detection
			if (tokenType == XMLHeadTokenizerConstants.XMLDeclStart) {
				fDeclDetected = true;
				String declText = token.getText();
				if (declText.startsWith("<?")) { //$NON-NLS-1$
					fInitialWhiteSpace = false;
				}
				else {
					fInitialWhiteSpace = true;
				}
			}

			// handle encoding detection
			if (canHandleAsUnicodeStream(tokenType)) {
				// side effect of canHandle is to create appropriate memento
			}
			else {
				if (tokenType == XMLHeadTokenizerConstants.XMLDelEncoding) {
					if (tokenizer.hasMoreTokens()) {
						token = tokenizer.getNextToken();
						tokenType = token.getType();
						if (isLegalString(tokenType)) {
							String enc = token.getText();
							if (enc != null && enc.length() > 0) {
								createEncodingMemento(enc, EncodingMemento.FOUND_ENCODING_IN_CONTENT);
							}
						}
					}
				}
			}
		}
		while (tokenizer.hasMoreTokens());

	}

	public boolean isDeclDetected() {
		if (!fHeaderParsed) {
			try {
				parseInput();
			}
			catch (IOException e) {
				fDeclDetected = false;
			}
			// we keep track of if header's already been
			// parse, so can make
			// multiple 'get' calls, without causing
			// reparsing.
			fHeaderParsed = true;
		}
		// fDeclDetected is set as part of parsing.
		return fDeclDetected;
	}

	public boolean hasInitialWhiteSpace() {
		return fInitialWhiteSpace;
	}

	protected void resetAll() {
	    super.resetAll();
		fDeclDetected = false;
		fInitialWhiteSpace = false;
	}
}
