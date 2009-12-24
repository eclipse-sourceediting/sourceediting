/*******************************************************************************
 * Copyright (c) 2009 Mukul Gandhi, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mukul Gandhi - bug 274952 - Initial API and implementation, of xs:long data 
 *                                 type.
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigInteger;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;

public class XSLong extends XSInteger {
	
	private static final String XS_LONG = "xs:long";

	/**
	 * Initializes a representation of 0
	 */
	public XSLong() {
	  this(BigInteger.valueOf(0));
	}
	
	/**
	 * Initializes a representation of the supplied long value
	 * 
	 * @param x
	 *            Long to be stored
	 */
	public XSLong(BigInteger x) {
		super(x);
	}
	
	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:long" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return XS_LONG;
	}
	
	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "long" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "long";
	}
	
	/**
	 * Creates a new ResultSequence consisting of the extractable long in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which the long is to be extracted
	 * @return New ResultSequence consisting of the 'long' supplied
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		// the function conversion rules apply here too. Get the argument
		// and convert it's string value to a long.
		AnyType aat = arg.first();

		try {
			BigInteger bigInt = new BigInteger(aat.string_value());
			
			// doing the range checking
			BigInteger min = BigInteger.valueOf(-9223372036854775808L);
			BigInteger max = BigInteger.valueOf(9223372036854775807L);			

			if (bigInt.compareTo(min) < 0 || bigInt.compareTo(max) > 0) {
			   // invalid input
			   DynamicError.throw_type_error();	
			}
			
			rs.add(new XSLong(bigInt));
			
			return rs;
		} catch (NumberFormatException e) {
			throw DynamicError.cant_cast(null);
		}

	}

}
