/*******************************************************************************
 * Copyright (c) 2009, 2010 Standards for Technology in Automotive Retail, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - bug 280547 - initial API and implementation. 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
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
 * Adjusts an xs:date value to a specific timezone, or to no timezone at
 * all. If <code>$timezone</code> is the empty sequence, returns an
 * <code>xs:date</code> without timezone. Otherwise, returns an
 * <code>xs:date</code> with a timezone.
 */
public class FnAdjustDateToTimeZone extends Function {
	private static Collection _expected_args = null;
	private static final XSDayTimeDuration minDuration = new XSDayTimeDuration(
			0, 14, 0, 0, true);
	private static final XSDayTimeDuration maxDuration = new XSDayTimeDuration(
			0, 14, 0, 0, false);

	/**
	 * Constructor for FnDateTime.
	 */
	public FnAdjustDateToTimeZone() {
		super(new QName("adjust-date-to-timezone"), 1, 2);
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
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return adjustDate(args, dynamic_context());
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
	public static ResultSequence adjustDate(Collection args,
			DynamicContext dc) throws DynamicError {

		Collection cargs = Function.convert_arguments(args, expectedArgs());

		ResultSequence rs = ResultSequenceFactory.create_new();

		// get args
		Iterator argiter = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argiter.next();
		if (arg1.empty()) {
			return rs;
		}
		ResultSequence arg2 = ResultSequenceFactory.create_new();
		if (argiter.hasNext()) {
			arg2 = (ResultSequence) argiter.next();
		}
		XSDate date = (XSDate) arg1.first();
		XSDayTimeDuration timezone = null;

		if (arg2.empty()) {
			if (date.timezoned()) {
				XSDate localized = new XSDate(date.calendar(), null);
				rs.add(localized);
				return rs;
			} else {
				return arg1;
			}
		}
		
		timezone = (XSDayTimeDuration) arg2.first();
		if (timezone.lt(minDuration, dc) || timezone.gt(maxDuration, dc)) {
			throw DynamicError.invalidTimezone();
		}
		
		if (date.tz() == null) {
			rs.add(new XSDate(date.calendar(), timezone));
			return rs;
		}
		
		try {
			XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)date.normalizeCalendar(date.calendar(), date.tz()));
			
			Duration duration = DatatypeFactory.newInstance().newDuration(timezone.string_value());
			xmlCalendar.add(duration);
			
			rs.add(new XSDate(xmlCalendar.toGregorianCalendar(), timezone));
		} catch (DatatypeConfigurationException ex) {
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
					.add(new SeqType(new XSDate(), SeqType.OCC_QMARK));
			_expected_args.add(new SeqType(new XSDayTimeDuration(),
					SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
