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
 * Returns $arg if it contains zero or one items. Otherwise, raises an error [err:FORG0003].
 * The type of the result depends on the type of $arg.
 */
public class FnZeroOrOne extends Function {
	/**
	 * Constructor for FnZeroOrOne.
	 */
	public FnZeroOrOne() {
		super(new QName("zero-or-one"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return zero_or_one(args);
	}
	/**
         * Zero-or-One operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:zero-or-one operation.
         */
	public static ResultSequence zero_or_one(Collection args) throws DynamicError {

		assert args.size() == 1;
			
		// get args
		Iterator citer = args.iterator();
		ResultSequence arg = (ResultSequence) citer.next();

		if(arg.size() > 1)
			throw DynamicError.more_one_item(null);
		
		return arg;
	}
}
