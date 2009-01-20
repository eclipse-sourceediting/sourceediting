/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

/**
 * Error caused by CUP Parser.
 */
public class CupError extends XPathError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1835784623280692274L;

	/**
	 * Constructor for CUP error.
	 * @param reason is the reason for the error.
 	 */
	public CupError(String reason) {
		super(reason);
	}
}
