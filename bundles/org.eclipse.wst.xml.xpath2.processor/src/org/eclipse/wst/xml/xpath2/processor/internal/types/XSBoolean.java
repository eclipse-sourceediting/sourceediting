/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Mukul Gandhi - bug274784 - improvements to xs:boolean data type implementation
 *     David Carver - bug 282223 - corrected casting to boolean.
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.function.CmpEq;
import org.eclipse.wst.xml.xpath2.processor.internal.function.CmpGt;
import org.eclipse.wst.xml.xpath2.processor.internal.function.CmpLt;

/**
 * A representation of a true or a false value.
 */
public class XSBoolean extends CtrType implements CmpEq, CmpGt, CmpLt {
	private boolean _value;

	/**
	 * Initiates the new representation to the boolean supplied
	 * 
	 * @param x
	 *       Initializes this datatype to represent this boolean
	 */
	public XSBoolean(boolean x) {
		_value = x;
	}

	/**
	 * Initiates to a default representation of false.
	 */
	public XSBoolean() {
	  this(false);
	}

	/**
	 * Retrieve the full type pathname of this datatype
	 * 
	 * @return "xs:boolean", the full datatype pathname
	 */
	@Override
	public String string_type() {
		return "xs:boolean";
	}

	/**
	 * Retrieve the datatype name
	 * 
	 * @return "boolean", which is the datatype name.
	 */
	@Override
	public String type_name() {
		return "boolean";
	}

	/**
	 * Retrieve the String representation of the boolean value stored
	 * 
	 * @return the String representation of the boolean value stored
	 */
	@Override
	public String string_value() {
		return "" + _value;
	}

	/**
	 * Retrieves the actual boolean value stored
	 * 
	 * @return the actual boolean value stored
	 */
	public boolean value() {
		return _value;
	}

	/**
	 * Creates a new result sequence consisting of the retrievable boolean value
	 * in the supplied result sequence
	 * 
	 * @param arg
	 *            The result sequence from which to extract the boolean value.
	 * @throws DynamicError
	 * @return A new result sequence consisting of the boolean value supplied.
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
		  return rs;
		
		AnyType anyType = arg.first();
		
		if (anyType instanceof XSDuration || anyType instanceof CalendarType ||
			anyType instanceof XSBase64Binary || anyType instanceof XSHexBinary ||
			anyType instanceof XSAnyURI) {
			throw DynamicError.invalidType();
		}
		
		String str_value = anyType.string_value();
		
		
		if (!(isCastable(anyType, str_value))) {
		   throw DynamicError.cant_cast(null);
		}

		Boolean b = null;
		if (isFalse(str_value)) {
		  b = Boolean.FALSE;	
		}
		else {
		  b = Boolean.TRUE;		
		}
		
		rs.add(new XSBoolean(b.booleanValue()));

		return rs;
	}

	private boolean isFalse(String str_value) {
		return str_value.equals("0") || str_value.equals("false") ||
		    str_value.equals("+0") || str_value.equals("-0") ||
		    str_value.equals("0.0E0") || str_value.equals("NaN");
	}

	private boolean isCastable(AnyType anyType, String str_value) {
		return str_value.equals("0") || str_value.equals("1") || 
			 str_value.equals("true") || str_value.equals("false") ||
			 anyType instanceof NumericType;
	}

	// comparisons
	/**
	 * Comparison for equality between the supplied and this boolean
	 * representation. Returns true if both represent same boolean value, false
	 * otherwise
	 * 
	 * @param arg
	 *            The XSBoolean representation of the boolean value to compare
	 *            with.
	 * @throws DynamicError
	 * @return New XSBoolean representation of true/false result of the equality
	 *         comparison
	 */
	public boolean eq(AnyType arg, DynamicContext context) throws DynamicError {
		XSBoolean barg = (XSBoolean) NumericType.get_single_type(arg,
				XSBoolean.class);

		return value() == barg.value();
	}

	/**
	 * Comparison between the supplied and this boolean representation. Returns
	 * true if this XSBoolean represents true and that XSBoolean supplied
	 * represents false. Returns false otherwise
	 * 
	 * @param arg
	 *            The XSBoolean representation of the boolean value to compare
	 *            with.
	 * @throws DynamicError
	 * @return New XSBoolean representation of true/false result of the
	 *         comparison
	 */
	public boolean gt(AnyType arg, DynamicContext context) throws DynamicError {
		XSBoolean barg = (XSBoolean) NumericType.get_single_type(arg,
				XSBoolean.class);

		boolean result = false;

		if (value() && !barg.value())
			result = true;
		return result;
	}

	/**
	 * Comparison between the supplied and this boolean representation. Returns
	 * true if this XSBoolean represents false and that XSBoolean supplied
	 * represents true. Returns false otherwise
	 * 
	 * @param arg
	 *            The XSBoolean representation of the boolean value to compare
	 *            with.
	 * @throws DynamicError
	 * @return New XSBoolean representation of true/false result of the
	 *         comparison
	 */
	public boolean lt(AnyType arg, DynamicContext context) throws DynamicError {
		XSBoolean barg = (XSBoolean) NumericType.get_single_type(arg,
				XSBoolean.class);

		boolean result = false;

		if (!value() && barg.value())
			result = true;
		return result;
	}

}
