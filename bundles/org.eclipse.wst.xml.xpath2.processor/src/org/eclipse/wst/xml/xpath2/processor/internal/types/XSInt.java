/*******************************************************************************
 * Copyright (c) 2009 Mukul Gandhi, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mukul Gandhi - bug 275105 - implementation of xs:int data type
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigInteger;

import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

public class XSInt extends XSLong {
	
	private static final String XS_INT = "xs:int";

	/**
	 * Initializes a representation of 0
	 */
	public XSInt() {
	  this(BigInteger.valueOf(0));
	}
	
	/**
	 * Initializes a representation of the supplied int value
	 * 
	 * @param x
	 *            Int to be stored
	 */
	public XSInt(BigInteger x) {
		super(x);
	}
	
	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:int" which is the datatype's full pathname
	 */
	public String string_type() {
		return XS_INT;
	}
	
	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "int" which is the datatype's name
	 */
	public String type_name() {
		return "int";
	}
	
	/**
	 * Creates a new ResultSequence consisting of the extractable 'int' in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which the int is to be extracted
	 * @return New ResultSequence consisting of the 'int' supplied
	 * @throws DynamicError
	 */
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		// the function conversion rules apply here too. Get the argument
		// and convert it's string value to an int.
		AnyType aat = arg.first();

		try {
			BigInteger bigInt = new BigInteger(aat.string_value());
			
			// doing the range checking
			BigInteger min = BigInteger.valueOf(-2147483648L);
			BigInteger max = BigInteger.valueOf(2147483647L);			

			if (bigInt.compareTo(min) < 0 || bigInt.compareTo(max) > 0) {
			   // invalid input
			   DynamicError.throw_type_error();	
			}
			
			rs.add(new XSInt(bigInt));
			
			return rs;
		} catch (NumberFormatException e) {
			throw DynamicError.cant_cast(null);
		}

	}

	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_INT;
	}
	

}
