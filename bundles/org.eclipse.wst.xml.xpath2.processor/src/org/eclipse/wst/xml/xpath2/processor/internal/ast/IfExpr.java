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

import java.util.*;

/**
 * Support for IF expressions.
 */
public class IfExpr extends Expr {
	private Collection _exprs;
	private Expr _then;
	private Expr _else;

	/**
	 * Constructor for IfExpr.
	 * 
	 * @param exps
	 *            Condition expressions.
	 * @param t
	 *            If true expressions.
	 * @param e
	 *            If false/else expressions.
	 */
	public IfExpr(Collection exps, Expr t, Expr e) {
		_exprs = exps;
		_then = t;
		_else = e;
	}

	/**
	 * Support for Visitor interface.
	 * 
	 * @return Result of Visitor operation.
	 */
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}

	/**
	 * Support for Iterator interface.
	 * 
	 * @return Result of Iterator operation.
	 */
	public Iterator iterator() {
		return _exprs.iterator();
	}

	/**
	 * Support for Expression interface.
	 * 
	 * @return Result of Expr operation.
	 */
	public Expr then_clause() {
		return _then;
	}

	/**
	 * Support for Expression interface.
	 * 
	 * @return Result of Expr operation.
	 */
	public Expr else_clause() {
		return _else;
	}
}
