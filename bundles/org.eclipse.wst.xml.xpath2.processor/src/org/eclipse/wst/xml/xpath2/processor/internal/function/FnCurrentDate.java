/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper S Moller - bug 286452 - always return the stable date/time from dynamic context
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Returns xs:date(fn:current-dateTime()). This is a xs:date (with timezone)
 * that is current at some time during the evaluation of a query or
 * transformation in which fn:current-date() is executed. This function is
 * stable. The precise i instant during the query or transformation represented
 * by the value of fn:current-date() is implementation dependent.
 */
public class FnCurrentDate extends Function {
	/**
	 * Constructor for FnCurrentDate.
	 */
	public FnCurrentDate() {
		super(new QName("current-date"), 0);
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
		return current_date(args, dynamic_context());
	}

	/**
	 * Current-Date operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param dc
	 *            Result of dynamic context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:current-date operation.
	 */
	public static ResultSequence current_date(Collection args, DynamicContext dc)
			throws DynamicError {
		assert args.size() == 0;

		AnyType res = new XSDate(dc.current_date_time(), dc.tz());

		return ResultSequenceFactory.create_new(res);
	}
}
