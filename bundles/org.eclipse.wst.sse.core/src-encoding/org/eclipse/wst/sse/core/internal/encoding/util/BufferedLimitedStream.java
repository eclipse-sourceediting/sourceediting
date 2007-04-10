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
package org.eclipse.wst.sse.core.internal.encoding.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BufferedLimitedStream extends BufferedInputStream {

	private int limitedCount;

	public BufferedLimitedStream(InputStream inStream, int size) {
		super(inStream, size);
		mark(size);
		try {
			limitedCount = Math.min(size, inStream.available());
		} catch (IOException e) {
			// unlikely
			limitedCount = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	public synchronized int available() throws IOException {

		return limitedCount - pos;
	}

	/**
	 * copied down from super class
	 */
	private void ensureOpen() throws IOException {
		if (in == null)
			throw new IOException("Stream closed"); //$NON-NLS-1$
	}

	/**
	 * copied down from super class then, changed to simiulate EOF if goes
	 * beyond buffered amount
	 */
	public synchronized int read() throws IOException {
		ensureOpen();
		// for this special stream, indicate "end of file" when buffer is
		// full
		if (pos >= limitedCount) {
			return -1;
		}
		return super.read();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		// for this special stream, indicate "end of file" when buffer is
		// full
		if (pos >= limitedCount) {
			return -1;
		}
		return super.read(b, off, len);
	}
}
