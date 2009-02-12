package org.eclipse.wst.xml.xpath2.processor.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllPsychoPathTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath2.processor.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestXPath20.class);
		//$JUnit-END$
		return suite;
	}

}
