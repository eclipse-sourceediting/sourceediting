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
 * Class for Element testing.
 */
public class ElementTest extends AttrElemTest {
	private boolean _qmark = false;

	/**
	 * Constructor for ElementTest. This takes in 4 inputs, Name, wildcard
	 * test(true/false), type and question mark test(true/false).
	 * 
	 * @param name
	 *            Name of element to test.
	 * @param wild
	 *            Wildcard test? (true/false).
	 * @param type
	 *            Type of element to test.
	 * @param qmark
	 *            Nilled property (true/false).
	 */
	public ElementTest(QName name, boolean wild, QName type, boolean qmark) {
		super(name, wild, type);
		_qmark = qmark;
	}

	/**
	 * Constructor for ElementTest. This takes in 3 inputs, Name, wildcard
	 * test(true/false)and type.
	 * 
	 * @param name
	 *            Name of element to test.
	 * @param wild
	 *            Wildcard test? (true/false).
	 * @param type
	 *            Type of element to test.
	 */
	public ElementTest(QName name, boolean wild, QName type) {
		super(name, wild, type);
	}

	/**
	 * Constructor for ElementTest. This takes in 2 inputs, Name, wildcard
	 * test(true/false).
	 * 
	 * @param name
	 *            Name of element to test.
	 * @param wild
	 *            Wildcard test? (true/false).
	 */
	public ElementTest(QName name, boolean wild) {
		super(name, wild);
	}

	/**
	 * Default Constructor for ElementTest.
	 */
	public ElementTest() {
		super();
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
	 * Set nilled property.
	 * 
	 * @return Result of operation.
	 */
	public boolean qmark() {
		return _qmark;
	}
}
