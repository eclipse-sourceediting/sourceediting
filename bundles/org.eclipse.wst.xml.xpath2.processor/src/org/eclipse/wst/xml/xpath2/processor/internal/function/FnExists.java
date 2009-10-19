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

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * If the value of $arg is not the empty sequence, the function returns true;
 * otherwise, the function returns false.
 */
public class FnExists extends Function {
	/**
	 * Constructor for FnExists.
	 */
	public FnExists() {
		super(new QName("exists"), 1);
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
		return exists(args);
	}

	/**
	 * Exists operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:exists operation.
	 */
	public static ResultSequence exists(Collection args) throws DynamicError {

		assert args.size() == 1;

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence arg1 = (ResultSequence) citer.next();

		if (arg1.empty())
			rs.add(new XSBoolean(false));
		else
			rs.add(new XSBoolean(true));

		return rs;
	}
}
