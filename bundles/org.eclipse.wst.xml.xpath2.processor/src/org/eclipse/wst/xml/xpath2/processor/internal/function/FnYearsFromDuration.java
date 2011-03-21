/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 273760 - wrong namespace for functions and data types
 *     Mukul Gandhi - bug 274805 - improvements to xs:integer data type
 *     David Carver - bug 282223 - implementation of xs:duration.
 *     David Carver (STAR) - bug 262765 - fixed expected xs:duration argument 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.math.BigInteger;
import java.util.*;

/**
 * Returns an xs:integer representing the years component in the canonical
 * lexical representation of the value of $arg. The result may be negative. If
 * $arg is the empty sequence, returns the empty sequence.
 */
public class FnYearsFromDuration extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnYearsFromDuration.
	 */
	public FnYearsFromDuration() {
		super(new QName("years-from-duration"), 1);
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
		return years_from_duration(args);
	}

	/**
	 * Years-from-Duration operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:years-from-duration operation.
	 */
	public static ResultSequence years_from_duration(Collection args)
			throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();

		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg1.empty()) {
			return rs;
		}

		XSDuration ymd = (XSDuration) arg1.first();

		int res = ymd.year();
		if (ymd.negative())
			res *= -1;

		rs.add(new XSInteger(BigInteger.valueOf(res)));

		return rs;
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public synchronized static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XSDuration(),
					SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
