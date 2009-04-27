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
public class XSGMonth extends CalendarType implements CmpEq {

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
	public XSGMonth(Calendar cal, boolean tz) {
		_calendar = cal;
		_timezoned = tz;
	}

	/**
	 * Initialises a representation of the current month
	 */
	public XSGMonth() {
		this(new GregorianCalendar(), false);
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "gMonth" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "gMonth";
	}

	/**
	 * Parses a String representation of a month and constructs a new XSGMonth
	 * representation of it.
	 * 
	 * @param str
	 *            The String representation of the month (and optional timezone)
	 * @return The XSGMonth representation of the supplied date
	 */
	public static XSGMonth parse_gMonth(String str) {
		// XXX

		String lame = "1983-";
		String lame2 = "-29T00:00:00.0";
		boolean tz = false;

		int index = str.indexOf('+', 0);
		if (index == -1)
			index = str.indexOf('-', 0);
		if (index == -1)
			index = str.indexOf('Z', 0);
		if (index != -1) {
			lame += str.substring(0, index);
			lame += lame2;
			lame += str.substring(index, str.length());
			tz = true;
		} else {
			lame += str + lame2;
		}

		XSDateTime dt = XSDateTime.parseDateTime(lame);
		if (dt == null)
			return null;

		return new XSGMonth(dt.calendar(), tz);
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable gMonth in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which the gMonth is to be extracted
	 * @return New ResultSequence consisting of the supplied month
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyAtomicType aat = (AnyAtomicType) arg.first();

		XSGMonth val = parse_gMonth(aat.string_value());

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
	 * Check for whether a timezone was specified at creation
	 * 
	 * @return True if a timezone was specified. False otherwise
	 */
	public boolean timezoned() {
		return _timezoned;
	}

	/**
	 * Retrieves a String representation of the stored month
	 * 
	 * @return String representation of the stored month
	 */
	@Override
	public String string_value() {
		String ret = "";

		ret += XSDateTime.pad_int(month(), 2);

		if (timezoned())
			ret += "Z";

		return ret;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:gMonth" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:gMonth";
	}

	/**
	 * Retrieves the Calendar representation of the month stored
	 * 
	 * @return Calendar representation of the month stored
	 */
	public Calendar calendar() {
		return _calendar;
	}

	/**
	 * Equality comparison between this and the supplied representation. This
	 * representation must be of type XSGMonth
	 * 
	 * @param arg
	 *            The XSGMonth to compare with
	 * @return True if the two representations are of the same month. False
	 *         otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType arg) throws DynamicError {
		XSGMonth val = (XSGMonth) NumericType.get_single_type(arg,
				XSGMonth.class);

		return calendar().equals(val.calendar());
	}
}
