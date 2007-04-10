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

class Yytoken {
	public int m_charBegin;
	public int m_length;
	public int m_line;
	public String m_text;
	public String m_type;

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

	public int getEndOffset() {
		return m_charBegin + m_length;
	}

	public int getLength() {
		return m_length;
	}

	public int getStartLine() {
		return m_line;
	}

	public int getStartOffset() {
		return m_charBegin;
	}

	public String getText() {
		return m_text;
	}

	public String getType() {
		return m_type;
	}

	public void setStartOffset(int startOffset) {
		m_charBegin = startOffset;
	}

	public String toString() {
		return "Text   : " + m_text + "\ntype : " + m_type + "\nline  : " + m_line + "\ncBeg. : " + m_charBegin + "\ncEnd. : " + getEndOffset() + "\ncLength. : " + m_length; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	public void updateOffset(int delta) {
		m_charBegin += delta;
	}

	public void updateText(String newText) {
		m_text = newText;
		m_length = newText.length();
	}
}
