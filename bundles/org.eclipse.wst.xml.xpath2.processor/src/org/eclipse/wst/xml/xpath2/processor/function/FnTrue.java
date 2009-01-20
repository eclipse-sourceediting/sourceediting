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
 * Returns the xs:boolean value true. Equivalent to xs:boolean("1").
 */
public class FnTrue extends Function {
	/**
	 * Constructor for FnTrue.
	 */
	public FnTrue() {
		super(new QName("true"), 0);
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
		return fn_true(args);
	}

	/**
	 * True operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:true operation.
	 */
	public static ResultSequence fn_true(Collection args) throws DynamicError {
		assert args.size() == 0;

		ResultSequence rs = ResultSequenceFactory.create_new();

		rs.add(new XSBoolean(true));

		return rs;
	}
}
