/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     David Carver - bug 298535 - Attribute instance of improvements 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.ast;

import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.PIType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;

/**
 * Class for Processing Instruction support.
 */
public class PITest extends KindTest {
	private String _arg;

	/**
	 * Constructor for PITest.
	 * 
	 * @param arg
	 *            instruction argument.
	 */
	public PITest(String arg) {
		_arg = arg;
	}

	/**
	 * Default Constructor for PITest.
	 */
	public PITest() {
		this(null);
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
	 * Support for String arguments.
	 * 
	 * @return Result of String operation.
	 */
	public String arg() {
		return _arg;
	}

	@Override
	public AnyType createTestType(ResultSequence rs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QName name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWild() {
		return false;
	}

	@Override
	public Class getXDMClassType() {
		return PIType.class;
	}

}
