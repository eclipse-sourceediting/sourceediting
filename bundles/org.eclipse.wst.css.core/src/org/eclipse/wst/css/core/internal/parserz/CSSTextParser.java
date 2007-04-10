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
package org.eclipse.wst.css.core.internal.parserz;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import org.eclipse.wst.css.core.internal.parser.CSSTokenizer;



public class CSSTextParser {
	public static final int MODE_STYLESHEET = 0;
	public static final int MODE_DECLARATION = 1;
	public static final int MODE_DECLARATION_VALUE = 2;

	public CSSTextParser(int parserMode) {
		initializeParserMode(parserMode);
	}

	public CSSTextParser(int parserMode, Reader reader) {
		initializeParserMode(parserMode);
		reset(reader);
	}

	public CSSTextParser(int parserMode, String input) {
		initializeParserMode(parserMode);
		reset(input);
	}

	public void reset(Reader reader) {
		getTokenizer().reset(reader, 0);
	}

	public void reset(String input) {
		getTokenizer().reset(new StringReader(input), 0);
	}

	public CSSTextToken[] getTokens() {
		List tokenList = getTokenList();
		CSSTextToken[] tokens = new CSSTextToken[tokenList.size()];
		return (CSSTextToken[]) tokenList.toArray(tokens);
	}

	public List getTokenList() {
		List tokens;
		try {
			tokens = getTokenizer().parseText();
		}
		catch (IOException e) {
			tokens = Collections.EMPTY_LIST;
		}
		return tokens;
	}

	private void initializeParserMode(int parserMode) {
		int initialState;
		int bufsize;
		switch (parserMode) {
			case MODE_STYLESHEET :
				initialState = CSSTokenizer.YYINITIAL;
				bufsize = CSSTokenizer.BUFFER_SIZE_NORMAL;
				break;
			case MODE_DECLARATION :
				initialState = CSSTokenizer.ST_DECLARATION;
				bufsize = CSSTokenizer.BUFFER_SIZE_NORMAL;
				break;
			case MODE_DECLARATION_VALUE :
				initialState = CSSTokenizer.ST_DECLARATION_PRE_VALUE;
				bufsize = CSSTokenizer.BUFFER_SIZE_SMALL;
				break;
			default :
				return;
		}
		if (0 < initialState) {
			CSSTokenizer tokenizer = getTokenizer();
			tokenizer.setInitialState(initialState);
			tokenizer.setInitialBufferSize(bufsize);
		}
	}

	private CSSTokenizer getTokenizer() {
		if (fTokenizer == null) {
			fTokenizer = new CSSTokenizer();
		}
		return fTokenizer;
	}

	private CSSTokenizer fTokenizer = null;
}
