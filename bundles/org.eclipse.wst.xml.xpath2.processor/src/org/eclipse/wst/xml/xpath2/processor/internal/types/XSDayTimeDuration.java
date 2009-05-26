/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 273760 - wrong namespace for functions and data types 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;

/**
 * A representation of the DayTimeDuration datatype
 */
public class XSDayTimeDuration extends XSDuration implements CmpEq, CmpLt,
		CmpGt,

		MathPlus, MathMinus, MathTimes, MathDiv,

		Cloneable {

	private int _days;
	private int _hours;
	private int _minutes;
	private double _seconds;
	private boolean _negative;

	/**
	 * Initialises to the supplied parameters. If more than 24 hours is
	 * supplied, the number of days is adjusted acordingly. The same occurs for
	 * minutes and seconds
	 * 
	 * @param days
	 *            Number of days in this duration of time
	 * @param hours
	 *            Number of hours in this duration of time
	 * @param minutes
	 *            Number of minutes in this duration of time
	 * @param seconds
	 *            Number of seconds in this duration of time
	 * @param negative
	 *            True if this duration of time represents a backwards passage
	 *            through time. False otherwise
	 */
	public XSDayTimeDuration(int days, int hours, int minutes, double seconds,
			boolean negative) {
		_days = days;
		_hours = hours;
		_minutes = minutes;
		_seconds = seconds;
		_negative = negative;

		if (_seconds >= 60) {
			int isec = (int) _seconds;
			double rem = _seconds - (isec);

			_minutes += isec / 60;
			_seconds = isec % 60;
			_seconds += rem;
		}
		if (_minutes >= 60) {
			_hours += _minutes / 60;
			_minutes = _minutes % 60;
		}
		if (_hours >= 24) {
			_days += _hours / 24;
			_hours = _hours % 24;
		}

	}

	/**
	 * Initialises to the given number of seconds
	 * 
	 * @param secs
	 *            Number of seconds in the duration of time
	 */
	public XSDayTimeDuration(double secs) {
		this(0, 0, 0, Math.abs(secs), secs < 0);
	}

	/**
	 * Initialises to a duration of no time (0days, 0hours, 0minutes, 0seconds)
	 */
	public XSDayTimeDuration() {
		this(0, 0, 0, 0.0, false);
	}

	/**
	 * Creates a copy of this representation of a time duration
	 * 
	 * @return New XDTDayTimeDuration representing the duration of time stored
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new XSDayTimeDuration(days(), hours(), minutes(), seconds(),
				negative());
	}

	/**
	 * Creates a new XDTDayTimeDuration by parsing the supplied String
	 * represented duration of time
	 * 
	 * @param str
	 *            String represented duration of time
	 * @return New XDTDayTimeDuration representing the duration of time supplied
	 */
	public static XSDayTimeDuration parseDTDuration(String str) {
		boolean negative = false;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		double seconds = 0;

		// string following the P
		String pstr = null;
		String tstr = null;

		// get the negative and pstr
		if (str.startsWith("-P")) {
			negative = true;
			pstr = str.substring(2, str.length());
		} else if (str.startsWith("P")) {
			negative = false;
			pstr = str.substring(1, str.length());
		} else
			return null;

		try {
			// get the days
			int index = pstr.indexOf('D');
			boolean did_something = false;

			// no D... must have T
			if (index == -1) {
				if (pstr.startsWith("T")) {
					tstr = pstr.substring(1, pstr.length());
				} else
					return null;
			} else {
				String digit = pstr.substring(0, index);
				days = Integer.parseInt(digit);
				tstr = pstr.substring(index + 1, pstr.length());

				if (tstr.startsWith("T")) {
					tstr = tstr.substring(1, tstr.length());
				} else {
					if (tstr.length() > 0)
						return null;
					tstr = "";
					did_something = true;
				}
			}

			// do the T str

			// hour
			index = tstr.indexOf('H');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				hours = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}
			// minute
			index = tstr.indexOf('M');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				minutes = Integer.parseInt(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}
			// seconds
			index = tstr.indexOf('S');
			if (index != -1) {
				String digit = tstr.substring(0, index);
				seconds = Double.parseDouble(digit);
				tstr = tstr.substring(index + 1, tstr.length());
				did_something = true;
			}
			if (did_something) {
				// make sure we parsed it all
				if (tstr.length() != 0)
					return null;
			} else {
				return null;
			}

		} catch (NumberFormatException err) {
			return null;
		}

		return new XSDayTimeDuration(days, hours, minutes, seconds, negative);
	}

	/**
	 * Retrives the datatype's name
	 * 
	 * @return "dayTimeDuration" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "dayTimeDuration";
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

		XSDayTimeDuration dtd = parseDTDuration(aat.string_value());

		if (dtd == null)
			throw DynamicError.cant_cast(null);

		rs.add(dtd);

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
	 * Retrieves the number of days within the duration of time stored
	 * 
	 * @return Number of days within the duration of time stored
	 */
	public int days() {
		return _days;
	}

	/**
	 * Retrieves the number of minutes (max 60) within the duration of time
	 * stored
	 * 
	 * @return Number of minutes within the duration of time stored
	 */
	public int minutes() {
		return _minutes;
	}

	/**
	 * Retrieves the number of hours (max 24) within the duration of time stored
	 * 
	 * @return Number of hours within the duration of time stored
	 */
	public int hours() {
		return _hours;
	}

	/**
	 * Retrieves the number of seconds (max 60) within the duration of time
	 * stored
	 * 
	 * @return Number of seconds within the duration of time stored
	 */
	public double seconds() {
		return _seconds;
	}

	/**
	 * Retrieves a String representation of the duration of time stored
	 * 
	 * @return String representation of the duration of time stored
	 */
	@Override
	public String string_value() {
		String ret = "";
		boolean did_something = false; // this should be constant ;D
		String tret = "";

		if (negative())
			ret += "-";

		ret += "P";

		if (days() != 0) {
			ret += days() + "D";
			did_something = true;
		}

		// do the "time" bit
		if (hours() != 0) {
			tret += hours() + "H";
			did_something = true;
		}
		if (minutes() != 0) {
			tret += minutes() + "M";
			did_something = true;
		}
		if (seconds() != 0) {
			tret += seconds() + "S";
			did_something = true;
		} else if (!did_something) {
			tret += "0" + "S";
		}

		if (tret.length() > 0)
			ret += "T" + tret;

		return ret;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xdt:dayTimeDuration" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xdt:dayTimeDuration";
	}

	/**
	 * Retrieves the duration of time stored as the number of seconds within it
	 * 
	 * @return Number of seconds making up this duration of time
	 */
	public double value() {
		double ret = days() * 24 * 60 * 60;

		ret += hours() * 60 * 60;
		ret += minutes() * 60;
		ret += seconds();

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
		XSDayTimeDuration val = (XSDayTimeDuration) NumericType
				.get_single_type(arg, XSDayTimeDuration.class);

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
		XSDayTimeDuration val = (XSDayTimeDuration) NumericType
				.get_single_type(arg, XSDayTimeDuration.class);

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
		XSDayTimeDuration val = (XSDayTimeDuration) NumericType
				.get_single_type(arg, XSDayTimeDuration.class);

		return value() > val.value();
	}

	/**
	 * Mathematical addition between this duration stored and the supplied
	 * duration of time (of type XDTDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to add
	 * @return New XDTDayTimeDuration representing the resulting duration after
	 *         the addition
	 * @throws DynamicError
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {
		XSDayTimeDuration val = (XSDayTimeDuration) NumericType
				.get_single_type(arg, XSDayTimeDuration.class);

		double res = value() + val.value();

		return ResultSequenceFactory.create_new(new XSDayTimeDuration(res));
	}

	/**
	 * Mathematical subtraction between this duration stored and the supplied
	 * duration of time (of type XDTDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to subtract
	 * @return New XDTDayTimeDuration representing the resulting duration after
	 *         the subtraction
	 * @throws DynamicError
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		XSDayTimeDuration val = (XSDayTimeDuration) NumericType
				.get_single_type(arg, XSDayTimeDuration.class);

		double res = value() - val.value();

		return ResultSequenceFactory.create_new(new XSDayTimeDuration(res));
	}

	/**
	 * Mathematical multiplication between this duration stored and the supplied
	 * duration of time (of type XDTDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to multiply by
	 * @return New XDTDayTimeDuration representing the resulting duration after
	 *         the multiplication
	 * @throws DynamicError
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		XSDouble val = (XSDouble) NumericType.get_single_type(arg,
				XSDouble.class);

		double res = value() * val.double_value();

		return ResultSequenceFactory.create_new(new XSDayTimeDuration(res));
	}

	/**
	 * Mathematical division between this duration stored and the supplied
	 * duration of time (of type XDTDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to divide by
	 * @return New XDTDayTimeDuration representing the resulting duration after
	 *         the division
	 * @throws DynamicError
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		if (arg.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = arg.first();

		if (at instanceof XSDouble) {
			XSDouble dt = (XSDouble) at;
			double ret = 0;

			if (!dt.zero())
				ret = value() / dt.double_value();

			return ResultSequenceFactory
					.create_new(new XSDayTimeDuration(ret));
		} else if (at instanceof XSDayTimeDuration) {
			XSDayTimeDuration md = (XSDayTimeDuration) at;

			double res = value() / md.value();

			return ResultSequenceFactory.create_new(new XSDecimal(BigDecimal.valueOf(res)));
		} else {
			DynamicError.throw_type_error();
			return null; // unreach
		}
	}

}
