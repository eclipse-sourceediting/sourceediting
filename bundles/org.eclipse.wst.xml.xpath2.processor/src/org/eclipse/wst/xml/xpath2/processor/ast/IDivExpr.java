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

package org.eclipse.wst.xml.xpath2.processor.ast;

/**
 * Support for Integer division.
 */
public class IDivExpr extends BinExpr {
	/**
	 * Constructor for IDivExpr.
	 * @param l left value.
	 * @param r right value.
	 */
	public IDivExpr(Expr l, Expr r) {
		super(l,r);
	}
	/**
	 * Support for Visitor interface.
	 * @return Result of Visitor operation.
	 */
	@Override
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}
}
