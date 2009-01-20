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
 * Returns a value obtained by adding together the values in $arg. If the
 * single-argument form of the function is used, then the value returned for an
 * empty sequence is the xs:integer value 0. If the two-argument form is used,
 * then the value returned for an empty sequence is the value of the $zero
 * argument.
 */
public class FnSum extends Function {
	/**
	 * Constructor for FnSum.
	 */
	public FnSum() {
		super(new QName("sum"), 1);
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
		return sum(args);
	}

	/**
	 * Sum operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:sum operation.
	 */
	public static ResultSequence sum(Collection args) throws DynamicError {

		ResultSequence arg = FnAvg.get_arg(args);

		if (arg.empty())
			return ResultSequenceFactory.create_new(new XSInteger(0));

		MathPlus total = null;
		for (Iterator i = arg.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if (!(at instanceof MathPlus))
				DynamicError.throw_type_error();

			if (total == null)
				total = (MathPlus) at;
			else {
				ResultSequence res = total.plus(ResultSequenceFactory
						.create_new(at));
				assert res.size() == 1;

				total = (MathPlus) res.first();
			}
		}

		return ResultSequenceFactory.create_new((AnyType) total);
	}
}
