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
 * selects an item from the input sequence $arg whose value is less than or equal
 * to the value of every other item in the input sequence. If there are two or
 * more such items, then the specific item whose value is returned is implementation
 * independent.
 */
public class FnMin extends Function {
	/**
	 * Constructor for FnMin.
	 */
	public FnMin() {
		super(new QName("min"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return min(args);
	}
	/**
         * Min operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:min operation.
         */
	public static ResultSequence min(Collection args) throws DynamicError {

		// XXX fix this
		ResultSequence arg = FnMax.get_arg(args, CmpLt.class);
		if(arg.empty())
			return ResultSequenceFactory.create_new();

		CmpLt min = null;

		for(Iterator i = arg.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if( !(at instanceof CmpLt))
				DynamicError.throw_type_error();
			
			CmpLt item = (CmpLt) at;

			if(min == null)
				min = item;
			else {
				boolean res = item.lt( (AnyType) min);
				
				if( res)
					min = item;
			}
		}

		return ResultSequenceFactory.create_new( (AnyType) min);
	}
}
