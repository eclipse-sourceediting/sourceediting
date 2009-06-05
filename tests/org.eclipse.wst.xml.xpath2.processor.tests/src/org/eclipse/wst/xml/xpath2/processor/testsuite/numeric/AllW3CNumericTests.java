package org.eclipse.wst.xml.xpath2.processor.testsuite.numeric;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllW3CNumericTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath2.processor.testsuite.numeric");
		//$JUnit-BEGIN$
		suite.addTestSuite(NumericUnaryPlusTest.class);
		suite.addTestSuite(NumericAddTest.class);
		suite.addTestSuite(NumericUnaryMinusTest.class);
		suite.addTestSuite(NumericMultiplyTest.class);
		suite.addTestSuite(NumericModTest.class);
		suite.addTestSuite(NumericGTTest.class);
		suite.addTestSuite(NumericIntegerDivideTest.class);
		suite.addTestSuite(NumericEqualTest.class);
		suite.addTestSuite(NumericLTTest.class);
		suite.addTestSuite(NumericSubtractTest.class);
		suite.addTestSuite(NumericDivideTest.class);
		//$JUnit-END$
		return suite;
	}

}
