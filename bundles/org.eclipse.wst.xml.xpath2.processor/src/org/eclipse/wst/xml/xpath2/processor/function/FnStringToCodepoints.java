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
 * Returns the sequence of code points that constitute an xs:string. If $arg is a
 * zero-length string or the empty sequence, the empty sequence is returned.
 */
public class FnStringToCodepoints extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnStringToCodepoints.
	 */
	public FnStringToCodepoints() {
		super(new QName("string-to-codepoints"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return string_to_codepoints(args);
	}
	/**
         * Base-Uri operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:base-uri operation.
         */
	public static ResultSequence string_to_codepoints(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();
		if(arg1.empty())
			return rs;

		XSString xstr = (XSString) arg1.first();	
		String str = xstr.value();

		// XXX this is wrong
		for(int i = 0; i < str.length(); i++) {
			char x = str.charAt(i);

			rs.add(new XSInteger(x));
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
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
