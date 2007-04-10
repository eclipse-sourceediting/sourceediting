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
import org.eclipse.wst.css.core.internal.provisional.document.ICSSMediaRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.stylesheets.MediaList;

public class CSSMediaRuleTest extends AbstractModelTest {
	public void testCreateRule() {
		ICSSStyleSheet sheet = getStyleSheet();
		ICSSDocument doc = sheet;
		ICSSMediaRule newRule = doc.createCSSMediaRule();
		MediaList newList = newRule.getMedia();
		newList.appendMedium("media1");
		newList.appendMedium("media2");
		sheet.insertRuleBefore(newRule, null);

		CSSRuleList ruleList = sheet.getCssRules();
		CSSRule rule = ruleList.item(0);
		assertTrue(rule instanceof CSSMediaRule);
		CSSMediaRule mediaRule = (CSSMediaRule) rule;

		MediaList mediaList = mediaRule.getMedia();
		assertEquals(2, mediaList.getLength());
		assertEquals("media1", mediaList.item(0));
		assertEquals("media2", mediaList.item(1));

		assertEquals("@media media1, media2 {" + FileUtil.commonEOL + "}", mediaRule.getCssText());
	}
}
