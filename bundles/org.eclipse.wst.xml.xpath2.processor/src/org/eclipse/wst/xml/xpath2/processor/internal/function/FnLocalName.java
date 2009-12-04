/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver - STAR - bug 262765 - fixed implementation of fn:local-name according to spec.  
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
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
 * Returns the local part of the name of $arg as an xs:string that will either
 * be the zero-length string or will have the lexical form of an xs:NCName.
 */
public class FnLocalName extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnLocalName.
	 */
	public FnLocalName() {
		super(new QName("local-name"), 0, 1);
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
		return local_name(args, dynamic_context());
	}

	/**
	 * Local-Name operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:local-name operation.
	 */
	public static ResultSequence local_name(Collection args, DynamicContext context)
			throws DynamicError {

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
			sname = name.local();

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
