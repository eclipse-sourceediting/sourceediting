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
 * This function tests whether the language of $node, or the context node if the
 * second argument is omitted, as specified by xml:lang attributes is the same
 * as, or is a sublanguage of, the language specified by $testlang. The language
 * of the argument node, or the context node if the second argument is omitted,
 * is determined by the value of the xml:lang attribute on the node, or, if the
 * node has no such attribute, by the value of the xml:lang attribute on the
 * nearest ancestor of the node that has an xml:lang attribute. If there is no
 * such ancestor, then the function returns false.
 */
public class FnLang extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnLang.
	 */
	public FnLang() {
		super(new QName("lang"), 2);
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
		return lang(args);
	}

	/**
	 * Language operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:lang operation.
	 */
	public static ResultSequence lang(Collection args) throws DynamicError {

		Collection cargs = Function.convert_arguments(args, expected_args());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get arg
		Iterator citer = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) citer.next();
		ResultSequence arg2 = (ResultSequence) citer.next();

		String lang = "";

		if (!arg1.empty()) {
			lang = ((XSString) arg1.first()).value();
		}

		NodeType an = (NodeType) arg2.first();

		rs.add(new XSBoolean(test_lang(an.node_value(), lang)));

		return rs;
	}

	/**
	 * Language test operation.
	 * 
	 * @param node
	 *            Node to test.
	 * @param lang
	 *            Language to test for.
	 * @return Boolean result of operation.
	 */
	private static boolean test_lang(Node node, String lang) {
		NamedNodeMap attrs = node.getAttributes();

		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				Attr attr = (Attr) attrs.item(i);

				if (!"xml:lang".equals(attr.getName()))
					continue;

				if (lang.equals(attr.getValue()))
					return true;
			}
		}

		Node parent = node.getParentNode();
		if (parent == null)
			return false;

		return test_lang(parent, lang);
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();

			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_QMARK));
			_expected_args.add(new SeqType(SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
