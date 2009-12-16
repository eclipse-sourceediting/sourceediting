/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 276134 - improvements to schema aware primitive type support
 *                                  for attribute/element nodes
 *     Jesper Moller - bug 275610 - Avoid big time and memory overhead for externals
 *     David Carver  - bug 281186 - implementation of fn:id and fn:idref
 *     David Carver (STAR) - bug 289304 - fix schema awarness of types on elements
 *     Jesper Moller - bug 297958 - Fix fn:nilled for elements
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

/**
 * A representation of the ElementType datatype
 */
public class ElementType extends NodeType {
	private Element _value;

	private String _string_value;

	/**
	 * Initialises to a null element
	 */
	public ElementType() {
		this(null);
	}

	/**
	 * Initialises according to the supplied parameters
	 * 
	 * @param v
	 *            The element being represented
	 */
	public ElementType(Element v) {
		super(v);
		_value = v;

		_string_value = null;
	}

	/**
	 * This deprecated and will be removed. Adopters need to use
	 * ElementType(Element)
	 * 
	 * @param v
	 * @param nodePosition
	 * @deprecated Use ElementType(Element v) instead.
	 */
	@Deprecated
	public ElementType(Element v, int nodePosition) {
		// unused parameter, nodePosition!
		super(v);
		_value = v;
		_string_value = null;
	}

	/**
	 * Retrieves the actual element value being represented
	 * 
	 * @return Actual element value being represented
	 */
	public Element value() {
		return _value;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "element" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "element";
	}

	/**
	 * Retrieves a String representation of the element being stored
	 * 
	 * @return String representation of the element being stored
	 */
	@Override
	public String string_value() {
		// XXX can we cache ?
		if (_string_value != null)
			return _string_value;

		_string_value = textnode_strings(_value);

		return _string_value;
	}

	/**
	 * Creates a new ResultSequence consisting of the element stored
	 * 
	 * @return New ResultSequence consisting of the element stored
	 */
	@Override
	public ResultSequence typed_value() {
		ResultSequence rs = ResultSequenceFactory.create_new();
		
		if (!(_value instanceof PSVIElementNSImpl)) {
			rs.add(new XSUntypedAtomic(string_value()));
			return rs;
		}

		PSVIElementNSImpl typeInfo = (PSVIElementNSImpl) _value;

		XSTypeDefinition typeDef = typeInfo.getTypeDefinition();

		if (typeDef != null) {
			XSSimpleTypeDefinition simpType = null;
			if (typeDef instanceof XSComplexTypeDefinition) {
				XSComplexTypeDefinition complexTypeDefinition = (XSComplexTypeDefinition) typeDef;
				simpType = complexTypeDefinition.getSimpleType();
			} else {
				simpType = (XSSimpleTypeDefinition) typeDef;
			}
			Object schemaTypeValue = getTypedValueForPrimitiveType(simpType);
			if (schemaTypeValue != null) {
				rs.add((AnyType) schemaTypeValue);
			} else {
				rs.add(new XSUntypedAtomic(string_value()));
			}
		} else {
			rs.add(new XSUntypedAtomic(string_value()));
		}

		return rs;
	}

	// recursively concatenate TextNode strings
	/**
	 * Recursively concatenate TextNode strings
	 * 
	 * @param node
	 *            Node to recurse
	 * @return String representation of the node supplied
	 */
	public static String textnode_strings(Node node) {
		String result = "";

		if (node.getNodeType() == Node.TEXT_NODE) {
			Text tn = (Text) node;
			result += tn.getData();
		}

		NodeList nl = node.getChildNodes();

		StringBuffer buf = new StringBuffer(result);
		// concatenate children
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);

			buf.append(textnode_strings(n));
		}

		result = buf.toString();
		return result;
	}

	/**
	 * Retrieves the name of the node
	 * 
	 * @return QName representation of the name of the node
	 */
	@Override
	public QName node_name() {
		QName name = new QName(_value.getPrefix(), _value.getLocalName(),
				_value.getNamespaceURI());

		return name;
	}

	@Override
	public ResultSequence nilled() {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (_value instanceof PSVIElementNSImpl) {
			PSVIElementNSImpl psviElement = (PSVIElementNSImpl) _value;
			rs.add(new XSBoolean(psviElement.getNil()));
		} else {
			rs.add(new XSBoolean(false));
		}

		return rs;
	}

	/**
	 * @since 1.1
	 */
	@Override
	public boolean isID() {
		return isElementType(SCHEMA_TYPE_ID);
	}

	/**
	 * @since 1.1
	 */
	@Override
	public boolean isIDREF() {
		return isElementType(SCHEMA_TYPE_IDREF);
	}

	protected boolean isElementType(String typeName) {
		TypeInfo typeInfo = _value.getSchemaTypeInfo();
		return isType(typeInfo, typeName);
	}

}
