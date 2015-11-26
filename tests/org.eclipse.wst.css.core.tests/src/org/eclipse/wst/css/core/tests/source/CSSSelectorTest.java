/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.source;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.document.CSSSelectorListImpl;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorCombinator;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSimpleSelector;


public class CSSSelectorTest extends TestCase {
	public void testSelector01() {
		ICSSSelectorList list = createSelectorList("H1, H2, H3");
		checkSelectorList(list, "H1, H2, H3", 3, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H1", 1, 1, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H1", false, 0, 0, 0, 0);

		selector = list.getSelector(1);
		checkSelector(selector, "H2", 1, 1, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H2", false, 0, 0, 0, 0);

		selector = list.getSelector(2);
		checkSelector(selector, "H3", 1, 1, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H3", false, 0, 0, 0, 0);
	}

	// see 214416 Dot char is not escaped in XML10Names.jFlex
	// for complications leading to this test being changed.
	public void testSelector02() {
		ICSSSelectorList list = createSelectorList("H\\\\, H\\1, H3");
		checkSelectorList(list, "H\\\\, H\\1, H3", 3, 2);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H\\\\", 1, 1, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H\\\\", false, 0, 0, 0, 0);

		selector = list.getSelector(1);
		checkSelector(selector, "H\\1", 1, 1, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H\\1", false, 0, 0, 0, 0);

		selector = list.getSelector(2);
		checkSelector(selector, "H3", 1, 1, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H3", false, 0, 0, 0, 0);
	}

	public void testSelector03() {
		ICSSSelectorList list = createSelectorList("H1.pastoral");
		checkSelectorList(list, "H1.pastoral", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H1.pastoral", 1, 101, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H1", false, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"pastoral"});
	}

	public void testSelector04() {
		ICSSSelectorList list = createSelectorList("P.pastoral.marine");
		checkSelectorList(list, "P.pastoral.marine", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "P.pastoral.marine", 1, 201, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "P", false, 0, 2, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"pastoral", "marine"});
	}

	public void testSelector05() {
		ICSSSelectorList list = createSelectorList("*.warning");
		checkSelectorList(list, "*.warning", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "*.warning", 1, 100, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "*", true, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"warning"});
	}

	public void testSelector06() {
		ICSSSelectorList list = createSelectorList(".warning");
		checkSelectorList(list, ".warning", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, ".warning", 1, 100, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "", true, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"warning"});
	}

	public void testSelector07() {
		ICSSSelectorList list = createSelectorList("*[lang=fr]");
		checkSelectorList(list, "*[lang=fr]", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "*[lang=fr]", 1, 100, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "*", true, 1, 0, 0, 0);
		checkSimpleSelectorAttributes(item, new String[]{"lang=fr"});
	}

	public void testSelector08() {
		ICSSSelectorList list = createSelectorList("[lang=fr]");
		checkSelectorList(list, "[lang=fr]", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "[lang=fr]", 1, 100, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "", true, 1, 0, 0, 0);
		checkSimpleSelectorAttributes(item, new String[]{"lang=fr"});
	}

	public void testSelector09() {
		ICSSSelectorList list = createSelectorList("*#myid");
		checkSelectorList(list, "*#myid", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "*#myid", 1, 10000, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "*", true, 0, 0, 1, 0);
		checkSimpleSelectorIDs(item, new String[]{"myid"});
	}

	public void testSelector10() {
		ICSSSelectorList list = createSelectorList("#myid");
		checkSelectorList(list, "#myid", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "#myid", 1, 10000, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "", true, 0, 0, 1, 0);
		checkSimpleSelectorIDs(item, new String[]{"myid"});
	}

	public void testSelector11() {
		ICSSSelectorList list = createSelectorList("H1#z98y");
		checkSelectorList(list, "H1#z98y", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H1#z98y", 1, 10001, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H1", false, 0, 0, 1, 0);
		checkSimpleSelectorIDs(item, new String[]{"z98y"});
	}

	public void testSelector12() {
		ICSSSelectorList list = createSelectorList("H1 EM");
		checkSelectorList(list, "H1 EM", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H1 EM", 3, 2, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H1", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "EM", false, 0, 0, 0, 0);
	}

	public void testSelector13() {
		ICSSSelectorList list = createSelectorList("DIV * P");
		checkSelectorList(list, "DIV * P", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "DIV * P", 5, 2, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "DIV", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "*", true, 0, 0, 0, 0);

		item = selector.getItem(3);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(4);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 0);
	}

	public void testSelector14() {
		ICSSSelectorList list = createSelectorList("DIV P *[href]");
		checkSelectorList(list, "DIV P *[href]", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "DIV P *[href]", 5, 102, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "DIV", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 0);

		item = selector.getItem(3);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(4);
		checkSimpleSelector(item, "*", true, 1, 0, 0, 0);
		checkSimpleSelectorAttributes(item, new String[]{"href"});
	}

	public void testSelector15() {
		ICSSSelectorList list = createSelectorList("BODY > P");
		checkSelectorList(list, "BODY > P", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "BODY > P", 3, 2, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "BODY", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, ">", ICSSSelectorCombinator.CHILD);

		item = selector.getItem(2);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 0);
	}

	public void testSelector16() {
		ICSSSelectorList list = createSelectorList("DIV OL>LI P");
		checkSelectorList(list, "DIV OL > LI P", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "DIV OL > LI P", 7, 4, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "DIV", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "OL", false, 0, 0, 0, 0);

		item = selector.getItem(3);
		checkSelectorCombinator(item, ">", ICSSSelectorCombinator.CHILD);

		item = selector.getItem(4);
		checkSimpleSelector(item, "LI", false, 0, 0, 0, 0);

		item = selector.getItem(5);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(6);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 0);
	}

	public void testSelector17() {
		ICSSSelectorList list = createSelectorList("MATH + P");
		checkSelectorList(list, "MATH + P", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "MATH + P", 3, 2, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "MATH", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, "+", ICSSSelectorCombinator.ADJACENT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 0);
	}

	public void testSelector18() {
		ICSSSelectorList list = createSelectorList("H1.opener + H2");
		checkSelectorList(list, "H1.opener + H2", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H1.opener + H2", 3, 102, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H1", false, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"opener"});

		item = selector.getItem(1);
		checkSelectorCombinator(item, "+", ICSSSelectorCombinator.ADJACENT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "H2", false, 0, 0, 0, 0);
	}

	public void testSelector19() {
		ICSSSelectorList list = createSelectorList("EXAMPLE[notation=decimal]");
		checkSelectorList(list, "EXAMPLE[notation=decimal]", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "EXAMPLE[notation=decimal]", 1, 101, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "EXAMPLE", false, 1, 0, 0, 0);
		checkSimpleSelectorAttributes(item, new String[]{"notation=decimal"});
	}

	public void testSelector20() {
		ICSSSelectorList list = createSelectorList("SPAN[hello=\"Cleveland\"][goodbye=\"Columbus\"]");
		checkSelectorList(list, "SPAN[hello=\"Cleveland\"][goodbye=\"Columbus\"]", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "SPAN[hello=\"Cleveland\"][goodbye=\"Columbus\"]", 1, 201, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "SPAN", false, 2, 0, 0, 0);
		checkSimpleSelectorAttributes(item, new String[]{"hello=\"Cleveland\"", "goodbye=\"Columbus\""});
	}

	public void testSelector21() {
		ICSSSelectorList list = createSelectorList("DIV > P:first-child");
		checkSelectorList(list, "DIV > P:first-child", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "DIV > P:first-child", 3, 102, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "DIV", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, ">", ICSSSelectorCombinator.CHILD);

		item = selector.getItem(2);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"first-child"});
	}

	public void testSelector22() {
		ICSSSelectorList list = createSelectorList("P:first-child EM");
		checkSelectorList(list, "P:first-child EM", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "P:first-child EM", 3, 102, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"first-child"});

		item = selector.getItem(1);
		checkSelectorCombinator(item, " ", ICSSSelectorCombinator.DESCENDANT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "EM", false, 0, 0, 0, 0);
	}

	public void testSelector23() {
		ICSSSelectorList list = createSelectorList("* > A:first-child");
		checkSelectorList(list, "* > A:first-child", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "* > A:first-child", 3, 101, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "*", true, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, ">", ICSSSelectorCombinator.CHILD);

		item = selector.getItem(2);
		checkSimpleSelector(item, "A", false, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"first-child"});
	}

	public void testSelector24() {
		ICSSSelectorList list = createSelectorList("A:link");
		checkSelectorList(list, "A:link", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "A:link", 1, 101, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "A", false, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"link"});
	}

	public void testSelector25() {
		ICSSSelectorList list = createSelectorList(":link");
		checkSelectorList(list, ":link", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, ":link", 1, 100, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "", true, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"link"});
	}

	public void testSelector26() {
		ICSSSelectorList list = createSelectorList("A:focus:hover");
		checkSelectorList(list, "A:focus:hover", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "A:focus:hover", 1, 201, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "A", false, 0, 0, 0, 2);
		checkSimpleSelectorPseudoNames(item, new String[]{"focus", "hover"});
	}

	public void testSelector27() {
		ICSSSelectorList list = createSelectorList("HTML:lang(de) > Q");
		checkSelectorList(list, "HTML:lang(de) > Q", 1, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "HTML:lang(de) > Q", 3, 102, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "HTML", false, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"lang(de)"});

		item = selector.getItem(1);
		checkSelectorCombinator(item, ">", ICSSSelectorCombinator.CHILD);

		item = selector.getItem(2);
		checkSimpleSelector(item, "Q", false, 0, 0, 0, 0);
	}

	public void testSelector28() {
		ICSSSelectorList list = createSelectorList("P > A:link, A.external:visited");
		checkSelectorList(list, "P > A:link, A.external:visited", 2, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "P > A:link", 3, 102, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "P", false, 0, 0, 0, 0);

		item = selector.getItem(1);
		checkSelectorCombinator(item, ">", ICSSSelectorCombinator.CHILD);

		item = selector.getItem(2);
		checkSimpleSelector(item, "A", false, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"link"});

		selector = list.getSelector(1);
		checkSelector(selector, "A.external:visited", 1, 201, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "A", false, 0, 1, 0, 1);
		checkSimpleSelectorClasses(item, new String[]{"external"});
		checkSimpleSelectorPseudoNames(item, new String[]{"visited"});
	}

	public void testSelector29() {
		ICSSSelectorList list = createSelectorList("P#hoge98 + *:hover > A:link, A.external:visited");
		checkSelectorList(list, "P#hoge98 + *:hover > A:link, A.external:visited", 2, 0);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "P#hoge98 + *:hover > A:link", 5, 10202, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "P", false, 0, 0, 1, 0);
		checkSimpleSelectorIDs(item, new String[]{"hoge98"});

		item = selector.getItem(1);
		checkSelectorCombinator(item, "+", ICSSSelectorCombinator.ADJACENT);

		item = selector.getItem(2);
		checkSimpleSelector(item, "*", true, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"hover"});

		item = selector.getItem(3);
		checkSelectorCombinator(item, ">", ICSSSelectorCombinator.CHILD);

		item = selector.getItem(4);
		checkSimpleSelector(item, "A", false, 0, 0, 0, 1);
		checkSimpleSelectorPseudoNames(item, new String[]{"link"});

		selector = list.getSelector(1);
		checkSelector(selector, "A.external:visited", 1, 201, 0);

		item = selector.getItem(0);
		checkSimpleSelector(item, "A", false, 0, 1, 0, 1);
		checkSimpleSelectorClasses(item, new String[]{"external"});
		checkSimpleSelectorPseudoNames(item, new String[]{"visited"});
	}

	public void testSelector30() {
		ICSSSelectorList list = createSelectorList("H1.123");
		checkSelectorList(list, "H1.123", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H1.123", 1, 101, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H1", false, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"123"});
	}

	public void testSelector31() {
		ICSSSelectorList list = createSelectorList("P.123.456");
		checkSelectorList(list, "P.123.456", 1, 2);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "P.123.456", 1, 201, 2);

		item = selector.getItem(0);
		checkSimpleSelector(item, "P", false, 0, 2, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"123", "456"});
	}

	public void testSelector32() {
		ICSSSelectorList list = createSelectorList("*.123");
		checkSelectorList(list, "*.123", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "*.123", 1, 100, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "*", true, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"123"});
	}

	public void testSelector33() {
		ICSSSelectorList list = createSelectorList(".123");
		checkSelectorList(list, ".123", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, ".123", 1, 100, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "", true, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"123"});
	}

	public void testSelector34() {
		ICSSSelectorList list = createSelectorList("H1.1x3");
		checkSelectorList(list, "H1.1x3", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H1.1x3", 1, 101, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H1", false, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"1x3"});
	}

	public void testSelector35() {
		ICSSSelectorList list = createSelectorList("H2.123#46");
		checkSelectorList(list, "H2.123#46", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H2.123#46", 1, 10101, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H2", false, 0, 1, 1, 0);
		checkSimpleSelectorClasses(item, new String[]{"123"});
		checkSimpleSelectorIDs(item, new String[]{"46"});
	}

	public void testSelector36() {
		ICSSSelectorList list = createSelectorList("H3.1x3#4t2");
		checkSelectorList(list, "H3.1x3#4t2", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "H3.1x3#4t2", 1, 10101, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "H3", false, 0, 1, 1, 0);
		checkSimpleSelectorClasses(item, new String[]{"1x3"});
		checkSimpleSelectorIDs(item, new String[]{"4t2"});
	}

	public void testSelector37() {
		ICSSSelectorList list = createSelectorList("*.123");
		checkSelectorList(list, "*.123", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, "*.123", 1, 100, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "*", true, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"123"});
	}

	public void testSelector38() {
		ICSSSelectorList list = createSelectorList(".123");
		checkSelectorList(list, ".123", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, ".123", 1, 100, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "", true, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"123"});
	}

	public void testSelector39() {
		ICSSSelectorList list = createSelectorList(".123f567");
		checkSelectorList(list, ".123f567", 1, 1);

		ICSSSelector selector;
		ICSSSelectorItem item;

		selector = list.getSelector(0);
		checkSelector(selector, ".123f567", 1, 100, 1);

		item = selector.getItem(0);
		checkSimpleSelector(item, "", true, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[]{"123f567"});
	}

	// Bug 221504: whitespace preceding a selector separator was causing selectors
	// to be merged
	public void testSelector40() {
		ICSSSelectorList list = createSelectorList("h1.fix , h2.fix, h3.fix , div#container");
		checkSelectorList(list, "h1.fix, h2.fix, h3.fix, div#container", 4, 0);
		
		ICSSSelector selector;
		ICSSSelectorItem item;
		
		selector = list.getSelector(0);
		checkSelector(selector, "h1.fix", 1, 101, 0);
		
		item = selector.getItem(0);
		checkSimpleSelector(item, "h1", false, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[] {"fix"});
		
		selector = list.getSelector(1);
		checkSelector(selector, "h2.fix", 1, 101, 0);
		
		item = selector.getItem(0);
		checkSimpleSelector(item, "h2", false, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[] {"fix"});
		
		selector = list.getSelector(2);
		checkSelector(selector, "h3.fix", 1, 101, 0);
		
		item = selector.getItem(0);
		checkSimpleSelector(item, "h3", false, 0, 1, 0, 0);
		checkSimpleSelectorClasses(item, new String[] {"fix"});
		
		selector = list.getSelector(3);
		checkSelector(selector, "div#container", 1, 10001, 0);
		
		item = selector.getItem(0);
		checkSimpleSelector(item, "div", false, 0, 0, 1, 0);
		checkSimpleSelectorIDs(item, new String[] {"container"});
	}

	private void checkSelectorList(ICSSSelectorList list, String formattedSource, int nSelectors, int nErrors) {
		assertEquals(formattedSource, list.getString());
		assertEquals(nSelectors, list.getLength());
		assertEquals(nErrors, list.getErrorCount());
	}

	private void checkSelector(ICSSSelector selector, String formattedSource, int items, int specificity, int nErrors) {
		assertEquals(formattedSource, selector.getString());
		assertEquals(items, selector.getLength());
		assertEquals(specificity, selector.getSpecificity());
		assertEquals(nErrors, selector.getErrorCount());
	}

	private void checkSimpleSelector(ICSSSelectorItem selector, String formattedSource, boolean isUniversal, int nAttributes, int nClasses, int nIds, int nPseudoNames) {
		assertTrue(selector instanceof ICSSSimpleSelector);
		assertEquals(formattedSource, ((ICSSSimpleSelector) selector).getName());
		assertEquals(isUniversal, ((ICSSSimpleSelector) selector).isUniversal());
		assertEquals(nAttributes, ((ICSSSimpleSelector) selector).getNumOfAttributes());
		assertEquals(nClasses, ((ICSSSimpleSelector) selector).getNumOfClasses());
		assertEquals(nIds, ((ICSSSimpleSelector) selector).getNumOfIDs());
		assertEquals(nPseudoNames, ((ICSSSimpleSelector) selector).getNumOfPseudoNames());
	}

	private void checkSimpleSelectorAttributes(ICSSSelectorItem selector, String[] items) {
		int nItems = ((ICSSSimpleSelector) selector).getNumOfAttributes();
		assertEquals(items.length, nItems);
		for (int i = 0; i < nItems; i++) {
			assertEquals(items[i], ((ICSSSimpleSelector) selector).getAttribute(i));
		}
	}

	private void checkSimpleSelectorClasses(ICSSSelectorItem selector, String[] items) {
		int nItems = ((ICSSSimpleSelector) selector).getNumOfClasses();
		assertEquals(items.length, nItems);
		for (int i = 0; i < nItems; i++) {
			assertEquals(items[i], ((ICSSSimpleSelector) selector).getClass(i));
		}
	}

	private void checkSimpleSelectorIDs(ICSSSelectorItem selector, String[] items) {
		int nItems = ((ICSSSimpleSelector) selector).getNumOfIDs();
		assertEquals(items.length, nItems);
		for (int i = 0; i < nItems; i++) {
			assertEquals(items[i], ((ICSSSimpleSelector) selector).getID(i));
		}
	}

	private void checkSimpleSelectorPseudoNames(ICSSSelectorItem selector, String[] items) {
		int nItems = ((ICSSSimpleSelector) selector).getNumOfPseudoNames();
		assertEquals(items.length, nItems);
		for (int i = 0; i < nItems; i++) {
			assertEquals(items[i], ((ICSSSimpleSelector) selector).getPseudoName(i));
		}
	}

	private void checkSelectorCombinator(ICSSSelectorItem combinator, String formattedSource, int combinatorType) {
		assertTrue(combinator instanceof ICSSSelectorCombinator);
		assertEquals(formattedSource, ((ICSSSelectorCombinator) combinator).getString());
		assertEquals(combinatorType, ((ICSSSelectorCombinator) combinator).getCombinatorType());
	}

	private ICSSSelectorList createSelectorList(String str) {
		return new CSSSelectorListImpl(str);
	}
}
