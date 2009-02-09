/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.types;

import org.eclipse.wst.xml.xpath2.processor.*;

/**
 * A representation of the Float datatype
 */
public class XSFloat extends NumericType {

	private float _value;

	/**
	 * Initiates a representation of the supplied number
	 * 
	 * @param x
	 *            The number to be stored
	 */
	public XSFloat(float x) {
		_value = x;
	}

	/**
	 * Initiates a representation of 0
	 */
	public XSFloat() {
		this(0);
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:float" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:float";
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "float" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "float";
	}

	/**
	 * Retrieves a String representation of the stored number
	 * 
	 * @return String representation of the stored number
	 */
	@Override
	public String string_value() {
		return "" + _value;
	}

	/**
	 * Check for whether this datatype represents NaN
	 * 
	 * @return True is this datatype represents NaN. False otherwise
	 */
	public boolean nan() {
		return _value == Float.NaN;
	}

	/**
	 * Check for whether this datatype represents 0
	 * 
	 * @return True if this datatype represents 0. False otherwise
	 */
	@Override
	public boolean zero() {
		return _value == 0.0;
	}

	/**
	 * Creates a new ResultSequence consisting of the retrievable float in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract the float
	 * @return New ResultSequence consisting of the float supplied
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		try {
			Float f = new Float(aat.string_value());
			rs.add(new XSFloat(f.floatValue()));
			return rs;
		} catch (NumberFormatException e) {
			throw DynamicError.cant_cast(null);
		}

	}

	/**
	 * Retrieves the actual float value stored
	 * 
	 * @return The actual float value stored
	 */
	public float float_value() {
		return _value;
	}

	/**
	 * Equality comparison between this number and the supplied representation.
	 * Currently no numeric type promotion exists so this representation must be
	 * of type XSFloat
	 * 
	 * @param aa
	 *            The datatype to compare with
	 * @return True if the two representations are of the same number. False
	 *         otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType aa) throws DynamicError {
		// XXX numeric promotion
		if (!(aa instanceof XSFloat))
			DynamicError.throw_type_error();

		XSFloat f = (XSFloat) aa;

		return float_value() == f.float_value();
	}

	/**
	 * Comparison between this number and the supplied representation. Currently
	 * no numeric type promotion is implemented so this representation must be
	 * of type XSFloat
	 * 
	 * @param arg
	 *            The datatype to compare with
	 * @return True if the supplied representation is a smaller number than the
	 *         one stored. False otherwise
	 * @throws DynamicError
	 */
	public boolean gt(AnyType arg) throws DynamicError {
		XSFloat val = (XSFloat) get_single_type(arg, XSFloat.class);
		return float_value() > val.float_value();
	}

	/**
	 * Comparison between this number and the supplied representation. Currently
	 * no numeric type promotion is implemented so this representation must be
	 * of type XSFloat
	 * 
	 * @param arg
	 *            The datatype to compare with
	 * @return True if the supplied representation is a greater number than the
	 *         one stored. False otherwise
	 * @throws DynamicError
	 */
	public boolean lt(AnyType arg) throws DynamicError {
		XSFloat val = (XSFloat) get_single_type(arg, XSFloat.class);
		return float_value() < val.float_value();
	}

	/**
	 * Mathematical addition operator between this XSFloat and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSFloat.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an addition with
	 * @return A XSFloat consisting of the result of the mathematical addition.
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {

		AnyType at = get_single_arg(arg);
		if (!(at instanceof XSFloat))
			DynamicError.throw_type_error();
		XSFloat val = (XSFloat) at;

		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				+ val.float_value()));
	}

	/**
	 * Mathematical subtraction operator between this XSFloat and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSFloat.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a subtraction with
	 * @return A XSFloat consisting of the result of the mathematical
	 *         subtraction.
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		AnyType at = get_single_arg(arg);
		if (!(at instanceof XSFloat))
			DynamicError.throw_type_error();
		XSFloat val = (XSFloat) at;

		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				- val.float_value()));
	}

	/**
	 * Mathematical multiplication operator between this XSFloat and the
	 * supplied ResultSequence. Due to no numeric type promotion or conversion,
	 * the ResultSequence must be of type XSFloat.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a multiplication with
	 * @return A XSFloat consisting of the result of the mathematical
	 *         multiplication.
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		XSFloat val = (XSFloat) get_single_type(arg, XSFloat.class);
		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				* val.float_value()));
	}

	/**
	 * Mathematical division operator between this XSFloat and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSFloat.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a division with
	 * @return A XSFloat consisting of the result of the mathematical division.
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		XSFloat val = (XSFloat) get_single_type(arg, XSFloat.class);
		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				/ val.float_value()));
	}

	/**
	 * Mathematical integer division operator between this XSFloat and the
	 * supplied ResultSequence. Due to no numeric type promotion or conversion,
	 * the ResultSequence must be of type XSFloat.
	 * 
	 * @param arg
	 *            The ResultSequence to perform an integer division with
	 * @return A XSInteger consisting of the result of the mathematical integer
	 *         division.
	 */
	public ResultSequence idiv(ResultSequence arg) throws DynamicError {
		XSFloat val = (XSFloat) get_single_type(arg, XSFloat.class);

		if (val.zero())
			throw DynamicError.div_zero(null);
		return ResultSequenceFactory.create_new(new XSInteger(
				(int) (float_value() / val.float_value())));
	}

	/**
	 * Mathematical modulus operator between this XSFloat and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSFloat.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a modulus with
	 * @return A XSFloat consisting of the result of the mathematical modulus.
	 */
	public ResultSequence mod(ResultSequence arg) throws DynamicError {
		XSFloat val = (XSFloat) get_single_type(arg, XSFloat.class);
		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				% val.float_value()));
	}

	/**
	 * Negates the number stored
	 * 
	 * @return A XSFloat representing the negation of the number stored
	 */
	@Override
	public ResultSequence unary_minus() {
		return ResultSequenceFactory
				.create_new(new XSFloat(-1 * float_value()));
	}

	/**
	 * Absolutes the number stored
	 * 
	 * @return A XSFloat representing the absolute value of the number stored
	 */
	@Override
	public NumericType abs() {
		return new XSFloat(Math.abs(float_value()));
	}

	/**
	 * Returns the smallest integer greater than the number stored
	 * 
	 * @return A XSFloat representing the smallest integer greater than the
	 *         number stored
	 */
	@Override
	public NumericType ceiling() {
		return new XSFloat((float) Math.ceil(float_value()));
	}

	/**
	 * Returns the largest integer smaller than the number stored
	 * 
	 * @return A XSFloat representing the largest integer smaller than the
	 *         number stored
	 */
	@Override
	public NumericType floor() {
		return new XSFloat((float) Math.floor(float_value()));
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSFloat representing the closest long of the number stored.
	 */
	@Override
	public NumericType round() {
		return new XSFloat(Math.round(float_value()));
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSFloat representing the closest long of the number stored.
	 */
	@Override
	public NumericType round_half_to_even() {
		return new XSFloat((float) Math.rint(float_value()));
	}
}
