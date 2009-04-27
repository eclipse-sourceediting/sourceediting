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

import org.eclipse.wst.xml.xpath2.processor.internal.types.XSInteger;

/**
 * The value of a numeric literal containing no "." anad no e or E character is
 * an atomic value of type xs:integer
 * 
 */
public class IntegerLiteral extends NumericLiteral {
	private XSInteger _value;

	/**
	 * Constructor for IntegerLiteral
	 * 
	 * @param value
	 *            integer value
	 */
	public IntegerLiteral(int value) {
		_value = new XSInteger(value);
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
	 * @return xs:integer value
	 */
	public XSInteger value() {
		return _value;
	}
}
