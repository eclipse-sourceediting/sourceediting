/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
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
package org.eclipse.wst.xsl.core.internal.validation;

/**
 * An exception used to indicate whether the maximum number of errors has been reached 
 * for a given Stylesheet validation.
 * 
 * @author Doug Satchwell
 */
public class MaxErrorsExceededException extends Exception
{
	private static final long serialVersionUID = 1L;	
}
