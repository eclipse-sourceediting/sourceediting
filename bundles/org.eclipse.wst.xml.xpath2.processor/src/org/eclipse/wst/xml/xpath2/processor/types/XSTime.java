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
 * A representation of the Time datatype
 */
public class XSTime extends CalendarType implements CmpEq,
 					            CmpLt,
					            CmpGt,
						     
					            MathMinus,
					            MathPlus,
						     
					            Cloneable {
						     
	private Calendar _calendar;
	private boolean  _timezoned;
        private XDTDayTimeDuration _tz;         


	/**
	 * Initialises to the supplied time and timezone
	 * @param cal Calendar representation of the time to be stored
	 * @param tz The timezone (possibly null) associated with this time
	 */
	public XSTime(Calendar cal, XDTDayTimeDuration tz) {
		_calendar = cal;
	
                _tz = tz;
                if(tz == null)          
                        _timezoned = false;
                else
                        _timezoned = true;      
	}
	/**
	 * Initialises to the current time
	 */
	public XSTime() {
		this(new GregorianCalendar(), null);
	}
		/**
		 * Creates a new copy of the time (and timezone) stored
		 * @return New XSTime representing the copy of the time and timezone
		 * @throws CloneNotSupportedException
		 */
        public Object clone() throws CloneNotSupportedException {
                Calendar c = (Calendar) calendar().clone();
                XDTDayTimeDuration t = tz();
                
                if(t != null)
                        t = (XDTDayTimeDuration) t.clone();

                return new XSTime(c, t);
        }                       

    /**
     * Retrieves the datatype's name
     * @return "time" which is the datatype's name
     */
	public String type_name() {
		return "time";
	}
	/**
	 * Creates a new XSTime representing the String represented supplied time
	 * @param str String represented time and timezone to be stored
	 * @return New XSTime representing the supplied time
	 */
	public static XSTime parse_time(String str) {
		// XXX fix this

		String lame = "1983-11-29T";
		boolean tz = false;

		int index = str.indexOf('+', 1);
		if(index == -1) 
			index = str.indexOf('-', 1);
		if(index == -1)
			index = str.indexOf('Z', 1);
		if(index != -1) {
			tz = true;
		}

		// thus life
		XSDateTime dt = XSDateTime.parseDateTime(lame + str);
		if(dt == null)
			return null;

		return new XSTime(dt.calendar(), dt.tz());	
	}
	/**
	 * Creates a new ResultSequence consisting of the extractable time from the
	 * supplied ResultSequence
	 * @param arg The ResultSequence from which to extract the time
	 * @return New ResultSequence consisting of the supplied time
	 * @throws DynamicError
	 */
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
                ResultSequence rs = ResultSequenceFactory.create_new();
                                        
                if(arg.empty())
                        return rs;
                                        
                AnyAtomicType aat = (AnyAtomicType) arg.first();
                                        
                XSTime t = parse_time(aat.string_value());
                                                        
                if(t == null)                 
                        throw DynamicError.cant_cast(null);
                                                
                rs.add(t);
                
		return rs;
	}
		/**
		 * Retrieves the hour stored as an integer
		 * @return The hour stored
		 */
        public int hour() {
                return _calendar.get(Calendar.HOUR_OF_DAY);
        }
        /**
         * Retrieves the minute stored as an integer
         * @return The minute stored
         */
        public int minute() {
                return _calendar.get(Calendar.MINUTE);
        }
        /**
         * Retrieves the seconds stored as an integer
         * @return The second stored
         */
        public double second() {
                double s = _calendar.get(Calendar.SECOND);

                double ms = _calendar.get(Calendar.MILLISECOND);

                ms /= 1000;

                s  += ms;
                return s;
        }
    /**
     * Check for whether the time stored has a timezone associated with it
     * @return True if the time has a timezone associated. False otherwise
     */
	public boolean timezoned() { return _timezoned; }
	/**
	 * Retrieves a String representation of the time stored
	 * @return String representation of the time stored
	 */
	public String string_value() {
		String ret = "";

                ret += XSDateTime.pad_int(hour(), 2);
        
                ret += ":";
                ret += XSDateTime.pad_int(minute(), 2);

                ret += ":"; 
                int isecond = (int) second();
                double sec = second();

                if( (sec - ((double) isecond) ) == 0.0)
                        ret += XSDateTime.pad_int(isecond, 2);
                else {
                        if(sec < 10.0)
                                ret += "0" + sec;
                        else
                                ret += sec;
                }        
                
		if(timezoned())
			ret += "Z";

		return ret;
	}
	/**
	 * Retrieves the datatype's full pathname
	 * @return "xs:time" which is the datatype's full pathname
	 */
	public String string_type() {
		return "xs:time";
	}
	/**
	 * Retrieves a Calendar representation of time stored
	 * @return Calendar representation of the time stored
	 */
	public Calendar calendar() {
		return _calendar;
	}
		/**
		 * Retrieves the timezone associated with the time stored as a duration of time
		 * @return The duration of time between the time stored and the actual time
		 * after the timezone is taken into account
		 */
        public XDTDayTimeDuration tz() { return _tz; }

        /**
         * Retrieves the time in milliseconds since the epoch
         * @return time stored in milliseconds since the epoch
         */
        public double value() {
                return calendar().getTimeInMillis()/1000.0;
        }

        /**
         * Equality comparison between this and the supplied XSTime representation
         * @param arg The XSTime to compare with
         * @return True if both XSTime's represent the same time. False otherwise
         * @throws DynamicError
         */
        public boolean eq(AnyType arg) throws DynamicError {
                XSTime val = (XSTime) NumericType.get_single_type(arg, XSTime.class);

                return calendar().equals(val.calendar());
        }
        /**
         * Comparison between this and the supplied XSTime representation
         * @param arg The XSTime to compare with
         * @return True if the supplied time represnts a point in time after that
         * represented by the time stored. False otherwise
         */
        public boolean lt(AnyType arg) throws DynamicError {
                XSTime val = (XSTime) NumericType.get_single_type(arg, XSTime.class);

                return calendar().before(val.calendar());
        }
        /**
         * Comparison between this and the supplied XSTime representation
         * @param arg The XSTime to compare with
         * @return True if the supplied time represnts a point in time before that
         * represented by the time stored. False otherwise
         * @throws DynamicError
         */
        public boolean gt(AnyType arg) throws DynamicError {
                XSTime val = (XSTime) NumericType.get_single_type(arg, XSTime.class);

                return calendar().after(val.calendar());
        }

        /**
    	 * Mathematical subtraction between this time stored and the supplied representation.
    	 * This supplied representation must be of either type XSTime (in which case the 
    	 * result is the duration of time between these two times) or a XDTDayTimeDuration
    	 * (in which case the result is the time when this duration is subtracted from the time
    	 * stored).
    	 * @param arg The representation to subtract (either XSTim or XDTDayTimeDuration)
    	 * @return A ResultSequence representing the result of the subtraction
    	 */
        public ResultSequence minus(ResultSequence arg) throws DynamicError {
		if(arg.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = arg.first();

		if(at instanceof XSTime) {
	                XSTime val = (XSTime) at;

	                double res = value() - val.value();

	                return ResultSequenceFactory.create_new(new XDTDayTimeDuration(res));
		}
		else if(at instanceof XDTDayTimeDuration) {
                	XDTDayTimeDuration val = (XDTDayTimeDuration) at;

			try {
	                	double ms = val.value()* -1000.0;

				XSTime res = (XSTime) clone();

				res.calendar().add(Calendar.MILLISECOND, (int) ms);
	
		                return ResultSequenceFactory.create_new(res);
			} catch(CloneNotSupportedException err) {
				assert false;
				return null;
			}
		}
		else {
			DynamicError.throw_type_error();
			return null; // unreach
		}
        }
        /**
         * Mathematical addition between this time stored and the supplied time duration.
         * @param arg A XDTDayTimeDuration representation of the duration of time to add
         * @return A XSTime representing the result of this addition.
         * @throws DynamicError
         */
        public ResultSequence plus(ResultSequence arg) throws DynamicError {
                XDTDayTimeDuration val = (XDTDayTimeDuration)
	                                 NumericType.get_single_type(arg, XDTDayTimeDuration.class);

		try {
	                double ms = val.value()*1000.0;

			XSTime res = (XSTime) clone();

			res.calendar().add(Calendar.MILLISECOND, (int) ms);
	
	                return ResultSequenceFactory.create_new(res);
		} catch(CloneNotSupportedException err) {
			assert false;
			return null;
		}
        }
}
