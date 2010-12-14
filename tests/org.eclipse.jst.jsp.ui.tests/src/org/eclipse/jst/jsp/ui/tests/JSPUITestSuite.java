/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.tests.JFaceTextTestSuite;
import org.eclipse.jst.jsp.ui.tests.breakpointproviders.BreakpointProvidersTest;
import org.eclipse.jst.jsp.ui.tests.contentassist.BeanInfoProviderTest;
import org.eclipse.jst.jsp.ui.tests.contentassist.JSPELContentAssistTest;
import org.eclipse.jst.jsp.ui.tests.contentassist.JSPJavaTranslatorTest;
import org.eclipse.jst.jsp.ui.tests.contentassist.JSPTranslationTest;
import org.eclipse.jst.jsp.ui.tests.contentassist.TestJSPContentAssistComputers;
import org.eclipse.jst.jsp.ui.tests.contentdescription.TestContentDescription;
import org.eclipse.jst.jsp.ui.tests.document.FileBufferDocumentTester;
import org.eclipse.jst.jsp.ui.tests.model.TestModelsFromFiles;
import org.eclipse.jst.jsp.ui.tests.modelquery.ModelQueryTester;
import org.eclipse.jst.jsp.ui.tests.other.ScannerUnitTests;
import org.eclipse.jst.jsp.ui.tests.other.UnitTests;
import org.eclipse.jst.jsp.ui.tests.pagedirective.TestPageDirective;
import org.eclipse.jst.jsp.ui.tests.partitioning.TestStructuredPartitionerJSP;
import org.eclipse.jst.jsp.ui.tests.registry.AdapterFactoryRegistryTest;
import org.eclipse.jst.jsp.ui.tests.validation.JSPHTMLValidatorTest;
import org.eclipse.jst.jsp.ui.tests.viewer.TestViewerConfigurationJSP;
import org.eclipse.text.tests.EclipseTextTestSuite;




public class JSPUITestSuite extends TestSuite {
	public static Test suite() {
		return new JSPUITestSuite();
	}

	public JSPUITestSuite() {
		super("JSP UI Test Suite");
		
		addTest(new TestSuite(ScannerUnitTests.class, "ScannerUnitTests"));
		addTest(new TestSuite(UnitTests.class, "UnitTests"));
		addTest(new TestSuite(TestStructuredPartitionerJSP.class, "TestStructuredPartioner"));
		addTest(new TestSuite(ModelQueryTester.class, "ModelQueryTester"));
		addTest(new TestSuite(JSPJavaTranslatorTest.class, "Mixed JavaScript Translator Tests"));
		addTest(new TestSuite(TestEmailNotice.class, "TestEmailNotice"));
		addTest(new TestSuite(BeanInfoProviderTest.class, "BeanInfo Provider Test"));
		addTest(new TestSuite(JSPTranslationTest.class, "Translator Tests"));
		addTest(new TestSuite(JSPELContentAssistTest.class, "JSP EL Content Assist Tests"));
		addTest(TestJSPContentAssistComputers.suite());
		addTest(new TestSuite(AdapterFactoryRegistryTest.class, "AdapterFactoryRegistry Tests"));
		addTest(new TestSuite(JSPUIPreferencesTest.class, "Preference Tests"));
		addTest(new TestSuite(TestViewerConfigurationJSP.class, "Source Viewer Configuration Tests"));
		addTest(new TestSuite(TestEditorConfigurationJSP.class, "Editor Configuration Tests"));
		
		// moved from jsp core tests because they require UI
		addTest(new TestSuite(TestPageDirective.class, "Page Directive Tests"));
		addTest(new TestSuite(FileBufferDocumentTester.class, "FileBuffer Document Tests"));
		addTest(new TestSuite(TestModelClone.class, "Model Clone Tests"));
		
		addTest(new TestSuite(TestModelsFromFiles.class, "Test Models From Files"));
		addTest(new TestSuite(TestModelEmbeddedContentType.class, "Test Model Embedded ContentType"));

		addTest(new TestSuite(TestContentDescription.class, "Content Description Tests"));
		addTest(new TestSuite(JSPHTMLValidatorTest.class, "JSP HTML Validator Test"));

		// Breakpoint Providers
		addTest(new TestSuite(BreakpointProvidersTest.class, "Additional JSP Breakpoint classes"));

		// pa_TODO fix this test
		//addTest(new TestSuite(JSPSearchTests.class));

		addTest(JFaceTextTestSuite.suite());
		addTest(EclipseTextTestSuite.suite());
	}
}
