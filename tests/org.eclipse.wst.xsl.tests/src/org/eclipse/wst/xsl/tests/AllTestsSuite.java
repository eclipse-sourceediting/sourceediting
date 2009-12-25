/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.tests;

import junit.framework.TestSuite;

import org.eclipse.wst.xml.xpath.core.tests.XPathCoreTests;
import org.eclipse.wst.xml.xpath.ui.internal.hander.tests.TestXPathProcessorHandler;
import org.eclipse.wst.xml.xpath.ui.tests.XPathUITestPlugin;
import org.eclipse.wst.xml.xpath2.processor.test.AllPsychoPathTests;
import org.eclipse.wst.xsl.launching.tests.LaunchingSuite;
import org.eclipse.wst.xsl.ui.tests.XSLUITestSuite;
import org.eclipse.wst.xsl.core.tests.XSLCoreTestSuite;
import org.eclipse.wst.xsl.exslt.core.tests.EXSLTCoreTestSuite;
import org.eclipse.wst.xsl.exslt.ui.tests.EXSLTUITestSuite;

/**
 * This class specifies all the bundles of this component that provide a test
 * suite to run during automated testing.
 */
public class AllTestsSuite extends TestSuite {


	public AllTestsSuite() {
		super("All XSL Test Suites");
		addTest(XSLUITestSuite.suite());
		addTest(XSLCoreTestSuite.suite());
		addTest(XPathCoreTests.suite());
		addTest(LaunchingSuite.suite());
		addTest(AllPsychoPathTests.suite());
		addTest(EXSLTCoreTestSuite.suite());
		addTestSuite(TestXPathProcessorHandler.class);
	//	addTest(EXSLTUITestSuite.suite());
	}

	/**
	 * This is just need to run in a development environment workbench.
	 */
	public void testAll() {
		// this method needs to exist, but doesn't really do anything
		// other than to signal to create an instance of this class.
		// The rest it automatic from the tests added in constructor.

	}
}
