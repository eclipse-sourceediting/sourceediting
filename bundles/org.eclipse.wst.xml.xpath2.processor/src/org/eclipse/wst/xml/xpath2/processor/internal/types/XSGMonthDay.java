/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver (STAR) - bug 262765 - Fix parsing of gMonthDay to valid date 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;

import java.util.*;

/**
 * A representation of the MonthDay datatype
 */
public class XSGMonthDay extends CalendarType implements CmpEq {

	private Calendar _calendar;
	private boolean _timezoned;

	/**
	 * Initialises a representation of the supplied month and day
	 * 
	 * @param cal
	 *            Calendar representation of the month and day to be stored
	 * @param tz
	 *            Timezone associated with this month and day
	 */
	public XSGMonthDay(Calendar cal, boolean tz) {
		_calendar = cal;
		_timezoned = tz;
	}

	/**
	 * Initialises a representation of the current month and day
	 */
	public XSGMonthDay() {
		this(new GregorianCalendar(TimeZone.getTimeZone("GMT")), false);
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "gMonthDay" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "gMonthDay";
	}

	/**
	 * Parses a String representation of a month and day and constructs a new
	 * XSGMonthDay representation of it.
	 * 
	 * @param str
	 *            The String representation of the month and day (and optional
	 *            timezone)
	 * @return The XSGMonthDay representation of the supplied date
	 */
	public static XSGMonthDay parse_gMonthDay(String str) {

		String startdate = "1972-";
		String starttime = "T00:00:00";
		boolean tz = false;

		int index = str.lastIndexOf('+', str.length());
		
		if (index == -1)
			index = str.lastIndexOf('-');
		if (index == -1)
			index = str.lastIndexOf('Z', str.length());
		if (index != -1) {
			int zIndex = str.lastIndexOf('Z', str.length());
			
			String[] split = str.split("-");
			startdate += split[2].replace("Z", "") + "-" + split[3].replace("Z", "");
			
			if (split.length > 4) {
				String[] timesplit = split[4].split(":");
				if (timesplit.length < 3) {
					starttime = "T";
					for (int cnt = 0; cnt < timesplit.length; cnt++) {
						starttime += timesplit[cnt] + ":";
					}
					starttime += "00";
				} else {
					starttime += timesplit[0] + ":" + timesplit[1] + ":" + timesplit[2];
				}
			}
			startdate = startdate.trim();
			startdate += starttime;

			if (zIndex != -1) {
				startdate += str.substring(zIndex);
				tz = true;
			}
		} else {
			startdate += str + starttime;
		}

		XSDateTime dt = XSDateTime.parseDateTime(startdate);
		if (dt == null)
			return null;

		return new XSGMonthDay(dt.calendar(), tz);
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable gMonthDay in
	 * the supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which the gMonthDay is to be extracted
	 * @return New ResultSequence consisting of the supplied month and day
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		XSGMonthDay val = parse_gMonthDay(aat.string_value());

		if (val == null)
			throw DynamicError.cant_cast(null);

		rs.add(val);

		return rs;
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
	 * Retrieves the actual day as an integer
	 * 
	 * @return The actual day as an integer
	 */
	public int day() {
		return _calendar.get(Calendar.DAY_OF_MONTH);
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
	 * Retrieves a String representation of the stored month and day
	 * 
	 * @return String representation of the stored month and day
	 */
	@Override
	public String string_value() {
		String ret = "";

		ret += XSDateTime.pad_int(month(), 2);

		ret += "-";
		ret += XSDateTime.pad_int(day(), 2);

		if (timezoned())
			ret += "Z";

		return ret;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:gMonthDay" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:gMonthDay";
	}

	/**
	 * Retrieves the Calendar representation of the month and day stored
	 * 
	 * @return Calendar representation of the month and day stored
	 */
	public Calendar calendar() {
		return _calendar;
	}

	/**
	 * Equality comparison between this and the supplied representation. This
	 * representation must be of type XSGMonthDay
	 * 
	 * @param arg
	 *            The XSGMonthDay to compare with
	 * @return True if the two representations are of the same month and day.
	 *         False otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType arg) throws DynamicError {
		XSGMonthDay val = (XSGMonthDay) NumericType.get_single_type(arg,
				XSGMonthDay.class);

		return calendar().equals(val.calendar());
	}
}
