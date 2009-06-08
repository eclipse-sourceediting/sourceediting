package org.eclipse.wst.xml.xpath2.processor.testsuite.functions;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllW3CFunctionTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath2.processor.testsuite.functions");
		//$JUnit-BEGIN$
		suite.addTestSuite(NameFuncTest.class);
		suite.addTestSuite(SecondsFromTimeFuncTest.class);
		suite.addTestSuite(StringJoinFuncTest.class);
		suite.addTestSuite(SeqBooleanFuncTest.class);
		suite.addTestSuite(ReplaceFuncTest.class);
		suite.addTestSuite(LowerCaseFuncTest.class);
		suite.addTestSuite(NodeLocalNameFuncTest.class);
		suite.addTestSuite(StringFuncTest.class);
		suite.addTestSuite(ContainsFuncTest.class);
		suite.addTestSuite(TokenizeFuncTest.class);
		suite.addTestSuite(CodepointToStringFuncTest.class);
		suite.addTestSuite(EscapeHTMLURIFuncTest.class);
		suite.addTestSuite(RoundEvenFuncTest.class);
		suite.addTestSuite(ResolveURIFuncTest.class);
		suite.addTestSuite(SecondsFromDateTimeFuncTest.class);
		suite.addTestSuite(MinutesFromDurationFuncTest.class);
		suite.addTestSuite(YearFromDateTimeFuncTest.class);
		suite.addTestSuite(EndsWithFuncTest.class);
		suite.addTestSuite(TimezoneFromTimeFuncTest.class);
		suite.addTestSuite(ErrorFuncTest.class);
		suite.addTestSuite(LocalNameFromQNameFuncTest.class);
		suite.addTestSuite(TraceFuncTest.class);
		suite.addTestSuite(TranslateFuncTest.class);
		suite.addTestSuite(MonthFromDateTimeFuncTest.class);
		suite.addTestSuite(DayFromDateFuncTest.class);
		suite.addTestSuite(EncodeURIfuncTest.class);
		suite.addTestSuite(FloorFuncTest.class);
		suite.addTestSuite(TimezoneFromDateFuncTest.class);
		suite.addTestSuite(NodeNamespaceURIFuncTest.class);
		suite.addTestSuite(ABSFuncTest.class);
		suite.addTestSuite(AdjDateTimeToTimezoneFuncTest.class);
		suite.addTestSuite(DateTimeFuncTest.class);
		suite.addTestSuite(FalseFuncTest.class);
		suite.addTestSuite(MonthsFromDurationFuncTest.class);
		suite.addTestSuite(RoundFuncTest.class);
		suite.addTestSuite(SubstringFuncTest.class);
		suite.addTestSuite(StaticBaseURIFuncTest.class);
		suite.addTestSuite(StartsWithFuncTest.class);
		suite.addTestSuite(compareFuncTest.class);
		suite.addTestSuite(AdjDateToTimezoneFuncTest.class);
		suite.addTestSuite(MinutesFromDateTimeFuncTest.class);
		suite.addTestSuite(NodeLangFuncTest.class);
		suite.addTestSuite(HoursFromTimeFuncTest.class);
		suite.addTestSuite(NamespaceURIFromQNameFuncTest.class);
		suite.addTestSuite(StringToCodepointFuncTest.class);
		suite.addTestSuite(StringLengthFuncTest.class);
		suite.addTestSuite(TrueFuncTest.class);
		suite.addTestSuite(CeilingFuncTest.class);
		suite.addTestSuite(AdjTimeToTimezoneFuncTest.class);
		suite.addTestSuite(NodeNumberFuncTest.class);
		suite.addTestSuite(MinutesFromTimeFuncTest.class);
		suite.addTestSuite(SubstringAfterFuncTest.class);
		suite.addTestSuite(CodepointEqualTest.class);
		suite.addTestSuite(NormalizeUnicodeFuncTest.class);
		suite.addTestSuite(NotFuncTest.class);
		suite.addTestSuite(YearFromDateFuncTest.class);
		suite.addTestSuite(YearsFromDurationFuncTest.class);
		suite.addTestSuite(IRIToURIfuncTest.class);
		suite.addTestSuite(NilledFuncTest.class);
		suite.addTestSuite(InScopePrefixesFuncTest.class);
		suite.addTestSuite(DataFuncTest.class);
		suite.addTestSuite(UpperCaseFuncTest.class);
		suite.addTestSuite(NormalizeSpaceFuncTest.class);
		suite.addTestSuite(HoursFromDurationFuncTest.class);
		suite.addTestSuite(TimezoneFromDateTimeFuncTest.class);
		suite.addTestSuite(MonthFromDateFuncTest.class);
		suite.addTestSuite(BaseURIFuncTest.class);
		suite.addTestSuite(ConcatFuncTest.class);
		suite.addTestSuite(NodeRootFuncTest.class);
		suite.addTestSuite(DayFromDateTimeFuncTest.class);
		suite.addTestSuite(MatchesFuncTest.class);
		suite.addTestSuite(SeqIndexOfFuncTest.class);
		suite.addTestSuite(SecondsFromDurationFuncTest.class);
		suite.addTestSuite(HoursFromDateTimeFuncTest.class);
		suite.addTestSuite(DaysFromDurationFuncTest.class);
		suite.addTestSuite(SurrogatesTest.class);
		suite.addTestSuite(DocumentURIFuncTest.class);
		suite.addTestSuite(SubstringBeforeFuncTest.class);
		//$JUnit-END$
		return suite;
	}

}
