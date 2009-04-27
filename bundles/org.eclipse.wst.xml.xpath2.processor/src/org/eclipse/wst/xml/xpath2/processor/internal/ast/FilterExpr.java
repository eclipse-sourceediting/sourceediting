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

import java.util.*;

import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

/**
 * A filter expression consists simply of a primary expression followed by zero
 * or more predicates. The result of the filter expression consists of all the
 * items returned by the primary expression for which all the predicates are
 * true. If no predicates are specified, the result is simply the result of the
 * primary expression. This result may contain nodes, atomic values, or any
 * combination of these. The ordering of the items returned by a filter
 * expression is the same as their order in the result of the primary
 * expression. Context positions are assigned to items based on their ordinal
 * position in the result sequence. The first context position is 1.
 */
public class FilterExpr extends StepExpr {
	private PrimaryExpr _pexpr;
	private Collection _exprs;
	private XSInteger _g_xsint;

	/**
	 * Constructor of FilterExpr.
	 * 
	 * @param pexpr
	 *            is copied to _pexpr.
	 * @param exprs
	 *            is copied to _exprs. g_xsint is created as a new XSInteger as
	 *            a result.
	 */
	public FilterExpr(PrimaryExpr pexpr, Collection exprs) {
		_pexpr = pexpr;
		_exprs = exprs;
		_g_xsint = new XSInteger();
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

	/**
	 * Get the primary expression.
	 * 
	 * @return The primary expression.
	 */
	public PrimaryExpr primary() {
		return _pexpr;
	}

	/**
	 * Get the next predicate.
	 * 
	 * @return The next predicate.
	 */
	public Iterator iterator() {
		return _exprs.iterator();
	}

	/**
	 * Set a new primary expression.
	 * 
	 * @param e
	 *            is set as the new primary expression.
	 */
	public void set_primary(PrimaryExpr e) {
		_pexpr = e;
	}

	/**
	 * Count the number of predicates.
	 * 
	 * @return The size of the collection of predicates.
	 */
	public int predicate_count() {
		return _exprs.size();
	}
}
