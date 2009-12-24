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
 *     Mukul Gandhi - bug 279377 - improvements to multiplication and division operations
 *                                 on xs:dayTimeDuration.
 *     David Carver - bug 282223 - implementation of xs:duration
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;

/**
 * A representation of the DayTimeDuration datatype
 */
public class XSDayTimeDuration extends XSDuration implements CmpEq, CmpLt,
		CmpGt,

		MathPlus, MathMinus, MathTimes, MathDiv,

		Cloneable {


	private static final String XS_DAY_TIME_DURATION = "xs:dayTimeDuration";

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
		super(0, 0, days, hours, minutes, seconds, negative);
	}

	/**
	 * Initialises to the given number of seconds
	 * 
	 * @param secs
	 *            Number of seconds in the duration of time
	 */
	public XSDayTimeDuration(double secs) {
		super(0, 0, 0, 0, 0, Math.abs(secs), secs < 0);
	}

	/**
	 * Initialises to a duration of no time (0days, 0hours, 0minutes, 0seconds)
	 */
	public XSDayTimeDuration() {
		super(0, 0, 0, 0, 0, 0.0, false);
	}

	/**
	 * Creates a copy of this representation of a time duration
	 * 
	 * @return New XSDayTimeDuration representing the duration of time stored
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new XSDayTimeDuration(days(), hours(), minutes(), seconds(),
				negative());
	}

	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();
	
		if (arg.empty())
			return rs;
	
		AnyAtomicType aat = (AnyAtomicType) arg.first();
		if (aat instanceof NumericType || aat instanceof CalendarType ||
			aat instanceof XSBoolean || aat instanceof XSBase64Binary ||
			aat instanceof XSHexBinary || aat instanceof XSAnyURI) {
			throw DynamicError.invalidType();
		}

		if (!isCastable(aat)) {
			throw DynamicError.cant_cast(null);
		}
	
		XSDuration dtd = castDayTimeDuration(aat);
	
		if (dtd == null)
			throw DynamicError.cant_cast(null);
	
		rs.add(dtd);
	
		return rs;
	}
	
	private XSDuration castDayTimeDuration(AnyAtomicType aat) {
		if (aat instanceof XSDuration) {
			XSDuration duration = (XSDuration) aat;
			return new XSDayTimeDuration(duration.days(), duration.hours(), duration.minutes(), duration.seconds(), duration.negative());
		}
		
		return parseDTDuration(aat.string_value());
	}

	
	/**
	 * Creates a new XSDayTimeDuration by parsing the supplied String
	 * represented duration of time
	 * 
	 * @param str
	 *            String represented duration of time
	 * @return New XSDayTimeDuration representing the duration of time supplied
	 */
	public static XSDuration parseDTDuration(String str) {
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
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:dayTimeDuration" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return XS_DAY_TIME_DURATION;
	}

	/**
	 * Mathematical addition between this duration stored and the supplied
	 * duration of time (of type XSDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to add
	 * @return New XSDayTimeDuration representing the resulting duration after
	 *         the addition
	 * @throws DynamicError
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {
		XSDuration val = (XSDuration) NumericType
				.get_single_type(arg, XSDayTimeDuration.class);
		
		double res = value() + val.value();

		return ResultSequenceFactory.create_new(new XSDayTimeDuration(res));
	}

	/**
	 * Mathematical subtraction between this duration stored and the supplied
	 * duration of time (of type XSDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to subtract
	 * @return New XSDayTimeDuration representing the resulting duration after
	 *         the subtraction
	 * @throws DynamicError
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		XSDuration val = (XSDuration) NumericType
				.get_single_type(arg, XSDayTimeDuration.class);

		double res = value() - val.value();

		return ResultSequenceFactory.create_new(new XSDayTimeDuration(res));
	}

	/**
	 * Mathematical multiplication between this duration stored and the supplied
	 * duration of time (of type XSDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to multiply by
	 * @return New XSDayTimeDuration representing the resulting duration after
	 *         the multiplication
	 * @throws DynamicError
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		ResultSequence convertedRS = arg;
		
		if (arg.size() == 1) {
			AnyType argValue = arg.first();
            if (argValue instanceof XSDecimal) {
            	convertedRS = ResultSequenceFactory.create_new(new XSDouble(argValue.string_value()));	
            }
		}
		
		XSDouble val = (XSDouble) NumericType.get_single_type(convertedRS,
				                                  XSDouble.class);
		if (val.nan()) {
			throw DynamicError.nan();
		}

		double res = value() * val.double_value();

		return ResultSequenceFactory.create_new(new XSDayTimeDuration(res));
	}

	/**
	 * Mathematical division between this duration stored and the supplied
	 * duration of time (of type XSDayTimeDuration)
	 * 
	 * @param arg
	 *            The duration of time to divide by
	 * @return New XSDayTimeDuration representing the resulting duration after
	 *         the division
	 * @throws DynamicError
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		if (arg.size() != 1)
			DynamicError.throw_type_error();

		AnyType at = arg.first();

		if (at instanceof XSDouble) {
			XSDouble dt = (XSDouble) at;
			double retval = 0;
			
			if (dt.nan()) {
				throw DynamicError.nan();
			}
			
			if (!dt.zero()) {
				BigDecimal ret = BigDecimal.ZERO;
				
				if (dt.infinite()) {
					retval = value() / dt.double_value();
				} else {
					ret = BigDecimal.valueOf(value());
					ret = ret.divide(BigDecimal.valueOf(dt.double_value()), 18, RoundingMode.HALF_EVEN);
					retval = ret.doubleValue();
				}
			} else {
				throw DynamicError.overflowUnderflow();
			}
//				ret = value() / dt.double_value();
			

			return ResultSequenceFactory
					.create_new(new XSDayTimeDuration(retval));
		} else if (at instanceof XSDecimal) {
			XSDecimal dt = (XSDecimal) at;
			
			BigDecimal ret = BigDecimal.ZERO;
							
			if (!dt.zero()) {
				ret = BigDecimal.valueOf(value());
				ret = ret.divide(dt.getValue(), 18, RoundingMode.HALF_EVEN);
			} else {
				throw DynamicError.overflowUnderflow();
			}
			
			return ResultSequenceFactory.create_new(new XSDayTimeDuration(
					ret.intValue()));	
		} else if (at instanceof XSDayTimeDuration) {
			XSDuration md = (XSDuration) at;

			BigDecimal res = null;
			try {
                Duration thisDuration  = DatatypeFactory.newInstance().newDuration(string_value());
				Duration mdduration = DatatypeFactory.newInstance().newDuration(md.string_value());
				double thistime = thisDuration.getTimeInMillis(Calendar.getInstance());
				double thattime = mdduration.getTimeInMillis(Calendar.getInstance());
				res = BigDecimal.valueOf(thistime);
				BigDecimal l = BigDecimal.valueOf(thattime);
				res = res.divide(l, 18, RoundingMode.HALF_EVEN);
			} catch (DatatypeConfigurationException ex) {
				
			}

//			double res = value() / md.value();

			return ResultSequenceFactory.create_new(new XSDecimal(res));
		} else {
			DynamicError.throw_type_error();
			return null; // unreach
		}
	}
	
}
