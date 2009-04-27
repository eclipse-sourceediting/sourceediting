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

import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * $arg is first reduced to an effective boolean value by applying the
 * fn:boolean() function. Returns true if the effective boolean value is false,
 * and false if the effective boolean value is true.
 */
public class FnNot extends Function {
	/**
	 * Constructor for FnNot.
	 */
	public FnNot() {
		super(new QName("not"), 1);
	}

	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @return Result of evaluation.
	 */
	@Override
	public ResultSequence evaluate(Collection args) {
		// 1 argument only!
		assert args.size() == arity();

		ResultSequence argument = (ResultSequence) args.iterator().next();

		return fn_not(argument);
	}

	/**
	 * Not operation.
	 * 
	 * @param arg
	 *            Result from the expressions evaluation.
	 * @return Result of fn:note operation.
	 */
	public static ResultSequence fn_not(ResultSequence arg) {
		ResultSequence rs = FnBoolean.fn_boolean(arg);

		XSBoolean ret = (XSBoolean) rs.first();

		boolean answer = false;

		if (ret.value() == false)
			answer = true;

		return ResultSequenceFactory.create_new(new XSBoolean(answer));
	}

}
