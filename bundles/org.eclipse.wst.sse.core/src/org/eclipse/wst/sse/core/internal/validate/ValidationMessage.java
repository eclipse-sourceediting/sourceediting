/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.validate;



/**
 */
public class ValidationMessage {
	public static final int IGNORE = -1;
	public static final int ERROR = 1;
	public static final int INFORMATION = 3;
	public static final int WARNING = 2;
	private int length;

	private String message;
	private int offset;
	private int severity;

	/**
	 */
	public ValidationMessage(String message, int offset, int severity) {
		this(message, offset, 0, severity);
	}

	/**
	 */
	public ValidationMessage(String message, int offset, int length, int severity) {
		super();

		this.message = message;
		this.offset = offset;
		this.length = length;
		this.severity = severity;
	}

	/**
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 */
	public int getOffset() {
		return this.offset;
	}

	/**
	 */
	public int getSeverity() {
		return this.severity;
	}
}
