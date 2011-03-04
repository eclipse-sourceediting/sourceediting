/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Moller - bug 275610 - Avoid big time and memory overhead for externals
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.w3c.dom.*;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;

/**
 * A representation of the DocumentType datatype
 */
public class DocType extends NodeType {
	private static final String DOCUMENT = "document";
	private Document _value;
	private String _string_value;

	/**
	 * Initialises according to the supplied parameters
	 * 
	 * @param v
	 *            The document being represented
	 */
	public DocType(Document v, TypeModel tm) {
		super(v, tm);
		_value = v;
		_string_value = null;
	}

	/**
	 * Retrieves the actual document being represented
	 * 
	 * @return Actual document being represented
	 */
	public Document value() {
		return _value;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "document" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return DOCUMENT;
	}

	/**
	 * Retrieves a String representation of the document being stored
	 * 
	 * @return String representation of the document being stored
	 */
	@Override
	public String string_value() {
		// XXX caching
		if (_string_value == null)
			_string_value = ElementType.textnode_strings(_value);

		return _string_value;
	}

	/**
	 * Creates a new ResultSequence consisting of the document being stored
	 * 
	 * @return New ResultSequence consisting of the document being stored
	 */
	@Override
	public ResultSequence typed_value() {
		ResultSequence rs = ResultSequenceFactory.create_new();

		// XXX no psvi
		rs.add(new XSUntypedAtomic(string_value()));

		return rs;
	}

	/**
	 * Retrieves the name of the node
	 * 
	 * @return QName representation of the name of the node
	 */
	@Override
	public QName node_name() {
		return null;
	}

	/**
	 * @since 1.1
	 */
	@Override
	public boolean isID() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @since 1.1
	 */
	@Override
	public boolean isIDREF() {
		return false;
	}
}
