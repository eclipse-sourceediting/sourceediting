/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - bug 280547 - initial API and implementation. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at
 * all. If <code>$timezone</code> is the empty sequence, returns an
 * <code>xs:dateTime</code> without timezone. Otherwise, returns an
 * <code>xs:dateTime</code> with a timezone.
 */
public class FnAdjustDateTimeToTimeZone extends Function {
	private static Collection _expected_args = null;
	private static final XSDayTimeDuration minDuration = new XSDayTimeDuration(
			0, 14, 0, 0, true);
	private static final XSDayTimeDuration maxDuration = new XSDayTimeDuration(
			0, 14, 0, 0, false);

	/**
	 * Constructor for FnDateTime.
	 */
	public FnAdjustDateTimeToTimeZone() {
		super(new QName("adjust-dateTime-to-timezone"), 1, 2);
	}

	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return adjustdateTime(args, dynamic_context());
	}

	/**
	 * Evaluate the function using the arguments passed.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param sc
	 *            Result of static context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of the fn:dateTime operation.
	 */
	public static ResultSequence adjustdateTime(Collection args,
			DynamicContext dc) throws DynamicError {

		Collection cargs = Function.convert_arguments(args, expectedArgs());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();
		if (arg1.empty()) {
			return rs;
		}
		ResultSequence arg2 = null;
		if (argiter.hasNext()) {
			arg2 = (ResultSequence) argiter.next();
		}
		XSDateTime dateTime = (XSDateTime) arg1.first();
		XSDayTimeDuration timezone = null;
		
		if (arg2 != null && arg2.empty()) {
			if (dateTime.timezoned()) {
				CalendarType localized = new XSDateTime(dateTime.calendar(), null);
				rs.add(localized);
				return rs;
			} else {
				return arg1;
			}
		} else if (arg2 == null) {
			CalendarType localized = new XSDateTime(dateTime.normalizeCalendar(dateTime.calendar(), dateTime.tz()), null);
			rs.add(localized);
			return rs;
		}

		timezone = (XSDayTimeDuration) arg2.first();
		if (timezone.lt(minDuration, dc) || timezone.gt(maxDuration, dc)) {
			throw DynamicError.invalidTimezone();
		}
		
		if (dateTime.tz() == null) {
			rs.add(new XSDateTime(dateTime.calendar(), timezone));
			return rs;
		}
		
		try {			
			XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)dateTime.normalizeCalendar(dateTime.calendar(), dateTime.tz()));
			
			Duration duration = DatatypeFactory.newInstance().newDuration(timezone.string_value());
			xmlCalendar.add(duration);

			rs.add(new XSDateTime(xmlCalendar.toGregorianCalendar(), timezone));
			
		} catch (DatatypeConfigurationException e) {
			throw DynamicError.invalidTimezone();
		}
		
		return rs;
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public synchronized static Collection expectedArgs() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args
					.add(new SeqType(new XSDateTime(), SeqType.OCC_QMARK));
			_expected_args.add(new SeqType(new XSDayTimeDuration(),
					SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
