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
 * The boolean operator 'instance of' takes the value of its first operand and
 * matches its type to the SequenceType in its second operand, according to the
 * rules for SequenceType matching.
 */
public class InstOfExpr extends BinExpr {
	/**
	 * Constructor for InstOfExpr.
	 * 
	 * @param l
	 *            input xpath expression/variable.
	 * @param r
	 *            SequenceType to check l against.
	 */
	public InstOfExpr(Expr l, SequenceType r) {
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
