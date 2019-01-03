/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.model;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

public class CSSCharsetRuleTest extends AbstractModelTest {
	private final String RULE = "@charset \"iso-8859-1\";";

	public void testInsertRule() {
		CSSStyleSheet sheet = getStyleSheet();

		assertEquals(0, sheet.insertRule(RULE, 0));

		CSSRuleList ruleList = sheet.getCssRules();
		CSSRule rule = ruleList.item(0);
		assertTrue(rule instanceof CSSCharsetRule);
		CSSCharsetRule charsetRule = (CSSCharsetRule) rule;
		assertEquals("iso-8859-1", charsetRule.getEncoding());
		assertEquals(RULE, charsetRule.getCssText());
	}

	public void testCreateRule() {
		ICSSStyleSheet sheet = getStyleSheet();
		ICSSDocument doc = sheet;
		CSSCharsetRule newRule = doc.createCSSCharsetRule();
		newRule.setEncoding("iso-8859-1");
		sheet.insertRuleBefore(newRule, null);

		CSSRuleList ruleList = sheet.getCssRules();
		CSSRule rule = ruleList.item(0);
		assertTrue(rule instanceof CSSCharsetRule);
		CSSCharsetRule charsetRule = (CSSCharsetRule) rule;
		assertEquals("iso-8859-1", charsetRule.getEncoding());
		assertEquals(RULE, charsetRule.getCssText());
	}
}
