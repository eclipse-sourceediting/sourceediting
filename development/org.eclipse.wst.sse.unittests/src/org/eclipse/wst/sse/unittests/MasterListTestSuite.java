/*****************************************************************************
 * Copyright (c) 2004,2008 IBM Corporation and others.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies  this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 * 
 ****************************************************************************/

package org.eclipse.wst.sse.unittests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.tests.JSPCoreTestSuite;
import org.eclipse.jst.jsp.tests.encoding.JSPEncodingTestSuite;
import org.eclipse.jst.jsp.ui.tests.JSPUITestSuite;
import org.eclipse.wst.css.core.tests.CSSCoreTestSuite;
import org.eclipse.wst.css.tests.encoding.CSSEncodingTestSuite;
import org.eclipse.wst.css.ui.tests.CSSUITestSuite;
import org.eclipse.wst.dtd.core.tests.DTDCoreTestSuite;
import org.eclipse.wst.dtd.ui.tests.DTDUITestSuite;
import org.eclipse.wst.html.core.tests.HTMLCoreTestSuite;
import org.eclipse.wst.html.tests.encoding.HTMLEncodingTestSuite;
import org.eclipse.wst.html.ui.tests.HTMLUITestSuite;
import org.eclipse.wst.sse.core.tests.SSEModelTestSuite;
import org.eclipse.wst.sse.ui.tests.SSEUITestSuite;
import org.eclipse.wst.xml.core.tests.SSEModelXMLTestSuite;
import org.eclipse.wst.xml.tests.encoding.EncodingTestSuite;
import org.eclipse.wst.xml.ui.tests.XMLUITestSuite;
import org.eclipse.wst.xml.validation.tests.internal.AllXMLTests;
import org.eclipse.wst.xsd.core.tests.internal.AllXSDCoreTests;
import org.eclipse.wst.xsd.validation.tests.internal.AllXSDTests;

public class MasterListTestSuite extends TestSuite {

	public MasterListTestSuite() {
		super("All Tests");

		System.setProperty("wtp.autotest.noninteractive", "true");

		addTest(SSEModelTestSuite.suite());
		addTest(SSEModelXMLTestSuite.suite());
		addTest(CSSCoreTestSuite.suite());
		addTest(HTMLCoreTestSuite.suite());
		addTest(JSPCoreTestSuite.suite());
		addTest(DTDCoreTestSuite.suite());

		addTest(AllXMLTests.suite());

		addTest(EncodingTestSuite.suite());
		addTest(CSSEncodingTestSuite.suite());
		addTest(HTMLEncodingTestSuite.suite());
		addTest(JSPEncodingTestSuite.suite());

		addTest(CSSUITestSuite.suite());
		addTest(HTMLUITestSuite.suite());
		addTest(SSEUITestSuite.suite());
		addTest(XMLUITestSuite.suite());
		addTest(DTDUITestSuite.suite());
		addTest(JSPUITestSuite.suite());

		addTest(AllXSDTests.suite());
		addTest(AllXSDCoreTests.suite());

		// addTest(RegressionBucket.suite());
		// addTest(AllTestCases.suite());

		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.wst.sse.unittests.additionalTests");
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].getName().equals("suite")) {
				TestSuite suite;
				try {
					suite = (TestSuite) elements[i].createExecutableExtension("class");
					addTest(new TestSuite(suite.getClass()));
				}
				catch (CoreException e) {
					Platform.getLog(Platform.getBundle("org.eclipse.wst.sse.unittests")).log(e.getStatus());
				}
			}
			else if (elements[i].getName().equals("test")) {
				Test test;
				try {
					test = (Test) elements[i].createExecutableExtension("class");
					addTest(new TestSuite(test.getClass()));
				}
				catch (CoreException e) {
					Platform.getLog(Platform.getBundle("org.eclipse.wst.sse.unittests")).log(e.getStatus());
				}
			}
		}
	}

	public void testAll() {
		// this method needs to exist, but doesn't really do anything
		// other than to signal to create an instance of this class.
		// The rest it automatic from the tests added in constructor.
	}
}
