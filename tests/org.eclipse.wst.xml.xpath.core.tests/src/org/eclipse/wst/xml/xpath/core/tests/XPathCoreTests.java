package org.eclipse.wst.xml.xpath.core.tests;

import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXPathCoreXPathParser;
import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXPathModel;
import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXPathParser;
import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXSLXPathHelper;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XPathCoreTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestXPathParser.class);
		suite.addTestSuite(TestXPathModel.class);
		suite.addTestSuite(TestXSLXPathHelper.class);
		suite.addTestSuite(TestXPathCoreXPathParser.class);
		//$JUnit-END$
		return suite;
	}

}
