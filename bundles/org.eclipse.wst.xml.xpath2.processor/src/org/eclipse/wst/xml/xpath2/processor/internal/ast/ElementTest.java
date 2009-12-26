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

import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DefaultDynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for Element testing.
 */
public class ElementTest extends AttrElemTest {
	private boolean _qmark = false;

	private AnyType anyType = null;

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

	@Override
	public AnyType createTestType(ResultSequence rs) {

		if (name() == null && !wild()) {
			return new ElementType();
		}

		AnyType at = rs.first();

		if (!(at instanceof NodeType)) {
			return new ElementType();
		}

		return createElementType(at);
	}

	private AnyType createElementType(AnyType at) {
		anyType = new ElementType();
		NodeType nodeType = (NodeType) at;
		Node node = nodeType.node_value();
		Document doc = null;
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			doc = (Document) node;
		} else {
			doc = nodeType.node_value().getOwnerDocument();
		}
		NodeList nodeList = doc.getElementsByTagNameNS(name().namespace(),
				name().local());

		if (nodeList.getLength() > 0) {
			anyType = createElementForXSDType(nodeList);
		}
		return anyType;
	}

	private AnyType createElementForXSDType(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			if (type() == null || !(element instanceof ItemPSVI)) {
				anyType = new ElementType(element);
				break;
			} else {
				ElementPSVI elempsvi = (ElementPSVI) element;
				XSTypeDefinition typedef = elempsvi.getTypeDefinition();
				if (typedef != null) {
					if (typedef.getName().equals(type().local())
							&& typedef.getNamespace().equals(
									type().namespace())) {
						anyType = new ElementType(element);
						break;
					}
				}
			}
		}
		return anyType;
	}

	@Override
	public boolean isWild() {
		return wild();
	}

	@Override
	public Class getXDMClassType() {
		return ElementType.class;
	}

}
