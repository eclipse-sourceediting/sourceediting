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

package org.eclipse.wst.xml.xpath2.processor.function;

import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * 
 * <p>
 * String comparison function.
 * </p>
 * 
 * <p>
 * Usage: fn:compare($comparand1 as xs:string?, $comparand2 as xs:string?) as
 * xs:integer?
 * </p>
 * 
 * <p>
 * This class returns -1, 0, or 1, depending on whether the value of $comparand1
 * is respectively less than, equal to, or greater than the value of
 * $comparand2.
 * </p>
 * 
 * <p>
 * If the value of $comparand2 begins with a string that is equal to the value
 * of $comparand1 (according to the collation that is used) and has additional
 * code points following that beginning string, then the result is -1. If the
 * value of $comparand1 begins with a string that is equal to the value of
 * $comparand2 and has additional code points following that beginning string,
 * then the result is 1.
 * </p>
 * 
 * <p>
 * If either argument is the empty sequence, the result is the empty sequence.
 * </p>
 */
public class FnCompare extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor of FnCompare.
	 */
	public FnCompare() {
		super(new QName("compare"), 2);
	}

	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            is evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the comparison of the arguments.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return compare(args);
	}

	/**
	 * Compare the arguments.
	 * 
	 * @param args
	 *            are compared.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of the comparison of the arguments.
	 */
	public static ResultSequence compare(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();
		if (arg1.empty())
			return rs;
		ResultSequence arg2 = (ResultSequence) argiter.next();
		if (arg2.empty())
			return rs;

		XSString xstr1 = (XSString) arg1.first();
		XSString xstr2 = (XSString) arg2.first();

		// XXX collations!!!
		int ret = xstr1.value().compareTo(xstr2.value());

		if (ret == 0)
			rs.add(new XSInteger(0));
		else if (ret < 0)
			rs.add(new XSInteger(-1));
		else
			rs.add(new XSInteger(1));

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
			SeqType arg = new SeqType(new XSString(), SeqType.OCC_QMARK);
			_expected_args.add(arg);
			_expected_args.add(arg);
		}

		return _expected_args;
	}
}
