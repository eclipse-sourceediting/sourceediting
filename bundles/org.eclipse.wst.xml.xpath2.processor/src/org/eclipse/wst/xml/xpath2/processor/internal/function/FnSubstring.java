/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 273795 - improvements to function, substring
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     David Carver - bug 282096 - improvements for surrogate handling 
 *     Jesper Steen Moeller - bug 282096 - reimplemented to be surrogate sensitive
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;
import org.eclipse.wst.xml.xpath2.processor.internal.utils.CodePointIterator;
import org.eclipse.wst.xml.xpath2.processor.internal.utils.StringCodePointIterator;

import java.util.*;

/**
 * <p>
 * Function to obtain a substring from a string.
 * </p>
 * 
 * <p>
 * Usage: fn:substring($sourceString as xs:string?, $startingLoc as xs:double)
 * as xs:string
 * </p>
 * 
 * <p>
 * This class returns the portion of the value of $sourceString beginning at the
 * position indicated by the value of $startingLoc. The characters returned do
 * not extend beyond $sourceString. If $startingLoc is zero or negative, only
 * those characters in positions greater than zero are returned.
 * </p>
 * 
 * <p>
 * If the value of $sourceString is the empty sequence, the zero-length string
 * is returned.
 * </p>
 * 
 * <p>
 * The first character of a string is located at position 1, not position 0.
 * </p>
 */
public class FnSubstring extends Function {

	/**
	 * Constructor for FnSubstring
	 */
	public FnSubstring() {
		super(new QName("substring"), 2, 3);
	}

	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            are evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the substring obtained from the arguments.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return substring(args);
	}

	/**
	 * Obtain a substring from the arguments.
	 * 
	 * @param args
	 *            are used to obtain a substring.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of obtaining a substring from the arguments.
	 */
	public static ResultSequence substring(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args(args));
		ResultSequence rs = ResultSequenceFactory.create_new();

		Iterator argi = cargs.iterator();
		ResultSequence stringArg = (ResultSequence) argi.next();
		ResultSequence startPosArg = (ResultSequence) argi.next();
		ResultSequence lengthArg = null;
		
		if (argi.hasNext()) {
		  lengthArg = (ResultSequence) argi.next();	
		}

		if (stringArg.empty()) {
			return emptyString(rs);
		}

		String str = ((XSString) stringArg.first()).value();
		double dstart = ((XSDouble) startPosArg.first()).double_value();
		
		// is start is NaN, no chars are returned
		if (Double.isNaN(dstart) || Double.NEGATIVE_INFINITY == dstart) {
			return emptyString(rs);
		}
		long istart = Math.round(dstart);
		
		long ilength = Long.MAX_VALUE;
		if (lengthArg != null) {
			double dlength = ((XSDouble) lengthArg.first()).double_value();
			if (Double.isNaN(dlength))
				return emptyString(rs);
			// Switch to the rounded kind
			ilength = Math.round(dlength);
			if (ilength <= 0)
				return emptyString(rs);
		}
		
		
		// could guess too short in cases supplementary chars 
		StringBuilder sb = new StringBuilder((int) Math.min(str.length(), ilength));

		// This looks like an inefficient way to iterate, but due to surrogate handling,
		// string indexes are no good here. Welcome to UTF-16!
		
		CodePointIterator strIter = new StringCodePointIterator(str);
		for (long p = 1; strIter.current() != CodePointIterator.DONE; ++p, strIter.next()) {
			if (istart <= p && p - istart < ilength)
				sb.appendCodePoint(strIter.current());
		}
		rs.add(new XSString(sb.toString()));

		return rs;
	}
	
	private static ResultSequence emptyString(ResultSequence rs) {
		rs.add(new XSString(""));
		return rs;
	}

	/**
	 * Calculate the expected arguments.
	 * 
	 * @return The expected arguments.
	 */
	public static Collection expected_args(Collection actualArgs) {
		Collection _expected_args = new ArrayList();
		
		_expected_args.add(new SeqType(new XSString(), SeqType.OCC_QMARK));
		_expected_args.add(new SeqType(new XSDouble(), SeqType.OCC_NONE));
		
		// for arity 3
		if (actualArgs.size() == 3) {
		  _expected_args.add(new SeqType(new XSDouble(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
