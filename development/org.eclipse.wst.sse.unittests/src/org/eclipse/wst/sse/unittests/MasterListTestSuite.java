/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.sse.unittests;

import junit.framework.TestSuite;

import org.eclipse.jst.jsp.core.tests.JSPCoreTestSuite;
import org.eclipse.jst.jsp.tests.encoding.JSPEncodingTestSuite;
import org.eclipse.jst.jsp.ui.tests.JSPUITestSuite;
import org.eclipse.wst.css.core.tests.CSSCoreTestSuite;
import org.eclipse.wst.css.tests.encoding.CSSEncodingTestSuite;
import org.eclipse.wst.css.ui.tests.CSSUITestSuite;
import org.eclipse.wst.dtd.ui.tests.DTDUITestSuite;
import org.eclipse.wst.html.core.tests.HTMLCoreTestSuite;
import org.eclipse.wst.html.tests.encoding.HTMLEncodingTestSuite;
import org.eclipse.wst.html.ui.tests.HTMLUITestSuite;
import org.eclipse.wst.sse.core.tests.SSEModelTestSuite;
import org.eclipse.wst.sse.ui.tests.SSEUITestSuite;
import org.eclipse.wst.wsdl.tests.AllTestCases;
import org.eclipse.wst.wsi.tests.internal.RegressionBucket;
import org.eclipse.wst.xml.core.tests.SSEModelXMLTestSuite;
import org.eclipse.wst.xml.tests.encoding.EncodingTestSuite;
import org.eclipse.wst.xml.ui.tests.XMLUITestSuite;
import org.eclipse.wst.xml.validation.tests.internal.AllXMLTests;
import org.eclipse.wst.xsd.validation.tests.internal.AllXSDTests;

/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 * 
 ****************************************************************************/

public class MasterListTestSuite extends TestSuite {

	public MasterListTestSuite() {
		super("All Tests");
		
		System.setProperty("wtp.autotest.noninteractive", "true");

		addTest(SSEModelTestSuite.suite());
		addTest(SSEModelXMLTestSuite.suite());
		addTest(CSSCoreTestSuite.suite());
		addTest(HTMLCoreTestSuite.suite());
		addTest(JSPCoreTestSuite.suite());

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
		addTest(RegressionBucket.suite());
		addTest(AllTestCases.suite());

	}

	public void testAll() {
		// this method needs to exist, but doesn't really do anything
		// other than to signal to create an instance of this class.
		// The rest it automatic from the tests added in constructor. 
	}
}
