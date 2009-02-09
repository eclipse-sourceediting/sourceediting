/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.function;

import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * Returns the sequence that results from removing from $arg all but one of a
 * set of values that are eq to one other. Values that cannot be compared, i.e.
 * the eq operator is not defined for their types, are considered to be
 * distinct. Values of type xdt:untypedAtomic are compared as if they were of
 * type xs:string. The order in which the sequence of values is returned is
 * implementation dependent.
 */
public class FnDistinctValues extends Function {
	/**
	 * Constructor for FnDistinctValues.
	 */
	public FnDistinctValues() {
		super(new QName("distinct-values"), 1);
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
		return distinct_values(args);
	}

	/**
	 * Support for Contains interface.
	 * 
	 * @param rs
	 *            input1 expression sequence.
	 * @param item
	 *            input2 expression of any atomic type.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of operation.
	 */
	private static boolean contains(ResultSequence rs, AnyAtomicType item)
			throws DynamicError {
		if (!(item instanceof CmpEq))
			return false;

		for (Iterator i = rs.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if (!(item instanceof CmpEq))
				continue;

			CmpEq cmp = (CmpEq) at;

			if (cmp.eq(item))
				return true;

		}
		return false;
	}

	/**
	 * Distinct-values operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:distinct-values operation.
	 */
	public static ResultSequence distinct_values(Collection args)
			throws DynamicError {

		assert args.size() == 1;

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence arg1 = (ResultSequence) citer.next();

		// XXX lame
		for (Iterator i = arg1.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if (!(at instanceof AnyAtomicType))
				DynamicError.throw_type_error();

			if (!contains(rs, (AnyAtomicType) at))
				rs.add(at);
		}

		return rs;
	}
}
