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
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.function.XSCtrLibrary;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A representation of the ElementType datatype
 */
public class ElementType extends NodeType {
	private Element _value;

	private String _string_value;

	// constructor only usefull for string_type()
	// XXX needs to be fixed in future
	/**
	 * Initialises to a null element
	 */
	public ElementType() {
		this(null, 0);
	}

	/**
	 * Initialises according to the supplied parameters
	 * 
	 * @param v
	 *            The element being represented
	 * @param doc_order
	 *            The document order
	 */
	public ElementType(Element v, int doc_order) {
		super(v, doc_order);
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
		
		PSVIElementNSImpl psviElem = (PSVIElementNSImpl)_value;
		XSTypeDefinition typeDef = psviElem.getTypeDefinition();
		
		if (typeDef != null && typeDef.getNamespace().equals(XSCtrLibrary.XML_SCHEMA_NS)) {
		  Object schemaTypeValue = getTypedValueForPrimitiveType(typeDef);
		  if (schemaTypeValue != null) {
			rs.add((AnyType)schemaTypeValue);  
		  }
		  else {
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

		// concatenate children
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);

			result += textnode_strings(n);
		}

		return result;
	}

	/**
	 * Retrieves the name of the node
	 * 
	 * @return QName representation of the name of the node
	 */
	@Override
	public QName node_name() {
		QName name = new QName(_value.getTagName());

		name.set_namespace(_value.getNamespaceURI());

		return name;
	}

	@Override
	public ResultSequence nilled() {
		ResultSequence rs = ResultSequenceFactory.create_new();

		// XXX PSVI !!!
		rs.add(new XSBoolean(false));

		return rs;
	}
}
