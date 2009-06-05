package org.eclipse.wst.xml.xpath2.processor.testsuite.functions;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllW3CFunctionTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath2.processor.testsuite.functions");
		//$JUnit-BEGIN$
		suite.addTestSuite(SubstringFuncTest.class);
		suite.addTestSuite(StaticBaseURIFuncTest.class);
		suite.addTestSuite(compareFuncTest.class);
		suite.addTestSuite(StringJoinFuncTest.class);
		suite.addTestSuite(LowerCaseFuncTest.class);
		suite.addTestSuite(StringToCodepointFuncTest.class);
		suite.addTestSuite(StringFuncTest.class);
		suite.addTestSuite(StringLengthFuncTest.class);
		suite.addTestSuite(CeilingFuncTest.class);
		suite.addTestSuite(CodepointToStringFuncTest.class);
		suite.addTestSuite(RoundEvenFuncTest.class);
		suite.addTestSuite(CodepointEqualTest.class);
		suite.addTestSuite(NormalizeUnicodeFuncTest.class);
		suite.addTestSuite(NilledFuncTest.class);
		suite.addTestSuite(DataFuncTest.class);
		suite.addTestSuite(UpperCaseFuncTest.class);
		suite.addTestSuite(NormalizeSpaceFuncTest.class);
		suite.addTestSuite(ErrorFuncTest.class);
		suite.addTestSuite(BaseURIFuncTest.class);
		suite.addTestSuite(ConcatFuncTest.class);
		suite.addTestSuite(TraceFuncTest.class);
		suite.addTestSuite(FloorFuncTest.class);
		suite.addTestSuite(ABSFuncTest.class);
		suite.addTestSuite(DateTimeFuncTest.class);
		suite.addTestSuite(DocumentURIFuncTest.class);
		suite.addTestSuite(RoundFuncTest.class);
		//$JUnit-END$
		return suite;
	}

}
