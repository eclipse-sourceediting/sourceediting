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
 * Returns the items of $sourceSeq in a non-deterministic order.
 */
public class FnUnordered extends Function {
	/**
	 * Constructor for FnUnordered.
	 */
	public FnUnordered() {
		super(new QName("unordered"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return unordered(args);
	}
	/**
         * Unordered operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:unordered operation.
         */
	public static ResultSequence unordered(Collection args) throws DynamicError {

		assert args.size() == 1;
			
		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence arg = (ResultSequence) citer.next();

		if(arg.empty())
			return rs;

		// XXX lame
		ArrayList tmp = new ArrayList();
		for(Iterator i = arg.iterator(); i.hasNext();)
			tmp.add(i.next());
		
		Collections.shuffle(tmp);

		for(Iterator i = tmp.iterator(); i.hasNext();)
			rs.add( (AnyType) i.next());

		return rs;
	}
}
