/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 334842 - improving support for the data types Name, NCName, ENTITY, 
 *                                 ID, IDREF and NMTOKEN. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.apache.xerces.util.XMLChar;
import org.eclipse.wst.xml.xpath2.api.ResultBuffer;
import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

/**
 * A representation of the NCName datatype
 */
public class XSNCName extends XSName {
	private static final String XS_NC_NAME = "xs:NCName";

	/**
	 * Initialises using the supplied String
	 * 
	 * @param x
	 *            String to be stored
	 */
	public XSNCName(String x) {
		super(x);
	}

	/**
	 * Initialises to null
	 */
	public XSNCName() {
		this(null);
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:NCName" which is the datatype's full pathname
	 */
	public String string_type() {
		return XS_NC_NAME;
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "NCName" which is the datatype's name
	 */
	public String type_name() {
		return "NCName";
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable NCName within
	 * the supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract the NCName
	 * @return New ResultSequence consisting of the NCName supplied
	 * @throws DynamicError
	 */
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		if (arg.empty())
			return ResultBuffer.EMPTY;

		AnyAtomicType aat = (AnyAtomicType) arg.first();
		String strValue = aat.getStringValue();
		
		if (!isConstraintSatisfied(strValue)) {
			// invalid input
			DynamicError.throw_type_error();
		}

		return new XSNCName(strValue);
	}
	
	/*
	 * Check if a string satisfies the constraints of NCName data type.
	 */
	protected boolean isConstraintSatisfied(String strValue) {
		
		boolean isValidNCName = true;
		
		if (!XMLChar.isValidNCName(strValue)) {
			isValidNCName = false;
		}
		
		return isValidNCName;
		
	} // isConstraintSatisfied

	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_NCNAME;
	}
}
