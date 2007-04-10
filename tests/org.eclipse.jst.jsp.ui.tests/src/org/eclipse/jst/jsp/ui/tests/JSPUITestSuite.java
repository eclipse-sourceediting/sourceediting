/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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

import org.eclipse.jst.jsp.ui.tests.contentassist.BeanInfoProviderTest;
import org.eclipse.jst.jsp.ui.tests.contentassist.JSPJavaTranslatorTest;
import org.eclipse.jst.jsp.ui.tests.contentassist.JSPTranslationTest;
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




public class JSPUITestSuite extends TestSuite {
	public static Test suite() {
		return new JSPUITestSuite();
	}

	public JSPUITestSuite() {
		super("SSE JSP UI Test Suite");
		
		addTest(new TestSuite(ScannerUnitTests.class, "ScannerUnitTests"));
		addTest(new TestSuite(UnitTests.class, "UnitTests"));
		addTest(new TestSuite(TestStructuredPartitionerJSP.class, "TestStructuredPartioner"));
		addTest(new TestSuite(ModelQueryTester.class, "ModelQueryTester"));
		addTest(new TestSuite(JSPJavaTranslatorTest.class, "JSPJavaTranslatorTest"));
		addTest(new TestSuite(TestEmailNotice.class, "TestEmailNotice"));
		addTest(new TestSuite(BeanInfoProviderTest.class, "BeanInfoProviderTest"));
		addTest(new TestSuite(JSPTranslationTest.class, "JSPTranslationTest"));
		addTest(new TestSuite(AdapterFactoryRegistryTest.class, "AdapterFactoryRegistryText"));
		addTest(new TestSuite(JSPUIPreferencesTest.class));
		addTest(new TestSuite(TestViewerConfigurationJSP.class));
		addTest(new TestSuite(TestEditorConfigurationJSP.class));
		
		// moved from jsp core tests because they require UI
		addTest(new TestSuite(TestPageDirective.class));
		addTest(new TestSuite(FileBufferDocumentTester.class));
		addTest(new TestSuite(TestModelClone.class));
		
		addTest(new TestSuite(TestModelsFromFiles.class, "TestModelsFromFiles"));
		addTest(new TestSuite(TestModelEmbeddedContentType.class, "TestModelEmbeddedContentType"));

		addTest(new TestSuite(TestContentDescription.class, "Content Description Tests"));
		addTest(new TestSuite(JSPHTMLValidatorTest.class, "JSPHTMLValidatorTest"));
		// pa_TODO fix this test
		//addTest(new TestSuite(JSPSearchTests.class));
	}
}
