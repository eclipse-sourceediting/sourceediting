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
 * The value of a string literal is an atomic value whose type
 * is xs:string and whose value is the string denoted by the characters
 * between the delimiting apostrophes or quotation marks. If the literal
 * is delimited by apostrophes, two adjacent apostrophes within the literal
 * are interpreted as a single apostrophe. Similarly, if the literal is delimited
 * by quotation marks, two adjacent quotation marks within the literal are 
 * interpreted as one quotation mark
 *
 */
public class StringLiteral extends Literal {
	private XSString _value;

	/** 
	 * Constructor for StringLiteral
	 *
	 * @param value string value
	 */
	public StringLiteral(String value) {
		_value = new XSString(value);
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
	 * @return string value
	 */
	public String string() { return _value.value(); }
	
	/**
	 * @return xs:string value
	 */
	public XSString value() { return _value; }
}
