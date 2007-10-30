/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xml.core.internal.document.test.NodeImplTestCase;
import org.eclipse.wst.xml.core.tests.contentmodel.TestAttributesOrder;
import org.eclipse.wst.xml.core.tests.contentmodel.TestCatalogRetrivalAndModelCreation;
import org.eclipse.wst.xml.core.tests.document.GetDocumentRegionsTest;
import org.eclipse.wst.xml.core.tests.document.TestStructuredDocument;
import org.eclipse.wst.xml.core.tests.document.TestXMLDocumentLoader;
import org.eclipse.wst.xml.core.tests.document.TransitionTests;
import org.eclipse.wst.xml.core.tests.dom.NameValidatorTests;
import org.eclipse.wst.xml.core.tests.format.TestFormatProcessorXML;
import org.eclipse.wst.xml.core.tests.model.TestModelManager;
import org.eclipse.wst.xml.core.tests.model.TestModelsFromFiles;
import org.eclipse.wst.xml.core.tests.model.TestStructuredModel;
import org.eclipse.wst.xml.core.tests.model.TestXMLModelLoader;
import org.eclipse.wst.xml.core.tests.text.TestStructuredPartitionerXML;



public class SSEModelXMLTestSuite extends TestSuite {
	public static Test suite() {
		return new SSEModelXMLTestSuite();
	}

	public SSEModelXMLTestSuite() {
		super("Test Suite for org.eclipse.wst.xml.core.tests");
		addTest(new TestSuite(TestModelsFromFiles.class));
		addTest(new TestSuite(TestXMLModelLoader.class));
		addTest(new TestSuite(TestXMLDocumentLoader.class));
		addTest(new TestSuite(TestStructuredDocument.class));
		
		addTest(new TestSuite(TestModelManager.class));
		addTest(new TestSuite(TestStructuredModel.class));
		addTest(new TestSuite(TestStructuredPartitionerXML.class));
		addTest(new TestSuite(NameValidatorTests.class));
		addTest(new TestSuite(TransitionTests.class));
		addTest(new TestSuite(XMLCorePreferencesTest.class));
		addTest(new TestSuite(TestCatalogRetrivalAndModelCreation.class));
		
		addTest(new TestSuite(GetDocumentRegionsTest.class));
		addTest(new TestSuite(NodeImplTestCase.class));

		addTest(new TestSuite(TestFormatProcessorXML.class));

		addTest(new TestSuite(TestAttributesOrder.class));
	}
}
