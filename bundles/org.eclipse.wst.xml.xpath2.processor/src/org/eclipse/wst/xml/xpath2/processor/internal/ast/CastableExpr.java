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

package org.eclipse.wst.xml.xpath2.processor.internal.ast;

/**
 * A class that tests whether a given value is castable into a given type. This
 * can be used to select an appropriate type for processing a given value.
 */
public class CastableExpr extends BinExpr {

	/**
	 * Constructor of CastableExpr
	 * 
	 * @param l
	 *            input xpath expression/variable.
	 * @param r
	 *            SingleType to check l against.
	 */
	public CastableExpr(Expr l, SingleType r) {
		super(l, r);
	}

	/**
	 * Support for Visitor interface.
	 * 
	 * @return Result of Visitor operation.
	 */
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}
}
