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
import org.w3c.dom.*;
/**
 * Returns the root of the tree to which $arg belongs.
 * This will usually, but not necessarily, be a document node.
 */
public class FnRoot extends Function {
	/**
	 * Constructor for FnRoot.
	 */	
	public FnRoot() {
		super(new QName("root"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		
		assert args.size() == arity();

		ResultSequence argument = (ResultSequence) args.iterator().next();

		return fn_root(argument, dynamic_context());
	}
	/**
         * Root operation.
         * @param arg Result from the expressions evaluation.
	 * @param dc Result of dynamic context operation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:root operation.
         */
	public static ResultSequence fn_root(ResultSequence arg, 
					     DynamicContext dc) throws DynamicError {

		ResultSequence rs = ResultSequenceFactory.create_new();

		// sanity check arg
		if(arg.size() == 0)
			return rs;

		if(arg.size() > 1)
			throw new DynamicError(TypeError.invalid_type(null));
		
		AnyType aa = arg.first();

		if(!(aa instanceof NodeType))
			throw new DynamicError(TypeError.invalid_type(null));
		
		NodeType nt = (NodeType) aa;

		// ok we got a sane argument... own it.

		Node root = nt.node_value();

		while(root != null) {
			Node newroot = root.getParentNode();

			// found it
			if(newroot == null)
				break;

			root = newroot;	
		}

		rs.add(NodeType.dom_to_xpath(root, dc.node_position(root)));

		return rs;
	}

}
