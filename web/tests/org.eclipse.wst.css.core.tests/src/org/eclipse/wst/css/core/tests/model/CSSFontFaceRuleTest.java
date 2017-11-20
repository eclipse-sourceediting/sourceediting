/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
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
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

public class CSSFontFaceRuleTest extends AbstractModelTest {
	public void testInsertRule() {
		final String RULE = "@font-face { font-family: \"Swiss 721\"; src: url(swiss721.pfr); /* The expanded Swiss 721 */ font-stretch: expanded; }";
		CSSStyleSheet sheet = getStyleSheet();
		assertEquals(0, sheet.insertRule(RULE, 0));

		CSSRuleList ruleList = sheet.getCssRules();
		CSSRule rule = ruleList.item(0);
		assertTrue(rule instanceof CSSFontFaceRule);

		CSSStyleDeclaration declaration = ((CSSFontFaceRule) rule).getStyle();
		assertEquals(3, declaration.getLength());

		CSSValue value;
		CSSPrimitiveValue primitiveValue;

		value = declaration.getPropertyCSSValue("font-family");
		assertTrue(value instanceof CSSPrimitiveValue);

		primitiveValue = (CSSPrimitiveValue) value;
		assertEquals(CSSPrimitiveValue.CSS_STRING, primitiveValue.getPrimitiveType());
		assertEquals("Swiss 721", primitiveValue.getStringValue());

		value = declaration.getPropertyCSSValue("src");
		assertTrue(value instanceof CSSPrimitiveValue);

		primitiveValue = (CSSPrimitiveValue) value;
		assertEquals(CSSPrimitiveValue.CSS_URI, primitiveValue.getPrimitiveType());
		assertEquals("swiss721.pfr", primitiveValue.getStringValue());

		value = declaration.getPropertyCSSValue("font-stretch");
		assertTrue(value instanceof CSSPrimitiveValue);

		primitiveValue = (CSSPrimitiveValue) value;
		assertEquals(CSSPrimitiveValue.CSS_IDENT, primitiveValue.getPrimitiveType());
		assertEquals("expanded", primitiveValue.getStringValue());
	}

	public void _testInsertText1() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSFontFaceRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(3, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;
		CSSValueList valueList;

		// rule 1

		rule = ruleList.item(0);
		assertEquals(CSSRule.FONT_FACE_RULE, rule.getType());
		assertTrue(rule instanceof CSSFontFaceRule);

		declaration = ((CSSFontFaceRule) rule).getStyle();
		assertEquals(4, declaration.getLength());

		value = declaration.getPropertyCSSValue("font-family");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_STRING, "Swiss 721"));

		value = declaration.getPropertyCSSValue("src");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_URI, "swiss721blk.pfr"));

		value = declaration.getPropertyCSSValue("font-style");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(3, valueList.getLength());

		checkPrimitiveString(valueList.item(0), new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "normal"));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(2), new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "italic"));

		value = declaration.getPropertyCSSValue("font-weight");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(3, valueList.getLength());

		checkPrimitiveNumber(valueList.item(0), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 800));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveNumber(valueList.item(2), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 900));
	}

	public void _testInsertText2() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSFontFaceRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(3, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;
		CSSValueList valueList;

		// rule 2

		rule = ruleList.item(1);
		assertEquals(CSSRule.FONT_FACE_RULE, rule.getType());
		assertTrue(rule instanceof CSSFontFaceRule);

		declaration = ((CSSFontFaceRule) rule).getStyle();
		assertEquals(6, declaration.getLength());

		value = declaration.getPropertyCSSValue("src");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(9, valueList.getLength());

		checkPrimitiveString(valueList.item(0), new PrimitiveString(ICSSPrimitiveValue.CSS_LOCAL, "Palatino"));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(2), new PrimitiveString(ICSSPrimitiveValue.CSS_LOCAL, "Times New Roman"));
		checkPrimitiveString(valueList.item(3), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(4), new PrimitiveString(ICSSPrimitiveValue.CSS_LOCAL, "New York"));
		checkPrimitiveString(valueList.item(5), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(6), new PrimitiveString(ICSSPrimitiveValue.CSS_LOCAL, "Utopia"));
		checkPrimitiveString(valueList.item(7), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(8), new PrimitiveString(CSSPrimitiveValue.CSS_URI, "http://somewhere/free/font"));

		value = declaration.getPropertyCSSValue("font-family");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "serif"));

		value = declaration.getPropertyCSSValue("font-weight");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(9, valueList.getLength());

		checkPrimitiveNumber(valueList.item(0), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 100));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveNumber(valueList.item(2), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 200));
		checkPrimitiveString(valueList.item(3), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveNumber(valueList.item(4), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 300));
		checkPrimitiveString(valueList.item(5), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveNumber(valueList.item(6), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 400));
		checkPrimitiveString(valueList.item(7), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveNumber(valueList.item(8), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 500));

		value = declaration.getPropertyCSSValue("font-style");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "normal"));

		value = declaration.getPropertyCSSValue("font-variant");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "normal"));

		value = declaration.getPropertyCSSValue("font-size");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "all"));
	}

	public void _testInsertText3() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSFontFaceRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(3, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;
		CSSValueList valueList;

		// rule 3

		rule = ruleList.item(2);
		assertEquals(CSSRule.FONT_FACE_RULE, rule.getType());
		assertTrue(rule instanceof CSSFontFaceRule);

		declaration = ((CSSFontFaceRule) rule).getStyle();
		assertEquals(5, declaration.getLength());

		value = declaration.getPropertyCSSValue("src");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(4, valueList.getLength());

		checkPrimitiveString(valueList.item(0), new PrimitiveString(ICSSPrimitiveValue.CSS_LOCAL, "Alabama Italic"));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(2), new PrimitiveString(CSSPrimitiveValue.CSS_URI, "http://www.fonts.org/A/alabama-italic"));
		checkPrimitiveString(valueList.item(3), new PrimitiveString(ICSSPrimitiveValue.CSS_FORMAT, "truetype"));

		value = declaration.getPropertyCSSValue("panose-1");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(10, valueList.getLength());

		checkPrimitiveNumber(valueList.item(0), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 2));
		checkPrimitiveNumber(valueList.item(1), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 4));
		checkPrimitiveNumber(valueList.item(2), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 5));
		checkPrimitiveNumber(valueList.item(3), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 2));
		checkPrimitiveNumber(valueList.item(4), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 5));
		checkPrimitiveNumber(valueList.item(5), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 4));
		checkPrimitiveNumber(valueList.item(6), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 5));
		checkPrimitiveNumber(valueList.item(7), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 9));
		checkPrimitiveNumber(valueList.item(8), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 3));
		checkPrimitiveNumber(valueList.item(9), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 3));

		value = declaration.getPropertyCSSValue("font-family");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(3, valueList.getLength());

		checkPrimitiveString(valueList.item(0), new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "Alabama"));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(2), new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "serif"));

		value = declaration.getPropertyCSSValue("font-weight");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(5, valueList.getLength());

		checkPrimitiveNumber(valueList.item(0), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 300));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveNumber(valueList.item(2), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 400));
		checkPrimitiveString(valueList.item(3), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveNumber(valueList.item(4), new PrimitiveNumber(ICSSPrimitiveValue.CSS_INTEGER, 500));

		value = declaration.getPropertyCSSValue("font-style");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(3, valueList.getLength());

		checkPrimitiveString(valueList.item(0), new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "italic"));
		checkPrimitiveString(valueList.item(1), new PrimitiveString(ICSSPrimitiveValue.CSS_COMMA, ","));
		checkPrimitiveString(valueList.item(2), new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "oblique"));
	}
}
