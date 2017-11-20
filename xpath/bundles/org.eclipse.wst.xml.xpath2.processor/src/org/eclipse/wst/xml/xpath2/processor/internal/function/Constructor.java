/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug274784 - improvements to xs:boolean data type implementation
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import java.util.Collection;

import org.eclipse.wst.xml.xpath2.api.EvaluationContext;
import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.internal.types.CtrType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;

/**
 * Constructor class for functions.
 */
public class Constructor extends Function {
	private CtrType _atomic_type;

	/**
	 * Constructor for Constructor class.
	 * 
	 * @param aat
	 *            input of any atomic type.
	 */
	public Constructor(CtrType aat) {
		super(new QName(aat.type_name()), 1);

		_atomic_type = aat;
	}

	// XXX IN GENRAL, I THIUNK WE NEED TO PULL SANITY CHECKING OUTSIDE!
	// PLUS I AM NOT ATOMIZING/ETC ETC HERE!!! BAD CODE
	// BUG XXX HACK DEATH
	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	public ResultSequence evaluate(Collection args, EvaluationContext ec) throws DynamicError {
		assert args.size() >= min_arity() && args.size() <= max_arity();

		// sanity checks
		ResultSequence arg = (ResultSequence) args.iterator().next();

		if (arg.size() > 1)
			DynamicError.throw_type_error();

		// do it
		return _atomic_type.constructor(arg);
	}

}
