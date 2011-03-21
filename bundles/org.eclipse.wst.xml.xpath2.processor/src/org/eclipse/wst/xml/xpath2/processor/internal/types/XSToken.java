/*******************************************************************************
 * Copyright (c) 2011 Mukul Gandhi, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mukul Gandhi - bug 334478 - implementation of xs:token data type
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

/**
 * A representation of the xs:token datatype
 */
public class XSToken extends XSNormalizedString {

	private static final String XS_TOKEN = "xs:token";

	/**
	 * Initialises using the supplied String
	 * 
	 * @param x
	 *            The String to initialise to
	 */
	public XSToken(String x) {
		super(x);
	}

	/**
	 * Initialises to null
	 */
	public XSToken() {
		this(null);
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:token" which is the datatype's full pathname
	 */
	public String string_type() {
		return XS_TOKEN;
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "token" which is the datatype's name
	 */
	public String type_name() {
		return "token";
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable String in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract the String
	 * @return New ResultSequence consisting of the supplied String
	 * @throws DynamicError
	 */
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
		   return rs;

		AnyType aat = arg.first();

		String srcString = aat.string_value();
		if (!isSatisfiesConstraints(srcString)) {
			// invalid input
			DynamicError.throw_type_error();
		}
		
		rs.add(new XSToken(srcString));

		return rs;
	}
	
	/*
	 * Does the string in context satisfies constraints of the datatype, xs:token. 
	 */
	protected boolean isSatisfiesConstraints(String srcString) {
	   
		boolean isToken = true;
		
		// satisfies constraints of xs:normalizedString and additionally must satisfy the condition,
		// the string must not have leading or trailing spaces and that have no internal sequences of two or more spaces.
		if (!super.isSatisfiesConstraints(srcString) || srcString.startsWith(" ") || 
			 srcString.endsWith(" ") || srcString.indexOf("  ") != -1) {
			isToken = false;
		}
		
		return isToken;
		  
	} // isSatisfiesConstraints

	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_TOKEN;
	}

}
