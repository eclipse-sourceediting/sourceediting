/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 274471 - improvements to fn:string function (support for arity 0) 
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Jesper Steen Moller  - bug 281938 - handle context and empty sequences correctly
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
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
	  super(new QName("string"), 0, 1);
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

		ResultSequence rs = ResultSequenceFactory.create_new();
		if (arg1.empty()) {
			rs.add(new XSString(""));
		} else {
			AnyType at = arg1.first();
			rs.add(new XSString(at.string_value()));
		}
		
		return rs;
	}

}
