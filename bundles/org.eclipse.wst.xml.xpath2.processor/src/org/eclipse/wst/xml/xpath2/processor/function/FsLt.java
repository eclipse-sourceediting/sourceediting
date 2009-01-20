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
 * Class for Less than function.
 */
public class FsLt extends Function {
	/**
	 * Constructor for FsLt.
	 */
	public FsLt() {
		super(new QName("lt"), 2);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		assert args.size() == arity();

		return fs_lt_value(args);
	}

	/**
         * Operation on the values of the arguments.
         * @param args input arguments.
         * @throws DynamicError Dynamic error.
         * @return Result of the operation.
         */
	public static ResultSequence fs_lt_value(Collection args) throws DynamicError {
		return FsEq.do_cmp_value_op(args, CmpLt.class, "lt");
	}
	/**
         * General operation on the arguments.
         * @param args input arguments.
         * @throws DynamicError Dynamic error.
         * @return Result of the operation.
         */
        public static ResultSequence fs_lt_general(Collection args) throws DynamicError {
                return FsEq.do_cmp_general_op(args, FsLt.class, "fs_lt_value");
        }
}
