/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver - STAR - bug 262765 - Fixed arguments for Name function. 
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Mukul Gandhi - bug 301539 - fixed "context undefined" bug in case of zero
 *                                 arity.
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Returns the name of a node, as an xs:string that is either the zero-length
 * string, or has the lexical form of an xs:QName.
 */
public class FnName extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnName.
	 */
	public FnName() {
		super(new QName("name"), 0, 1);
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
		return name(args, dynamic_context());
	}

	/**
	 * Name operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param context
	 *            Dynamic context.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:name operation.
	 */
	public static ResultSequence name(Collection args, DynamicContext context) throws DynamicError {

		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get arg
		ResultSequence arg1 = null;
		
		if (cargs.isEmpty()) {
			if (context.context_item() == null)
				throw DynamicError.contextUndefined();
			else {
				arg1 = ResultSequenceFactory.create_new();
				arg1.add(context.context_item());
			}
		} else {
			arg1 = (ResultSequence) cargs.iterator().next();
		}
		
		if (arg1.empty()) {
		   rs.add(new XSString(""));
		   return rs;
		}

		NodeType an = (NodeType) arg1.first();
		
		QName name = an.node_name();

		String sname = "";
		if (name != null)
		  sname = name.string_value();

		rs.add(new XSString(sname));

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
