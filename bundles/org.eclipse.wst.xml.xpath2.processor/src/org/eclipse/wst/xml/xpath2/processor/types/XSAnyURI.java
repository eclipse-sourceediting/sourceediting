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

package org.eclipse.wst.xml.xpath2.processor.types;

import org.eclipse.wst.xml.xpath2.processor.*;
/**
 * Represents a Universal Resource Identifier (URI) reference
 */
public class XSAnyURI extends CtrType {

	private String _value;

	/**
	 * Arity 1 Constructor
	 * @param x String representation of the URI
	 */
	public XSAnyURI(String x) {
		_value = x;
	}
	
	/**
	 * Arity 0 Constructor. Initiates URI to null.
	 */
	public XSAnyURI() {
		this(null);
	}
	
	/**
	 * Retrieve full type pathname of this datatype
	 * @return "xs:anyURI", the full type pathname of this datatype
	 */
	@Override
	public String string_type() {
		return "xs:anyURI";
	}

	/**
	 * Retrieve type name of this datatype
	 * @return "anyURI", the type name of this datatype
	 */
	@Override
	public String type_name() {
		return "anyURI";
	}

	/**
	 * Transforms and retrieves the URI value of this URI datatype in String format
	 * @return the URI value held by this instance of the URI datatype as a String
	 */
	@Override
	public String string_value() {
		return _value;
	}

	/**
	 * Creation of a result sequence consisting of a URI from a previous result sequence.
	 * @param arg previous result sequence
	 * @throws DynamicError
	 * @return new result sequence consisting of the URI supplied
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if(arg.empty())
			return rs;
		
		AnyAtomicType aat = (AnyAtomicType) arg.first();

		rs.add(new XSAnyURI(aat.string_value()));

		return rs;
	}
}
