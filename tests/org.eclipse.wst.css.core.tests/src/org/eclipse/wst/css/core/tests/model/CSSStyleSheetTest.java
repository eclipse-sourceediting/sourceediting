/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.core.tests.model;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;


public class CSSStyleSheetTest extends AbstractModelTest {
	private final static String RULE_H1 = "H1 { color : red; }";
	private final static String RULE_H2 = "H2 { color : red; }";
	private final static String RULE_H3 = "H3 { color : red; }";

	public void testInsertRule() {
		CSSStyleSheet sheet = getStyleSheet();

		assertEquals(0, sheet.insertRule(RULE_H3, 0));
		assertEquals(0, sheet.insertRule(RULE_H1, 0));
		assertEquals(1, sheet.insertRule(RULE_H2, 1));

		CSSRuleList ruleList = sheet.getCssRules();
		CSSRule rule;

		rule = ruleList.item(0);
		assertEquals(RULE_H1, rule.getCssText());
		rule = ruleList.item(1);
		assertEquals(RULE_H2, rule.getCssText());
		rule = ruleList.item(2);
		assertEquals(RULE_H3, rule.getCssText());

	}

	public void testDeleteRule() {
		CSSStyleSheet sheet = getStyleSheet();

		assertEquals(0, sheet.insertRule(RULE_H3, 0));
		assertEquals(0, sheet.insertRule(RULE_H1, 0));
		assertEquals(1, sheet.insertRule(RULE_H2, 1));

		CSSRuleList ruleList;
		CSSRule rule;

		sheet.deleteRule(1);
		ruleList = sheet.getCssRules();

		rule = ruleList.item(0);
		assertEquals(RULE_H1, rule.getCssText());
		rule = ruleList.item(1);
		assertEquals(RULE_H3, rule.getCssText());

		sheet.deleteRule(1);
		ruleList = sheet.getCssRules();

		rule = ruleList.item(0);
		assertEquals(RULE_H1, rule.getCssText());

		sheet.deleteRule(0);

		try {
			sheet.deleteRule(0);
		}
		catch (DOMException e) {
			assertEquals(DOMException.INDEX_SIZE_ERR, e.code);
		}
	}
}