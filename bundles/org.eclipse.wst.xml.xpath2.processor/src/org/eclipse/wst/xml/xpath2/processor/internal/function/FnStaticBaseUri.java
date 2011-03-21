/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Returns the value of the base-uri property from the static context. If the
 * base-uri property is undefined, the empty sequence is returned. Components of
 * the static context are discussed in Section C.1 Static Context Components in
 * the specification.
 */
public class FnStaticBaseUri extends Function {
	/**
	 * Constructor for FnStaticBaseUri.
	 */
	public FnStaticBaseUri() {
		super(new QName("static-base-uri"), 0);
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
		return static_base_uri(args, static_context());
	}

	/**
	 * Static-base-Uri operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param sc
	 *            Result of static context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:static-base-uri operation.
	 */
	public static ResultSequence static_base_uri(Collection args,
			StaticContext sc) throws DynamicError {
		assert args.size() == 0;
		assert sc != null;

		// make a copy prolly
		return ResultSequenceFactory.create_new(sc.base_uri());
	}
}
