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

import java.util.*;

/**
 * A representation of the YearMonth datatype
 */
public class XSGYearMonth extends CalendarType implements CmpEq {

	private Calendar _calendar;
	private boolean _timezoned;

	/**
	 * Initialises a representation of the supplied year and month
	 * 
	 * @param cal
	 *            Calendar representation of the year and month to be stored
	 * @param tz
	 *            Timezone associated with this year and month
	 */
	public XSGYearMonth(Calendar cal, boolean tz) {
		_calendar = cal;
		_timezoned = tz;
	}

	/**
	 * Initialises a representation of the current year and month
	 */
	public XSGYearMonth() {
		this(new GregorianCalendar(), false);
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "gYearMonth" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "gYearMonth";
	}

	/**
	 * Parses a String representation of a year and month and constructs a new
	 * XSGYearMonth representation of it.
	 * 
	 * @param str
	 *            The String representation of the year and month (and optional
	 *            timezone)
	 * @return The XSGYearMonth representation of the supplied date
	 */
	public static XSGYearMonth parse_gYearMonth(String str) {
		// XXX

		String lame = "";
		String lame2 = "-01T00:00:00.0";
		boolean tz = false;

		int index = str.indexOf('+', 1);
		if (index == -1) {
			index = str.indexOf('-', 1);
			if (index == -1)
				return null;
			index = str.indexOf('-', index + 1);
		}
		if (index == -1)
			index = str.indexOf('Z', 1);
		if (index != -1) {
			lame = str.substring(0, index);
			lame += lame2;
			lame += str.substring(index, str.length());
			tz = true;
		} else {
			lame = str + lame2;
		}

		XSDateTime dt = XSDateTime.parseDateTime(lame);
		if (dt == null)
			return null;

		return new XSGYearMonth(dt.calendar(), tz);
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable gYearMonth in
	 * the supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which the gYearMonth is to be
	 *            extracted
	 * @return New ResultSequence consisting of the supplied year and month
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		XSGYearMonth val = parse_gYearMonth(aat.string_value());

		if (val == null)
			throw DynamicError.cant_cast(null);

		rs.add(val);

		return rs;
	}

	/**
	 * Retrieves the actual year as an integer
	 * 
	 * @return The actual year as an integer
	 */
	public int year() {
		int y = _calendar.get(Calendar.YEAR);
		if (_calendar.get(Calendar.ERA) == GregorianCalendar.BC)
			y *= -1;

		return y;
	}

	/**
	 * Retrieves the actual month as an integer
	 * 
	 * @return The actual month as an integer
	 */
	public int month() {
		return _calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * Check for whether a timezone was specified at creation
	 * 
	 * @return True if a timezone was specified. False otherwise
	 */
	public boolean timezoned() {
		return _timezoned;
	}

	/**
	 * Retrieves a String representation of the stored year and month
	 * 
	 * @return String representation of the stored year and month
	 */
	@Override
	public String string_value() {
		String ret = "";

		ret += XSDateTime.pad_int(year(), 4);

		ret += "-";
		ret += XSDateTime.pad_int(month(), 2);

		if (timezoned())
			ret += "Z";

		return ret;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:gYearMonth" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:gYearMonth";
	}

	/**
	 * Retrieves the Calendar representation of the year and month stored
	 * 
	 * @return Calendar representation of the year and month stored
	 */
	public Calendar calendar() {
		return _calendar;
	}

	/**
	 * Equality comparison between this and the supplied representation. This
	 * representation must be of type XSGYearMonth
	 * 
	 * @param arg
	 *            The XSGYearMonth to compare with
	 * @return True if the two representations are of the same year and month.
	 *         False otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType arg) throws DynamicError {
		XSGYearMonth val = (XSGYearMonth) NumericType.get_single_type(arg,
				XSGYearMonth.class);

		return calendar().equals(val.calendar());
	}
}
