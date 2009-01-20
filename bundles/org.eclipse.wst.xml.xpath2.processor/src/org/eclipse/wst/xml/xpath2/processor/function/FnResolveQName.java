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
 * Returns an xs:QName value (that is, an expanded-QName) by taking an xs:string that has
 * the lexical form of an xs:QName (a string in the form "prefix:local-name" or
 * "local-name") and resolving it using the in-scope namespaces for a given element.
 */
public class FnResolveQName extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnResolveQName.
	 */
	public FnResolveQName() {
		super(new QName("resolve-QName"), 2);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return resolve_QName(args, static_context());
	}
	/**
         * Resolve-QName operation.
         * @param args Result from the expressions evaluation.
	 * @param sc Result of static context operation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:resolve-QName operation.
         */
	public static ResultSequence resolve_QName(Collection args,
						   StaticContext sc) throws DynamicError {

		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();

		if(arg1.empty())
			return rs;

		String name = ((XSString)arg1.first()).value();	
		ResultSequence arg2 = (ResultSequence) argiter.next();
		ElementType elem = (ElementType) arg2.first();

		// XXX fix this to use real namespaces
		QName qn = QName.parse_QName(name);

		if(qn == null)
			throw DynamicError.lexical_error(null);

		rs.add(qn);	

		return rs;
	}
	/**
         * Obtain a list of expected arguments.
         * @return Result of operation.
         */
	public static Collection expected_args() {
		if(_expected_args == null) {
			_expected_args = new ArrayList();
			SeqType arg = new SeqType(new XSString(), 
						  SeqType.OCC_QMARK);
			_expected_args.add(arg);
			_expected_args.add(new SeqType(new ElementType(),
						       SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
