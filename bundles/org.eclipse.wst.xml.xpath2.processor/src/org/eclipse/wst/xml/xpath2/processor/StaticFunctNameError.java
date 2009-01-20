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
 * Error caused by static function name.
 */
public class StaticFunctNameError extends StaticNameError {

	public static final String FUNCTION_NOT_FOUND = "XP0017";
	
	/**
	 * Constructor for static function name error
	 * @param reason is the reason for the error.
 	 */
	public StaticFunctNameError(String reason) {
		super(FUNCTION_NOT_FOUND,reason);
	}
}
