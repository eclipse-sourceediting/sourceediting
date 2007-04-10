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


public class CharConversionErrorWithDetail extends CharacterCodingException {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private String fCharsetName;

	public CharConversionErrorWithDetail() {
		super();
	}

	/**
	 * @param s
	 */
	public CharConversionErrorWithDetail(String charsetName) {
		super();
		fCharsetName = charsetName;
	}

	/**
	 * @return Returns the fCharsetName.
	 */
	public String getCharsetName() {
		return fCharsetName;
	}
}
