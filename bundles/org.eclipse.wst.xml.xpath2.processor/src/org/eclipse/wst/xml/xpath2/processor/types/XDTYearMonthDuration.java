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
import org.eclipse.wst.xml.xpath2.processor.function.*;

/**
 * A representation of the YearMonthDuration datatype
 */
public class XDTYearMonthDuration extends XSDuration implements CmpEq, CmpLt,
		CmpGt,

		MathPlus, MathMinus, MathTimes, MathDiv {

	private int _year;
	private int _month;
	private boolean _negative;

	/**
	 * Initialises using the supplied parameters. If the number of months
	 * supplied is more than 12, the number of years is adjusted accordingly.
	 * 
	 * @param year
	 *            Number of years in this duration of time
	 * @param month
	 *            Number of months in this duration of time
	 * @param negative
	 *            True if this duration of time represents a backwards passage
	 *            through time. False otherwise
	 */
	public XDTYearMonthDuration(int year, int month, boolean negative) {
		_year = year;
		_month = month;
		_negative = negative;

		if (_month >= 12) {
			_year += (_month / 12);
			_month = _month % 12;
		}

	}

	/**
	 * Initialises to the given number of months
	 * 
	 * @param months
	 *            Number of months in the duration of time
	 */
	public XDTYearMonthDuration(int months) {
		this(0, Math.abs(months), months < 0);
	}

	/**
	 * Initialises to a duration of no time (0years and 0months)
	 */
	public XDTYearMonthDuration() {
		this(0, 0, false);
	}

	/**
	 * Creates a new XDTYearMonthDuration by parsing the supplied String
	 * represented duration of time
	 * 
	 * @param str
	 *            String represented duration of time
	 * @return New XDTYearMonthDuration representing the duration of time
	 *         supplied
	 */
	public static XDTYearMonthDuration parseYMDuration(String str) {
		boolean negative = false;
		int year = 0;
		int month = 0;

		int state = 0; // 0 beginning
		// 1 year
		// 2 month
		// 3 done
		// 4 expecting P
		// 5 expecting Y or M
		// 6 expecting M or end
		// 7 expecting end

		String digits = "";
		for (int i = 0; i < str.length(); i++) {
			char x = str.charAt(i);

			switch (state) {
			// beginning
			case 0:
				if (x == '-') {
					negative = true;
					state = 4;
				} else if (x == 'P')
					state = 5;
				else
					return null;
				break;

			case 4:
				if (x == 'P')
					state = 5;
				else
					return null;
				break;

			case 5:
				if ('0' <= x && x <= '9')
					digits += x;
				else if (x == 'Y') {
					if (digits.length() == 0)
						return null;
					year = Integer.parseInt(digits);
					digits = "";
					state = 6;
				} else if (x == 'M') {
					if (digits.length() == 0)
						return null;
					month = Integer.parseInt(digits);
					state = 7;
				} else
					return null;
				break;

			case 6:
				if ('0' <= x && x <= '9')
					digits += x;
				else if (x == 'M') {
					if (digits.length() == 0)
						return null;
					month = Integer.parseInt(digits);
					state = 7;

				} else
					return null;
				break;

			case 7:
				return null;

			default:
				assert false;
				return null;

			}
		}

		return new XDTYearMonthDuration(year, month, negative);
	}

	/**
	 * Retrives the datatype's name
	 * 
	 * @return "yearMonthDuration" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "yearMonthDuration";
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable time duration
	 * from the supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract
	 * @return New ResultSequence consisting of the time duration extracted
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		XDTYearMonthDuration ymd = parseYMDuration(aat.string_value());

		if (ymd == null)
			throw DynamicError.cant_cast(null);

		rs.add(ymd);

		return rs;
	}

	/**
	 * Retrieves whether this duration represents a backward passage through
	 * time
	 * 
	 * @return True if this duration represents a backward passage through time.
	 *         False otherwise
	 */
	public boolean negative() {
		return _negative;
	}

	/**
	 * Retrieves the number of years within the duration of time stored
	 * 
	 * @return Number of years within the duration of time stored
	 */
	public int year() {
		return _year;
	}

	/**
	 * Retrieves the number of months within the duration of time stored
	 * 
	 * @return Number of months within the duration of time stored
	 */
	public int month() {
		return _month;
	}

	/**
	 * Retrieves a String representation of the duration of time stored
	 * 
	 * @return String representation of the duration of time stored
	 */
	@Override
	public String string_value() {
		String strval = "";

		if (negative())
			strval += "-";

		strval += "P";

		int years = year();
		if (years != 0)
			strval += years + "Y";

		int months = month();
		if (months == 0) {
			if (years == 0)
				strval += months + "M";
		} else
			strval += months + "M";

		return strval;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xdt:yearMonthDuration" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xdt:yearMonthDuration";
	}

	/**
	 * Retrieves the duration of time stored as the number of months within it
	 * 
	 * @return Number of months making up this duration of time
	 */
	public int value() {
		int ret = year() * 12 + month();

		if (negative())
			ret *= -1;

		return ret;
	}

	/**
	 * Equality comparison between this and the supplied duration of time.
	 * 
	 * @param arg
	 *            The duration of time to compare with
	 * @return True if they both represent the duration of time. False otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType arg) throws DynamicError {
		XDTYearMonthDuration val = (XDTYearMonthDuration) NumericType
				.get_single_type(arg, XDTYearMonthDuration.class);

		return value() == val.value();
	}

	/**
	 * Comparison between this and the supplied duration of time.
	 * 
	 * @param arg
	 *            The duration of time to compare with
	 * @return True if the supplied time represents a larger duration than that
	 *         stored. False otherwise
	 * @throws DynamicError
	 */
	public boolean lt(AnyType arg) throws DynamicError {
		XDTYearMonthDuration val = (XDTYearMonthDuration) NumericType
				.get_single_type(arg, XDTYearMonthDuration.class);

		return value() < val.value();
	}

	/**
	 * Comparison between this and the supplied duration of time.
	 * 
	 * @param arg
	 *            The duration of time to compare with
	 * @return True if the supplied time represents a smaller duration than that
	 *         stored. False otherwise
	 * @throws DynamicError
	 */
	public boolean gt(AnyType arg) throws DynamicError {
		XDTYearMonthDuration val = (XDTYearMonthDuration) NumericType
				.get_single_type(arg, XDTYearMonthDuration.class);

		return value() > val.value();
	}

	/**
	 * Mathematical addition between this duration stored and the supplied
	 * duration of time (of type XDTYearMonthDuration)
	 * 
	 * @param arg
	 *            The duration of time to add
	 * @return New XDTYearMonthDuration representing the resulting duration
	 *         after the addition
	 * @throws DynamicError
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {
		XDTYearMonthDuration val = (XDTYearMonthDuration) NumericType
				.get_single_type(arg, XDTYearMonthDuration.class);

		int res = value() + val.value();

		return ResultSequenceFactory.create_new(new XDTYearMonthDuration(res));
	}

	/**
	 * Mathematical subtraction between this duration stored and the supplied
	 * duration of time (of type XDTYearMonthDuration)
	 * 
	 * @param arg
	 *            The duration of time to subtract
	 * @return New XDTYearMonthDuration representing the resulting duration
	 *         after the subtraction
	 * @throws DynamicError
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		XDTYearMonthDuration val = (XDTYearMonthDuration) NumericType
				.get_single_type(arg, XDTYearMonthDuration.class);

		int res = value() - val.value();

		return ResultSequenceFactory.create_new(new XDTYearMonthDuration(res));
	}

	/**
	 * Mathematical multiplication between this duration stored and the supplied
	 * duration of time (of type XDTYearMonthDuration)
	 * 
	 * @param arg
	 *            The duration of time to multiply by
	 * @return New XDTYearMonthDuration representing the resulting duration
	 *         after the multiplication
	 * @throws DynamicError
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		XSDouble val = (XSDouble) NumericType.get_single_type(arg,
				XSDouble.class);

		int res = (int) Math.round(value() * val.double_value());

		return ResultSequenceFactory.create_new(new XDTYearMonthDuration(res));
	}

	/**
	 * Mathematical division between this duration stored and the supplied
	 * duration of time (of type XDTYearMonthDuration)
	 * 
	 * @param arg
	 *            The duration of time to divide by
	 * @return New XDTYearMonthDuration representing the resulting duration
	 *         after the division
	 * @throws DynamicError
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		if (arg.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = arg.first();

		if (at instanceof XSDouble) {
			XSDouble dt = (XSDouble) at;

			int ret = 0;

			if (!dt.zero())
				ret = (int) Math.round(value() / dt.double_value());

			return ResultSequenceFactory.create_new(new XDTYearMonthDuration(
					ret));
		} else if (at instanceof XDTYearMonthDuration) {
			XDTYearMonthDuration md = (XDTYearMonthDuration) at;

			double res = (double) value() / md.value();

			return ResultSequenceFactory.create_new(new XSDecimal(res));
		} else {
			DynamicError.throw_type_error();
			return null; // unreach
		}
	}

}
