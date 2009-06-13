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

import org.eclipse.wst.xml.xpath2.processor.testsuite.core.AbbrAxesTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.AxesTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.BooleanEqualTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.BooleanGTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.BooleanLTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.CombNodeSeqTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.NameTestTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.NodeTestTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.ParenExprTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.SequenceTypeSyntaxTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.core.UnabbrAxesTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.dates.DateAddYMDTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.dates.DateTimeSubtractYMDTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.dates.YearMonthDurationAddDTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.dates.YearMonthDurationSubtractTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.numeric.AllW3CNumericTests;
import org.eclipse.wst.xml.xpath2.processor.testsuite.numeric.NumericEqualTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.numeric.NumericGTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.numeric.NumericLTTest;
import org.eclipse.wst.xml.xpath2.processor.testsuite.numeric.NumericUnaryPlusTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllW3CXPath20Tests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for W3C XPath 2.0 test suite.");
		//$JUnit-BEGIN$
		suite.addTest(AllW3CNumericTests.suite());
		suite.addTestSuite(SequenceTypeSyntaxTest.class);
		suite.addTestSuite(CombNodeSeqTest.class);
		suite.addTestSuite(BooleanLTTest.class);
		suite.addTestSuite(AxesTest.class);
		suite.addTestSuite(NodeTestTest.class);
		suite.addTestSuite(BooleanEqualTest.class);
		suite.addTestSuite(AbbrAxesTest.class);
		suite.addTestSuite(NameTestTest.class);
		suite.addTestSuite(YearMonthDurationSubtractTest.class);
		suite.addTestSuite(BooleanGTTest.class);
		suite.addTestSuite(DateTimeSubtractYMDTest.class);
		suite.addTestSuite(YearMonthDurationAddDTTest.class);
		suite.addTestSuite(DateAddYMDTest.class);
		suite.addTestSuite(ParenExprTest.class);
		suite.addTestSuite(UnabbrAxesTest.class);
		//$JUnit-END$
		return suite;
	}

}
