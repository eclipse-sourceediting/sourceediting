/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - initial API and implementation based on W3C XPath 2.0
 *                           Test Suite.
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import org.eclipse.wst.xml.xpath2.processor.testsuite.AbbrAxesTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.AxesTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.BooleanEqualTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.BooleanGTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.BooleanLTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.CombNodeSeqTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.DateAddYMDTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.DateTimeSubtractYMDTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.NodeTestTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.NumericEqualTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.NumericGTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.NumericLTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.NumericUnaryPlusTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.ParenExprTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.SequenceTypeSyntaxTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.YearMonthDurationAddDTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.YearMonthDurationSubtractTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllW3CXPath20Tests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for W3C XPath 2.0 test suite.");
		//$JUnit-BEGIN$
		suite.addTestSuite(NumericUnaryPlusTest.class);
		suite.addTestSuite(SequenceTypeSyntaxTest.class);
		suite.addTestSuite(NumericGTTest.class);
//		suite.addTestSuite(DateTimeAddDTDTest.class);
//		suite.addTestSuite(TimeSubtractTest.class);
		suite.addTestSuite(CombNodeSeqTest.class);
//		suite.addTestSuite(DayTimeDurationDivideDTDTest.class);
		suite.addTestSuite(BooleanLTTest.class);
//		suite.addTestSuite(NumericMultiplyTest.class);
		suite.addTestSuite(AxesTest.class);
//		suite.addTestSuite(DateSubtractDTDTest.class);
		suite.addTestSuite(NodeTestTest.class);
//		suite.addTestSuite(TimeAddDTDTest.class);
		suite.addTestSuite(BooleanEqualTest.class);
//		suite.addTestSuite(YearMonthDurationAddTest.class);
//		suite.addTestSuite(DayTimeDurationAddTest.class);
//		suite.addTestSuite(dateTimesSubtractTest.class);
//		suite.addTestSuite(InternalContextExprTest.class);
//		suite.addTestSuite(DayTimeDurationMultiplyTest.class);
		suite.addTestSuite(AbbrAxesTest.class);
//		suite.addTestSuite(DateTimeSubtractDTDTest.class);
//		suite.addTestSuite(NumericSubtractTest.class);
//		suite.addTestSuite(DayTimeDurationDivideTest.class);
//		suite.addTestSuite(NameTestTest.class);
		suite.addTestSuite(NumericLTTest.class);
//		suite.addTestSuite(NumericDivideTest.class);
		suite.addTestSuite(YearMonthDurationSubtractTest.class);
//		suite.addTestSuite(RangeExprTest.class);
		suite.addTestSuite(BooleanGTTest.class);
//		suite.addTestSuite(DateSubtractYMDTest.class);
//		suite.addTestSuite(LiteralsTest.class);
//		suite.addTestSuite(TimeSubtractDTDTest.class);
//		suite.addTestSuite(YearMonthDurationMultiplyTest.class);
//		suite.addTestSuite(YearMonthDurationDivideTest.class);
//		suite.addTestSuite(NumericAddTest.class);
//		suite.addTestSuite(commaOpTest.class);
//		suite.addTestSuite(NumericIntegerDivideTest.class);
//		suite.addTestSuite(FilterExprTest.class);
//		suite.addTestSuite(YearMonthDurationDivideYMDTest.class);
//		suite.addTestSuite(PredicatesTest.class);
//		suite.addTestSuite(ExternalContextExprTest.class);
//		suite.addTestSuite(NumericModTest.class);
		suite.addTestSuite(DateTimeSubtractYMDTest.class);
		suite.addTestSuite(NumericEqualTest.class);
//		suite.addTestSuite(DatesSubtractTest.class);
//		suite.addTestSuite(DateAddDTDTest.class);
//		suite.addTestSuite(DayTimeDurationSubtractTest.class);
//		suite.addTestSuite(NumericUnaryMinusTest.class);
		suite.addTestSuite(YearMonthDurationAddDTTest.class);
		suite.addTestSuite(DateAddYMDTest.class);
		suite.addTestSuite(ParenExprTest.class);
//		suite.addTestSuite(UnabbrAxesTest.class);
		//$JUnit-END$
		return suite;
	}

}
