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

package org.eclipse.wst.xml.xpath2.processor.function;

import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * Returns the value indicated by $arg or, if $arg is not specified, the context
 * item after atomization, converted to an xs:double. If $arg or the context
 * item cannot be converted to an xs:double, the xs:double value NaN is
 * returned. If the context item is undefined an error is raised:
 * [err:FONC0001].
 */
public class FnNumber extends Function {
	/**
	 * Constructor for FnNumber.
	 */
	public FnNumber() {
		super(new QName("number"), 1);
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

		assert args.size() == arity();

		ResultSequence argument = (ResultSequence) args.iterator().next();

		return fn_number(argument, dynamic_context());
	}

	/**
	 * Number operation.
	 * 
	 * @param arg
	 *            Result from the expressions evaluation.
	 * @param dc
	 *            Result of dynamic context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:number operation.
	 */
	public static ResultSequence fn_number(ResultSequence arg, DynamicContext dc)
			throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.size() > 1)
			throw new DynamicError(TypeError.invalid_type(null));

		AnyType at = null;
		if (arg.size() == 1)
			at = arg.first();
		else
			at = dc.context_item();

		if (!(at instanceof AnyAtomicType))
			DynamicError.throw_type_error();

		AnyAtomicType aat = (AnyAtomicType) at;

		XSDouble d = XSDouble.parse_double(aat.string_value());
		if (d == null)
			d = new XSDouble(Double.NaN);

		rs.add(d);
		return rs;
	}

}
