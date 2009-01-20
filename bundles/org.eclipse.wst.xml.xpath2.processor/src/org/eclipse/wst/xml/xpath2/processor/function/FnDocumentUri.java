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
 * Returns the value of the document-uri property for $arg as defined by the dm:document-uri
 * accessor function defined in Section 6.1.2 AccessorsDM.
 * If $arg is the empty sequence, the empty sequence is returned.
 * Returns the empty sequence if the node is not a document node or if its document-uri
 * property is a relative URI. Otherwise, returns an absolute URI expressed as an xs:string.
 */
public class FnDocumentUri extends Function {
	private static Collection _expected_args = null;
	/**
	 * Constructor for FnDocumentUri.
	 */
	public FnDocumentUri() {
		super(new QName("document-uri"), 1);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return document_uri(args);
	}
	/**
         * Document-Uri operation.
         * @param args Result from the expressions evaluation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:document-uri operation.
         */
	public static ResultSequence document_uri(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args,
                                                              expected_args());

		ResultSequence arg1 = (ResultSequence) cargs.iterator().next();

		ResultSequence rs = ResultSequenceFactory.create_new();
		if(arg1.empty())
			return rs;

		NodeType nt = (NodeType) arg1.first();

		if( !(nt instanceof DocType))
			return rs;

		DocType dt = (DocType) nt;	
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
