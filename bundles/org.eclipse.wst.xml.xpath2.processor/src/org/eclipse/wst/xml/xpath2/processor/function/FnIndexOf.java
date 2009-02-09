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
 * Returns a sequence of positive integers giving the positions within the
 * sequence $seqParam of items that are equal to $srchParam.
 */
public class FnIndexOf extends Function {
	/**
	 * Constructor for FnIndexOf.
	 */
	public FnIndexOf() {
		super(new QName("index-of"), 2);
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
		return index_of(args);
	}

	/**
	 * Obtain a comparable type.
	 * 
	 * @param at
	 *            expression of any type.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of operation.
	 */
	private static CmpEq get_comparable(AnyType at) throws DynamicError {
		if (!(at instanceof AnyAtomicType))
			DynamicError.throw_type_error();

		if (!(at instanceof CmpEq))
			throw DynamicError.not_cmp(null);

		return (CmpEq) at;
	}

	/**
	 * Index-Of operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:index-of operation.
	 */
	public static ResultSequence index_of(Collection args) throws DynamicError {

		assert args.size() == 2;

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence arg1 = (ResultSequence) citer.next();
		ResultSequence arg2 = (ResultSequence) citer.next();

		// sanity chex
		if (arg2.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = arg2.first();

		get_comparable(at);

		int index = 1;

		for (Iterator i = arg1.iterator(); i.hasNext();) {
			CmpEq candidate = get_comparable((AnyType) i.next());

			if (candidate.eq(at))
				rs.add(new XSInteger(index));

			index++;
		}

		return rs;
	}
}
