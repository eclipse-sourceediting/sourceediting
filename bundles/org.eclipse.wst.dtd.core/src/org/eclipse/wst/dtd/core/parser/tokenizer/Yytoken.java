/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.dtd.core.parser.tokenizer;

class Yytoken {
	public String m_type;
	public String m_text;
	public int m_line;
	public int m_charBegin;
	public int m_length;

	Yytoken(String type) {
		m_type = type;
	}

	Yytoken(String type, String text, int line, int charBegin, int length) {
		m_type = type;
		m_text = new String(text);
		m_line = line;
		m_charBegin = charBegin;
		m_length = length;
	}

	public String getType() {
		return m_type;
	}

	public int getStartLine() {
		return m_line;
	}

	public int getStartOffset() {
		return m_charBegin;
	}

	public int getEndOffset() {
		return m_charBegin + m_length;
	}

	public int getLength() {
		return m_length;
	}

	public String getText() {
		return m_text;
	}

	public void setStartOffset(int startOffset) {
		m_charBegin = startOffset;
	}

	public void updateOffset(int delta) {
		m_charBegin += delta;
	}

	public void updateText(String newText) {
		m_text = newText;
		m_length = newText.length();
	}

	public void delete(int start, int length) {
		int stringStart = start - m_charBegin;
		String oldString = m_text;
		m_text = oldString.substring(0, stringStart);
		m_text += oldString.substring(stringStart + length);

		m_length = m_text.length();
		System.out.println("new string = " + m_text); //$NON-NLS-1$

	}

	public boolean equals(Yytoken other) {
		return m_type == other.getType() && m_text.equals(other.getText()) && m_line == other.getStartLine() && m_charBegin == other.getStartOffset() && m_length == other.getLength();
	}

	public String toString() {
		return "Text   : " + m_text + "\ntype : " + m_type + "\nline  : " + m_line + "\ncBeg. : " + m_charBegin + "\ncEnd. : " + getEndOffset() + "\ncLength. : " + m_length; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}
}

