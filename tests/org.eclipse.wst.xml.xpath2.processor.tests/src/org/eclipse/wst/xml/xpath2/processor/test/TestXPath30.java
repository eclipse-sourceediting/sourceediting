/*******************************************************************************
 *Copyright (c) 2013 Jesper Steen Møller and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    Jesper Steen Møller - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import java.math.BigInteger;
import java.net.URL;

import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.XPath2Expression;
import org.eclipse.wst.xml.xpath2.processor.Engine;
import org.eclipse.wst.xml.xpath2.processor.JFlexCupParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.LetExpr;
import org.eclipse.wst.xml.xpath2.processor.util.DynamicContextBuilder;
import org.eclipse.wst.xml.xpath2.processor.util.StaticContextBuilder;

public class TestXPath30 extends AbstractPsychoPathTest {

	public void testParseInvalidXPathExpression() throws Exception {
		try {
			XPathParser xpp = new JFlexCupParser();
			String xpath = "let $ := 123 return $a";
			xpp.parse(xpath);
			fail("XPath parsing suceeded when it should have failed.");
		} catch (XPathParserException ex) {

		}
	}

	public void testParseValidXPathExpression() throws Exception {
		StaticContextBuilder staticContextBuilder = new StaticContextBuilder();

		XPath2Expression expr = new Engine().parseExpression("let $a := 1, $b := $a+2, $c := $a + $b return $c+5", staticContextBuilder);
		DynamicContextBuilder dynamicContextBuilder = new DynamicContextBuilder(staticContextBuilder);
		ResultSequence result = expr.evaluate(dynamicContextBuilder, new Object[0]);
		assertEquals(1,  result.size());
		assertEquals(BigInteger.valueOf(9),  result.value(0));
	}
}
