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
 * Returns the value of $arg represented as a xs:string. If no argument is
 * supplied, this function returns the string value of the context item (.).
 */
// XXX if no args, use context item!!! need to implement
public class FnString extends Function {
	/**
	 * Constructor for FnString.
	 */
	public FnString() {
		super(new QName("string"), 1);
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
		return string(args);
	}

	/**
	 * String operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:string operation.
	 */
	public static ResultSequence string(Collection args) throws DynamicError {

		assert args.size() == 1;

		ResultSequence arg1 = (ResultSequence) args.iterator().next();

		// sanity check args
		if (arg1.size() > 1)
			throw new DynamicError(TypeError.invalid_type(null));

		ResultSequence rs = ResultSequenceFactory.create_new();
		if (arg1.empty()) {
			rs.add(new XSString(""));
			return rs;
		}

		AnyType at = arg1.first();

		rs.add(new XSString(at.string_value()));

		return rs;
	}

}
