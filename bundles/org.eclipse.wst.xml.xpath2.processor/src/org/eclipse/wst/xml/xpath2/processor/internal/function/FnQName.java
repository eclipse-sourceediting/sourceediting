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

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Returns an xs:QName with the namespace URI given in $paramURI. If $paramURI
 * is the empty string or the empty sequence, it represents "no namespace". The
 * prefix (or absence of a prefix) in $paramQName is retained in the returned
 * xs:QName value. The local name in the result is taken from the local part of
 * $paramQName.
 */
public class FnQName extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnQName.
	 */
	public FnQName() {
		super(new QName("QName"), 2);
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
		return resolve_QName(args, static_context());
	}

	/**
	 * Resolve the QName of the given arguments.
	 * 
	 * @param args
	 *            Result from teh expressions evaluation.
	 * @param sc
	 *            Result of static context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of the fn:QName operation.
	 */
	public static ResultSequence resolve_QName(Collection args, StaticContext sc)
			throws DynamicError {

		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();

		String ns = null;
		if (!arg1.empty())
			ns = ((XSString) arg1.first()).value();
		ResultSequence arg2 = (ResultSequence) argiter.next();
		String name = ((XSString) arg2.first()).value();

		QName qn = QName.parse_QName(name);
		if (qn == null)
			throw DynamicError.lexical_error(null);
		qn.set_namespace(ns);

		rs.add(qn);

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
			SeqType arg = new SeqType(new XSString(), SeqType.OCC_QMARK);
			_expected_args.add(arg);
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
