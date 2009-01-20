/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.types;

import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.function.*;

import java.util.*;
/**
 * A representation of the MonthDay datatype
 */
public class XSGMonthDay extends CalendarType implements CmpEq {
	
	private Calendar _calendar;
	private boolean  _timezoned;

	/**
	 * Initialises a representation of the supplied month and day
	 * @param cal Calendar representation of the month and day to be stored
	 * @param tz Timezone associated with this month and day
	 */
	public XSGMonthDay(Calendar cal, boolean tz) {
		_calendar = cal;
		_timezoned = tz;
	}
	/**
	 * Initialises a representation of the current month and day
	 */
	public XSGMonthDay() {
		this(new GregorianCalendar(), false);
	}
	/**
	 * Retrieves the datatype's name
	 * @return "gMonthDay" which is the datatype's name
	 */
	public String type_name() {
		return "gMonthDay";
	}
	/**
	 * Parses a String representation of a month and day and constructs a new
	 * XSGMonthDay representation of it.
	 * @param str The String representation of the month and day (and optional timezone)
	 * @return The XSGMonthDay representation of the supplied date
	 */
	public static XSGMonthDay parse_gMonthDay(String str) {
		// XXX

		String lame = "1983-";
		String lame2 = "T00:00:00.0";
		boolean tz = false;

		int index = str.indexOf('+', 0);
		if(index == -1) {
			index = str.indexOf('-', 0);
			if(index == -1)
				return null;
			index = str.indexOf('-', index + 1);
		}	
		if(index == -1)
			index = str.indexOf('Z', 0);
		if(index != -1) {
			lame += str.substring(0, index);
			lame += lame2;
			lame += str.substring(index, str.length());
			tz = true;
		}
		else {
			lame += str + lame2;
		}

		XSDateTime dt = XSDateTime.parseDateTime(lame);
		if(dt == null)
			return null;

		return new XSGMonthDay(dt.calendar(), tz);	
	}
	/**
	 * Creates a new ResultSequence consisting of the extractable gMonthDay in the supplied 
	 * ResultSequence
	 * @param arg The ResultSequence from which the gMonthDay is to be extracted
	 * @return New ResultSequence consisting of the supplied month and day
	 * @throws DynamicError
	 */
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
                ResultSequence rs = ResultSequenceFactory.create_new();
                                        
                if(arg.empty())
                        return rs;
                                        
                AnyAtomicType aat = (AnyAtomicType) arg.first();
                                        
                XSGMonthDay val = parse_gMonthDay(aat.string_value());
                                                        
                if(val == null)                 
                        throw DynamicError.cant_cast(null);
                                                
                rs.add(val);
                
		return rs;
	}
	/**
	 * Retrieves the actual month as an integer
	 * @return The actual month as an integer
	 */
	public int month() { 
		return _calendar.get(Calendar.MONTH) + 1; 
	}
		/**
		 * Retrieves the actual day as an integer
		 * @return The actual day as an integer
		 */
        public int day() { 
                return _calendar.get(Calendar.DAY_OF_MONTH);
        }

    /**
     * Check for whether a timezone was specified at creation
     * @return True if a timezone was specified. False otherwise
     */
	public boolean timezoned() { return _timezoned; }
	/**
	 * Retrieves a String representation of the stored month and day
	 * @return String representation of the stored month and day
	 */
	public String string_value() {
		String ret = "";

		ret += XSDateTime.pad_int(month(), 2);
		
		ret += "-";
		ret += XSDateTime.pad_int(day(), 2);

		if(timezoned())
			ret += "Z";

		return ret;
	}
	/**
	 * Retrieves the datatype's full pathname
	 * @return "xs:gMonthDay" which is the datatype's full pathname
	 */
	public String string_type() {
		return "xs:gMonthDay";
	}
	/**
	 * Retrieves the Calendar representation of the month and day stored
	 * @return Calendar representation of the month and day stored
	 */
	public Calendar calendar() {
		return _calendar;
	}

		/**
		 * Equality comparison between this and the supplied representation. This representation
		 * must be of type XSGMonthDay
		 * @param arg The XSGMonthDay to compare with
		 * @return True if the two representations are of the same month and day. False otherwise
		 * @throws DynamicError
		 */
        public boolean eq(AnyType arg) throws DynamicError {
                XSGMonthDay val = (XSGMonthDay)
                             NumericType.get_single_type(arg, XSGMonthDay.class);

                return calendar().equals(val.calendar());
        }
}
