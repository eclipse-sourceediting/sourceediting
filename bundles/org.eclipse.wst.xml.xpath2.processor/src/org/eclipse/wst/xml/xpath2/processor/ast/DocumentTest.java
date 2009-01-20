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
/**
 * Class for Document testing.
 */
public class DocumentTest extends KindTest {
	/**
	 * Set internal value for NONE.
	 */
	public static final int NONE = 0;
	/**
	 * Set internal value for ELEMENT.
	 */
	public static final int ELEMENT = 1;
	/**
	 * Set internal value for SCHEMA_ELEMENT.
	 */
	public static final int SCHEMA_ELEMENT = 2;
	
	// XXX: polymorphism
	private int _type;

	private ElementTest _etest;
	private SchemaElemTest _schema_etest;
	/**
	 * Constructor for DocumentTest.
	 * @param type Type of element to test.
	 * @param arg xpath object to test.
	 */
	public DocumentTest(int type, Object arg) {
		_etest = null;
		_schema_etest = null;

		_type = type;
		switch(_type) {
			case ELEMENT:
				_etest = (ElementTest)arg;
				break;
			case SCHEMA_ELEMENT:
				_schema_etest = (SchemaElemTest)arg;
				break;
		}
	}
	/**
	 * Default Constructor for DocumentTest.
	 */
	public DocumentTest() {
		this(NONE,null);
	}
	/**
	 * Support for Visitor interface.
	 * @return Result of Visitor operation.
	 */
	public Object accept(XPathVisitor v) {
		return v.visit(this);
	}
	/**
	 * Get test type.
	 * @return Type of test.
	 */
	public int type() { return _type; }
	/**
	 * Element test.
	 * @return Element test.
	 */
	public ElementTest elem_test() { return _etest; }
	/**
	 * Schema element test.
	 * @return Schema element test.
	 */
	public SchemaElemTest schema_elem_test() { return _schema_etest; }

}
