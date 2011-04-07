/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver - STAR - bug 262765 - clean up fn:root according to spec. 
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Jesper Steen Moeller - bug 281159 - tak extra care to find the root 
 *     Jesper Steen Moller - bug 275610 - Avoid big time and memory overhead for externals
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.SeqType;
import org.eclipse.wst.xml.xpath2.processor.internal.TypeError;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Returns the root of the tree to which $arg belongs. This will usually, but
 * not necessarily, be a document node.
 */
public class FnRoot extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnRoot.
	 */
	public FnRoot() {
		super(new QName("root"), 0, 1);
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

		//ResultSequence argument = (ResultSequence) args.iterator().next();

		return fn_root(args, dynamic_context());
	}

	/**
	 * Root operation.
	 * 
	 * @param arg
	 *            Result from the expressions evaluation.
	 * @param dc
	 *            Result of dynamic context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:root operation.
	 */
	public static ResultSequence fn_root(Collection args, DynamicContext dc)
			throws DynamicError {

		Collection cargs = Function.convert_arguments(args, expected_args());
		
		ResultSequence rs = ResultSequenceFactory.create_new();

		// sanity check arg
//		if (cargs.isEmpty())
//			return rs;

		if (cargs.size() > 1)
			throw new DynamicError(TypeError.invalid_type(null));
		
		ResultSequence arg = null;
		if (cargs.isEmpty()) {
			arg = ResultSequenceFactory.create_new();
			if (dc.context_item() == null) {
				throw DynamicError.contextUndefined();
			}
			arg.add(dc.context_item());
		} else {
			arg = (ResultSequence) cargs.iterator().next();			
		}
		
		if (arg.empty()) {
			return rs;
		}
		
		AnyType aa = arg.first();

		if (!(aa instanceof NodeType))
			throw new DynamicError(TypeError.invalid_type(null));

		NodeType nt = (NodeType) aa;

		// ok we got a sane argument... own it.

		Node root = nt.node_value();

		while (root != null && ! (root instanceof Document)) {
			Node newroot = root.getParentNode();
			if (newroot == null && root instanceof Attr) {
				newroot = ((Attr)root).getOwnerElement();
			}
				
			// found it
			if (newroot == null)
				break;

			root = newroot;
		}

		rs.add(NodeType.dom_to_xpath(root, dc.getTypeModel(root)));

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
			SeqType arg = new SeqType(SeqType.OCC_QMARK);
			_expected_args.add(arg);
		}

		return _expected_args;
	}
	

}
