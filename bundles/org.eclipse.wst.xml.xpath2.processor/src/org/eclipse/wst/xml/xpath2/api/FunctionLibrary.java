/*******************************************************************************
 * Copyright (c) 2009 Jesper Moller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Moller - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.api;


/**
 * Interface for function libraries support.
 * @since 2.0
 */
public interface FunctionLibrary {

	public static final int VARIABLE_ARITY = Integer.MAX_VALUE;
	
	/**
	 * Checks whether the function exists or not.
	 * 
	 * @param name
	 *            Name of function.
	 * @param arity
	 *            arity of the function, 
	 * @return Result of the test.
	 */
	public boolean functionExists(String name, int arity);

	/**
	 * Function support.
	 * 
	 * @param name
	 *            local name .
	 * @param arity
	 *            arity of the function.
	 * @return The function from the library.
	 */
	public Function resolveFunction(String localName, int arity);

	/**
	 * Returns the namespace of the function library.
	 * 
	 * @return Namespace.
	 */
	public String getNamespace();
}
