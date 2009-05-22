/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 274471 - improvements to fn:string function (support for arity 0) 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Returns the value of $arg represented as a xs:string. If no argument is
 * supplied, this function returns the string value of the context item (.).
 */
public class FnString extends Function {
	/**
	 * Constructor for FnString.
	 */
	public FnString() {
	  super(new QName("string"), -1);
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
		return string(args, dynamic_context());
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
	public static ResultSequence string(Collection args, DynamicContext d_context) throws DynamicError {

		assert (args.size() == 0 || args.size() == 1);

		ResultSequence arg1 = null;
				
		if (args.isEmpty()) {
			// support for arity = 0
			return getResultSetForArityZero(d_context);
		}
		else {
			arg1 = (ResultSequence) args.iterator().next();	
		}

		// sanity check args
		if (arg1.size() > 1)
			throw new DynamicError(TypeError.invalid_type(null));

		if (arg1.empty()) {
			// support for arity = 0
			return getResultSetForArityZero(d_context);
		}

		AnyType at = arg1.first();

		ResultSequence rs = ResultSequenceFactory.create_new();
		rs.add(new XSString(at.string_value()));

		return rs;
	}
	
	/*
	 * Helper function for arity 0
	 */
	private static ResultSequence getResultSetForArityZero(DynamicContext d_context) {
		ResultSequence rs = ResultSequenceFactory.create_new();
		
		AnyType contextItem = d_context.context_item();
		if (contextItem != null) {
		  // if context item is defined, then that is the default argument
		  // to fn:string function
		  rs.add(new XSString(contextItem.string_value()));
		}
		else {
		  rs.add(new XSString(""));
		}
		return rs;
	}

}
