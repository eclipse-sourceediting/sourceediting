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
 * If the value of $arg is the empty sequence, the function returns true; otherwise,
 * the function returns false.
 */
public class FnEmpty extends Function {
	/**
	 * Constructor for FnEmpty.
	 */
	public FnEmpty() {
		super(new QName("empty"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return empty(args);
	}
	/**
         * Empty operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:empty operation.
         */
	public static ResultSequence empty(Collection args) throws DynamicError {

		assert args.size() == 1;
			
		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence arg1 = (ResultSequence) citer.next();

		if(arg1.empty())
			rs.add(new XSBoolean(true));
		else
			rs.add(new XSBoolean(false));
		
		return rs;
	}
}
