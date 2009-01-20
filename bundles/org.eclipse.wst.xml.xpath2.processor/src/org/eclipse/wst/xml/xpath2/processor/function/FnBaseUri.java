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
 * Returns the value of the base-uri property for $arg as defined by the accessor
 * function dm:base-uri() for that kind of node in Section 5.1 base-uri Accessor of 
 * the specification.
 * If $arg is the empty sequence, the empty sequence is returned.
 * Document, element and processing-instruction nodes have a base-uri property which
 * may be empty. The base-uri property of all other node types is the empty sequence.
 * The value of the base-uri property is returned if it exists and is not empty.
 * Otherwise, if the node has a parent, the value of dm:base-uri() applied to its
 * parent is returned, recursively. If the node does not have a parent, or if the
 * recursive ascent up the ancestor chain encounters a node whose base-uri property
 * is empty and it does not have a parent, the empty sequence is returned.
 */
public class FnBaseUri extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnBaseUri.
	 */
	public FnBaseUri() {
		super(new QName("base-uri"), 1);
	}
        /**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return base_uri(args);
	}
	/**
         * Base-Uri operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:base-uri operation.
         */
	public static ResultSequence base_uri(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();

		ResultSequence rs = ResultSequenceFactory.create_new();
		if(arg1.empty())
			return rs;

		NodeType nt = (NodeType) arg1.first();

		// XXX need to implement
		assert false;

		return rs;
	}
	/**
	 * Obtain a list of expected arguments.
	 * @return Result of operation.
	 */
	public static Collection expected_args() {
		if(_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
