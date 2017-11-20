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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class XMLDeclDetector {
	private static final int MAX_BUF_SIZE = 1024 * 8;
	private static final int MAX_MARK_SIZE = 1024 * 8;
	protected boolean fHeaderParsed;
	private boolean fIsXML;
	protected Reader fReader;
	//private boolean DEBUG = false;
	private XMLHeadTokenizer fTokenizer;

	private boolean canHandleAsUnicodeStream(String tokenType) {
		boolean canHandleAsUnicodeStream = false;
		if (EncodingParserConstants.UTF83ByteBOM.equals(tokenType)) {
			canHandleAsUnicodeStream = true;
			//fUnicode = "UTF-8"; //$NON-NLS-1$
		} else if (EncodingParserConstants.UTF16BE.equals(tokenType)) {
			canHandleAsUnicodeStream = true;
			//fUnicode = "UTF-16BE"; //$NON-NLS-1$
		} else if (EncodingParserConstants.UTF16LE.equals(tokenType)) {
			canHandleAsUnicodeStream = true;
			//fUnicode = "UTF-16"; //$NON-NLS-1$
		}
		return canHandleAsUnicodeStream;
	}

	final private void ensureInputSet() {
		if (fReader == null) {
			throw new IllegalStateException("input must be set before use"); //$NON-NLS-1$
		}
	}

	//private String fUnicode;

	/**
	 * @return Returns the tokenizer.
	 */
	private XMLHeadTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new XMLHeadTokenizer();
		}
		return fTokenizer;
	}

	/**
	 * @return Returns the isXML.
	 */
	public boolean isXML() throws IOException {
		ensureInputSet();
		if (!fHeaderParsed) {
			parseInput();
		}
		return fIsXML;
	}

	private void parseInput() throws IOException {
		XMLHeadTokenizer tokenizer = getTokenizer();
		tokenizer.reset(fReader);
		HeadParserToken token = null;
		String tokenType = null;
		do {
			token = tokenizer.getNextToken();
			tokenType = token.getType();
			if (canHandleAsUnicodeStream(tokenType)) {
				fReader.reset();
				// this is (obviously) not always true.
				// TODO: need to fix so we "remember" original iFile or
				// inputstream, and
				// create appropriate InputStreamReader.
				// I'm not sure what to do for the set(reader) case ... if its
				// even relevent.
				// plus, ensure against infinite loops!
				fIsXML = true;
				//fReader = new InputStreamReader(fReader, fUnicode);
				// parseInput();
			} else {
				if (XMLHeadTokenizerConstants.XMLDelEncoding.equals(tokenType)) {
					fIsXML = true;
				}
			}
		} while (tokenizer.hasMoreTokens());

	}

	private void resetAll() {
		fReader = null;
		fHeaderParsed = false;
		fIsXML = false;
		//fUnicode = null;

	}

	public void set(IFile iFile) throws CoreException {
		resetAll();
		InputStream inputStream = iFile.getContents(true);
		InputStream resettableStream = new BufferedInputStream(inputStream, MAX_BUF_SIZE);
		resettableStream.mark(MAX_MARK_SIZE);
		set(resettableStream);
	}

	public void set(InputStream inputStream) {
		resetAll();
		fReader = new ByteReader(inputStream);
		try {
			fReader.mark(MAX_MARK_SIZE);
		} catch (IOException e) {
			// impossible, since we know ByteReader supports marking
			throw new Error(e);
		}
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
			fReader.mark(MAX_MARK_SIZE);
		} catch (IOException e) {
			// impossble, since we just checked if markable
			throw new Error(e);
		}

	}

}
