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
 * The value returned is the nearest (that is, numerically closest) numeric to $arg that
 * is a multiple of ten to the power of minus $precision. If two such values are equally
 * near (e.g. if the fractional part in $arg is exactly .500...), returns the one whose
 * least significant digit is even. If type of $arg is one of the four numeric types
 * xs:float, xs:double, xs:decimal or xs:integer the type of the return is the same as
 * the type of $arg. If the type of $arg is a type derived from one of the numeric types,
 * the type of the return is the base numeric type.
 */
public class FnRoundHalfToEven extends Function {
	/**
	 * Constructor for FnRoundHalfToEven.
	 */
	public FnRoundHalfToEven() {
		super(new QName("round-half-to-even"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		// 1 argument only!
		assert args.size() == arity();

		ResultSequence argument = (ResultSequence) args.iterator().next();

		return fn_round_half_to_even(argument);
	}
	/**
         * Round-Half-to-Even operation.
         * @param arg Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:round-half-to-even operation.
         */
	public static ResultSequence fn_round_half_to_even(ResultSequence arg) 
							   throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		// sanity chex
		NumericType nt = FnAbs.get_single_numeric_arg(arg);
		
		// empty arg
		if(nt == null)
			return rs;
	
		rs.add(nt.round_half_to_even());
		return rs;
	}
}
