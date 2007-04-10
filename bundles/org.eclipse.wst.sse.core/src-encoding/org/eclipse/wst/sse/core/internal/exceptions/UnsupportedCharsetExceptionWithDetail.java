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

import java.nio.charset.UnsupportedCharsetException;

import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;


/**
 * This is intended for same purpose as it super class, but simply provides
 * more information about than the name in error. This is especially useful
 * for "UIs" which can present users with the error, and the
 * "appropriateDefault" that can be used for a particular input.
 */
public class UnsupportedCharsetExceptionWithDetail extends UnsupportedCharsetException {

	/**
	 * Default <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private EncodingMemento fEncodingMementio;

	public UnsupportedCharsetExceptionWithDetail(EncodingMemento encodingMemento) {
		this(encodingMemento.getDetectedCharsetName());
		fEncodingMementio = encodingMemento;
	}

	protected UnsupportedCharsetExceptionWithDetail(String charsetName) {
		super(charsetName);
	}

	public EncodingMemento getEncodingMemento() {
		return fEncodingMementio;
	}

}
