/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 273760 - wrong namespace for functions and data types 
 *     David Carver - bug 262765 - fix issue with casting items to XSDouble cast
 *                                 needed to cast to Numeric so that evaluations
 *                                 and formatting occur correctly.
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Returns the average of the values in the input sequence $arg, that is, the
 * sum of the values divided by the number of values.
 */
public class FnAvg extends Function {
	/**
	 * Constructor for FnAvg.
	 */
	public FnAvg() {
		super(new QName("avg"), 1);
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
		return avg(args);
	}

	/**
	 * Average value operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:avg operation.
	 */
	public static ResultSequence avg(Collection args) throws DynamicError {

		ResultSequence arg = get_arg(args);

		if (arg.empty())
			return ResultSequenceFactory.create_new();

		int elems = 0;

		MathPlus total = null;
		for (Iterator i = arg.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if (!(at instanceof MathPlus))
				DynamicError.throw_type_error();
			
			if (at.string_value().equals("NaN")) {
				ResultSequence res = ResultSequenceFactory.create_new();
				res.add(at);
				return res;
			}		

			if (total == null)
				total = (MathPlus) at;
			else {
				ResultSequence res = total.plus(ResultSequenceFactory
						.create_new(at));
				assert res.size() == 1;

				total = (MathPlus) res.first();
			}
			elems++;
		}

		if (!(total instanceof MathDiv))
			DynamicError.throw_type_error();

		MathDiv avg = (MathDiv) total;
		ResultSequence res = null;
		if (avg instanceof XSDecimal) {
			// Need to promote then divide
			XSDecimal dec = new XSDecimal(((XSDecimal) avg).string_value());
			res = dec.div(ResultSequenceFactory
					.create_new(new XSDecimal(BigDecimal.valueOf(elems))));
		} else if (avg instanceof XSDouble) {
			 XSDouble d = new XSDouble(((XSDouble) avg).string_value());
			 res = d.div(ResultSequenceFactory
					.create_new(new XSDouble(elems)));
		} else if (avg instanceof XSFloat) {
			XSFloat flt = new XSFloat(((XSFloat) avg).float_value());
			res = avg.div(ResultSequenceFactory
					.create_new(new XSFloat(elems)));
		} else if (avg instanceof XSDuration) {
			res = avg.div(ResultSequenceFactory
					.create_new(new XSDecimal(BigDecimal.valueOf(elems))));
		}

		return res;
	}

	/**
	 * Obtain input argument for operation.
	 * 
	 * @param args
	 *            input expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Resulting expression from the operation.
	 */
	public static ResultSequence get_arg(Collection args) throws DynamicError {
		assert args.size() == 1;

		ResultSequence arg = (ResultSequence) args.iterator().next();

		if (arg.empty())
			return arg;

		AnyType first = arg.first();

		Class durtype = null;
		if (first instanceof XSDayTimeDuration)
			durtype = XSDayTimeDuration.class;
		else if (first instanceof XSYearMonthDuration)
			durtype = XSYearMonthDuration.class;
		else
			durtype = null;

		// duration
		if (durtype != null) {
			for (Iterator i = arg.iterator(); i.hasNext();) {
				AnyType at = (AnyType) i.next();

				if (!durtype.isInstance(at))
					DynamicError.throw_type_error();
			}
			return arg;
		}

		ResultSequence rs = ResultSequenceFactory.create_new();
		for (Iterator i = arg.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			NumericType d = null;
			if (at instanceof XSUntypedAtomic) {
				d = new XSDecimal(((XSUntypedAtomic) at).string_value());
			} else if (at instanceof XSDouble) {
				d = (XSDouble) at; 
			} else if (at instanceof NumericType) {
				d = (NumericType) at;
			} else if (at instanceof NodeType) {
				d = new XSDecimal(at.string_value()); 
			} else
				DynamicError.throw_type_error();

			if (d == null)
				throw DynamicError.cant_cast(null);

			rs.add(d);
		}
		return rs;
	}
}
