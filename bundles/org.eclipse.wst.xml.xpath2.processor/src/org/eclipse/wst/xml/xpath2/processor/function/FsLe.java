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
 * Class for Less than or equal to function.
 */
public class FsLe extends Function {
	/**
	 * Constructor for FsLe.
	 */
	public FsLe() {
		super(new QName("le"), 2);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		assert args.size() == arity();

		return fs_le_value(args);
	}

	/**
         * Operation on the values of the arguments.
         * @param args input arguments.
         * @throws DynamicError Dynamic error.
         * @return Result of the operation.
         */
	public static ResultSequence fs_le_value(Collection args) throws DynamicError {
		ResultSequence less = FsLt.fs_lt_value(args);

		if( ((XSBoolean)less.first()).value())
			return less;
	
		ResultSequence equal = FsEq.fs_eq_value(args);

		if( ((XSBoolean)equal.first()).value())
			return equal;

		return ResultSequenceFactory.create_new(new XSBoolean(false));
	}
	/**
         * General operation on the arguments.
         * @param args input arguments.
         * @throws DynamicError Dynamic error.
         * @return Result of the operation.
         */
        public static ResultSequence fs_le_general(Collection args) throws DynamicError {
                return FsEq.do_cmp_general_op(args, FsLe.class, "fs_le_value");
        }
}
