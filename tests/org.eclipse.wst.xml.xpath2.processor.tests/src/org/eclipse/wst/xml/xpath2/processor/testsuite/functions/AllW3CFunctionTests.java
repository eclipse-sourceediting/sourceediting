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
		suite.addTestSuite(StartsWithFuncTest.class);
		suite.addTestSuite(compareFuncTest.class);
		suite.addTestSuite(StringJoinFuncTest.class);
		suite.addTestSuite(LowerCaseFuncTest.class);
		suite.addTestSuite(StringToCodepointFuncTest.class);
		suite.addTestSuite(ReplaceFuncTest.class);
		suite.addTestSuite(StringLengthFuncTest.class);
		suite.addTestSuite(StringFuncTest.class);
		suite.addTestSuite(ContainsFuncTest.class);
		suite.addTestSuite(CeilingFuncTest.class);
		suite.addTestSuite(TrueFuncTest.class);
		suite.addTestSuite(TokenizeFuncTest.class);
		suite.addTestSuite(CodepointToStringFuncTest.class);
		suite.addTestSuite(SubstringAfterFuncTest.class);
		suite.addTestSuite(EscapeHTMLURIFuncTest.class);
		suite.addTestSuite(ResolveURIFuncTest.class);
		suite.addTestSuite(RoundEvenFuncTest.class);
		suite.addTestSuite(NormalizeUnicodeFuncTest.class);
		suite.addTestSuite(CodepointEqualTest.class);
		suite.addTestSuite(NotFuncTest.class);
		suite.addTestSuite(IRIToURIfuncTest.class);
		suite.addTestSuite(NilledFuncTest.class);
		suite.addTestSuite(DataFuncTest.class);
		suite.addTestSuite(UpperCaseFuncTest.class);
		suite.addTestSuite(NormalizeSpaceFuncTest.class);
		suite.addTestSuite(EndsWithFuncTest.class);
		suite.addTestSuite(ErrorFuncTest.class);
		suite.addTestSuite(BaseURIFuncTest.class);
		suite.addTestSuite(TraceFuncTest.class);
		suite.addTestSuite(ConcatFuncTest.class);
		suite.addTestSuite(TranslateFuncTest.class);
		suite.addTestSuite(EncodeURIfuncTest.class);
		suite.addTestSuite(MatchesFuncTest.class);
		suite.addTestSuite(FloorFuncTest.class);
		suite.addTestSuite(ABSFuncTest.class);
		suite.addTestSuite(DateTimeFuncTest.class);
		suite.addTestSuite(FalseFuncTest.class);
		suite.addTestSuite(SurrogatesTest.class);
		suite.addTestSuite(RoundFuncTest.class);
		suite.addTestSuite(SubstringBeforeFuncTest.class);
		suite.addTestSuite(DocumentURIFuncTest.class);
		//$JUnit-END$
		return suite;
	}

}
