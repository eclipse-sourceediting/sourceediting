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
package org.eclipse.wst.sse.core.internal.exceptions;


public class MalformedOutputExceptionWithDetail extends MalformedInputExceptionWithDetail {

	/**
	 * Default <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Disallow default constructor. If attemptedEncoding and charPostion can
	 * not be provided, use sun.io.MalformedException.
	 */
	private MalformedOutputExceptionWithDetail() {
		// default constructor is disallowed, since if
		// extra info can not be provided, the regular
		// Malformed exception should be thrown
	}

	/**
	 * Constructor for MalformedOutputExceptionWithDetail.
	 * 
	 * @param attemptedJavaEncoding
	 * @param attemptedIANAEncoding
	 * @param charPostion
	 */
	public MalformedOutputExceptionWithDetail(String attemptedJavaEncoding, String attemptedIANAEncoding, int charPostion) {
		super(attemptedJavaEncoding, attemptedIANAEncoding, charPostion);
	}

}
