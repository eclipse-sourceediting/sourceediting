/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

public class CSSPageRuleTest extends AbstractModelTest {
	public void testInsertText1() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSPageRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(6, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;
		CSSValueList valueList;

		// rule 1

		rule = ruleList.item(0);
		assertEquals(CSSRule.PAGE_RULE, rule.getType());
		assertTrue(rule instanceof CSSPageRule);

		declaration = ((CSSPageRule) rule).getStyle();
		assertEquals(2, declaration.getLength());

		value = declaration.getPropertyCSSValue("size");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(2, valueList.getLength());

		checkPrimitiveNumber(valueList.item(0), new PrimitiveNumber(CSSPrimitiveValue.CSS_IN, (float) 8.5));
		checkPrimitiveNumber(valueList.item(1), new PrimitiveNumber(CSSPrimitiveValue.CSS_IN, 11));

		value = declaration.getPropertyCSSValue("margin");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 2));
	}

	public void testInsertText2() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSPageRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(6, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		// rule 2

		rule = ruleList.item(1);
		assertEquals(CSSRule.PAGE_RULE, rule.getType());
		assertTrue(rule instanceof CSSPageRule);

		declaration = ((CSSPageRule) rule).getStyle();
		assertEquals(2, declaration.getLength());

		value = declaration.getPropertyCSSValue("size");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "auto"));

		value = declaration.getPropertyCSSValue("margin");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_PERCENTAGE, 10));
	}

	public void testInsertText3() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSPageRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(6, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		// rule 3

		rule = ruleList.item(2);
		assertEquals(CSSRule.PAGE_RULE, rule.getType());
		assertTrue(rule instanceof CSSPageRule);

		assertEquals(":left", ((CSSPageRule) rule).getSelectorText());

		declaration = ((CSSPageRule) rule).getStyle();
		assertEquals(2, declaration.getLength());

		value = declaration.getPropertyCSSValue("margin-left");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 4));

		value = declaration.getPropertyCSSValue("margin-right");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 3));
	}

	public void testInsertText4() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSPageRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(6, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		// rule 4

		rule = ruleList.item(3);
		assertEquals(CSSRule.PAGE_RULE, rule.getType());
		assertTrue(rule instanceof CSSPageRule);

		assertEquals(":right", ((CSSPageRule) rule).getSelectorText());

		declaration = ((CSSPageRule) rule).getStyle();
		assertEquals(2, declaration.getLength());

		value = declaration.getPropertyCSSValue("margin-left");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 3));

		value = declaration.getPropertyCSSValue("margin-right");
		checkPrimitiveNumber(value, new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 4));
	}

	public void testInsertText5() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSPageRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(6, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;
		CSSValueList valueList;

		// rule 5

		rule = ruleList.item(4);
		assertEquals(CSSRule.PAGE_RULE, rule.getType());
		assertTrue(rule instanceof CSSPageRule);

		assertEquals("narrow", ((CSSPageRule) rule).getSelectorText());

		declaration = ((CSSPageRule) rule).getStyle();
		assertEquals(1, declaration.getLength());

		value = declaration.getPropertyCSSValue("size");
		assertTrue(value instanceof CSSValueList);

		valueList = (CSSValueList) value;
		assertEquals(2, valueList.getLength());

		checkPrimitiveNumber(valueList.item(0), new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 9));
		checkPrimitiveNumber(valueList.item(1), new PrimitiveNumber(CSSPrimitiveValue.CSS_CM, 18));
	}

	public void testInsertText6() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSPageRuleTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList ruleList = sheet.getCssRules();
		assertEquals(6, ruleList.getLength());

		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		// rule 6

		rule = ruleList.item(5);
		assertEquals(CSSRule.PAGE_RULE, rule.getType());
		assertTrue(rule instanceof CSSPageRule);

		assertEquals("rotated", ((CSSPageRule) rule).getSelectorText());

		declaration = ((CSSPageRule) rule).getStyle();
		assertEquals(1, declaration.getLength());

		value = declaration.getPropertyCSSValue("size");
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "landscape"));
	}
}
