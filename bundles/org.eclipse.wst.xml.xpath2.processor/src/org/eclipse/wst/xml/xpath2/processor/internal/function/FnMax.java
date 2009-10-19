/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Moller - bug 280555 - Add pluggable collation support
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Selects an item from the input sequence $arg whose value is greater than or
 * equal to the value of every other item in the input sequence. If there are
 * two or more such items, then the specific item whose value is returned is
 * implementation dependent.
 */
public class FnMax extends Function {
	/**
	 * Constructor for FnMax.
	 */
	public FnMax() {
		super(new QName("max"), 1);
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
		return max(args, dynamic_context());
	}

	/**
	 * Max operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param context 
	 *            Relevant dynamic context
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:max operation.
	 */
	public static ResultSequence max(Collection args, DynamicContext context) throws DynamicError {

		// XXX fix this
		ResultSequence arg = get_arg(args, CmpGt.class);
		if (arg.empty())
			return ResultSequenceFactory.create_new();

		CmpGt max = null;

		for (Iterator i = arg.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if (!(at instanceof CmpGt))
				DynamicError.throw_type_error();

			CmpGt item = (CmpGt) at;

			if (max == null)
				max = item;
			else {
				boolean res = item.gt((AnyType) max, context);

				if (res)
					max = item;
			}
		}

		return ResultSequenceFactory.create_new((AnyType) max);
	}

	/**
	 * Obtain arguments.
	 * 
	 * @param args
	 *            input expressions.
	 * @param op
	 *            input class.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of operation.
	 */
	public static ResultSequence get_arg(Collection args, Class op)
			throws DynamicError {
		assert args.size() == 1;

		ResultSequence arg = (ResultSequence) args.iterator().next();

		if (arg.empty())
			return arg;

		AnyType at = arg.first();

		// check for operator
		// XXX ok this is wrong... [promotion, and other reasons]
		if (op.isInstance(at) && !(at instanceof NumericType)) {
			Class type = at.getClass();

			for (Iterator i = arg.iterator(); i.hasNext();) {
				at = (AnyType) i.next();
				if (!(type.isInstance(at)))
					DynamicError.throw_type_error();
			}
		} else {
			arg = FnAvg.get_arg(args);
		}

		return arg;
	}
}
