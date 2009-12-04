/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Steen Moller - bug 281159 - fix document loading and resolving URIs 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.net.URI;
import java.util.*;

/**
 * Retrieves a document using an xs:anyURI supplied as an xs:string. If $uri is
 * not a valid xs:anyURI, an error is raised [err:FODC0005]. If it is a relative
 * URI Reference, it is resolved relative to the value of the base URI property
 * from the static context. The resulting absolute URI Reference is cast to an
 * xs:string. If the Available documents discussed in Section 2.1.2 Dynamic
 * ContextXP provides a mapping from this string to a document node, the
 * function returns that document node. If the Available documents maps the
 * string to an empty sequence, then the function returns an empty sequence. If
 * the Available documents provides no mapping for the string, an error is
 * raised [err:FODC0005].
 */
public class FnDoc extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnDoc.
	 */
	public FnDoc() {
		super(new QName("doc"), 1);
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
		return doc(args, dynamic_context());
	}

	/**
	 * Doc operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param dc
	 *            Result of dynamic context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:doc operation.
	 */
	public static ResultSequence doc(Collection args, DynamicContext dc)
			throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();

		if (arg1.empty())
			return ResultSequenceFactory.create_new();

		String uri = ((XSString) arg1.first()).value();

		URI resolved = dc.resolve_uri(uri);
		if (resolved == null)
			throw DynamicError.invalid_doc(null);

		ResultSequence rs = dc.get_doc(resolved);
		if (rs == null)
			throw DynamicError.doc_not_found(null);

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
		}

		return _expected_args;
	}
}
