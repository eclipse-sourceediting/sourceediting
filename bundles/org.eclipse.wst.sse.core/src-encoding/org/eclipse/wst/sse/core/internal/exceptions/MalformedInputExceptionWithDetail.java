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
package org.eclipse.wst.sse.core.internal.exceptions;

import java.nio.charset.CharacterCodingException;


/**
 * Intended to be a more precise form of the MalformedInputException, where
 * character position and attempted encoding can be attempted.
 */
public class MalformedInputExceptionWithDetail extends CharacterCodingException {

	/**
	 * Default <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private int fCharPosition;
	private String fDetectedCharsetName;
	private boolean fExceededMax = false;
	private String fJavaCharsetName;
	private int fMaxBuffer;

	/**
	 * Disallow default constructor. If attemptedEncoding and charPostion can
	 * not be provided, use one of java's MalformedException.
	 */
	protected MalformedInputExceptionWithDetail() {
		// Nothing to do
	}

	public MalformedInputExceptionWithDetail(String encodingName, int charPostion) {
		this.fJavaCharsetName = encodingName;
		this.fDetectedCharsetName = encodingName;
		this.fCharPosition = charPostion;
	}

	public MalformedInputExceptionWithDetail(String attemptedJavaEncoding, String attemptedIANAEncoding, int charPostion) {
		this.fJavaCharsetName = attemptedJavaEncoding;
		this.fDetectedCharsetName = attemptedIANAEncoding;
		this.fCharPosition = charPostion;
	}

	/**
	 * If charPosition = -1 this could be because the character position
	 * exceeded the maximum buffer size, maxBuffer, then exceededMax = true.
	 */
	public MalformedInputExceptionWithDetail(String attemptedJavaEncoding, String attemptedIANAEncoding, int charPostion, boolean exceededMax, int maxBuffer) {
		this.fJavaCharsetName = attemptedJavaEncoding;
		this.fDetectedCharsetName = attemptedIANAEncoding;
		this.fCharPosition = charPostion;
		this.fExceededMax = exceededMax;
		this.fMaxBuffer = maxBuffer;
	}

	/**
	 */
	public java.lang.String getAttemptedIANAEncoding() {
		return fDetectedCharsetName;
	}

	/**
	 */
	public java.lang.String getAttemptedJavaEncoding() {
		return fJavaCharsetName;
	}

	/**
	 * @return int
	 */
	public int getCharPosition() {
		return fCharPosition;
	}

	/**
	 * Returns the maxBuffer.
	 * 
	 * @return int
	 */
	public int getMaxBuffer() {
		return fMaxBuffer;
	}

	/**
	 * Returns the exceededMax.
	 * 
	 * @return boolean
	 */
	public boolean isExceededMax() {
		return fExceededMax;
	}

}
