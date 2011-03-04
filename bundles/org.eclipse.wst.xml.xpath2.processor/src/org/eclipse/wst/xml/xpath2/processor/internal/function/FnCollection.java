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
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;
import org.w3c.dom.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Summary: This function takes an xs:string as argument and returns a sequence
 * of nodes obtained by interpreting $arg as an xs:anyURI and resolving it
 * according to the mapping specified in Available collections described in
 * Section C.2 Dynamic Context Components. If Available collections provides a
 * mapping from this string to a sequence of nodes, the function returns that
 * sequence. If Available collections maps the string to an empty sequence, 
 * then the function returns an empty sequence. If Available collections
 * provides no mapping for the string, an error is raised [err:FODC0004]. If
 * $arg is not specified, the function returns the sequence of the nodes in the
 * default collection in the dynamic context. See Section C.2 Dynamic Context
 * ComponentsXP. If the value of the default collection is undefined an error
 * is raised [err:FODC0002].
 *
 * If the $arg is a relative xs:anyURI, it is resolved against the value of the
 * base-URI property from the static context. If $arg is not a valid xs:anyURI,
 * an error is raised [err:FODC0004].
 *
 * If $arg is the empty sequence, the function behaves as if it had been called
 * without an argument. See above.
 *
 * By default, this function is ·stable·. This means that repeated calls on the
 * function with the same argument will return the same result. However, for
 * performance reasons, implementations may provide a user option to evaluate
 * the function without a guarantee of stability. The manner in which any such
 * option is provided is ·implementation-defined·. If the user has not selected
 * such an option, a call to this function must either return a stable result or
 * must raise an error: [err:FODC0003].
 */
public class FnCollection extends Function {
	private static Collection _expected_args = null;
	
	public static final String DEFAULT_COLLECTION_URI = "http://www.w3.org/2005/xpath-functions/collection/default";

	/**
	 * Constructor for FnDoc.
	 */
	public FnCollection() {
		super(new QName("collection"), 0, 1);
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
		return collection(args, dynamic_context());
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
	public static ResultSequence collection(Collection args, DynamicContext dc)
			throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = null;
		ResultSequence rs = ResultSequenceFactory.create_new();
		if (argiter.hasNext()) {
			arg1 = (ResultSequence) argiter.next();
		} else {
			return getCollection(DEFAULT_COLLECTION_URI, dc);
		}
			

		if (arg1.empty())
			return ResultSequenceFactory.create_new();

		String uri = ((XSString) arg1.first()).value();
		
		
		try {
			new URI(uri);
		} catch (URISyntaxException ex) {
			throw DynamicError.doc_not_found(null);
		}
		
		if (uri.indexOf(":") < 0) {
			throw DynamicError.invalidCollectionArgument();
		}
		

		URI resolved = dc.resolve_uri(uri);
		if (resolved == null)
			throw DynamicError.invalid_doc(null);

		rs = getCollection(uri, dc);
		if (rs.empty())
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
	
	private static ResultSequence getCollection(String uri, DynamicContext dc) {
		ResultSequence rs = ResultSequenceFactory.create_new();
		Map<String, List<Document>> collectionMap = dc.get_collections();
		List<Document> docList = collectionMap.get(uri);
		for (int i = 0; i < docList.size(); i++) {
			Document doc = docList.get(i);
			rs.add(new DocType(doc, dc.getTypeModel(doc)));
		}
		return rs;
		
	}
}
