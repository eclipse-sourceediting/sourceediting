/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.utils;



/**
 * The SourceEditingRuntimeException is often thrown by Source Editor methods when
 * a service we use throws a checked exception, but we want to convert and treat
 * as a runtime exception. (Such as BadLocationException is a common example).
 */
public class TestRuntimeException extends RuntimeException {
	/**
	 * Default <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private Throwable originalException;

	public TestRuntimeException() {
		super();
	}

	public TestRuntimeException(String s) {
		super(s);
	}

	/**
	 * This form of the constructor is used to wrapper another exception.
	 */
	public TestRuntimeException(Throwable t) {
		super();
		originalException = t;
	}

	/**
	 * This form of the constructor is used to wrapper another exception, but still
	 * provide a new descriptive message.
	 */
	public TestRuntimeException(Throwable t, String s) {
		super(s);
		originalException = t;
	}

	public String getMessage() {
		String result = super.getMessage();
		if ((result != null) && (!result.endsWith("."))) //$NON-NLS-1$
			result = result + "."; //$NON-NLS-1$
		if (originalException != null) {
			String embeddedMessage = originalException.getMessage();
			// not all exceptions (e.g. many NullPointer exception) have messages
			String originalError = "Original error:";	//$NON-NLS-1$
			if (embeddedMessage != null)
				result = result + "  " + originalError + " " + embeddedMessage;//$NON-NLS-2$//$NON-NLS-1$
			else
				result = result + "  " + originalError + " " + originalException.toString();//$NON-NLS-2$//$NON-NLS-1$
		}
		return result;
	}

	public Throwable getOriginalException() {
		return originalException;
	}

	public String toString() {
		// we don't put super.toString or getClass to "hide" that it was a
		// SourceEditing exception (otherwise, focus goes on that,
		// instead of original exception.
		String message = getMessage();
		// message should never be null ... but just in case
		return (message != null) ? message : super.toString();

	}
}
