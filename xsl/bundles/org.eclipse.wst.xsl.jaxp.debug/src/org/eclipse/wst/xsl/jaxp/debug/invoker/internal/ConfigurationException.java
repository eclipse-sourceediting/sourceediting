/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.invoker.internal;

/**
 * An exception thrown when configuration of an XSL transform pipeline fails.
 * 
 * @author Doug Satchwell
 */
public class ConfigurationException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create the exception.
	 * 
	 * @param msg the exception message
	 * @param t the underlying cause
	 */
	public ConfigurationException(String msg, Throwable t)
	{
		super(msg, t);
	}
}
