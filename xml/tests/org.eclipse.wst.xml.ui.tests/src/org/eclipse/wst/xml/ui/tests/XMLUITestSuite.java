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
 *     David Carver (STAR) - bug 259447 - content assistance tests
 *******************************************************************************/
package org.eclipse.wst.xml.ui.tests;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xml.ui.internal.validation.TestDelegatingSourceValidatorForXML;
import org.eclipse.wst.xml.ui.internal.validation.TestMarkupValidator;
import org.eclipse.wst.xml.ui.tests.contentassist.TestXMLContentAssistComputers;
import org.eclipse.wst.xml.ui.tests.contentmodel.TestInferredContentModel;
import org.eclipse.wst.xml.ui.tests.viewer.TestViewerConfigurationXML;


public class XMLUITestSuite extends TestSuite {
	public static Test suite() {
		return new XMLUITestSuite();
	}

	public XMLUITestSuite() {
		super("XML UI Test Suite");
		addTest(XMLCodeFoldingTest.suite());
		addTest(new TestSuite(VerifyEditorPlugin.class));
		addTest(new TestSuite(XMLUIPreferencesTest.class));
		addTest(new TestSuite(TestViewerConfigurationXML.class));
		addTest(new TestSuite(TestEditorConfigurationXML.class));
		addTest(new TestSuite(TestOpenEditorXML.class));
		addTest((new TestSuite(TestPropertySheetConfiguration.class)));
		addTest(new TestSuite(TestNewXMLGenerator.class));
		addTestSuite(TestDelegatingSourceValidatorForXML.class);
		addTestSuite(TestMarkupValidator.class);
		addTestSuite(TestReconcilerXML.class);
		addTestSuite(TestSourceValidationFramework.class);
		addTest(new TestSuite(TestSpellcheckDelegateAdapaterFactory.class, "Test Spellcheck Delegate Adapter Factory"));
		addTest(new TestSuite(TestInferredContentModel.class, "Test Inferred Grammar Support"));
		addTest(TestXMLContentAssistComputers.suite());
	}
}
