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

import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;

public class CSSImportRuleTest extends AbstractModelTest {

	public void testInsertRuleDoubleQuote() {
		checkInsert("@import \"dummy.css\";", "dummy.css", null);
	}

	public void testInsertRuleSingleQuote() {
		checkInsert("@import \'dummy.css\';", "dummy.css", null);
	}

	public void testInsertRuleUrlNoQuote() {
		checkInsert("@import url(dummy.css);", "dummy.css", null);
	}

	public void testInsertRuleUrlDoubleQuote() {
		checkInsert("@import url(\"dummy.css\");", "dummy.css", null);
	}

	public void testInsertRuleUrlSingleQuote() {
		checkInsert("@import url(\'dummy.css\');", "dummy.css", null);
	}

	public void testInsertRuleMedia1() {
		checkInsert("@import url(\"dummy.css\") media1;", "dummy.css", new String[]{"media1"});
	}

	public void testInsertRuleMedia2() {
		checkInsert("@import url(\'dummy.css\') media1, media2;", "dummy.css", new String[]{"media1", "media2"});
	}

	public void testCreateRule() {
		ICSSStyleSheet sheet = getStyleSheet();
		ICSSDocument doc = sheet;
		ICSSImportRule newRule = doc.createCSSImportRule();
		newRule.setHref("dummy.css");
		MediaList newList = newRule.getMedia();
		newList.appendMedium("media1");
		newList.appendMedium("media2");
		sheet.insertRuleBefore(newRule, null);

		CSSRuleList ruleList = sheet.getCssRules();
		CSSRule rule = ruleList.item(0);
		assertTrue(rule instanceof CSSImportRule);
		CSSImportRule importRule = (CSSImportRule) rule;
		assertEquals("dummy.css", importRule.getHref());
		MediaList mediaList = importRule.getMedia();
		assertEquals(2, mediaList.getLength());
		assertEquals("media1", mediaList.item(0));
		assertEquals("media2", mediaList.item(1));

		assertEquals("@import url(\"dummy.css\") media1, media2;", importRule.getCssText());
	}

	private void checkInsert(String ruleString, String href, String[] media) {
		CSSStyleSheet sheet = getStyleSheet();

		assertEquals(0, sheet.insertRule(ruleString, 0));

		CSSRuleList ruleList = sheet.getCssRules();
		CSSRule rule = ruleList.item(0);
		assertTrue(rule instanceof CSSImportRule);

		CSSImportRule importRule = (CSSImportRule) rule;
		assertEquals(href, importRule.getHref());

		if (media != null) {
			assertEquals(media.length, importRule.getMedia().getLength());
			for (int i = 0; i < media.length; i++) {
				assertEquals(media[i], importRule.getMedia().item(i));
			}
		}

		assertEquals(ruleString, importRule.getCssText());
	}
}
