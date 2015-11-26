/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.model;

import java.io.IOException;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;

public class CSSStyleRuleTest extends AbstractModelTest {
	public void testInsertTextNumbers() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSStyleRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(3, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		rule = ruleList.item(0);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		assertEquals(20, declaration.getLength());

		// 01
		value = declaration.getPropertyCSSValue("NUMBER");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_NUMBER, 123.456f));

		// 02
		value = declaration.getPropertyCSSValue("PERCENTAGE");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_PERCENTAGE, 123.456f));

		// 03
		value = declaration.getPropertyCSSValue("EMS");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_EMS, 123.456f));

		// 04
		value = declaration.getPropertyCSSValue("EXS");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_EXS, 123.456f));

		// 05
		value = declaration.getPropertyCSSValue("PX");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_PX, +123.456f));
		value = declaration.getPropertyCSSValue("PX2");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_PX, -123f));


		
		// 06
		value = declaration.getPropertyCSSValue("CM");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 123.456f));

		// 07
		value = declaration.getPropertyCSSValue("MM");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_MM, 123.456f));

		// 08
		value = declaration.getPropertyCSSValue("IN");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_IN, 123.456f));

		// 09
		value = declaration.getPropertyCSSValue("PT");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_PT, 123.456f));

		// 10
		value = declaration.getPropertyCSSValue("PC");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_PC, 123.456f));

		// 11
		value = declaration.getPropertyCSSValue("DEG");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_DEG, -123.456f));

		// 12
		value = declaration.getPropertyCSSValue("RAD");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_RAD, 123.456f));

		// 13
		value = declaration.getPropertyCSSValue("GRAD");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_GRAD, 123.456f));

		// 14
		value = declaration.getPropertyCSSValue("MS");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_MS, 123.456f));

		// 15
		value = declaration.getPropertyCSSValue("S");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_S, 123.456f));

		// 16
		value = declaration.getPropertyCSSValue("HZ");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_HZ, 123.456f));

		// 17
		value = declaration.getPropertyCSSValue("KHZ");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_KHZ, 123.456f));

		// 18
		value = declaration.getPropertyCSSValue("DIMENSION");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_DIMENSION, -123.456f));

		// 19
		value = declaration.getPropertyCSSValue("INTEGER");
		checkPrimitiveNumber(value, new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 123));
	}

	public void testInsertTextStrings() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSStyleRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(3, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		rule = ruleList.item(1);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		assertEquals(11, declaration.getLength());

		// 01
		value = declaration.getPropertyCSSValue("STRING");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_STRING, "string"));

		// 02
		value = declaration.getPropertyCSSValue("URI");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_URI, "http://www.ibm.com/"));

		// 03
		value = declaration.getPropertyCSSValue("IDENT");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "left"));

		// 04
		value = declaration.getPropertyCSSValue("HASH");
		checkPrimitiveString(value, new PrimitiveString(ICSSPrimitiveValue.CSS_HASH, "#abcdef"));

		// 05
		value = declaration.getPropertyCSSValue("URANGE");
		checkPrimitiveString(value, new PrimitiveString(ICSSPrimitiveValue.CSS_URANGE, "U+20A7"));

		// 06
		value = declaration.getPropertyCSSValue("SLASH");
		checkPrimitiveString(value, new PrimitiveString(ICSSPrimitiveValue.CSS_SLASH, "/"));

		// 07
		value = declaration.getPropertyCSSValue("COMMA");
		checkPrimitiveString(value, new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));

		// 08
		value = declaration.getPropertyCSSValue("INHERIT_PRIMITIVE");
		checkPrimitiveString(value, new PrimitiveString(ICSSPrimitiveValue.CSS_INHERIT_PRIMITIVE, "inherit"));

		// 09
		value = declaration.getPropertyCSSValue("ATTR");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_ATTR, "KEY"));

		// 10
		value = declaration.getPropertyCSSValue("FORMAT");
		checkPrimitiveString(value, new PrimitiveString(ICSSPrimitiveValue.CSS_FORMAT, "truedoc"));

		// 11
		value = declaration.getPropertyCSSValue("LOCAL");
		checkPrimitiveString(value, new PrimitiveString(ICSSPrimitiveValue.CSS_LOCAL, "Excelsior Roman"));
	}

	public void testInsertTextFunctions() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSStyleRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(3, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		rule = ruleList.item(2);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		assertEquals(3, declaration.getLength());

		value = declaration.getPropertyCSSValue("COUNTER");
		checkPrimitiveCounter(value, "par-num", "upper-roman", null);

		value = declaration.getPropertyCSSValue("RECT");
		checkPrimitiveRect(value, new Object[]{new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 12), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 34), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 56), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 78)});

		value = declaration.getPropertyCSSValue("RGBCOLOR");
		checkPrimitiveRgb(value, new Object[]{new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 255), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 128), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 0)});
	}

}
