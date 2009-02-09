/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.function;

import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * <p>
 * Function to calculate string length.
 * </p>
 * 
 * <p>
 * Usage: fn:string-length($arg as xs:string?) as xs:integer
 * </p>
 * 
 * <p>
 * This class returns an xs:integer equal to the length in characters of the
 * value of $arg.
 * </p>
 * 
 * <p>
 * If the value of $arg is the empty sequence, the xs:integer 0 is returned.
 * </p>
 */
public class FnStringLength extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnStringLength
	 */
	public FnStringLength() {
		super(new QName("string-length"), 1);
	}

	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            are evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the string length of the arguments.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return string_length(args);
	}

	/**
	 * Obtain the string length of the arguments.
	 * 
	 * @param args
	 *            are used to obtain the string length.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of obtaining the string length from the arguments.
	 */
	public static ResultSequence string_length(Collection args)
			throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();

		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg1.empty()) {
			rs.add(new XSInteger(0));
			return rs;
		}

		String str = ((XSString) arg1.first()).value();

		rs.add(new XSInteger(str.length()));

		return rs;
	}

	/**
	 * Calculate the expected arguments.
	 * 
	 * @return The expected arguments.
	 */
	public static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
