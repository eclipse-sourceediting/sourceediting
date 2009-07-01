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

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;

import java.util.*;

/**
 * A representation of the gMonth datatype
 */
public class XSGYear extends CalendarType implements CmpEq {

	private Calendar _calendar;
	private boolean _timezoned;

	/**
	 * Initialises a representation of the supplied month
	 * 
	 * @param cal
	 *            Calendar representation of the month to be stored
	 * @param tz
	 *            Timezone associated with this month
	 */
	public XSGYear(Calendar cal, boolean tz) {
		_calendar = cal;
		_timezoned = tz;
	}

	/**
	 * Initialises a representation of the current year
	 */
	public XSGYear() {
		this(new GregorianCalendar(TimeZone.getTimeZone("GMT")), false);
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "gYear" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "gYear";
	}

	/**
	 * Parses a String representation of a year and constructs a new XSGYear
	 * representation of it.
	 * 
	 * @param str
	 *            The String representation of the year (and optional timezone)
	 * @return The XSGYear representation of the supplied date
	 */
	public static XSGYear parse_gYear(String str) {
		// XXX

		String lame = "";
		String lame2 = "-01-01T00:00:00.0";
		boolean tz = false;

		int index = str.indexOf('+', 1);
		if (index == -1)
			index = str.indexOf('-', 1);
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

		return new XSGYear(dt.calendar(), tz);
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable gYear in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which the gYear is to be extracted
	 * @return New ResultSequence consisting of the supplied year
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		XSGYear val = parse_gYear(aat.string_value());

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
	 * Check for whether a timezone was specified at creation
	 * 
	 * @return True if a timezone was specified. False otherwise
	 */
	public boolean timezoned() {
		return _timezoned;
	}

	/**
	 * Retrieves a String representation of the stored year
	 * 
	 * @return String representation of the stored year
	 */
	@Override
	public String string_value() {
		String ret = "";

		ret += XSDateTime.pad_int(year(), 4);

		if (timezoned())
			ret += "Z";

		return ret;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:gYear" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:gYear";
	}

	/**
	 * Retrieves the Calendar representation of the year stored
	 * 
	 * @return Calendar representation of the year stored
	 */
	public Calendar calendar() {
		return _calendar;
	}

	/**
	 * Equality comparison between this and the supplied representation. This
	 * representation must be of type XSGYear
	 * 
	 * @param arg
	 *            The XSGYear to compare with
	 * @return True if the two representations are of the same year. False
	 *         otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType arg) throws DynamicError {
		XSGYear val = (XSGYear) NumericType.get_single_type(arg, XSGYear.class);

		return calendar().equals(val.calendar());
	}
}
