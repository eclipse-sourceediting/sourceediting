/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
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
import org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific.TestContentTypeHandlers;
import org.eclipse.jst.jsp.core.tests.contenttypeidentifier.contentspecific.TestModelHandlers;
import org.eclipse.jst.jsp.core.tests.model.TestModelIncludes;
import org.eclipse.jst.jsp.core.tests.model.TestModelRelease;
import org.eclipse.jst.jsp.core.tests.model.TestModelWithNoFile;
import org.eclipse.jst.jsp.core.tests.source.JSPTokenizerTest;
import org.eclipse.jst.jsp.core.tests.taglibindex.TestIndex;



public class JSPCoreTestSuite extends TestSuite {
	public static Test suite() {
		return new JSPCoreTestSuite();
	}

	public JSPCoreTestSuite() {
		super("SSEModelTestSuiteForJSP");

		addTest(TestCeanupFormat.suite());
		addTest(ModelCloneSuite.suite());
		addTest(new TestSuite(TestModelHandlers.class, "TestModelHandlers"));
		addTest(new TestSuite(TestContentTypeHandlers.class, "TestContentTypeHandlers"));
		addTest(new TestSuite(TestModelManager.class, "TestModelManager"));
		addTest(new TestSuite(FormatTester.class, "FormatTester"));
		addTest(new TestSuite(TestModelRelease.class, "JSP Model Tests"));
		addTest(new TestSuite(TestModelWithNoFile.class, "JSP Model Tests"));
		addTest(new TestSuite(TestIndex.class, "TaglibIndex Tests"));
		addTest(new TestSuite(JSPTokenizerTest.class, "Special Parsing Tests"));
		addTest(new TestSuite(TestModelIncludes.class));
		addTest(new TestSuite(JSPCorePreferencesTest.class));
	}
}