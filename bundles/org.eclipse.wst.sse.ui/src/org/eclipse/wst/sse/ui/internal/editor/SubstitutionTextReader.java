/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.editor;



import java.io.IOException;
import java.io.Reader;

/**
 * Copied from org.eclipse.jdt.internal.ui.text.SubstitutionTextReader
 * Also copied in org.eclipse.wst.javascript.common.ui.contentassist.javadoc.SubstitutionTextReader
 * Modifications were made to read() to allow whitespaces
 */
/**
 * Reads the text contents from a reader and computes for each character a
 * potential substitution. The substitution may eat more characters than only
 * the one passed into the computation routine.
 */
public abstract class SubstitutionTextReader extends SingleCharReader {

	protected static final String LINE_DELIM = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
	private StringBuffer fBuffer;
	private int fCharAfterWhiteSpace;
	private int fIndex;

	private Reader fReader;

	private boolean fReadFromBuffer;

	/**
	 * Tells whether white space characters are skipped.
	 */
	private boolean fSkipWhiteSpace = true;
	private boolean fWasWhiteSpace;


	protected SubstitutionTextReader(Reader reader) {
		fReader = reader;
		fBuffer = new StringBuffer();
		fIndex = 0;
		fReadFromBuffer = false;
		fCharAfterWhiteSpace = -1;
		fWasWhiteSpace = true;
	}

	/**
	 * @see Reader#close()
	 */
	public void close() throws IOException {
		fReader.close();
	}

	/**
	 * Implement to compute the substitution for the given character and if
	 * necessary subsequent characters. Use <code>nextChar</code> to read
	 * subsequent characters.
	 */
	protected abstract String computeSubstitution(int c) throws IOException;

	/**
	 * Returns the internal reader.
	 */
	protected Reader getReader() {
		return fReader;
	}

	protected final boolean isSkippingWhitespace() {
		return fSkipWhiteSpace;
	}

	/**
	 * Returns the next character.
	 */
	protected int nextChar() throws IOException {
		fReadFromBuffer = (fBuffer.length() > 0);
		if (fReadFromBuffer) {
			char ch = fBuffer.charAt(fIndex++);
			if (fIndex >= fBuffer.length()) {
				fBuffer.setLength(0);
				fIndex = 0;
			}
			return ch;
		} else {
			int ch = fCharAfterWhiteSpace;
			if (ch == -1) {
				ch = fReader.read();
			}
			if (fSkipWhiteSpace && Character.isWhitespace((char) ch)) {
				do {
					ch = fReader.read();
				} while (Character.isWhitespace((char) ch));
				if (ch != -1) {
					fCharAfterWhiteSpace = ch;
					return ' ';
				}
			} else {
				fCharAfterWhiteSpace = -1;
			}
			return ch;
		}
	}

	/**
	 * @see Reader#read()
	 */
	public int read() throws IOException {
		int c;
		do {

			c = nextChar();
			while (!fReadFromBuffer) {
				String s = computeSubstitution(c);
				if (s == null)
					break;
				if (s.length() > 0)
					fBuffer.insert(0, s);
				c = nextChar();
			}

		} while (fSkipWhiteSpace && fWasWhiteSpace && ((c == ' ') && !fReadFromBuffer)); // AFW
		// - if
		// whitespace
		// is
		// from
		// buffer,
		// then
		// it
		// should
		// be
		// read
		fWasWhiteSpace = ((c == ' ' && !fReadFromBuffer) || c == '\r' || c == '\n'); // AFW
		// - if
		// whitespace
		// is
		// from
		// buffer,
		// then
		// it
		// should
		// be
		// read
		return c;
	}

	/**
	 * @see Reader#ready()
	 */
	public boolean ready() throws IOException {
		return fReader.ready();
	}

	/**
	 * @see Reader#reset()
	 */
	public void reset() throws IOException {
		fReader.reset();
		fWasWhiteSpace = true;
		fCharAfterWhiteSpace = -1;
		fBuffer.setLength(0);
		fIndex = 0;
	}

	protected final void setSkipWhitespace(boolean state) {
		fSkipWhiteSpace = state;
	}
}
