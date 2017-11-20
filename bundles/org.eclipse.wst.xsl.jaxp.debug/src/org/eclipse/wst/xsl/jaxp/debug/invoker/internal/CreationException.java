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
 * Checked Exception for object creation errors.
 * 
 * @author Doug Satchwell
 */
public class CreationException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new instance of this with the given message.
	 * 
	 * @param message the message
	 */
	public CreationException(String message)
	{
		super(message);
	}

	/**
	 * Create a new instance of this with the given message and cause.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public CreationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}