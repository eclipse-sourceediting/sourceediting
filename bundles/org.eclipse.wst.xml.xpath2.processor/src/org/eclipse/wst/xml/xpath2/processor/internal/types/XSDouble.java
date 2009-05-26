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
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigInteger;
import java.text.DecimalFormat;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;

/**
 * A representation of the Double datatype
 */
public class XSDouble extends NumericType {

	private double _value;

	/**
	 * Initialises a representation of the supplied number
	 * 
	 * @param x
	 *            Number to be stored
	 */
	public XSDouble(double x) {
		_value = x;
	}

	/**
	 * Initialises a representation of 0
	 */
	public XSDouble() {
		this(0);
	}

	/**
	 * Initialises using a String represented number
	 * 
	 * @param init
	 *            String representation of the number to be stored
	 */
	public XSDouble(String init) throws DynamicError {
		try {
			_value = new Double(init).doubleValue();
		} catch (NumberFormatException e) {
			throw DynamicError.cant_cast(null);
		}
	}

	/**
	 * Creates a new representation of the String represented number
	 * 
	 * @param i
	 *            String representation of the number to be stored
	 * @return New XSDouble representing the number supplied
	 */
	public static XSDouble parse_double(String i) {
		try {
			Double d = new Double(i);
			return new XSDouble(d.doubleValue());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Creates a new result sequence consisting of the retrievable double number
	 * in the supplied result sequence
	 * 
	 * @param arg
	 *            The result sequence from which to extract the double number.
	 * @throws DynamicError
	 * @return A new result sequence consisting of the double number supplied.
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyType aat = arg.first();

		XSDouble d = parse_double(aat.string_value());
		if (d == null)
			throw DynamicError.cant_cast(null);

		rs.add(d);
		return rs;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:double" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:double";
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "double" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "double";
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
		return  Double.toString(_value);
	}

	/**
	 * Check for whether this XSDouble represents NaN
	 * 
	 * @return True if this XSDouble represents NaN. False otherwise.
	 */
	public boolean nan() {
		return Double.isNaN(_value);
	}

	/**
	 * Check for whether this XSDouble represents 0
	 * 
	 * @return True if this XSDouble represents 0. False otherwise.
	 */
	@Override
	public boolean zero() {
		return (Double.compare(_value, 0.0E0) == 0);
	}

	/**
	 * Retrieves the actual value of the number stored
	 * 
	 * @return The actual value of the number stored
	 */
	public double double_value() {
		return _value;
	}

	/**
	 * Equality comparison between this number and the supplied representation.
	 * Currently no numeric type promotion exists so the supplied representation
	 * must be of type XSDouble.
	 * 
	 * @param aa
	 *            Representation to be compared with (must currently be of type
	 *            XSDouble)
	 * @return True if the 2 representations represent the same number. False
	 *         otherwise
	 */
	public boolean eq(AnyType aa) throws DynamicError {

		if (!(aa instanceof XSDouble))
			throw DynamicError.throw_type_error();

		XSDouble d = (XSDouble) aa;

		return double_value() == d.double_value();
	}

	/**
	 * Comparison between this number and the supplied representation. Currently
	 * no numeric type promotion exists so the supplied representation must be
	 * of type XSDouble.
	 * 
	 * @param arg
	 *            Representation to be compared with (must currently be of type
	 *            XSDouble)
	 * @return True if the supplied type represents a number smaller than this
	 *         one stored. False otherwise
	 */
	public boolean gt(AnyType arg) throws DynamicError {
		XSDouble val = (XSDouble) get_single_type(arg, XSDouble.class);
		return double_value() > val.double_value();
	}

	/**
	 * Comparison between this number and the supplied representation. Currently
	 * no numeric type promotion exists so the supplied representation must be
	 * of type XSDouble.
	 * 
	 * @param arg
	 *            Representation to be compared with (must currently be of type
	 *            XSDouble)
	 * @return True if the supplied type represents a number greater than this
	 *         one stored. False otherwise
	 */
	public boolean lt(AnyType arg) throws DynamicError {
		XSDouble val = (XSDouble) get_single_type(arg, XSDouble.class);
		return double_value() < val.double_value();
	}

	// math
	/**
	 * Mathematical addition operator between this XSDouble and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDouble.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an addition with
	 * @return A XSDouble consisting of the result of the mathematical addition.
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {
		AnyType at = get_single_arg(arg);
		if (!(at instanceof XSDouble))
			DynamicError.throw_type_error();
		XSDouble val = (XSDouble) at;

		return ResultSequenceFactory.create_new(new XSDouble(double_value()
				+ val.double_value()));
	}

	/**
	 * Mathematical subtraction operator between this XSDouble and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDouble.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an subtraction with
	 * @return A XSDouble consisting of the result of the mathematical
	 *         subtraction.
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		XSDouble val = (XSDouble) get_single_type(arg, XSDouble.class);

		return ResultSequenceFactory.create_new(new XSDouble(double_value()
				- val.double_value()));
	}

	/**
	 * Mathematical multiplication operator between this XSDouble and the
	 * supplied ResultSequence. Due to no numeric type promotion or conversion,
	 * the ResultSequence must be of type XSDouble.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an multiplication with
	 * @return A XSDouble consisting of the result of the mathematical
	 *         multiplication.
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		XSDouble val = (XSDouble) get_single_type(arg, XSDouble.class);
		return ResultSequenceFactory.create_new(new XSDouble(double_value()
				* val.double_value()));
	}

	/**
	 * Mathematical division operator between this XSDouble and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDouble.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an division with
	 * @return A XSDouble consisting of the result of the mathematical division.
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		XSDouble val = (XSDouble) get_single_type(arg, XSDouble.class);
		return ResultSequenceFactory.create_new(new XSDouble(double_value()
				/ val.double_value()));
	}

	/**
	 * Mathematical integer division operator between this XSDouble and the
	 * supplied ResultSequence. Due to no numeric type promotion or conversion,
	 * the ResultSequence must be of type XSDouble.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an integer division with
	 * @return A XSInteger consisting of the result of the mathematical integer
	 *         division.
	 */
	public ResultSequence idiv(ResultSequence arg) throws DynamicError {
		XSDouble val = (XSDouble) get_single_type(arg, XSDouble.class);

		if (val.zero())
			throw DynamicError.div_zero(null);
		
		return ResultSequenceFactory.create_new(new XSInteger(
				BigInteger.valueOf((int) (double_value() / val.double_value()))));
	}

	/**
	 * Mathematical modulus operator between this XSDouble and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSDouble.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a modulus with
	 * @return A XSDouble consisting of the result of the mathematical modulus.
	 */
	public ResultSequence mod(ResultSequence arg) throws DynamicError {
		XSDouble val = (XSDouble) get_single_type(arg, XSDouble.class);
		return ResultSequenceFactory.create_new(new XSDouble(double_value()
				% val.double_value()));
	}

	/**
	 * Negation of the number stored
	 * 
	 * @return A XSDouble representing the negation of this XSDecimal
	 */
	@Override
	public ResultSequence unary_minus() {
		return ResultSequenceFactory.create_new(new XSDouble(-1
				* double_value()));
	}

	// functions
	/**
	 * Absolutes the number stored
	 * 
	 * @return A XSDouble representing the absolute value of the number stored
	 */
	@Override
	public NumericType abs() {
		return new XSDouble(Math.abs(double_value()));
	}

	/**
	 * Returns the smallest integer greater than the number stored
	 * 
	 * @return A XSDouble representing the smallest integer greater than the
	 *         number stored
	 */
	@Override
	public NumericType ceiling() {
		return new XSDouble(Math.ceil(double_value()));
	}

	/**
	 * Returns the largest integer smaller than the number stored
	 * 
	 * @return A XSDouble representing the largest integer smaller than the
	 *         number stored
	 */
	@Override
	public NumericType floor() {
		return new XSDouble(Math.floor(double_value()));
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSDouble representing the closest long of the number stored.
	 */
	@Override
	public NumericType round() {
		return new XSDouble(Math.round(double_value()));
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSDouble representing the closest long of the number stored.
	 */
	@Override
	public NumericType round_half_to_even() {
		return new XSDouble(Math.rint(double_value()));
	}
}
