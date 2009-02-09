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
 * <p>
 * Sequence reverse function.
 * </p>
 * 
 * <p>
 * Usage: fn:reverse($arg as item()*) as item()*
 * </p>
 * 
 * <p>
 * This class reverses the order of items in a sequence. If $arg is the empty
 * sequence, the empty sequence is returned.
 * </p>
 */
public class FnReverse extends Function {

	/**
	 * Constructor for FnReverse.
	 */
	public FnReverse() {
		super(new QName("reverse"), 1);
	}

	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            are evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the reversal of the arguments.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return reverse(args);
	}

	/**
	 * Reverse the arguments.
	 * 
	 * @param args
	 *            are reversed.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of the reversal of the arguments.
	 */
	public static ResultSequence reverse(Collection args) throws DynamicError {

		assert args.size() == 1;

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence arg = (ResultSequence) citer.next();

		if (arg.empty())
			return rs;

		// XXX lame
		ListIterator i = arg.iterator();

		while (i.hasNext())
			i.next();

		while (i.hasPrevious())
			rs.add((AnyType) i.previous());

		return rs;
	}
}
