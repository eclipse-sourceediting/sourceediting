/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Support for Except operation on node types.
 */
public class OpExcept extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for OpExcept.
	 */
	public OpExcept() {
		super(new QName("except"), 2);
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
		assert args.size() >= min_arity() && args.size() <= max_arity();

		return op_except(args);
	}

	/**
	 * Op-Except operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of operation.
	 */
	public static ResultSequence op_except(Collection args) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		// convert arguments
		Collection cargs = Function.convert_arguments(args, expected_args());

		// get arguments
		Iterator iter = cargs.iterator();
		ResultSequence one = (ResultSequence) iter.next();
		ResultSequence two = (ResultSequence) iter.next();

		// XXX lame
		for (Iterator i = one.iterator(); i.hasNext();) {
			NodeType node = (NodeType) i.next();
			boolean found = false;

			// death
			for (Iterator j = two.iterator(); j.hasNext();) {
				NodeType node2 = (NodeType) j.next();

				if (node.node_value() == node2.node_value()) {
					found = true;
					break;
				}

			}
			if (!found)
				rs.add(node);
		}
		rs = NodeType.eliminate_dups(rs);
		rs = NodeType.sort_document_order(rs);

		return rs;
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public synchronized static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();

			SeqType st = new SeqType(SeqType.OCC_STAR);

			_expected_args.add(st);
			_expected_args.add(st);
		}
		return _expected_args;
	}
}
