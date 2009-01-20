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
 * Returns an xs:integer representing the days component in the canonical lexical
 * representation of the value of $arg. The result may be negative.
 * If $arg is the empty sequence, returns the empty sequence.
 */
public class FnDaysFromDuration extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnDaysFromDuration.
	 */
	public FnDaysFromDuration() {
		super(new QName("days-from-duration"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return days_from_duration(args);
	}
	/**
         * Days-From-Duration operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:days-from-duration operation.
         */
	public static ResultSequence days_from_duration(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();

		ResultSequence rs = ResultSequenceFactory.create_new();

		if(arg1.empty()) {
			return rs;
		}

		XDTDayTimeDuration dtd = (XDTDayTimeDuration) arg1.first();

		int res = dtd.days();

		if(dtd.negative())
			res *= -1;

		rs.add(new XSInteger(res));	

		return rs;
	}
	/**
         * Obtain a list of expected arguments.
         * @return Result of operation.
         */
	public static Collection expected_args() {
		if(_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XDTDayTimeDuration(),
						       SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
