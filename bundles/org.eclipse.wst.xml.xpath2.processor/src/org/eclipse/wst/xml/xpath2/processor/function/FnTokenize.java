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
 * This function breaks the $input string into a sequence of strings, treating any
 * substring that matches $pattern as a separator. The separators themselves are
 * not returned.
 */
public class FnTokenize extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnTokenize.
	 */
	public FnTokenize() {
		super(new QName("tokenize"), 2);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return tokenize(args);
	}
	/**
         * Tokenize operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:tokenize operation.
         */
	public static ResultSequence tokenize(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();
		String str1 = "";
		if(!arg1.empty()) 
			str1 = ((XSString)arg1.first()).value();
		
		ResultSequence arg2 = (ResultSequence) argiter.next();
		String pattern = ((XSString)arg2.first()).value();

		// XXX THIS IS NOT CORRECT
		try {
			String[] ret = str1.split(pattern,-1);

			for(int i = 0; i < ret.length; i++)
				rs.add(new XSString(ret[i]));
		} catch(PatternSyntaxException err) {
			throw DynamicError.regex_error(null);
		}

		return rs;
	}
	/**
         * Obtain a list of expected arguments.
         * @return Result of operation.
         */
	public static Collection expected_args() {
		if(_expected_args == null) {
			_expected_args = new ArrayList();
			SeqType arg = new SeqType(new XSString(), 
						  SeqType.OCC_QMARK);
			_expected_args.add(arg);
			_expected_args.add(new SeqType(new XSString(),
						       SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
