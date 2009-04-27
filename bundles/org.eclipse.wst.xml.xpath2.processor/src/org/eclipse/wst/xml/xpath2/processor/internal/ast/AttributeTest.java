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
 * Class used to match an attribute node by its name and/or type.
 */
public class AttributeTest extends AttrElemTest {
	/**
	 * Constructor for AttributeTest. This one takes in 3 inputs, Name, wildcard
	 * test(true/false) and type.
	 * 
	 * @param name
	 *            QName.
	 * @param wild
	 *            Wildcard test, True/False.
	 * @param type
	 *            QName type.
	 */
	public AttributeTest(QName name, boolean wild, QName type) {
		super(name, wild, type);
	}

	/**
	 * Constructor for AttributeTest. This one takes in 2 inputs, Name and
	 * wildcard test(true/false).
	 * 
	 * @param name
	 *            QName.
	 * @param wild
	 *            Wildcard test, True/False.
	 */
	public AttributeTest(QName name, boolean wild) {
		super(name, wild);
	}

	/**
	 * Default Constructor for AttributeTest.
	 */
	public AttributeTest() {
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
}
