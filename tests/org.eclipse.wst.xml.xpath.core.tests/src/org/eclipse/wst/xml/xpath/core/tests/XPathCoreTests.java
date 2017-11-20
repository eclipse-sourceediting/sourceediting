package org.eclipse.wst.xml.xpath.core.tests;

import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXPath20Helper;
import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXSLXPathHelper;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XPathCoreTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestXSLXPathHelper.class);
		suite.addTestSuite(TestXPath20Helper.class);
		//$JUnit-END$
		return suite;
	}

}
