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
package org.eclipse.wst.sse.ui.internal.editor;



import java.io.IOException;
import java.io.Reader;

/**
 * Copied from org.eclipse.jdt.internal.corext.javadoc.SingleCharReader
 * Also copied in contentassist.html.javascript.tmpcopied.SingleCharReader
 * No modifications were made
 */
public abstract class SingleCharReader extends Reader {

	/**
	 * @see Reader#read()
	 */
	public abstract int read() throws IOException;

	/**
	 * @see Reader#read(char[],int,int)
	 */
	public int read(char cbuf[], int off, int len) throws IOException {
		int end = off + len;
		for (int i = off; i < end; i++) {
			int ch = read();
			if (ch == -1) {
				if (i == off) {
					return -1;
				}
				else {
					return i - off;
				}
			}
			cbuf[i] = (char) ch;
		}
		return len;
	}

	/**
	 * @see Reader#ready()
	 */
	public boolean ready() throws IOException {
		return true;
	}

	/**
	 * Gets the content as a String
	 */
	public String getString() throws IOException {
		StringBuffer buf = new StringBuffer();
		int ch;
		while ((ch = read()) != -1) {
			buf.append((char) ch);
		}
		return buf.toString();
	}
}
