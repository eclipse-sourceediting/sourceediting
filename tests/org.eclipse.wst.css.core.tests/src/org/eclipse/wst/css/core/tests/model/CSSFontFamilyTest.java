/*******************************************************************************
 * Copyright (c) 2009, 2013 IBM Corporation and others.
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
import org.eclipse.wst.css.core.internal.provisional.document.ICSSValue;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;


public class CSSFontFamilyTest extends AbstractModelTest {

	private static final String FONT_FAMILY = "font-family";

	public void testFontFamilies() throws IOException  {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSFontFamilyTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList rules = sheet.getCssRules();
		assertEquals(4, rules.getLength());

		// Rule 1: No whitespace
		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		// Rule 1: No whitespace
		rule = rules.item(0);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		value = declaration.getPropertyCSSValue(FONT_FAMILY);
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "Courier"));

		// Rule 2: Single whitespace
		rule = rules.item(1);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		value = declaration.getPropertyCSSValue(FONT_FAMILY);
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "Courier New"));

		// Rule 3: In quotes
		rule = rules.item(2);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		value = declaration.getPropertyCSSValue(FONT_FAMILY);
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_STRING, "Courier New"));

		// Rule 4: Tabs and spaces all over
		rule = rules.item(3);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		value = declaration.getPropertyCSSValue(FONT_FAMILY);
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "Comic Sans"));
	}

	public void testValueModification() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString("src/org/eclipse/wst/css/core/tests/testfiles", "CSSFontFamilyTest.css"));

		CSSStyleSheet sheet = (CSSStyleSheet) model.getDocument();
		CSSRuleList rules = sheet.getCssRules();
		assertEquals(4, rules.getLength());

		// Rule 1: No whitespace
		CSSRule rule;
		CSSStyleDeclaration declaration;
		CSSValue value;

		// Rule 1: No whitespace
		rule = rules.item(0);
		assertEquals(CSSRule.STYLE_RULE, rule.getType());
		assertTrue(rule instanceof CSSStyleRule);

		declaration = ((CSSStyleRule) rule).getStyle();
		value = declaration.getPropertyCSSValue(FONT_FAMILY);
		checkPrimitiveString(value, new PrimitiveString(CSSPrimitiveValue.CSS_IDENT, "Courier"));

		declaration.setProperty(FONT_FAMILY, "\"Times New Roman\", Times, serif", "");
		value = declaration.getPropertyCSSValue(FONT_FAMILY);
		assertTrue(value instanceof ICSSValue);
		assertEquals("\"Times New Roman\", Times, serif", ((ICSSValue) value).getCSSValueText());

		declaration.setProperty(FONT_FAMILY, "\"Times New Roman\", Times", "");
		value = declaration.getPropertyCSSValue(FONT_FAMILY);
		assertTrue(value instanceof ICSSValue);
		assertEquals("\"Times New Roman\", Times", ((ICSSValue) value).getCSSValueText());
	}
}
