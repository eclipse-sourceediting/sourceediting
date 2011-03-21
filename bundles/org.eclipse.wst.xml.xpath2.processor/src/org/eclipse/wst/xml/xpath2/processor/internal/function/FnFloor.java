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
 * Returns the largest (closest to positive infinity) number with no fractional
 * part that is not greater than the value of $arg. If type of $arg is one of
 * the four numeric types xs:float, xs:double, xs:decimal or xs:integer the type
 * of the return is the same as the type of $arg. If the type of $arg is a type
 * derived from one of the numeric types, the type of the return is the base
 * numeric type. For float and double arguments, if the argument is positive
 * zero (+0), then positive zero (+0) is returned. If the argument is negative
 * zero (-0), then negative zero (-0) is returned.
 */
public class FnFloor extends Function {
	/**
	 * Constructor for FnFloor.
	 */
	public FnFloor() {
		super(new QName("floor"), 1);
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

		return fn_floor(argument);
	}

	/**
	 * Floor operation.
	 * 
	 * @param arg
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:floor operation.
	 */
	public static ResultSequence fn_floor(ResultSequence arg)
			throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		// sanity chex
		NumericType nt = FnAbs.get_single_numeric_arg(arg);

		// empty arg
		if (nt == null)
			return rs;

		rs.add(nt.floor());
		return rs;
	}
}
