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
package org.eclipse.wst.sse.core.internal.text;

import java.io.IOException;
import java.io.Reader;

public class CharSequenceReader extends Reader {
	private int fCurrentPosition;
	private int fMaximumReadOffset;

	private CharSequence fOriginalSource;

	/**
	 *  
	 */
	CharSequenceReader() {
		super();
	}


	public CharSequenceReader(CharSequence originalSource, int offset, int length) {
		// ISSUE: should we "fail fast" if requested length is more than there
		// is?
		fOriginalSource = originalSource;
		int startOffset = offset;
		int maxRequestedOffset = startOffset + length;
		int maxPossibleOffset = 0 + originalSource.length();
		fMaximumReadOffset = Math.min(maxRequestedOffset, maxPossibleOffset);

		fCurrentPosition = startOffset;

	}

	/**
	 * @param lockObject
	 */
	CharSequenceReader(Object lockObject) {
		super(lockObject);
		// for thread safety, may need to add back locking mechanism
		// in our custom constructor. This constructor left here just
		// for a reminder.
	}

	public void close() throws IOException {
		// nothing to do when we close
		// (may be to eventually "unlock" or null out some varibles
		// just for hygene.
		// or, perhaps if already closed once throw IOException? for
		// consistency?
	}

	/**
	 * @return Returns the originalSource.
	 * @deprecated - only temporarily public, should be 'default' eventually
	 *             or go away altogether.
	 */
	public CharSequence getOriginalSource() {
		return fOriginalSource;
	}

	public int read() {
		int result = -1;
		if (fCurrentPosition < fMaximumReadOffset) {
			result = fOriginalSource.charAt(fCurrentPosition++);
		}
		return result;
	}

	/**
	 * Read characters into a portion of an array. This method will block
	 * until some input is available, an I/O error occurs, or the end of the
	 * stream is reached.
	 * 
	 * @param cbuf
	 *            Destination buffer
	 * @param off
	 *            Offset at which to start storing characters
	 * @param len
	 *            Maximum number of characters to read
	 * 
	 * @return The number of characters read, or -1 if the end of the stream
	 *         has been reached
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 */

	public int read(char[] cbuf, int off, int len) throws IOException {
		int charsToRead = -1;
		// if already over max, just return -1
		// remember, currentPosition is what is getting ready to be read
		// (that is, its already been incremented in read()).
		if (fCurrentPosition < fMaximumReadOffset) {


			int buffMaxToRead = cbuf.length - off;
			int minRequested = Math.min(buffMaxToRead, len);
			int lengthRemaining = fMaximumReadOffset - fCurrentPosition;
			charsToRead = Math.min(minRequested, lengthRemaining);


			CharSequence seq = fOriginalSource.subSequence(fCurrentPosition, fCurrentPosition + charsToRead);
			// for now, hard assumption that original is a String since source
			// is assumed to be document, or text store
			String seqString = (String) seq;
			seqString.getChars(0, seqString.length(), cbuf, off);



			fCurrentPosition = fCurrentPosition + charsToRead;


		}
		return charsToRead;
	}
}
