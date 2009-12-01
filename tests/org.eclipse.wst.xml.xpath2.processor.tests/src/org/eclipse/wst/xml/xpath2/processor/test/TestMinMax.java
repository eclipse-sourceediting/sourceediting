/*******************************************************************************
 * Copyright (c) 2009 Jesper Steen Moller and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jesper S Moller - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import java.util.Comparator;

import org.eclipse.wst.xml.xpath2.processor.CollationProvider;
import org.eclipse.wst.xml.xpath2.processor.DefaultDynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;

public class TestMinMax extends AbstractPsychoPathTest {

	private static final String URN_X_ECLIPSE_XPATH20_FUNKY_COLLATOR = "urn:x-eclipse:xpath20:funky-collator";

	public void testEmpty() throws Exception {
		assertXPathEvalation("max( () )", "");
		assertXPathEvalation("min( () )", "");
	}

	public void testYMDMinMax() throws Exception {
		assertXPathEvalation("max( (xs:yearMonthDuration('P0Y1M'), xs:yearMonthDuration('P1Y1M'), xs:yearMonthDuration('P1Y0M')) )", "P1Y1M");
		assertXPathEvalation("min( (xs:yearMonthDuration('P0Y1M'), xs:yearMonthDuration('P1Y1M'), xs:yearMonthDuration('P1Y0M')) )", "P1M");
	}

	public void testDTDMax() throws Exception {
		assertXPathEvalation("max( (xs:dayTimeDuration('P1DT2H'), xs:dayTimeDuration('P1D'), xs:dayTimeDuration('PT22H')) )", "P1DT2H");
		assertXPathEvalation("min( (xs:dayTimeDuration('P1DT2H'), xs:dayTimeDuration('P1D'), xs:dayTimeDuration('PT22H')) )", "PT22H");
	}

	public void testAtomic() throws Exception {
		assertXPathEvalation("max( (xs:untypedAtomic('1'), 0.9, xs:untypedAtomic('2.1')) )", "2.1");
		assertXPathEvalation("min( (xs:untypedAtomic('1'), 0.9, xs:untypedAtomic('2.1')) )", "0.9");
	}

	public void testMixed() throws Exception {
		assertDynamicError("max( ('a', 1.2, 1) )", "FORG0006");
		assertDynamicError("min( ('a', 1.2, 1) )", "FORG0006");
		assertDynamicError("max( (xs:dayTimeDuration('P1DT2H'), 123) )", "FORG0006");
		assertDynamicError("min( (xs:dayTimeDuration('P1DT2H'), 123) )", "FORG0006");
		
		assertDynamicError("max( (xs:untypedAtomic('1'), 1.2, '2.1') )", "FORG0006");
		assertDynamicError("min( (xs:untypedAtomic('1'), 1.2, '2.1') )", "FORG0006");
	}

	private void assertXPathEvalation(String xpath, String expectedResult) throws XPathParserException, StaticError,
			DynamicError {
		DynamicContext dc = setupDynamicContext(null);

		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		String resultValue = buildResultString(rs);

		assertEquals(expectedResult, resultValue);
	}


	private void assertDynamicError(String xpath, String errorCode) throws XPathParserException, StaticError {
		DynamicContext dc = setupDynamicContext(null);

		XPath path = compileXPath(dc, xpath);

		try {
			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			eval.evaluate(path);
			fail("Error " + errorCode + " expected here");
		} catch (DynamicError de) {
			assertEquals("Wrong error code", errorCode, de.code());
		} catch (Throwable t) {
			fail("Unexpected error: " + t.getMessage());
		}
	}

	public void testStringMax() throws Exception {
		DefaultDynamicContext dc = setupDynamicContext(null);
		dc.set_collation_provider(createLengthCollatorProvider());

		String xpath = "max( ('1000', '200', '30', '4') )";

		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		String resultValue = rs.first().string_value();

		// lexicographically, '4' is biggest
		assertEquals("4", resultValue);
		dc.set_default_collation(URN_X_ECLIPSE_XPATH20_FUNKY_COLLATOR);

		rs = eval.evaluate(path);
		resultValue = rs.first().string_value();

		// length-wise, '1000' is biggest!
		assertEquals("1000", resultValue);
	}
	
	private CollationProvider createLengthCollatorProvider() {
		return new CollationProvider() {
			public Comparator get_collation(String name) {
				if (name.equals(URN_X_ECLIPSE_XPATH20_FUNKY_COLLATOR)) {
					return new Comparator<String>() {
						public int compare(String o1, String o2) {
							return o1.length() - o2.length();
						}
					};
				}
				return null;
			}
		};
	}

	
}
