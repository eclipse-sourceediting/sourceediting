package org.eclipse.wst.xml.xpath2.processor.testsuite.functions;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllW3CFunctionTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath2.processor.testsuite.functions");
		//$JUnit-BEGIN$
		suite.addTestSuite(SubstringFuncTest.class);
		suite.addTestSuite(ErrorFuncTest.class);
		suite.addTestSuite(StaticBaseURIFuncTest.class);
		suite.addTestSuite(compareFuncTest.class);
		suite.addTestSuite(BaseURIFuncTest.class);
		suite.addTestSuite(StringJoinFuncTest.class);
		suite.addTestSuite(TraceFuncTest.class);
		suite.addTestSuite(ConcatFuncTest.class);
		suite.addTestSuite(StringToCodepointFuncTest.class);
		suite.addTestSuite(StringFuncTest.class);
		suite.addTestSuite(CeilingFuncTest.class);
		suite.addTestSuite(CodepointToStringFuncTest.class);
		suite.addTestSuite(FloorFuncTest.class);
		suite.addTestSuite(RoundEvenFuncTest.class);
		suite.addTestSuite(CodepointEqualTest.class);
		suite.addTestSuite(ABSFuncTest.class);
		suite.addTestSuite(NilledFuncTest.class);
		suite.addTestSuite(DataFuncTest.class);
		suite.addTestSuite(DateTimeFuncTest.class);
		suite.addTestSuite(RoundFuncTest.class);
		suite.addTestSuite(DocumentURIFuncTest.class);
		//$JUnit-END$
		return suite;
	}

}
