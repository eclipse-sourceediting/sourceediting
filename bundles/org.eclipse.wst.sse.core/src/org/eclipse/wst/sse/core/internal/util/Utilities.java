/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.util;



import java.io.BufferedInputStream;
import java.io.InputStream;

import org.eclipse.wst.sse.core.internal.encoding.CodedIO;
import org.eclipse.wst.sse.core.internal.encoding.util.BufferedLimitedStream;



public class Utilities {

	/**
	 * a common calculation in some of the parsing methods (e.g. in
	 * ContextRegion and IStructuredDocumentRegion)
	 */
	public static int calculateLengthDifference(String changes, int lengthToReplace) {
		// determine the length by the text itself, or, if there is no text to
		// insert (i.e. we are doing a delete) then calculate the length as
		// a negative number to denote the amount to delete.
		// For a straight insert, the selection Length will be zero.
		int lengthDifference = 0;
		if (changes == null) {
			// the delete case
			lengthDifference = 0 - lengthToReplace;
		}
		else {
			lengthDifference = changes.length() - lengthToReplace;
		}
		if (Debug.debugStructuredDocument) {
			System.out.println("lengthDifference: " + lengthDifference);//$NON-NLS-1$
		}
		return lengthDifference;
	}

	/**
	 * Returns true iff both parameters are not null and the object is within
	 * the array. Careful, this uses identity. Not good for basic strings.
	 */
	public static boolean contains(Object[] objectArray, Object object) {
		boolean result = false;
		// if object or objectArray is null, return false
		if ((objectArray != null) && (object != null)) {
			for (int i = 0; i < objectArray.length; i++) {
				if (objectArray[i] == object) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	public static boolean containsString(String[] objectArray, String object) {
		boolean result = false;
		// if object or objectArray is null, return false
		if ((objectArray != null) && (object != null)) {
			for (int i = 0; i < objectArray.length; i++) {
				if (objectArray[i].equals(object)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Ensures that an InputStream has mark/reset support, is readlimit is
	 * set, and that the stream is "limitable" (that is, reports "end of
	 * input" rather than allow going past mark). This is very specialized
	 * stream introduced to overcome
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=67211. See also
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=68565
	 */
	public static InputStream getLimitedStream(InputStream original) {
		if (original == null)
			return null;
		if (original instanceof BufferedLimitedStream)
			return original;
		return new BufferedLimitedStream(original, CodedIO.MAX_BUF_SIZE);
	}

	/**
	 * <p>
	 * Ensures that an InputStream has mark/reset support.
	 * </p>
	 * <p>
	 * It's vital that a BufferedInputStream <b>not</b> be wrapped in another
	 * BufferedInputStream as each can preemptively consume <i>n</i> bytes
	 * (e.g. 2048) from the parent stream before any requests are made. The
	 * cascading effect is that the second/inner BufferedInputStream can never
	 * rewind itself to the first <i>n</i> bytes since they were already
	 * consumed by its parent.
	 * </p>
	 */
	public static InputStream getMarkSupportedStream(InputStream original) {
		if (original == null)
			return null;
		if (original.markSupported())
			return original;
		InputStream buffered = new BufferedInputStream(original, CodedIO.MAX_BUF_SIZE);
		buffered.mark(CodedIO.MAX_MARK_SIZE);
		return buffered;
	}

	/**
	 * Used for log/trace messages. Id is assumed to be some form of a
	 * filename. See IModelManager.
	 */
	public static String makeShortId(Object id) {
		if (id == null)
			id = "NOID";//$NON-NLS-1$
		String whole = id.toString();
		String part = whole.substring(whole.lastIndexOf("/") + 1); //$NON-NLS-1$
		return "..." + part; //$NON-NLS-1$
	}


	/**
	 * Utilities constructor comment.
	 */
	public Utilities() {
		super();
	}
}
