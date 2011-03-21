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
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * The value returned is the nearest (that is, numerically closest) numeric to
 * $arg that is a multiple of ten to the power of minus $precision. If two such
 * values are equally near (e.g. if the fractional part in $arg is exactly
 * .500...), returns the one whose least significant digit is even. If type of
 * $arg is one of the four numeric types xs:float, xs:double, xs:decimal or
 * xs:integer the type of the return is the same as the type of $arg. If the
 * type of $arg is a type derived from one of the numeric types, the type of the
 * return is the base numeric type.
 */
public class FnRoundHalfToEven extends Function {
	/**
	 * Constructor for FnRoundHalfToEven.
	 */
	public FnRoundHalfToEven() {
		super(new QName("round-half-to-even"), 1, 2);
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
		ResultSequence argument = (ResultSequence) args.iterator().next();
		if (args.size() == 2) {
			return fn_round_half_to_even(args);
		}
		
		return fn_round_half_to_even(argument);
	}

	/**
	 * Round-Half-to-Even operation.
	 * 
	 * @param arg
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:round-half-to-even operation.
	 */
	public static ResultSequence fn_round_half_to_even(ResultSequence arg)
			throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		NumericType nt = FnAbs.get_single_numeric_arg(arg);

		// empty arg
		if (nt == null)
			return rs;
		
		rs.add(nt.round_half_to_even());
		return rs;
	}
	
	public static ResultSequence fn_round_half_to_even(Collection args) throws DynamicError {
		
		if (args.size() > 2 || args.size() <= 1) {
			throw new DynamicError(TypeError.invalid_type(null));
		}
		
		Iterator argIt = args.iterator();
		ResultSequence rsArg1 =  (ResultSequence) argIt.next();
		ResultSequence rsPrecision = (ResultSequence) argIt.next();
		
		NumericType nt = (NumericType) rsArg1.first();
		NumericType ntPrecision = (NumericType) rsPrecision.first();
		
		ResultSequence rs = ResultSequenceFactory.create_new();
		
		rs.add(nt.round_half_to_even(Integer.parseInt(ntPrecision.string_value())));
		return rs;

	}
}
