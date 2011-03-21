/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver - bug 282096 - improvements for surrogate handling
 *     David Carver - bug 262765 - improvements to Regular Expression   
 *     Jesper Steen Moeller - bug 282096 - clean up string storage
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;
import java.util.regex.*;

/**
 * The function returns true if $input matches the regular expression supplied
 * as $pattern as influenced by the value of $flags, if present; otherwise, it
 * returns false.
 */
public class FnMatches extends AbstractRegExFunction {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnMatches.
	 */
	public FnMatches() {
		super(new QName("matches"), 2, 3);
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
		if (!arg1.empty()) {
			str1 = ((XSString) arg1.first()).value();
		}

		ResultSequence arg2 = (ResultSequence) argiter.next();
		String pattern = ((XSString) arg2.first()).value();
		String flags = null;

		if (argiter.hasNext()) {
			ResultSequence flagRS = null;
			flagRS = (ResultSequence) argiter.next();
			flags = flagRS.first().string_value();
			if (validflags.indexOf(flags) == -1 && flags.length() > 0 ) {
				throw DynamicError.regex_flags_error(null);
			}
		}

		try {
			boolean result = false;
			result = matches(pattern, flags, str1);
			rs.add(new XSBoolean(result));
			return rs;
		} catch (PatternSyntaxException pex) {
			throw DynamicError.regex_error(pex.getMessage());
		}
	}
	

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public synchronized static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			SeqType arg = new SeqType(new XSString(), SeqType.OCC_QMARK);
			_expected_args.add(arg);
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
