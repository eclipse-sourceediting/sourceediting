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
package org.eclipse.jst.jsp.core.internal.contenttype;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;
import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;
import org.eclipse.wst.xml.core.internal.contenttype.XMLHeadTokenizerConstants;

public class JSPResourceEncodingDetector extends AbstractResourceEncodingDetector implements IResourceCharsetDetector {

	private String fPageEncodingValue = null;
	private JSPHeadTokenizer fTokenizer = null;
	private String fLanguage;

	private String fContentTypeValue;
	private String fXMLDecEncodingName;
	private String fCharset;
	private boolean unicodeCase;
	private String fContentType;

	protected void parseInput() throws IOException {
		JSPHeadTokenizer tokenizer = getTokinizer();
		tokenizer.reset(fReader);
		parseHeader(tokenizer);
		// unicode stream cases are created directly in parseHeader
		if (!unicodeCase) {
			String enc = getAppropriateEncoding();
			if (enc != null && enc.length() > 0) {
				createEncodingMemento(enc, EncodingMemento.FOUND_ENCODING_IN_CONTENT);
			}
		}
	}

	private JSPHeadTokenizer getTokinizer() {
		if (fTokenizer == null) {
			fTokenizer = new JSPHeadTokenizer();
		}
		return fTokenizer;
	}

	/**
	 * There can sometimes be mulitple 'encodings' specified in a file. This
	 * is an attempt to centralize the rules for deciding between them.
	 * Returns encoding according to priority: 1. XML Declaration 2. page
	 * directive pageEncoding name 3. page directive contentType charset name
	 */
	private String getAppropriateEncoding() {
		String result = null;
		if (fXMLDecEncodingName != null)
			result = fXMLDecEncodingName;
		else if (fPageEncodingValue != null)
			result = fPageEncodingValue;
		else if (fCharset != null)
			result = fCharset;
		return result;
	}

	/**
	 *  
	 */
	public JSPResourceEncodingDetector() {
		super();
	}

	public String getSpecDefaultEncoding() {
		// by JSP Spec
		final String enc = "ISO-8859-1"; //$NON-NLS-1$
		return enc;
	}

	private boolean canHandleAsUnicodeStream(String tokenType) {
		boolean canHandleAsUnicode = false;
		if (tokenType == EncodingParserConstants.UTF83ByteBOM) {
			canHandleAsUnicode = true;
			String enc = "UTF-8"; //$NON-NLS-1$
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
			fEncodingMemento.setUTF83ByteBOMUsed(true);
		}
		else if (tokenType == EncodingParserConstants.UTF16BE) {
			canHandleAsUnicode = true;
			String enc = "UTF-16BE"; //$NON-NLS-1$
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
		}
		else if (tokenType == EncodingParserConstants.UTF16LE) {
			canHandleAsUnicode = true;
			String enc = "UTF-16"; //$NON-NLS-1$
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
		}
		return canHandleAsUnicode;
	}

	/**
	 * Looks for what ever encoding properties the tokenizer returns. Its the
	 * responsibility of the tokenizer to stop when appropriate and not go too
	 * far.
	 */
	private void parseHeader(JSPHeadTokenizer tokenizer) throws IOException {
		fPageEncodingValue = null;
		fCharset = null;

		HeadParserToken token = null;
		do {
			// don't use 'get' here (at least until reset issue fixed)
			token = tokenizer.getNextToken();
			String tokenType = token.getType();
			if (canHandleAsUnicodeStream(tokenType))
				unicodeCase = true;
			else {

				if (tokenType == XMLHeadTokenizerConstants.XMLDelEncoding) {
					if (tokenizer.hasMoreTokens()) {
						HeadParserToken valueToken = tokenizer.getNextToken();
						String valueTokenType = valueToken.getType();
						if (isLegalString(valueTokenType)) {
							fXMLDecEncodingName = valueToken.getText();
						}
					}
				}
				else if (tokenType == JSPHeadTokenizerConstants.PageEncoding) {
					if (tokenizer.hasMoreTokens()) {
						HeadParserToken valueToken = tokenizer.getNextToken();
						String valueTokenType = valueToken.getType();
						if (isLegalString(valueTokenType)) {
							fPageEncodingValue = valueToken.getText();
						}
					}
				}
				else if (tokenType == JSPHeadTokenizerConstants.PageContentType) {
					if (tokenizer.hasMoreTokens()) {
						HeadParserToken valueToken = tokenizer.getNextToken();
						String valueTokenType = valueToken.getType();
						if (isLegalString(valueTokenType)) {
							fContentTypeValue = valueToken.getText();
						}
					}
				}
				else if (tokenType == JSPHeadTokenizerConstants.PageLanguage) {
					if (tokenizer.hasMoreTokens()) {
						HeadParserToken valueToken = tokenizer.getNextToken();
						String valueTokenType = valueToken.getType();
						if (isLegalString(valueTokenType)) {
							fLanguage = valueToken.getText();
						}
					}
				}
			}
		}
		while (tokenizer.hasMoreTokens());
		if (fContentTypeValue != null) {
			parseContentTypeValue(fContentTypeValue);
		}

	}

	private boolean isLegalString(String valueTokenType) {
		if (valueTokenType == null)
			return false;
		else
			return valueTokenType.equals(EncodingParserConstants.StringValue) || valueTokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
	}

	private void parseContentTypeValue(String contentType) {
		Pattern pattern = Pattern.compile(";\\s*charset\\s*=\\s*"); //$NON-NLS-1$
		String[] parts = pattern.split(contentType);
		if (parts.length > 0) {
			// if only one item, it can still be charset instead of
			// contentType
			if (parts.length == 1) {
				if (parts[0].length() > 6) {
					String checkForCharset = parts[0].substring(0, 7);
					if (checkForCharset.equalsIgnoreCase("charset")) { //$NON-NLS-1$
						int eqpos = parts[0].indexOf('=');
						eqpos = eqpos + 1;
						if (eqpos < parts[0].length()) {
							fCharset = parts[0].substring(eqpos);
							fCharset = fCharset.trim();
						}
					}
				}
			}
			else {
				fContentType = parts[0];
			}
		}
		if (parts.length > 1) {
			fCharset = parts[1];
		}
	}

	/**
	 *  
	 */

	public void set(IFile iFile) throws CoreException {
		reset();
		super.set(iFile);
	}

	private void reset() {
		fCharset = null;
		fContentTypeValue = null;
		fPageEncodingValue = null;
		fXMLDecEncodingName = null;
		unicodeCase = false;
	}

	public void set(InputStream inputStream) {
		reset();
		super.set(inputStream);
	}

	public void set(Reader reader) {
		reset();
		super.set(reader);
	}

	public String getLanguage() throws IOException {
		ensureInputSet();
		if (!fHeaderParsed) {
			parseInput();
			fHeaderParsed = true;
		}
		return fLanguage;
	}

	/**
	 * @return Returns the contentType.
	 */
	public String getContentType() throws IOException {
		ensureInputSet();
		if (!fHeaderParsed) {
			parseInput();
			// we keep track of if header's already been parse, so can make
			// multiple 'get' calls, without causing reparsing.
			fHeaderParsed = true;
			// Note: there is a "hidden assumption" here that an empty
			// string in content should be treated same as not present.
		}
		return fContentType;
	}
}