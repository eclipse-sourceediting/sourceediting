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

import java.util.*;
/**
 * The except operator takes two node sequences as operands and returns a sequence 
 * containing all the nodes that occur in the first operand but not in the second operand.
 */
public class ExceptExpr extends BinExpr {

	/**
	 * Constructor for ExceptExpr.
	 * @param l input1 xpath expression/variable.
	 * @param r input2 xpath expression/variable.
	 */
	public ExceptExpr(Expr l, Expr r) {
		super(l,r);
	}

	/**
	 * Support for Visitor interface.
	 * @return Result of Visitor operation.
	 */
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}
}
