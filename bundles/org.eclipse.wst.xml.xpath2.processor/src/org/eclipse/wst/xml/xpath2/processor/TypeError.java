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
 * Error caused by bad types.
 */
public class TypeError extends XPathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 932275035706936883L;
	// errorcode specified in http://www.w3.org/2004/10/xqt-errors i fink
	private String _code;

	/**
	 * Constructor for type error.
	 * @param code is the error code.
	 * @param err is the reason for the error.
	 */
	public TypeError(String code, String err) {
		super(err);
		_code = code;
	}
	
	/**
	 * Get the error code.
	 * @return The error code.
	 */
	public String code() { return _code; }


	/**
	 * "Factory" for building errors
	 * @param err is the reason for the error.
	 * @return the error.
	 */
	public static TypeError ci_not_node(String err) {
		String error = "Context item is not a node.";

		if(err != null)
			error += " " + err;

		
		return new TypeError("XP0020", error);
	}

	/**
	 * "Factory" for building errors
	 * @param err is the reason for the error.
	 * @return the error.
	 */
	public static TypeError mixed_vals(String err) {
		String error = "The result of the last step in a path expression contains both nodes and atomic values.";

		if(err != null)
			error += " " + err;
		
		return new TypeError("XP0018", error);
	}

	/**
	 * "Factory" for building errors
	 * @param err is the reason for the error.
	 * @return the error.
	 */	
	public static TypeError step_conatins_atoms(String err) {
		String error = "The result of an step (other than the last step) in a path expression contains an atomic value.";

		if(err != null)
			error += " " + err;
		
		return new TypeError("XP0019", error);
	}

	/**
	 * "Factory" for building errors
	 * @param err is the reason for the error.
	 * @return the error.
	 */	
	public static TypeError invalid_type(String err) {
		String error = "Value does not match a required type.";

		if(err != null)
			error += " " + err;
		
		return new TypeError("XP0006", error);
	}
}
