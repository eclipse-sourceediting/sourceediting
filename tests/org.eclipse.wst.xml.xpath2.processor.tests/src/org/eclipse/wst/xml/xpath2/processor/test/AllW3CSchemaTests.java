package org.eclipse.wst.xml.xpath2.processor.test;

import org.eclipse.wst.xml.xpath2.processor.testsuite.schema.*;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllW3CSchemaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath2.processor.testsuite.schema");
		//$JUnit-BEGIN$
		suite.addTestSuite(UseCaseSGMLTest.class);
		suite.addTestSuite(SeqExprCastSITest.class);
//		suite.addTestSuite(STAxesTest.class);
		suite.addTestSuite(UseCaseSTRINGTest.class);
		suite.addTestSuite(UseCaseNSTest.class);
		suite.addTestSuite(UserDefinedSITest.class);
		suite.addTestSuite(ancestorAxisTest.class);
		suite.addTestSuite(UseCaseSEQTest.class);
		suite.addTestSuite(followingSiblingAxisTest.class);
		suite.addTestSuite(CatalogTest.class);
		suite.addTestSuite(precedingSiblingAxisTest.class);
		suite.addTestSuite(UseCaseTREETest.class);
		suite.addTestSuite(NumericEqualSITest.class);
		suite.addTestSuite(MiscFunctionsTest.class);
		suite.addTestSuite(ancestorOrSelfAxisTest.class);
		// XPath doesn't do validation only XQuery
//		suite.addTestSuite(ValidateExpressionTest.class);
//		suite.addTestSuite(NotationEQSITest.class);
		//$JUnit-END$
		return suite;
	}

}
