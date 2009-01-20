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
 * Computes the effective boolean value of the sequence $arg.
 * If $arg is the empty sequence, returns false.
 * If $arg contains a single atomic value, then the function returns false if $arg is:
 * - The singleton xs:boolean value false.
 * - The singleton value "" (zero-length string) of type xs:string or xdt:untypedAtomic.
 * - A singleton numeric value that is numerically equal to zero.
 * - The singleton xs:float or xs:double value NaN.
 * In all other cases, returns true.
 */
public class FnBoolean extends Function {
	/**
	 * Constructor for FnBoolean.
	 */
	public FnBoolean() {
		super(new QName("boolean"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) {
		// 1 argument only!
		assert args.size() == arity();

		ResultSequence argument = (ResultSequence) args.iterator().next();

		return fn_boolean(argument);
	}

	private static ResultSequence make_sequence(boolean val) {
		ResultSequence rs = ResultSequenceFactory.create_new();
		rs.add(new XSBoolean(val));
		return rs;
	}

	private static ResultSequence make_true() {
		return make_sequence(true);
	}
	
	private static ResultSequence make_false() {
		return make_sequence(false);
	}
	/**
         * Boolean operation.
         * @param arg Result from the expressions evaluation.
         * @return Result of fn:boolean operation.
         */
	public static ResultSequence fn_boolean(ResultSequence arg) {
		if(arg.empty()) 
			return make_false();
		
		if(arg.size() > 1) 
			return make_true();

		AnyType at = arg.first();

		// XXX ??
		if(!(at instanceof AnyAtomicType))
			return make_true();

	
		// ok we got 1 single atomic type element
		
		if(at instanceof XSBoolean) {
			 if(!((XSBoolean)at).value())
			 	return make_false();
		}
		
		if( (at instanceof XSString) || (at instanceof
		UntypedAtomic)) {
			if(at.string_value().equals(""))
				return make_false();
		}
		
		if(at instanceof NumericType) {
			if( ((NumericType)at).zero())
				return make_false();
		}

		if( (at instanceof XSFloat) && (((XSFloat)at).nan()) ) 
			return make_false();
		
		if( (at instanceof XSDouble) && (((XSDouble)at).nan()) ) 
			return make_false();
	

		return make_true();
	}

}
