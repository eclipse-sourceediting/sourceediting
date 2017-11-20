/*****************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.css.core.tests.format.TestCleanupProcessorCSS;
import org.eclipse.wst.css.core.tests.format.TestFormatProcessorCSS;
import org.eclipse.wst.css.core.tests.model.CSSCharsetRuleTest;
import org.eclipse.wst.css.core.tests.model.CSSFontFaceRuleTest;
import org.eclipse.wst.css.core.tests.model.CSSFontFamilyTest;
import org.eclipse.wst.css.core.tests.model.CSSImportRuleTest;
import org.eclipse.wst.css.core.tests.model.CSSMediaRuleTest;
import org.eclipse.wst.css.core.tests.model.CSSMetaModelTest;
import org.eclipse.wst.css.core.tests.model.CSSPageRuleTest;
import org.eclipse.wst.css.core.tests.model.CSSStyleRuleTest;
import org.eclipse.wst.css.core.tests.model.CSSStyleSheetTest;
import org.eclipse.wst.css.core.tests.model.TestCSSDecl;
import org.eclipse.wst.css.core.tests.source.CSSSelectorTest;
import org.eclipse.wst.css.core.tests.source.CSSSourceParserTest;
import org.eclipse.wst.css.core.tests.source.CSSTextParserTest;
import org.eclipse.wst.css.core.tests.source.CSSUrlTest;
import org.eclipse.wst.css.core.tests.stylesheets.StyleSheetTest;
import org.eclipse.wst.css.core.tests.testfiles.CSSModelLoadTest;


public class CSSAllTests extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("SSE Model CSS Tests");

		addSourceTests(suite);
		addModelTests(suite);

		suite.addTestSuite(CSSCorePreferencesTest.class);
		suite.addTestSuite(StyleSheetTest.class);
		return suite;
	}

	private static void addSourceTests(TestSuite suite) {
		suite.addTestSuite(CSSSourceParserTest.class);
		suite.addTestSuite(CSSTextParserTest.class);
		suite.addTestSuite(CSSSelectorTest.class);
		suite.addTestSuite(CSSUrlTest.class);
	}

	private static void addModelTests(TestSuite suite) {
		suite.addTestSuite(CSSStyleSheetTest.class);
		suite.addTestSuite(CSSCharsetRuleTest.class);
		suite.addTestSuite(CSSImportRuleTest.class);
		suite.addTestSuite(CSSStyleRuleTest.class);
		suite.addTestSuite(CSSMediaRuleTest.class);
		suite.addTestSuite(CSSPageRuleTest.class);
		suite.addTestSuite(CSSFontFaceRuleTest.class);
		suite.addTestSuite(CSSFontFamilyTest.class);
		suite.addTestSuite(TestFormatProcessorCSS.class);
		suite.addTestSuite(TestCleanupProcessorCSS.class);
		suite.addTestSuite(TestCSSDecl.class);
		suite.addTestSuite(CSSModelLoadTest.class);
		suite.addTestSuite(CSSMetaModelTest.class);
	}
}