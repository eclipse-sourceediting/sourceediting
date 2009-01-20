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
 * A representation of the Day datatype
 */
public class XSGDay extends CalendarType implements CmpEq {
	
	private Calendar _calendar;
	private boolean  _timezoned;

	/**
	 * Initialises a representation of the supplied day
	 * @param cal Calendar representation of the day to be stored
	 * @param tz Timezone associated with this day
	 */
	public XSGDay(Calendar cal, boolean tz) {
		_calendar = cal;
		_timezoned = tz;
	}
	/**
	 * Initialises a representation of the current day
	 */
	public XSGDay() {
		this(new GregorianCalendar(), false);
	}
	/**
	 * Retrieves the datatype's name
	 * @return "gDay" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "gDay";
	}
	/**
	 * Parses a String representation of a day and constructs a new
	 * XSGDay representation of it.
	 * @param str The String representation of the day (and optional timezone)
	 * @return The XSGDay representation of the supplied date
	 */
	public static XSGDay parse_gDay(String str) {
		// XXX

		String lame = "1983-11-";
		String lame2 = "T00:00:00.0";
		boolean tz = false;

		int index = str.indexOf('+', 0);
		if(index == -1) 
			index = str.indexOf('-', 0);
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

		return new XSGDay(dt.calendar(), tz);	
	}
	/**
	 * Creates a new ResultSequence consisting of the extractable gDay in the supplied 
	 * ResultSequence
	 * @param arg The ResultSequence from which the gDay is to be extracted
	 * @return New ResultSequence consisting of the supplied day
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
                ResultSequence rs = ResultSequenceFactory.create_new();
                                        
                if(arg.empty())
                        return rs;
                                        
                AnyAtomicType aat = (AnyAtomicType) arg.first();
                                        
                XSGDay val = parse_gDay(aat.string_value());
                                                        
                if(val == null)                 
                        throw DynamicError.cant_cast(null);
                                                
                rs.add(val);
                
		return rs;
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
	 * Retrieves a String representation of the stored day
	 * @return String representation of the stored day
	 */
	@Override
	public String string_value() {
		String ret = "";

		ret += XSDateTime.pad_int(day(), 2);

		if(timezoned())
			ret += "Z";

		return ret;
	}
	/**
	 * Retrieves the datatype's full pathname
	 * @return "xs:gDay" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:gDay";
	}
	/**
	 * Retrieves the Calendar representation of the day stored
	 * @return Calendar representation of the day stored
	 */
	public Calendar calendar() {
		return _calendar;
	}

		/**
		 * Equality comparison between this and the supplied representation. This representation
		 * must be of type XSGDay
		 * @param arg The XSGDay to compare with
		 * @return True if the two representations are of the same day. False otherwise
		 * @throws DynamicError
		 */
        public boolean eq(AnyType arg) throws DynamicError {
                XSGDay val = (XSGDay) NumericType.get_single_type(arg, XSGDay.class);

                return calendar().equals(val.calendar());
        }
}
