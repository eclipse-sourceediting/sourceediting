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

import org.eclipse.wst.xml.xpath2.processor.types.*;

/**
 *The value of a numeric literal containing "." but no e or E character is an
 * atomic value of type xs:decimal
 * 
 */
public class DecimalLiteral extends NumericLiteral {
	private XSDecimal _value;

	/**
	 * Constructor for DecimalLiteral
	 * 
	 * @param value
	 *            double value
	 */
	public DecimalLiteral(double value) {
		_value = new XSDecimal(value);
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
	 * @return xs:decimal value
	 */
	public XSDecimal value() {
		return _value;
	}
}
