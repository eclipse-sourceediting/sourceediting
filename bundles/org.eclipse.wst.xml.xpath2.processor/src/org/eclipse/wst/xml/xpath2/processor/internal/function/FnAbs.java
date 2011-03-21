/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Returns the absolute value of $arg. If $arg is negative returns -$arg
 * otherwise returns $arg. If type of $arg is one of the four numeric types
 * xs:float, xs:double, xs:decimal or xs:integer the type of the return is the
 * same as the type of $arg. If the type of $arg is a type derived from one of
 * the numeric types, the type of the return is the base numeric type. For
 * xs:float and xs:double arguments, if the argument is positive zero (+0) or
 * negative zero (-0), then positive zero (+0) is returned. If the argument is
 * positive or negative infinity, positive infinity is returned.
 */
public class FnAbs extends Function {
	/**
	 * Constructor for FnAbs.
	 */
	public FnAbs() {
		super(new QName("abs"), 1);
	}

	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		// 1 argument only!
		assert args.size() >= min_arity() && args.size() <= max_arity();

		ResultSequence argument = (ResultSequence) args.iterator().next();

		return fn_abs(argument);
	}

	/**
	 * Absolute value operation.
	 * 
	 * @param arg
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:abs operation.
	 */
	public static ResultSequence fn_abs(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		// sanity chex
		NumericType nt = get_single_numeric_arg(arg);

		// empty arg
		if (nt == null)
			return rs;
		
		if (nt instanceof XSDouble) {
			XSDouble dat = (XSDouble) nt;
			if (dat.zero() || dat.negativeZero()) {
				rs.add(new XSDouble("0"));
				return rs;
			}
			if (dat.infinite()) {
				rs.add(new XSDouble(Double.POSITIVE_INFINITY));
				return rs;
			}
		}

		if (nt instanceof XSFloat) {
			XSFloat dat = (XSFloat) nt;
			if (dat.zero() || dat.negativeZero()) {
				rs.add(new XSFloat((new Float(0)).floatValue()));
				return rs;
			}
			if (dat.infinite()) {
				rs.add(new XSFloat(Float.POSITIVE_INFINITY));
				return rs;
			}
		}


		rs.add(nt.abs());
		return rs;
	}

	/**
	 * Obtain numeric value from expression.
	 * 
	 * @param arg
	 *            input expression.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Resulting numeric type from the operation.
	 */
	public static NumericType get_single_numeric_arg(ResultSequence arg)
			throws DynamicError {
		int size = arg.size();
		if (size > 1)
			DynamicError.throw_type_error();

		if (size == 0)
			return null;

		AnyType at = arg.first();

		if (!(at instanceof NumericType))
			throw DynamicError.invalidType();
				
		return (NumericType) at;
	}

}
