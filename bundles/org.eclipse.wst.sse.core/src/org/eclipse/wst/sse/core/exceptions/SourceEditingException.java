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
package org.eclipse.wst.sse.core.exceptions;



import org.eclipse.wst.sse.core.internal.nls.ResourceHandler;

/**
 * The SourceEditingException is often thrown by Source Editor methods in
 * order to have a predictable, uniform interface to the Source Editing APIs.
 * If the OriginalException is not null, then it must be examined to determine
 * the true cause of the exception.
 */
public class SourceEditingException extends Exception {

	private Throwable originalException;

	public SourceEditingException() {
		super();
	}

	public SourceEditingException(String s) {
		super(s);
	}

	/**
	 * This form of the constructor is used to wrapper another exception.
	 */
	public SourceEditingException(Throwable t) {
		super();
		originalException = t;
	}

	/**
	 * This form of the constructor is used to wrapper another exception, but
	 * still provide a new descriptive message.
	 */
	public SourceEditingException(Throwable t, String s) {
		super(s);
		originalException = t;
	}

	/**
	 * The message depends on if this is a case that has an embedded
	 * exception.
	 */
	public String getMessage() {
		String result = null;
		if (originalException != null) {
			result = originalException.getMessage();
		} else {
			result = super.getMessage();
		}
		return result;
	}

	public Throwable getOriginalException() {
		return originalException;
	}

	public String toString() {
		// we don't put super.toString to "hide" that it was a
		// sourceEditing exception
		String s = null; //super.toString();
		String originalError = ResourceHandler.getString("Original_Error__UI_"); //$NON-NLS-1$ = "Original Error:"

		if (originalException != null) {
			s = originalError + " " + originalException.toString(); //$NON-NLS-1$ 
		}
		return s;
	}
}
