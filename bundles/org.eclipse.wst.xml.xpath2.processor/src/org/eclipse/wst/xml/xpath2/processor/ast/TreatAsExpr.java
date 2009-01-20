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
 * Support for Treat operation. This does not change the value of the operand,
 * rather it ensues the operand has a correct type at evaluation time.
 */
public class TreatAsExpr extends BinExpr {
	/**
	 * Constructor for TreatAsExpr.
	 * 
	 * @param l
	 *            xpath expression/variable.
	 * @param r
	 *            SequenceType to treat as.
	 */
	public TreatAsExpr(Expr l, SequenceType r) {
		super(l, r);
	}

	/**
	 * Support for Visitor interface.
	 * 
	 * @return Result of Visitor operation.
	 */
	@Override
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}
}
