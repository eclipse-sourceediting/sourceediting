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
 * This exception is thrown when there is a problem with an XPath exception.
 */
public class XPathException extends Exception {
	private String _reason;

	/**
 	 * Constructor for XPathException
	 * @param reason Is the reason why the exception has been thrown.
 	 */
	public XPathException(String reason) {
		_reason = reason;
	}
	
	/**
	 * The reason why the exception has been thrown.
	 * @return the reason why the exception has been throw. 
	 */
	public String reason() {
		return _reason;
	}
}
