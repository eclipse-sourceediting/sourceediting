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
 * Returns the contiguous sequence of items in the value of $sourceSeq beginning
 * at the position indicated by the value of $startingLoc and continuing for the
 * number of items indicated by the value of $length. More specifically, returns
 * the items in $sourceString whose position $p obeys: - fn:round($startingLoc)
 * <= $p < fn:round($startingLoc) + fn:round($length)
 */
public class FnSubsequence extends Function {
	/**
	 * Constructor for FnSubsequence.
	 */
	public FnSubsequence() {
		super(new QName("subsequence"), 3);
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
		return subsequence(args);
	}

	/**
	 * Subsequence operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:subsequence operation.
	 */
	public static ResultSequence subsequence(Collection args)
			throws DynamicError {

		assert args.size() == 3;

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator citer = args.iterator();
		ResultSequence seq = (ResultSequence) citer.next();
		ResultSequence startLoc = (ResultSequence) citer.next();
		ResultSequence length = (ResultSequence) citer.next();

		// sanity chex
		if (startLoc.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = startLoc.first();
		if (!(at instanceof XSDouble))
			DynamicError.throw_type_error();

		int start = (int) ((XSDouble) at).double_value();

		if (length.size() != 1)
			DynamicError.throw_type_error();
		at = length.first();
		if (!(at instanceof XSDouble))
			DynamicError.throw_type_error();

		int len = (int) ((XSDouble) at).double_value();

		if (seq.empty())
			return rs;

		int pos = 1;

		if (start < 1)
			start = 1;
		int seqlen = seq.size();

		int end = start + len;

		for (Iterator i = seq.iterator(); i.hasNext();) {
			at = (AnyType) i.next();

			if (start <= pos && pos < end)
				rs.add(at);

			pos++;
		}
		return rs;
	}
}
