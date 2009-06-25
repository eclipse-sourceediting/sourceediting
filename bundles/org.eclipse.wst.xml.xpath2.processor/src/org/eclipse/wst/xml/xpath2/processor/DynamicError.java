/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver (STAR) - bug 273763 - correct error codes.
 *                           bug 280106 - Correct XPDY0021 - XPST0003 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import org.eclipse.wst.xml.xpath2.processor.internal.TypeError;

/**
 * Dynamic Error like division by 0 or type errors.
 */
public class DynamicError extends XPathException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6146830764753685791L;

	// errorcode specified in http://www.w3.org/2004/10/xqt-errors i fink
	private String _code;

	// XXX dirty... should fix the error stuff
	// have a type error encapsulated in a dynamic error
	private TypeError _te;

	/**
	 * Constructor for Dynamic Error.
	 * 
	 * @param code
	 *            is the code that is set.
	 * @param err
	 *            is the reason for the error.
	 */
	public DynamicError(String code, String err) {
		super(err);
		_code = code;
		_te = null;
	}

	/**
	 * Constructor for Dynamic Error.
	 * 
	 * @param te
	 *            is the error type.
	 */
	public DynamicError(TypeError te) {
		super(te.reason());
		_te = te;
	}

	/**
	 * Returns the string of the code.
	 * 
	 * @return the code.
	 */
	public String code() {
		if (_te != null)
			return _te.code();
		return _code;
	}

	/**
	 * Returns the dynamic error.
	 * 
	 * @param err
	 *            is the error
	 * @return the DynamicError.
	 */
	public static DynamicError cant_cast(String err) {
		String error = "Can't cast to required type.";

		if (err != null)
			error += " " + err;

		return new DynamicError("XPST0003", error);
	}

	/**
	 * Returns the dynamic error.
	 * 
	 * @throws DynamicError
	 *             a Dynamic Error
	 * @return the DynamicError.
	 */
	public static DynamicError throw_type_error() throws DynamicError {
		
		throw new DynamicError(new TypeError(XPathParserException.INVALID_XPATH_EXPRESSION, "Invalid static type."));
	}

	/**
	 * Returns the dynamic error.
	 * 
	 * @param err
	 *            is the error
	 * @return the DynamicError.
	 */
	public static DynamicError user_error(String err) {
		String error = "Error reported by user.";

		if (err != null)
			error += " " + err;

		return new DynamicError("FOER0000", error);
	}

	/**
	 * Returns the dynamic error.
	 * 
	 * @param err
	 *            is the error
	 * @return the DynamicError.
	 */
	public static DynamicError regex_error(String err) {
		String error = "Invalid regular expression.";

		if (err != null)
			error += " " + err;

		return new DynamicError("FORX0002", error);
	}

	private static DynamicError make_error(String code, String err, String msg) {
		String error = err;

		if (msg != null)
			error += msg;

		return new DynamicError(code, error);
	}

	/**
	 * Returns the error message when reads an Invalid lexical value
	 * 
	 * @param msg
	 *            is the message
	 * @return the make_error
	 */
	public static DynamicError lexical_error(String msg) {
		return make_error("FOCA0002", "Invalid lexical value.", msg);
	}

	/**
	 * Returns the error message when reads an Items not comparable
	 * 
	 * @param msg
	 *            is the message
	 * @return the make_error
	 */
	public static DynamicError not_cmp(String msg) {
		return make_error("FOTY0012", "Items not comparable", msg);
	}

	/**
	 * Returns the error message
	 * 
	 * @param msg
	 *            is the message
	 * @return the make_error
	 */
	public static DynamicError more_one_item(String msg) {
		return make_error(
				"FORG0003",
				"fn:zero-or-one called with a sequence containing more than one item",
				msg);
	}

	/**
	 * Returns the error message
	 * 
	 * @param msg
	 *            is the message
	 * @return the make_error
	 */
	public static DynamicError empty_seq(String msg) {
		return make_error("FORG0004",
				"fn:one-or-more called with a sequence containing no items",
				msg);
	}

	/**
	 * Returns the error message
	 * 
	 * @param msg
	 *            is the message
	 * @return the make_error
	 */
	public static DynamicError not_one(String msg) {
		return make_error(
				"FORG0005",
				"fn:exactly-one called with a sequence containing zero or more than one item",
				msg);
	}

	/**
	 * Returns the error message when reads Invalid argument to fn:doc
	 * 
	 * @param msg
	 *            is the message
	 * @return the make_error
	 */
	public static DynamicError invalid_doc(String msg) {
		return make_error("FODC0005", "Invalid argument to fn:doc", msg);
	}

	/**
	 * Returns the error message when reads a Division by zero
	 * 
	 * @param msg
	 *            is the message
	 * @return the make_error
	 */
	public static DynamicError div_zero(String msg) {
		return make_error("FOAR0001", "Division by zero", msg);
	}
	
	/**
	 * @since 1.1
	 */
	public static DynamicError contextUndefined() {
		return make_error("XPDY0002", "Context is undefined.", "");
	}
}
