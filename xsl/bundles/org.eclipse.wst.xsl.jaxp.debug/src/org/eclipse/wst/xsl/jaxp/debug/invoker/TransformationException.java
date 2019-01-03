/*******************************************************************************
 * Copyright (c) 2007, 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.invoker;

/**
 * Checked Exception for transformation errors.
 * 
 * @author Doug Satchwell
 */
public class TransformationException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new instance of this with the given message.
	 * 
	 * @param message the message
	 */
	public TransformationException(String message)
	{
		super(message);
	}

	/**
	 * Create a new instance of this with the given message and cause.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public TransformationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}