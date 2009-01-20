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
import java.util.regex.*;
/**
 * Returns the xs:boolean value false. Equivalent to xs:boolean("0").
 */
public class FnFalse extends Function {
	/**
	 * Constructor for FnFalse.
	 */
	public FnFalse() {
		super(new QName("false"), 0);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return fn_false(args);
	}
	/**
         * False operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:false operation.
         */
	public static ResultSequence fn_false(Collection args) throws DynamicError {
		assert args.size() == 0;

		return ResultSequenceFactory.create_new(new XSBoolean(false));
	}
}
