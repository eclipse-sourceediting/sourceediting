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
 * Returns the local part of the name of $arg as an xs:string that will either be
 * the zero-length string or will have the lexical form of an xs:NCName.
 */
public class FnLocalName extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnLocalName.
	 */
	public FnLocalName() {
		super(new QName("local-name"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return local_name(args);
	}
	/**
         * Local-Name operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:local-name operation.
         */
	public static ResultSequence local_name(Collection args) throws DynamicError {

		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get arg
		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();

		if(arg1.empty()) {
			rs.add(new XSString(""));
			return rs;
		}	
		
		NodeType an = (NodeType) arg1.first();
		
		QName name = an.node_name();

		String sname = "";
		if(name != null)
			sname = name.local();
		
		rs.add(new XSString(sname));

		return rs;
	}
	/**
         * Obtain a list of expected arguments.
         * @return Result of operation.
         */
	public static Collection expected_args() {
		if(_expected_args == null) {
			_expected_args = new ArrayList();
			SeqType arg = new SeqType(SeqType.OCC_QMARK);
			_expected_args.add(arg);
		}

		return _expected_args;
	}
}
