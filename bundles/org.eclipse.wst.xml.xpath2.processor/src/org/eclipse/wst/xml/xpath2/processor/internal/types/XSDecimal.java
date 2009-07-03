/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 274805 - improvements to xs:integer data type
 *     David Carver - bug 277774 - XSDecimal returning wrong values.
 *     David Carver - bug 262765 - various numeric formatting fixes and calculations
 *     David Carver (STAR) - bug 282223 - Can't Cast Exponential values to Decimal values. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;

/**
 * A representation of the Decimal datatype
 */
public class XSDecimal extends NumericType {

	private BigDecimal _value;
	private XPathDecimalFormat format = new XPathDecimalFormat("0.####################");

	/**
	 * Initiates a representation of 0.0
	 */
	public XSDecimal() {
		this(BigDecimal.valueOf(0.0));
	}

	/**
	 * Initiates a representation of the supplied number
	 * 
	 * @param x
	 *            Number to be stored
	 */
	public XSDecimal(BigDecimal x) {
		_value = x;
	}
	
	public XSDecimal(String x) {
		_value = new BigDecimal(x);
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:decimal" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:decimal";
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "decimal" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "decimal";
	}

	/**
	 * Retrieves a String representation of the Decimal value stored
	 * 
	 * @return String representation of the Decimal value stored
	 */
	@Override
	public String string_value() {
		
		if (zero()) {
			return "0";
		}
		
		_value = _value.stripTrailingZeros();
		return format.xpathFormat(_value);
	}

	/**
	 * Check if this XSDecimal represents 0
	 * 
	 * @return True if this XSDecimal represents 0. False otherwise
	 */
	@Override
	public boolean zero() {
		return (_value.compareTo(BigDecimal.valueOf(0.0)) == 0);
	}

	/**
	 * Creates a new result sequence consisting of the retrievable decimal
	 * number in the supplied result sequence
	 * 
	 * @param arg
	 *            The result sequence from which to extract the decimal number.
	 * @throws DynamicError
	 * @return A new result sequence consisting of the decimal number supplied.
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyType aat = arg.first();
		
		if (!isCastable(aat)) {
			throw DynamicError.cant_cast(null);
		}

		try {
			// XPath doesn't allow for converting Exponents to Decimal values.
			
			XSDecimal decimal = castDecimal(aat);
			
			rs.add(decimal);
			return rs;
		} catch (NumberFormatException e) {
			throw DynamicError.cant_cast(null);
		}

	}

	private boolean isCastable(AnyType aat) {
		if (aat.string_value().contains("E") || aat.string_value().contains("e") && !(aat instanceof XSBoolean)) {
			return false;
		}

		if (aat instanceof XSString || aat instanceof XSUntypedAtomic ||
			aat instanceof NodeType) {
			return true;
		}
		if (aat instanceof XSBoolean || aat instanceof NumericType) {
			return true;
		}
		return false;
	}

	private XSDecimal castDecimal(AnyType aat) {
		if (aat instanceof XSBoolean) {
			if (aat.string_value().equals("true")) {
				return new XSDecimal(BigDecimal.ONE);
			} else {
				return new XSDecimal(BigDecimal.ZERO);
			}
		}
		return new XSDecimal(aat.string_value());
	}
	
	/**
	 * Retrieves the actual value of the number stored
	 * 
	 * @return The actual value of the number stored
	 * @deprecated Use getValue() instead.
	 */
	public double double_value() {
		return _value.doubleValue();
	}
	
	public BigDecimal getValue() {
		return _value;
	}

	/**
	 * Sets the number stored to that supplied
	 * 
	 * @param x
	 *            Number to be stored
	 */
	public void set_double(double x) {
		_value = BigDecimal.valueOf(x);
	}

	// comparisons
	/**
	 * Equality comparison between this number and the supplied representation.
	 * Currently no numeric type promotion exists so the supplied representation
	 * must be of type XSDecimal.
	 * 
	 * @param at
	 *            Representation to be compared with (must currently be of type
	 *            XSDecimal)
	 * @return True if the 2 representation represent the same number. False
	 *         otherwise
	 */
	public boolean eq(AnyType at) throws DynamicError {

		if (!(at instanceof XSDecimal))
			DynamicError.throw_type_error();

		XSDecimal dt = (XSDecimal) at;
		return (_value.compareTo(dt.getValue()) == 0);
	}

	/**
	 * Comparison between this number and the supplied representation. Currently
	 * no numeric type promotion exists so the supplied representation must be
	 * of type XSDecimal.
	 * 
	 * @param arg
	 *            Representation to be compared with (must currently be of type
	 *            XSDecimal)
	 * @return True if the supplied type represents a number smaller than this
	 *         one stored. False otherwise
	 */
	public boolean gt(AnyType arg) throws DynamicError {
		XSDecimal val = (XSDecimal) get_single_type(arg, XSDecimal.class);
		return (_value.compareTo(val.getValue()) == 1);
	}

	/**
	 * Comparison between this number and the supplied representation. Currently
	 * no numeric type promotion exists so the supplied representation must be
	 * of type XSDecimal.
	 * 
	 * @param arg
	 *            Representation to be compared with (must currently be of type
	 *            XSDecimal)
	 * @return True if the supplied type represents a number greater than this
	 *         one stored. False otherwise
	 */
	public boolean lt(AnyType arg) throws DynamicError {
		XSDecimal val = (XSDecimal) get_single_type(arg, XSDecimal.class);
		return (_value.compareTo(val.getValue()) == -1);
	}

	// math
	/**
	 * Mathematical addition operator between this XSDecimal and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDecimal.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an addition with
	 * @return A XSDecimal consisting of the result of the mathematical
	 *         addition.
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {
		// get arg
		AnyType at = get_single_arg(arg);
		if (!(at instanceof XSDecimal))
			DynamicError.throw_type_error();
		XSDecimal dt = (XSDecimal) at;

		// own it
		return ResultSequenceFactory.create_new(new XSDecimal(_value.add(dt.getValue())));
	}

	/**
	 * Mathematical subtraction operator between this XSDecimal and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDecimal.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a subtraction with
	 * @return A XSDecimal consisting of the result of the mathematical
	 *         subtraction.
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		AnyType at = get_single_arg(arg);
		if (!(at instanceof XSDecimal))
			DynamicError.throw_type_error();
		XSDecimal dt = (XSDecimal) at;

		return ResultSequenceFactory.create_new(new XSDecimal(_value.subtract(dt.getValue())));
	}

	/**
	 * Mathematical multiplication operator between this XSDecimal and the
	 * supplied ResultSequence. Due to no numeric type promotion or conversion,
	 * the ResultSequence must be of type XSDecimal.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a multiplication with
	 * @return A XSDecimal consisting of the result of the mathematical
	 *         multiplication.
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		XSDecimal val = (XSDecimal) get_single_type(arg, XSDecimal.class);
		BigDecimal result = _value.multiply(val.getValue());
		return ResultSequenceFactory.create_new(new XSDecimal(result));
	}

	/**
	 * Mathematical division operator between this XSDecimal and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDecimal.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a division with
	 * @return A XSDecimal consisting of the result of the mathematical
	 *         division.
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		XSDecimal val = (XSDecimal) get_single_type(arg, XSDecimal.class);
		if (val.zero())
			throw DynamicError.div_zero(null);
		BigDecimal result = getValue().divide(val.getValue(), 18, RoundingMode.HALF_EVEN);
//		BigDecimal result = BigDecimal.valueOf(double_value() / val.double_value());
		return ResultSequenceFactory.create_new(new XSDecimal(result));
	}

	/**
	 * Mathematical integer division operator between this XSDecimal and the
	 * supplied ResultSequence. Due to no numeric type promotion or conversion,
	 * the ResultSequence must be of type XSDecimal.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an integer division with
	 * @return A XSInteger consisting of the result of the mathematical integer
	 *         division.
	 */
	public ResultSequence idiv(ResultSequence arg) throws DynamicError {
		XSDecimal val = (XSDecimal) get_single_type(arg, XSDecimal.class);

		if (val.zero())
			throw DynamicError.div_zero(null);
		BigInteger _ivalue = _value.toBigInteger();
		BigInteger ival =  val.getValue().toBigInteger();
		BigInteger result = _ivalue.divide(ival);
		return ResultSequenceFactory.create_new(new 
				           XSInteger(result));
	}

	/**
	 * Mathematical modulus operator between this XSDecimal and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDecimal.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a modulus with
	 * @return A XSDecimal consisting of the result of the mathematical modulus.
	 */
	public ResultSequence mod(ResultSequence arg) throws DynamicError {
		XSDecimal val = (XSDecimal) get_single_type(arg, XSDecimal.class);
		BigDecimal result = _value.remainder(val.getValue());
		return ResultSequenceFactory.create_new(new XSDecimal(result));
	}

	/**
	 * Negation of the number stored
	 * 
	 * @return A XSDecimal representing the negation of this XSDecimal
	 */
	@Override
	public ResultSequence unary_minus() {
		BigDecimal result = _value.negate();
		return ResultSequenceFactory.create_new(new XSDecimal(result));
	}

	// functions
	/**
	 * Absolutes the number stored
	 * 
	 * @return A XSDecimal representing the absolute value of the number stored
	 */
	@Override
	public NumericType abs() {
		return new XSDecimal(_value.abs());
	}

	/**
	 * Returns the smallest integer greater than the number stored
	 * 
	 * @return A XSDecimal representing the smallest integer greater than the
	 *         number stored
	 */
	@Override
	public NumericType ceiling() {
		BigDecimal ceiling = _value.setScale(0, RoundingMode.CEILING);
		return new XSDecimal(ceiling);
	}

	/**
	 * Returns the largest integer smaller than the number stored
	 * 
	 * @return A XSDecimal representing the largest integer smaller than the
	 *         number stored
	 */
	@Override
	public NumericType floor() {
		BigDecimal floor = _value.setScale(0, RoundingMode.FLOOR);
		return new XSDecimal(floor);
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSDecimal representing the closest long of the number stored.
	 */
	@Override
	public NumericType round() {
		BigDecimal round = _value.setScale(0, RoundingMode.UP);
		return new XSDecimal(round);
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSDecimal representing the closest long of the number stored.
	 */
	@Override
	public NumericType round_half_to_even() {
		return round_half_to_even(0);
	}

	/**
	 * Returns the closest integer of the number stored with the specified precision.
	 * 
	 * @param precision An integer precision 
	 * @return A XSDecimal representing the closest long of the number stored.
	 */
	@Override
	public NumericType round_half_to_even(int precision) {
		BigDecimal round = _value.setScale(precision, RoundingMode.HALF_EVEN);
		return new XSDecimal(round);
	}
}
