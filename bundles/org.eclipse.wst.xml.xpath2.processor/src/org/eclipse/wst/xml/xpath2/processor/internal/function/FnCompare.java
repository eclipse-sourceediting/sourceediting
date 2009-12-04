/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Jesper Steen Moeller - bug 280555 - Add pluggable collation support
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.math.BigInteger;
import java.util.*;

/**
 * 
 * <p>
 * String comparison function.
 * </p>
 * 
 * <p>
 * Usage: fn:compare($comparand1 as xs:string?, $comparand2 as xs:string?) as
 * xs:integer?
 * </p>
 * 
 * <p>
 * This class returns -1, 0, or 1, depending on whether the value of $comparand1
 * is respectively less than, equal to, or greater than the value of
 * $comparand2.
 * </p>
 * 
 * <p>
 * If the value of $comparand2 begins with a string that is equal to the value
 * of $comparand1 (according to the collation that is used) and has additional
 * code points following that beginning string, then the result is -1. If the
 * value of $comparand1 begins with a string that is equal to the value of
 * $comparand2 and has additional code points following that beginning string,
 * then the result is 1.
 * </p>
 * 
 * <p>
 * If either argument is the empty sequence, the result is the empty sequence.
 * </p>
 */
public class FnCompare extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor of FnCompare.
	 */
	public FnCompare() {
		super(new QName("compare"), 2, 3);
	}

	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            is evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the comparison of the arguments.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return compare(args, dynamic_context());
	}

	/**
	 * Compare the arguments.
	 * 
	 * @param args
	 *            are compared (optional 3rd argument is the collation)
	 * @param dynamicContext
	 * 	       Current dynamic context 
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of the comparison of the arguments.
	 */
	public static ResultSequence compare(Collection args, DynamicContext dynamicContext) throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();
		ResultSequence arg2 = (ResultSequence) argiter.next();

		String collationUri = dynamicContext.default_collation_name();
		if (argiter.hasNext()) {
			ResultSequence collArg = (ResultSequence) argiter.next();
			collationUri = collArg.first().string_value();
		}

		XSString xstr1 = arg1.empty() ? null : (XSString) arg1.first();
		XSString xstr2 = arg2.empty() ? null : (XSString) arg2.first();

		BigInteger result = compare_string(collationUri, xstr1, xstr2, dynamicContext);
		if (result != null) {
			return ResultSequenceFactory.create_new(new XSInteger(result));
		} else {
			return ResultSequenceFactory.create_new();			
		}
	}

	public static BigInteger compare_string(String collationUri, XSString xstr1,
			XSString xstr2, DynamicContext dynamicContext) throws DynamicError {
		Comparator collator = dynamicContext.get_collation(collationUri);
		if (collator == null) throw DynamicError.unsupported_collation(collationUri);

		if (xstr1 == null || xstr2 == null) return null;
		
		int ret = collator.compare(xstr1.value(), xstr2.value());

		if (ret == 0)
			return BigInteger.ZERO;
		else if (ret < 0)
			return BigInteger.valueOf(-1);
		else
			return BigInteger.ONE;
	}

	/**
	 * Calculate the expected arguments.
	 * 
	 * @return The expected arguments.
	 */
	public synchronized static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			SeqType arg = new SeqType(new XSString(), SeqType.OCC_QMARK);
			_expected_args.add(arg);
			_expected_args.add(arg);
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
