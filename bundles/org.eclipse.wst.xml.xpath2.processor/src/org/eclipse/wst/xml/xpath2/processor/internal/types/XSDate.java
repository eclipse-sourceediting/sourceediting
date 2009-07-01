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
 *     Mukul Gandhi - bug 274792 - improvements to xs:date constructor function. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;

import java.util.*;

/**
 * Representation of a date of the form year-month-day and optional timezone
 */
public class XSDate extends CalendarType implements CmpEq, CmpLt, CmpGt,

MathMinus, MathPlus,

Cloneable {
	private Calendar _calendar;
	private boolean _timezoned;
	private XSDayTimeDuration _tz;

	/**
	 * Initialises a new represenation of a supplied date
	 * 
	 * @param cal
	 *            The Calendar representation of the date to be stored
	 * @param tz
	 *            The timezone of the date to be stored.
	 */
	public XSDate(Calendar cal, XSDayTimeDuration tz) {
		_calendar = cal;

		_tz = tz;
		if (tz == null)
			_timezoned = false;
		else
			_timezoned = true;
	}

	/**
	 * Initialises a new representation of the current date
	 */
	public XSDate() {
		this(new GregorianCalendar(TimeZone.getTimeZone("GMT")), null);
	}

	/**
	 * Retrieves the datatype name
	 * 
	 * @return "date" which is the dataype name
	 */
	@Override
	public String type_name() {
		return "date";
	}

	/**
	 * Creates a copy of this date representation
	 * 
	 * @return A copy of this date representation
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Calendar c = (Calendar) calendar().clone();
		XSDayTimeDuration t = tz();

		if (t != null)
			t = (XSDayTimeDuration) t.clone();

		return new XSDate(c, t);
	}

	/**
	 * Parses a String representation of a date (of the form year-month-day or
	 * year-month-day+timezone) and constructs a new XSDate representation of
	 * it.
	 * 
	 * @param str
	 *            The String representation of the date (and optional timezone)
	 * @return The XSDate representation of the supplied date
	 */
	public static XSDate parse_date(String str) {
		// XXX
		// sorry kids... but i don't wanna go through all the mess
		// again...
		// i guess the ends justify the means...
		// not really =P

		String lame = "";
		String lame2 = "T00:00:00.0";
		boolean tz = false;

		int index = str.indexOf('+', 1);
		if (index == -1) {
			index = str.indexOf('-', 1);
			if (index == -1)
				return null;
			index = str.indexOf('-', index + 1);
			if (index == -1)
				return null;
			index = str.indexOf('-', index + 1);
		}
		if (index == -1)
			index = str.indexOf('Z', 1);
		if (index != -1) {
			lame = str.substring(0, index);
			// here we go
			lame += lame2;
			lame += str.substring(index, str.length());
			tz = true;
		} else {
			lame = str + lame2;
		}

		// sorry again =D
		XSDateTime dt = XSDateTime.parseDateTime(lame);
		if (dt == null)
			return null;

		return new XSDate(dt.calendar(), dt.tz());
	}

	/**
	 * Creates a new result sequence consisting of the retrievable date value in
	 * the supplied result sequence
	 * 
	 * @param arg
	 *            The result sequence from which to extract the date value.
	 * @throws DynamicError
	 * @return A new result sequence consisting of the date value supplied.
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		// function conversion rules apply here also.
		// for whatever be argument atype, get it's string value,
		// and ensure that the string value is a valid date string.
		AnyType aat = arg.first();
		
		XSDate dt = parse_date(aat.string_value());

		if (dt == null)
			throw DynamicError.cant_cast(null);

		rs.add(dt);

		return rs;
	}

	/**
	 * Retrieve the year from the date stored
	 * 
	 * @return the year value of the date stored
	 */
	public int year() {
		int y = _calendar.get(Calendar.YEAR);
		if (_calendar.get(Calendar.ERA) == GregorianCalendar.BC)
			y *= -1;

		return y;
	}

	/**
	 * Retrieve the month from the date stored
	 * 
	 * @return the month value of the date stored
	 */
	public int month() {
		return _calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * Retrieve the day from the date stored
	 * 
	 * @return the day value of the date stored
	 */
	public int day() {
		return _calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Retrieves whether this date has an optional timezone associated with it
	 * 
	 * @return True if there is a timezone associated with this date. False
	 *         otherwise.
	 */
	public boolean timezoned() {
		return _timezoned;
	}

	/**
	 * Retrieves a String representation of the date stored
	 * 
	 * @return String representation of the date stored
	 */
	@Override
	public String string_value() {
		String ret = "";

		ret += XSDateTime.pad_int(year(), 4);

		ret += "-";
		ret += XSDateTime.pad_int(month(), 2);

		ret += "-";
		ret += XSDateTime.pad_int(day(), 2);

		if (timezoned())
			ret += "Z";

		return ret;
	}

	/**
	 * Retrive the datatype full pathname
	 * 
	 * @return "xs:date" which is the datatype full pathname
	 */
	@Override
	public String string_type() {
		return "xs:date";
	}

	/**
	 * Retrieves the Calendar representation of the date stored
	 * 
	 * @return Calendar representation of the date stored
	 */
	public Calendar calendar() {
		return _calendar;
	}

	/**
	 * Retrieves the timezone associated with the date stored
	 * 
	 * @return the timezone associated with the date stored
	 */
	public XSDayTimeDuration tz() {
		return _tz;
	}

	// comparisons
	/**
	 * Equality comparison on this and the supplied dates (taking timezones into
	 * account)
	 * 
	 * @param arg
	 *            XSDate representation of the date to compare to
	 * @throws DynamicError
	 * @return True if the two dates are represent the same exact point in time.
	 *         False otherwise.
	 */
	public boolean eq(AnyType arg) throws DynamicError {
		XSDate val = (XSDate) NumericType.get_single_type(arg, XSDate.class);

		return calendar().equals(val.calendar());
	}

	/**
	 * Comparison on this and the supplied dates (taking timezones into account)
	 * 
	 * @param arg
	 *            XSDate representation of the date to compare to
	 * @throws DynamicError
	 * @return True if in time, this date lies before the date supplied. False
	 *         otherwise.
	 */
	public boolean lt(AnyType arg) throws DynamicError {
		XSDate val = (XSDate) NumericType.get_single_type(arg, XSDate.class);

		return calendar().before(val.calendar());
	}

	/**
	 * Comparison on this and the supplied dates (taking timezones into account)
	 * 
	 * @param arg
	 *            XSDate representation of the date to compare to
	 * @throws DynamicError
	 * @return True if in time, this date lies after the date supplied. False
	 *         otherwise.
	 */
	public boolean gt(AnyType arg) throws DynamicError {
		XSDate val = (XSDate) NumericType.get_single_type(arg, XSDate.class);

		return calendar().after(val.calendar());
	}

	// XXX this is incorrect [epoch]
	/**
	 * Currently unsupported method. Retrieves the date in milliseconds since
	 * the begining of epoch
	 * 
	 * @return Number of milliseconds since the begining of the epoch
	 */
	public double value() {
		return calendar().getTimeInMillis() / 1000.0;
	}

	// math
	/**
	 * Mathematical minus operator between this XSDate and a supplied result
	 * sequence (XSDate, XDTYearMonthDuration and XDTDayTimeDuration are only
	 * valid ones).
	 * 
	 * @param arg
	 *            The supplied ResultSequence that is on the right of the minus
	 *            operator. If this is an XSDate, the result will be a
	 *            XDTDayTimeDuration of the duration of time between these two
	 *            dates. If arg is an XDTYearMonthDuration or an
	 *            XDTDayTimeDuration the result will be a XSDate of the result
	 *            of the current date minus the duration of time supplied.
	 * @return New ResultSequence consisting of the result of the mathematical
	 *         minus operation.
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		if (arg.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = arg.first();
		try {
			if (at instanceof XSDate) {
				XSDate val = (XSDate) NumericType.get_single_type(arg,
						XSDate.class);

				double res = value() - val.value();

				return ResultSequenceFactory.create_new(new XSDayTimeDuration(
						res));
			} else if (at instanceof XSYearMonthDuration) {
				XSYearMonthDuration val = (XSYearMonthDuration) at;

				XSDate res = (XSDate) clone();

				res.calendar().add(Calendar.MONTH, val.value() * -1);
				return ResultSequenceFactory.create_new(res);

			} else if (at instanceof XSDayTimeDuration) {
				XSDayTimeDuration val = (XSDayTimeDuration) at;

				XSDate res = (XSDate) clone();

				res.calendar().add(Calendar.MILLISECOND,
						(int) (val.value() * -1000.0));
				return ResultSequenceFactory.create_new(res);
			} else {
				DynamicError.throw_type_error();
				return null; // unreach
			}

		} catch (CloneNotSupportedException err) {
			assert false;
			return null;
		}
	}

	/**
	 * Mathematical addition operator between this XSDate and a supplied result
	 * sequence (XDTYearMonthDuration and XDTDayTimeDuration are only valid
	 * ones).
	 * 
	 * @param arg
	 *            The supplied ResultSequence that is on the right of the minus
	 *            operator. If arg is an XDTYearMonthDuration or an
	 *            XDTDayTimeDuration the result will be a XSDate of the result
	 *            of the current date minus the duration of time supplied.
	 * @return New ResultSequence consisting of the result of the mathematical
	 *         minus operation.
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {
		if (arg.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = arg.first();

		try {
			if (at instanceof XSYearMonthDuration) {
				XSYearMonthDuration val = (XSYearMonthDuration) at;

				XSDate res = (XSDate) clone();

				res.calendar().add(Calendar.MONTH, val.value());
				return ResultSequenceFactory.create_new(res);
			} else if (at instanceof XSDayTimeDuration) {
				XSDayTimeDuration val = (XSDayTimeDuration) at;

				XSDate res = (XSDate) clone();

				res.calendar().add(Calendar.MILLISECOND,
						(int) (val.value() * 1000.0));
				return ResultSequenceFactory.create_new(res);
			} else {
				DynamicError.throw_type_error();
				return null; // unreach
			}
		} catch (CloneNotSupportedException err) {
			assert false;
			return null;
		}

	}

}
