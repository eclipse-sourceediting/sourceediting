/*******************************************************************************
 * Copyright (c) 2009 Mukul Gandhi, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mukul Gandhi - bug 277609 - Initial API and implementation, of xs:nonNegativeInteger
 *                                 data type.
 *     David Carver (STAR) - bug 262765 - fixed abs value tests.
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigInteger;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;

public class XSNonNegativeInteger extends XSInteger {
	
	private static final String XS_NON_NEGATIVE_INTEGER = "xs:nonNegativeInteger";

	/**
	 * Initializes a representation of 0
	 */
	public XSNonNegativeInteger() {
	  this(BigInteger.valueOf(0));
	}
	
	/**
	 * Initializes a representation of the supplied nonNegativeInteger value
	 * 
	 * @param x
	 *            nonNegativeInteger to be stored
	 */
	public XSNonNegativeInteger(BigInteger x) {
		super(x);
	}
	
	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:nonNegativeInteger" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return XS_NON_NEGATIVE_INTEGER;
	}
	
	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "nonNegativeInteger" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "nonNegativeInteger";
	}
	
	/**
	 * Creates a new ResultSequence consisting of the extractable nonNegativeInteger
	 * in the supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which the nonNegativeInteger is to be extracted
	 * @return New ResultSequence consisting of the 'nonNegativeInteger' supplied
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		// the function conversion rules apply here too. Get the argument
		// and convert it's string value to a nonNegativeInteger.
		AnyType aat = arg.first();

		try {
			BigInteger bigInt = new BigInteger(aat.string_value());
			
			// doing the range checking
			// min value is, 0
			// max value is INF
			BigInteger min = BigInteger.valueOf(0L);			

			if (bigInt.compareTo(min) < 0) {
			   // invalid input
			   throw DynamicError.cant_cast(null);	
			}
			
			rs.add(new XSNonNegativeInteger(bigInt));
			
			return rs;
		} catch (NumberFormatException e) {
			throw DynamicError.cant_cast(null);
		}

	}

}
