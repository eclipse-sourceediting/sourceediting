/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi  - bug 276134 - improvements to schema aware primitive type support
 *                                 for attribute/element nodes 
 *     Jesper Moller - bug 281159 - we were missing out on qualified attributes
 *     David Carver  - bug 281186 - implementation of fn:id and fn:idref
 *     Jesper Moller- bug 275610 - Avoid big time and memory overhead for externals
 *     David Carver (STAR) - bug 289304 - fixe schema awarness of types on attributes
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *      Mukul Gandhi - bug 323900 - improving computing the typed value of element &
 *                                  attribute nodes, where the schema type of nodes
 *                                  are simple, with varieties 'list' and 'union'.  
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.util.Collections;
import java.util.List;

import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

/**
 * A representation of the AttributeType datatype
 */
public class AttrType extends NodeType {
	private static final String ATTRIBUTE = "attribute";
	Attr _value;

	// constructor only usefull for string_type()
	// XXX needs to be fixed in future
	/**
	 * Initialises to null
	 */
	public AttrType() {
		this(null, null);
	}

	/**
	 * Initialises according to the supplied parameters
	 * 
	 * @param v
	 *            The attribute being represented
	 */
	public AttrType(Attr v, TypeModel tm) {
		super(v, tm);
		_value = v;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "attribute" which is the datatype's full pathname
	 */
	public String string_type() {
		return ATTRIBUTE;
	}

	/**
	 * Retrieves a String representation of the attribute being stored
	 * 
	 * @return String representation of the attribute being stored
	 */
	public String string_value() {
		return _value.getValue();
	}

	/**
	 * Creates a new ResultSequence consisting of the attribute being stored
	 * 
	 * @return New ResultSequence consisting of the attribute being stored
	 */
	public ResultSequence typed_value() {
		ResultSequence rs = ResultSequenceFactory.create_new();

		TypeDefinition typeDef = getType();

		if (typeDef != null) {
			List/*<Short>*/ types = typeDef.getSimpleTypes(_value);
 		    rs = getXDMTypedValue(typeDef, types);
		}
		else {
		   rs.add(new XSUntypedAtomic(string_value()));  
		}
		return rs;
	}

	/**
	 * Retrieves the name of the node
	 * 
	 * @return Name of the node
	 */
	public QName node_name() {
		QName name = new QName(_value.getPrefix(), _value.getLocalName(),
				_value.getNamespaceURI());

		return name;
	}

	/**
	 * Checks if the current node is of type ID
	 * @since 1.1;
	 */
	public boolean isID() {
		return isAttrType(SCHEMA_TYPE_ID);
	}
	
	/**
	 * 
	 * @since 1.1
	 */
	public boolean isIDREF() {
		return isAttrType(SCHEMA_TYPE_IDREF);
	}
	
	protected boolean isAttrType(String typeName) {
		if (_value.getOwnerDocument().isSupported("Core", "3.0")) {
			return typeInfo(typeName);
		}
		return false;
	}
	
	private boolean typeInfo(String typeName) {
		TypeInfo typeInfo = _value.getSchemaTypeInfo();
		return isType(typeInfo, typeName);
	}

	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_UNTYPEDATOMIC;
	}
	
}
