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
 *     David Carver (STAR) - bug 277774 - XSDecimal returning wrong values.
 *     David Carver (STAR) - bug 262765 - various numeric formatting fixes and calculations
 *     David Carver (STAR) - bug 282223 - Can't Cast Exponential values to Decimal values.
 *     David Carver (STAR) - bug 262765 - fixed edge case where rounding was occuring when it shouldn't. 
 *     Jesper Steen Moller - bug 262765 - fix type tests
 *     David Carver (STAR) - bug 262765 - fixed abs value tests.
 *     Jesper Steen Moller - bug 281028 - fixed division of zero (no, not by)
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Iterator;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;

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
		this(BigDecimal.ZERO);
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
		_value = new BigDecimal(x, MathContext.DECIMAL128);
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
		
		if (aat instanceof XSDuration || aat instanceof CalendarType ||
			aat instanceof XSBase64Binary || aat instanceof XSHexBinary ||
			aat instanceof XSAnyURI) {
			throw DynamicError.invalidType();
		}
		
		if (aat.string_value().contains("-INF")) {
			throw DynamicError.cant_cast(null);
		}
		
		if (!isLexicalValue(aat.string_value())) {
			throw DynamicError.invalidLexicalValue();
		}
		
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

	protected boolean isLexicalValue(String value) {
		if (value.equalsIgnoreCase("inf")) {
			return false;
		}
		
		if (value.equalsIgnoreCase("-inf")) {
			return false;
		}
		return true;
	}
	private boolean isCastable(AnyType aat) throws DynamicError {
		if (aat instanceof XSBoolean || aat instanceof NumericType) {
			return true;
		}		
		
		if (aat.string_value().contains("E") || aat.string_value().contains("e") && !(aat instanceof XSBoolean)) {
			return false;
		}

		if (aat instanceof XSString || aat instanceof XSUntypedAtomic ||
			aat instanceof NodeType) {
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
	@Deprecated
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
	 * @param at
	 *            Representation to be compared with (must currently be of type
	 *            XSDecimal)
	 * 
	 * 
	 * @return True if the 2 representation represent the same number. False
	 *         otherwise
	 */
	public boolean eq(AnyType at, DynamicContext context) throws DynamicError {
		XSDecimal dt = null;
		if (!(at instanceof XSDecimal)) { 
			ResultSequence rs = ResultSequenceFactory.create_new(at);
			
			ResultSequence crs = constructor(rs);
			if (crs.empty()) {
				throw DynamicError.throw_type_error();
			}
		
			AnyType cat = crs.first();

			dt = (XSDecimal) cat;
	    } else {
	    	dt = (XSDecimal) at;
	    }
		return (_value.compareTo(dt.getValue()) == 0);
	}

	/**
	 * Comparison between this number and the supplied representation. 
	 * 
	 * @param arg
	 *            Representation to be compared with (must currently be of type
	 *            XSDecimal)
	 * @return True if the supplied type represents a number smaller than this
	 *         one stored. False otherwise
	 */
	public boolean gt(AnyType arg, DynamicContext context) throws DynamicError {
		AnyType carg = convertArg(arg);
		
		XSDecimal val = (XSDecimal) get_single_type(carg, XSDecimal.class);
		return (_value.compareTo(val.getValue()) == 1);
	}

	protected AnyType convertArg(AnyType arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new(arg);
		rs = constructor(rs);
		AnyType carg = rs.first();
		return carg;
	}
	
	/**
	 * Comparison between this number and the supplied representation. 
	 * 
	 * @param arg
	 *            Representation to be compared with (must currently be of type
	 *            XSDecimal)
	 * @return True if the supplied type represents a number greater than this
	 *         one stored. False otherwise
	 */
	public boolean lt(AnyType arg, DynamicContext context) throws DynamicError {
		AnyType carg = convertArg(arg);
		XSDecimal val = (XSDecimal) get_single_type(carg, XSDecimal.class);
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
		ResultSequence carg = convertResultSequence(arg);
		AnyType at = get_single_arg(carg);
		if (!(at instanceof XSDecimal))
			DynamicError.throw_type_error();
		XSDecimal dt = (XSDecimal) at;

		// own it
		return ResultSequenceFactory.create_new(new XSDecimal(_value.add(dt.getValue())));
	}
	
	private ResultSequence convertResultSequence(ResultSequence arg)
			throws DynamicError {
		ResultSequence carg = arg;
		Iterator it = carg.iterator();
		while (it.hasNext()) {
			AnyType type = (AnyType) it.next();
			if (type.string_type().equals("xs:untypedAtomic") ||
				type.string_type().equals("xs:string")) {
				throw DynamicError.invalidType();
			}
		}
		carg = constructor(carg);
		return carg;
	}	

	/**
	 * Mathematical subtraction operator between this XSDecimal and the supplied
	 * ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform a subtraction with
	 * @return A XSDecimal consisting of the result of the mathematical
	 *         subtraction.
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		
		ResultSequence carg = convertResultSequence(arg);

		AnyType at = get_single_arg(carg);
		if (!(at instanceof XSDecimal))
			DynamicError.throw_type_error();
		XSDecimal dt = (XSDecimal) at;

		return ResultSequenceFactory.create_new(new XSDecimal(_value.subtract(dt.getValue())));
	}

	/**
	 * Mathematical multiplication operator between this XSDecimal and the
	 * supplied ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform a multiplication with
	 * @return A XSDecimal consisting of the result of the mathematical
	 *         multiplication.
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		ResultSequence carg = convertResultSequence(arg);

		XSDecimal val = (XSDecimal) get_single_type(carg, XSDecimal.class);
		BigDecimal result = _value.multiply(val.getValue());
		return ResultSequenceFactory.create_new(new XSDecimal(result));
	}

	/**
	 * Mathematical division operator between this XSDecimal and the supplied
	 * ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform a division with
	 * @return A XSDecimal consisting of the result of the mathematical
	 *         division.
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		ResultSequence carg = convertResultSequence(arg);
			
		XSDecimal val = (XSDecimal) get_single_type(carg, XSDecimal.class);
		if (val.zero()) {
			throw DynamicError.div_zero(null);
		}
		BigDecimal result = getValue().divide(val.getValue(), 18, RoundingMode.HALF_EVEN);
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
		ResultSequence carg = convertResultSequence(arg);

		XSDecimal val = (XSDecimal) get_single_type(carg, XSDecimal.class);

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
		ResultSequence carg = convertResultSequence(arg);

		XSDecimal val = (XSDecimal) get_single_type(carg, XSDecimal.class);
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
