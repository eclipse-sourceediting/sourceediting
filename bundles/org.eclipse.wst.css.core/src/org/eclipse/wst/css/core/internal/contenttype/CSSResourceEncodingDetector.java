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
package org.eclipse.wst.css.core.internal.contenttype;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;
import org.eclipse.wst.sse.core.internal.encoding.NonContentBasedEncodingRules;
import org.eclipse.wst.xml.core.internal.contenttype.EncodingParserConstants;



public class CSSResourceEncodingDetector implements IResourceCharsetDetector {
	class NullMemento extends EncodingMemento {
		/**
		 * 
		 */
		public NullMemento() {
			super();
			String defaultCharset = NonContentBasedEncodingRules.useDefaultNameRules(null);
			setJavaCharsetName(defaultCharset);
			setAppropriateDefault(defaultCharset);
			setDetectedCharsetName(null);
		}
	}


	private CSSHeadTokenizer fTokenizer;
	private EncodingMemento fEncodingMemento;
	private boolean fHeaderParsed;
	private Reader fReader;

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
		else if (tokenType == EncodingParserConstants.UTF16BE || tokenType == EncodingParserConstants.UTF16LE) {
			canHandleAsUnicodeStream = true;
			String enc = "UTF-16"; //$NON-NLS-1$
			byte[] bom = (tokenType == EncodingParserConstants.UTF16BE) ? IContentDescription.BOM_UTF_16BE : IContentDescription.BOM_UTF_16LE;
			createEncodingMemento(enc, EncodingMemento.DETECTED_STANDARD_UNICODE_BYTES);
			fEncodingMemento.setUnicodeStream(true);
			fEncodingMemento.setUnicodeBOM(bom);
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
		boolean result = false;
		if (valueTokenType != null) {
			result = valueTokenType.equals(EncodingParserConstants.StringValue) || valueTokenType.equals(EncodingParserConstants.UnDelimitedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTerminatedStringValue) || valueTokenType.equals(EncodingParserConstants.InvalidTermintatedUnDelimitedStringValue);
		}
		return result;
	}

	private void parseInput() throws IOException {
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

	/**
	 * Note: once this instance is created, trace info still needs to be
	 * appended by caller, depending on the context its created.
	 */
	private void createEncodingMemento(String detectedCharsetName) {
		fEncodingMemento = new EncodingMemento();
		fEncodingMemento.setJavaCharsetName(getAppropriateJavaCharset(detectedCharsetName));
		fEncodingMemento.setDetectedCharsetName(detectedCharsetName);
		// TODO: if detectedCharset and spec default is
		// null, need to use "work
		// bench based" defaults.
		fEncodingMemento.setAppropriateDefault(getSpecDefaultEncoding());
	}

	/**
	 * convience method all subclasses can use (but not override)
	 * 
	 * @param detectedCharsetName
	 * @param reason
	 */
	private void createEncodingMemento(String detectedCharsetName, String reason) {
		createEncodingMemento(detectedCharsetName);
	}

	/**
	 * convience method all subclasses can use (but not override)
	 */
	private final void ensureInputSet() {
		if (fReader == null) {
			throw new IllegalStateException("input must be set before use"); //$NON-NLS-1$
		}
	}

	/**
	 * This method can return null, if invalid charset name (in which case
	 * "appropriateDefault" should be used, if a name is really need for some
	 * "save anyway" cases).
	 * 
	 * @param detectedCharsetName
	 * @return
	 */
	private String getAppropriateJavaCharset(String detectedCharsetName) {
		String result = null;
		// 1. Check explicit mapping overrides from
		// property file -- its here we pick up "rules" for cases
		// that are not even in Java
		result = CodedIO.checkMappingOverrides(detectedCharsetName);
		// 2. Use the "canonical" name from JRE mappings
		// Note: see Charset JavaDoc, the name you get one
		// with can be alias,
		// the name you get back is "standard" name.
		Charset javaCharset = null;
		try {
			javaCharset = Charset.forName(detectedCharsetName);
		}
		catch (UnsupportedCharsetException e) {
			// only set invalid, if result is same as detected -- they won't
			// be equal if
			// overridden
			if (result != null && result.equals(detectedCharsetName)) {
				fEncodingMemento.setInvalidEncoding(detectedCharsetName);
			}
		}
		catch (IllegalCharsetNameException e) {
			// only set invalid, if result is same as detected -- they won't
			// be equal if
			// overridden
			if (result != null && result.equals(detectedCharsetName)) {
				fEncodingMemento.setInvalidEncoding(detectedCharsetName);
			}
		}
		// give priority to java cononical name, if present
		if (javaCharset != null) {
			result = javaCharset.name();
			// but still allow overrides
			result = CodedIO.checkMappingOverrides(result);
		}
		return result;
	}

	public String getEncoding() throws IOException {
		return getEncodingMemento().getDetectedCharsetName();
	}

	public EncodingMemento getEncodingMemento() throws IOException {
		ensureInputSet();
		if (!fHeaderParsed) {
			parseInput();
			// we keep track of if header's already been
			// parse, so can make
			// multiple 'get' calls, without causing
			// reparsing.
			fHeaderParsed = true;
			// Note: there is a "hidden assumption" here
			// that an empty
			// string in content should be treated same as
			// not present.
		}
		if (fEncodingMemento == null) {
			handleSpecDefault();
		}
		if (fEncodingMemento == null) {
			// safty net
			fEncodingMemento = new NullMemento();
		}
		return fEncodingMemento;
	}

	public EncodingMemento getSpecDefaultEncodingMemento() {
		resetAll();
		EncodingMemento result = null;
		String enc = getSpecDefaultEncoding();
		if (enc != null) {
			createEncodingMemento(enc, EncodingMemento.DEFAULTS_ASSUMED_FOR_EMPTY_INPUT);
			fEncodingMemento.setAppropriateDefault(enc);
			result = fEncodingMemento;
		}
		return result;
	}

	private void handleSpecDefault() {
		String encodingName;
		encodingName = getSpecDefaultEncoding();
		if (encodingName != null) {
			// createEncodingMemento(encodingName,
			// EncodingMemento.USED_CONTENT_TYPE_DEFAULT);
			fEncodingMemento = new EncodingMemento();
			fEncodingMemento.setJavaCharsetName(encodingName);
			fEncodingMemento.setAppropriateDefault(encodingName);
		}
	}

	/**
	 * 
	 */
	private void resetAll() {
		fReader = null;
		fHeaderParsed = false;
		fEncodingMemento = null;
	}

	/**
	 * 
	 */
	public void set(InputStream inputStream) {
		resetAll();
		fReader = new ByteReader(inputStream);
		try {
			fReader.mark(CodedIO.MAX_MARK_SIZE);
		}
		catch (IOException e) {
			// impossible, since we know ByteReader
			// supports marking
			throw new Error(e);
		}
	}

	/**
	 * 
	 */
	public void set(IStorage iStorage) throws CoreException {
		resetAll();
		InputStream inputStream = iStorage.getContents();
		InputStream resettableStream = new BufferedInputStream(inputStream, CodedIO.MAX_BUF_SIZE);
		resettableStream.mark(CodedIO.MAX_MARK_SIZE);
		set(resettableStream);
		// TODO we'll need to "remember" IFile, or
		// get its (or its project's) settings, in case
		// those are needed to handle cases when the
		// encoding is not in the file stream.
	}

	/**
	 * Note: this is not part of interface to help avoid confusion ... it
	 * expected this Reader is a well formed character reader ... that is, its
	 * all ready been determined to not be a unicode marked input stream. And,
	 * its assumed to be in the correct position, at position zero, ready to
	 * read first character.
	 */
	public void set(Reader reader) {
		resetAll();
		fReader = reader;
		if (!fReader.markSupported()) {
			fReader = new BufferedReader(fReader);
		}
		try {
			fReader.mark(CodedIO.MAX_MARK_SIZE);
		}
		catch (IOException e) {
			// impossble, since we just checked if markable
			throw new Error(e);
		}
	}

}
