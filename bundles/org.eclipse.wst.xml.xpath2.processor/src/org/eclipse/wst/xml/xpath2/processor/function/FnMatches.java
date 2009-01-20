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
import java.util.regex.*;

/**
 * The function returns true if $input matches the regular expression supplied
 * as $pattern as influenced by the value of $flags, if present; otherwise, it
 * returns false.
 */
public class FnMatches extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnMatches.
	 */
	public FnMatches() {
		super(new QName("matches"), 2);
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
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return matches(args);
	}

	/**
	 * Matches operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:matches operation.
	 */
	public static ResultSequence matches(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();
		String str1 = "";
		if (!arg1.empty())
			str1 = ((XSString) arg1.first()).value();

		ResultSequence arg2 = (ResultSequence) argiter.next();
		String pattern = ((XSString) arg2.first()).value();

		// XXX THIS IS NOT CORRECT
		try {
			boolean result = false;

			if (pattern.indexOf('^') == -1 && pattern.indexOf('$') == -1) {
				result = str1.indexOf(pattern) != -1;
			} else {
				result = str1.matches(pattern);
			}

			rs.add(new XSBoolean(result));
			return rs;
		} catch (PatternSyntaxException err) {
			throw DynamicError.regex_error(null);
		}
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			SeqType arg = new SeqType(new XSString(), SeqType.OCC_QMARK);
			_expected_args.add(arg);
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
