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
 * <p>Function to obtain a substring from a string.</p>
 *
 * <p>Usage: fn:substring($sourceString as xs:string?,
 *                     $startingLoc as xs:double) as xs:string</p>
 *
 * <p>This class returns the portion of the value of $sourceString beginning at the position
 * indicated by the value of $startingLoc. The characters returned do not extend beyond
 * $sourceString. If $startingLoc is zero or negative, only those characters in positions
 * greater than zero are returned.</p>
 *
 * <p>If the value of $sourceString is the empty sequence, the zero-length string is returned.</p>
 *
 * <p>The first character of a string is located at position 1, not position 0.</p>
 */
public class FnSubstring extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnSubstring
	 */
	public FnSubstring() {
		super(new QName("substring"), 2);
	}

	/**
	 * Evaluate the arguments.
	 * @param args are evaluated.
	 * @throws DynamicError Dynamic error.
	 * @return The evaluation of the substring obtained from the arguments.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return substring(args);
	}

	/**
	 * Obtain a substring from the arguments.
	 * @param args are used to obtain a substring.
	 * @throws DynamicError Dynamic error.
	 * @return The result of obtaining a substring from the arguments.
	 */
	public static ResultSequence substring(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		Iterator argi = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argi.next();
		ResultSequence arg2 = (ResultSequence) argi.next();


		ResultSequence rs = ResultSequenceFactory.create_new();

		if(arg1.empty())
			return rs;

		String str = ((XSString)arg1.first()).value();
		double dstart = ((XSDouble)arg2.first()).double_value();

		int start = (int) Math.round(dstart);

		if(start > str.length())
			return rs;

		rs.add(new XSString(str.substring(start)));	

		return rs;
	}

	/**
	 * Calculate the expected arguments.
	 * @return The expected arguments.
	 */
	public static Collection expected_args() {
		if(_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XSString(),
						       SeqType.OCC_QMARK));
			_expected_args.add(new SeqType(new XSDouble(),
						       SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
