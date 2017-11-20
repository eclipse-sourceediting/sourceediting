/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.ast;

import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

/**
 * Class for Variable Expression Pairs.
 */
public class VarExprPair {
	private QName _var;
	private Expr _expr;

	/**
	 * Constructor for VarExprPair.
	 * 
	 * @param var
	 *            QName variable.
	 * @param expr
	 *            Expression.
	 */
	public VarExprPair(QName var, Expr expr) {
		_var = var;
		_expr = expr;
	}

	/**
	 * Support for QName interface.
	 * 
	 * @return Result of QName operation.
	 */
	public QName varname() {
		return _var;
	}

	/**
	 * Support for Expression interface.
	 * 
	 * @return Result of Expr operation.
	 */
	public Expr expr() {
		return _expr;
	}
}
