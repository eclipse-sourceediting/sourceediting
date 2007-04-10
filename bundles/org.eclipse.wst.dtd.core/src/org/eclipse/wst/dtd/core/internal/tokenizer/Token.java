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
package org.eclipse.wst.dtd.core.internal.tokenizer;

public class Token extends Yytoken {
	public static final int COMMENT_START = 9;
	public static final int CONNECTOR = 6;
	public static final int CONTENT_ANY = 43;
	public static final int CONTENT_EMPTY = 42;
	public static final int CONTENT_PCDATA = 44;
	public static final int ELEMENT_CONTENT = 41;

	public static final int ELEMENT_TAG = 40;
	public static final int END_TAG = 2;
	public static final int ENTITY_CONTENT = 32;
	public static final int ENTITY_PARM = 31;

	public static final int ENTITY_TAG = 30;
	public static final int EXCLAMATION = 8;
	public static final int LEFT_PAREN = 3;
	public static final int NAME = 0;
	public static final int NOTATION_CONTENT = 21;


	public static final int NOTATION_TAG = 20;

	// public static final int CONNECT_CHOICE = 5;
	// public static final int CONNECT_SEQUENCE = 6;
	// public static final int OCCUR_OPTIONAL = 7;
	// public static final int OCCUR_ONE_OR_MORE = 8;
	// public static final int OCCUR_ZERO_OR_MORE = 9;
	public static final int OCCUR_TYPE = 7;
	public static final int RIGHT_PAREN = 4;
	public static final int START_TAG = 1;
	public static final int WHITESPACE = 5;


	public Token(String type) {
		super(type);
	}

	public Token(String type, String text, int line, int charBegin, int length) {
		super(type, text, line, charBegin, length);
	}

	public Token createCopy() {
		Token copy = new Token(getType(), getText(), getStartLine(), getStartOffset(), getLength());
		return copy;
	}

}
