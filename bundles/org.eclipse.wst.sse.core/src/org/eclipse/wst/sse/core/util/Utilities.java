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
package org.eclipse.wst.sse.core.util;



import java.io.BufferedInputStream;
import java.io.InputStream;

import org.eclipse.wst.encoding.CodedIO;
import org.eclipse.wst.encoding.internal.BufferedLimitedStream;


public class Utilities {


	/**
	 * Utilities constructor comment.
	 */
	public Utilities() {
		super();
	}

	/**
	 * a common calculation in some of the parsing methods (e.g. in ContextRegion and IStructuredDocumentRegion)
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
	 * Careful, this uses identity. Not good for basic strings.
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
	 * Used for log/trace messages. Id is assumed to be some form of a filename. See IModelManager.
	 */
	public static String makeShortId(Object id) {
		if (id == null)
			id = "NOID"; 
		String whole = id.toString();
		String part = whole.substring(whole.lastIndexOf("/") + 1); //$NON-NLS-1$
		return "..." + part; //$NON-NLS-1$
	}

	/**
	 * Ensures that an InputStream has mark/reset support.
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
	 * Ensures that an InputStream has mark/reset support, 
	 * is readlimit is set, and that the stream is "limitable"
	 * (that is, reports "end of input" rather than allow going past 
	 * mark). This is very specialized stream introduced to overcome
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=67211.
	 * See also https://bugs.eclipse.org/bugs/show_bug.cgi?id=68565
	 */
	public static InputStream getLimitedStream(InputStream original) {
		if (original == null)
			return null;
		if (original instanceof BufferedLimitedStream)
			return original;
		return new BufferedLimitedStream(original, CodedIO.MAX_BUF_SIZE);
	}
}
