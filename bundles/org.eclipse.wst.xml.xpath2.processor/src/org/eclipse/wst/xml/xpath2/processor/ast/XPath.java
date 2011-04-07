/*******************************************************************************
 * Copyright (c) 2005, 2011 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *     Jesper Steen Moller  - bug 340933 - Migrate to new XPath2 API
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.ast;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.wst.xml.xpath2.api.DynamicContext;
import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.StaticContext;
import org.eclipse.wst.xml.xpath2.api.XPath2Expression;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator2;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.XPathNode;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.XPathVisitor;

/**
 * Support for XPath.
 * 
 * @deprecated This is only for internal use, use XPath2Expression instead
 */
public class XPath extends XPathNode implements XPath2Expression {
	private Collection _exprs;
	private StaticContext _staticContext;

	/**
	 * Constructor for XPath.
	 * 
	 * @param exprs
	 *            XPath expressions.
	 */
	public XPath(Collection exprs) {
		_exprs = exprs;
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
	 * @since 2.0
	 */
	public Collection getFreeVariables() {
		// TODO: fetch free variables
		return Collections.emptyList();
	}

	/**
	 * @since 2.0
	 */
	public Collection getResolvedFunctions() {
		// TODO: fetch the functions in use
		return Collections.emptyList();
	}

	/**
	 * @since 2.0
	 */
	public Collection getAxes() {
		// TODO: fetch the axis in uses
		return Collections.emptyList();
	}

	/**
	 * @since 2.0
	 */
	public ResultSequence evaluate(DynamicContext dynamicContext, Object[] contextItems) {
		if (_staticContext == null) throw new IllegalStateException("Static Context not set yet!");
		return new DefaultEvaluator2(_staticContext, dynamicContext, contextItems).evaluate2(this);
	}
	
	/**
	 * @since 2.0
	 */
	public StaticContext getStaticContext() {
		return _staticContext;
	}

	/**
	 * @since 2.0
	 */
	public void setStaticContext(StaticContext context) {
		if (_staticContext != null) throw new IllegalStateException("Static Context already set!");
		this._staticContext = context;
	}
}
