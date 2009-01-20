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
 * The fn:error function causes the evaluation of the outermost XQuery or transformation
 * to stop. While this function never returns a value, an error, if it occurs, is
 * returned to the external processing environment as an xs:anyURI or an xs:QName.
 * The error xs:anyURI is derived from the error xs:QName. An error xs:QName with
 * namespace URI NS and local part LP will be returned as the xs:anyURI NS#LP. The
 * method by which the xs:anyURI or xs:QName is returned to the external processing
 * environment is implementation dependent.
 */
public class FnError extends Function {

	// XXX overloaded...
	/**
	 * Constructor for FnError.
	 */
	public FnError() {
		super(new QName("error"), 0);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return error(args);
	}
	/**
         * Error operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:error operation.
         */
	public static ResultSequence error(Collection args) throws DynamicError {
		
		throw DynamicError.user_error(null);
	}
}
