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
 * Returns a new sequence constructed from the value of $target with the value of $inserts
 * inserted at the position specified by the value of $position. (The value of $target is
 * not affected by the sequence construction.)
 */
public class FnInsertBefore extends Function {
	/**
	 * Constructor for FnInsertBefore.
	 */
	public FnInsertBefore() {
		super(new QName("insert-before"), 3);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return insert_before(args);
	}
	/**
         * Insert-Before operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:insert-before operation.
         */
	public static ResultSequence insert_before(Collection args) throws DynamicError {

		assert args.size() == 3;
			
		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence target = (ResultSequence) citer.next();
		ResultSequence arg2 = (ResultSequence) citer.next();
		ResultSequence inserts = (ResultSequence) citer.next();

		// sanity chex
		if(arg2.size() != 1)
			DynamicError.throw_type_error();
		
		AnyType at = arg2.first();
		if( !(at instanceof XSInteger))
			DynamicError.throw_type_error();

		// XXX cloning!
		if(target.empty())
			return inserts;
		if(inserts.empty())
			return target;

		int position = ((XSInteger)at).int_value();

		if(position < 1)
			position = 1;
		int target_size = target.size();

		if(position > target_size)
			position = target_size + 1;
		

		int curpos = 1;

		for(Iterator i = target.iterator(); i.hasNext();) {
			at = (AnyType) i.next();

			if(curpos == position)
				rs.concat(inserts);

			rs.add(at);	
			
			curpos++;
		}
		if(curpos == position)
			rs.concat(inserts);

		return rs;
	}
}
