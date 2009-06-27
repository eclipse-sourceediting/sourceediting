/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - improvements to the function implementation 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Creates an xs:string from a sequence of code points. Returns the zero-length
 * string if $arg is the empty sequence. If any of the code points in $arg is
 * not a legal XML character, an error is raised [err:FOCH0001].
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
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return codepoints_to_string(args);
	}

	/**
	 * Codepoints to string operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:codepoints-to-string operation.
	 */
	public static ResultSequence codepoints_to_string(Collection args)
			throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();
		if (arg1.empty()) {
			rs.add(new XSString(""));
			return rs;
		}

		int[] codePointArray = new int[arg1.size()];
		int codePointIndex = 0;
		for (Iterator i = arg1.iterator(); i.hasNext();) {
			XSInteger code = (XSInteger) i.next();
			codePointArray[codePointIndex] = code.int_value().intValue();
			codePointIndex++;
		}

		// "new String(int[] codePoints, int offset, int count)" is a facility
		// introduced in Java 1.5
		rs.add(new XSString(new String(codePointArray, 0, codePointArray.length)));
		
		return rs;
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XSInteger(), SeqType.OCC_STAR));
		}

		return _expected_args;
	}
}
