package org.eclipse.wst.xsl.tests;

import org.eclipse.wst.xsl.launching.tests.LaunchingSuite;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTestsSuite extends TestCase {

	public static TestSuite suite() {
		TestSuite completeTestSuite = new TestSuite();
		completeTestSuite.addTest(LaunchingSuite.suite());
		return completeTestSuite;
	}
}
