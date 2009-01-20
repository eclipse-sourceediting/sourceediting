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
 * Creates an xs:string from a sequence of code points. Returns the zero-length string
 * if $arg is the empty sequence. If any of the code points in $arg is not a legal XML
 * character, an error is raised [err:FOCH0001].
 */
public class FnCodepointsToString extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnCodepointsToString.
	 */
	public FnCodepointsToString() {
		super(new QName("codepoints-to-string"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return codepoints_to_string(args);
	}
	/**
         * Codepoints to string operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:codepoints-to-string operation.
         */
	public static ResultSequence codepoints_to_string(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();
		if(arg1.empty()) {
			rs.add(new XSString(""));
			return rs;
		}	

		// XXX this is wrong
		StringBuffer sb = new StringBuffer();
		for(Iterator i = arg1.iterator(); i.hasNext();) {
			XSInteger code = (XSInteger) i.next();

			sb.append( (char) code.int_value());
		}

		rs.add(new XSString(sb.toString()));

		return rs;
	}
	/**
         * Obtain a list of expected arguments.
         * @return Result of operation.
         */
	public static Collection expected_args() {
		if(_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XSInteger(), SeqType.OCC_STAR));
		}

		return _expected_args;
	}
}
