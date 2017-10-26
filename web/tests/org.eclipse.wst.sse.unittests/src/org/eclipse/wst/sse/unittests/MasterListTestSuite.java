/*****************************************************************************
 * Copyright (c) 2004, 2016 IBM Corporation and others.
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

import org.eclipse.jst.jsp.core.tests.JSPCoreTestSuite;
import org.eclipse.jst.jsp.ui.tests.JSPUITestSuite;
import org.eclipse.wst.css.core.tests.CSSCoreTestSuite;
import org.eclipse.wst.css.ui.tests.CSSUITestSuite;
import org.eclipse.wst.dtd.core.tests.DTDCoreTestSuite;
import org.eclipse.wst.dtd.ui.tests.DTDUITestSuite;
import org.eclipse.wst.html.core.tests.HTMLCoreTestSuite;
import org.eclipse.wst.html.ui.tests.HTMLUITestSuite;
import org.eclipse.wst.jsdt.web.core.tests.AllWebCoreTests;
import org.eclipse.wst.jsdt.web.ui.tests.AllWebUITests;
import org.eclipse.wst.sse.core.tests.SSEModelTestSuite;
import org.eclipse.wst.sse.ui.tests.SSEUITestSuite;
import org.eclipse.wst.xml.core.tests.SSEModelXMLTestSuite;
import org.eclipse.wst.xml.ui.tests.XMLUITestSuite;
import org.eclipse.wst.xml.validation.tests.internal.AllXMLTests;
import org.eclipse.wst.xsd.core.tests.internal.AllXSDCoreTests;
import org.eclipse.wst.xsd.validation.tests.internal.AllXSDTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		SSEModelTestSuite.class,
		SSEModelXMLTestSuite.class,
		DTDCoreTestSuite.class,
		AllXSDCoreTests.class,
		CSSCoreTestSuite.class,
		HTMLCoreTestSuite.class,
		JSPCoreTestSuite.class,
		AllXMLTests.class,
		AllXSDTests.class,
		SSEUITestSuite.class,
		XMLUITestSuite.class,
		DTDUITestSuite.class,
		CSSUITestSuite.class,
		HTMLUITestSuite.class,
		JSPUITestSuite.class,
		AllWebCoreTests.class,
		AllWebUITests.class,
//		addTest(RunJSDTCoreTests.class);
//		addTest(JSDTCompilerTests.class);
//		addTest(JSDTUITests.class);
//		addTest(EncodingTestSuite.class);
//		addTest(CSSEncodingTestSuite.class);
//		addTest(HTMLEncodingTestSuite.class);
//		addTest(JSPEncodingTestSuite.class);
//		addTest(new AllTestsSuite());
		
		// addTest(RegressionBucket.class);
		// addTest(AllTestCases.class);

})

public class MasterListTestSuite {
}
