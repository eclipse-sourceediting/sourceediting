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
 * Class for binary operation And.
 * The value of an and-expression is determined by the effective boolean values 
 * (EBV's) of its operands.
 */
public class AndExpr extends BinExpr {

	/**
	 * Constructor for AndExpr.
	 * @param l input1 xpath expression.
	 * @param r input2 xpath expression.
	 */
	public AndExpr(Expr l, Expr r) {
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
