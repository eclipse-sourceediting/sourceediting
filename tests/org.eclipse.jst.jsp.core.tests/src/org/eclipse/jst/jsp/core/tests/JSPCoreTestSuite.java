/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.core.tests.cleanupformat.FormatTester;
import org.eclipse.jst.jsp.core.tests.contentmodels.TestFixedCMDocuments;
import org.eclipse.jst.jsp.core.tests.contentmodels.TestTaglibCMTests;
import org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific.TestContentTypeHandlers;
import org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific.TestModelHandlers;
import org.eclipse.jst.jsp.core.tests.dom.AttrValueTest;
import org.eclipse.jst.jsp.core.tests.dom.TestImportedNodes;
import org.eclipse.jst.jsp.core.tests.model.TestModelIncludes;
import org.eclipse.jst.jsp.core.tests.model.TestModelRelease;
import org.eclipse.jst.jsp.core.tests.model.TestModelWithNoFile;
import org.eclipse.jst.jsp.core.tests.source.JSPTokenizerTest;
import org.eclipse.jst.jsp.core.tests.tei.TEIValidation;
import org.eclipse.jst.jsp.core.tests.translation.JSPJavaTranslatorCoreTest;
import org.eclipse.jst.jsp.core.tests.translation.JSPJavaTranslatorCustomTagTest;
import org.eclipse.jst.jsp.core.tests.translation.JSPTranslatorPersistenceTest;
import org.eclipse.jst.jsp.core.tests.validation.JSPActionValidatorTest;
import org.eclipse.jst.jsp.core.tests.validation.JSPBatchValidatorTest;
import org.eclipse.jst.jsp.core.tests.validation.JSPDirectiveValidatorTest;
import org.eclipse.jst.jsp.core.tests.validation.JSPJavaValidatorTest;
import org.eclipse.jst.jsp.css.core.tests.source.JSPedCSSSourceParserTest;

public class JSPCoreTestSuite extends TestSuite {
	public static Test suite() {
		return new JSPCoreTestSuite();
	}

	public JSPCoreTestSuite() {
		super("JSP Core Test Suite");

		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		String wtp_autotest_noninteractive = null;
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");

		addTest(TestCeanupFormat.suite());
		addTest(ModelCloneSuite.suite());
		addTest(new TestSuite(TestModelHandlers.class, "Test JSP Model Handlers"));
		addTest(new TestSuite(TestContentTypeHandlers.class, "Test JSP Content Type Handlers"));
		addTest(new TestSuite(TestModelManager.class, "Test Model Manager with JSP"));
		addTest(new TestSuite(FormatTester.class, "Format Tester"));
		addTest(new TestSuite(TestModelRelease.class, "Model Tests"));
		addTest(new TestSuite(TestModelWithNoFile.class, "Model with no file Tests"));
		// temporarily removed since unstable, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=213754 
		// addTest(new TestSuite(TestIndex.class, "TaglibIndex Tests"));
		addTest(new TestSuite(JSPTokenizerTest.class, "Special Parsing Tests"));
		addTest(new TestSuite(AttrValueTest.class, "JSP Attribute Tests"));
		addTest(new TestSuite(JSPJavaTranslatorCoreTest.class, "Core Java Translator Tests"));
		addTest(new TestSuite(TestModelIncludes.class, "Core Fragment Inclusion Tests"));
		addTest(new TestSuite(JSPCorePreferencesTest.class, "Preference Tests"));
		addTest(new TestSuite(JSPedCSSSourceParserTest.class, "Special Parsing Tests for JSP-CSS content"));
		addTest(new TestSuite(TEIValidation.class, "TagExtraInfo Validation Tests"));
		addTest(new TestSuite(JSPJavaTranslatorCustomTagTest.class, "Java Custom Tag Tests"));
		addTest(new TestSuite(JSPJavaValidatorTest.class, "Java Validator Tests"));
		addTest(new TestSuite(TestImportedNodes.class, "Imported Nodes Tests"));
		addTest(new TestSuite(TestFixedCMDocuments.class, "Fixed CMDocument Creation Tests"));
		addTest(new TestSuite(TestTaglibCMTests.class, "Custom Tag Library Content Model Tests"));
		addTest(new TestSuite(JSPActionValidatorTest.class, "Action Validator Tests"));
		addTest(new TestSuite(JSPBatchValidatorTest.class, "Batch Validator Tests"));
		addTest(new TestSuite(JSPDirectiveValidatorTest.class, "Directive Validator Tests"));
		addTest(JSPTranslatorPersistenceTest.suite());

		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
	}
}
