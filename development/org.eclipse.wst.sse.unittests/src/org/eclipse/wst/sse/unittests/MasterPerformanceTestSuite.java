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

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.ui.tests.performance.JSPUIPerformanceTests;
import org.eclipse.wst.css.ui.tests.performance.CSSUIPerformanceTestSuite;
import org.eclipse.wst.html.ui.tests.performance.HTMLUIPerformanceTestSuite;
import org.eclipse.wst.sse.ui.tests.performance.SSEUIPerformanceTestSuite;
import org.eclipse.wst.xml.ui.tests.performance.XMLUIPerformanceTestSuite;

/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 * 
 ****************************************************************************/

public class MasterPerformanceTestSuite extends TestSuite {

	public MasterPerformanceTestSuite() {
		super("All Tests");

		addTest(JSPUIPerformanceTests.suite());
		addTest(CSSUIPerformanceTestSuite.suite());
		addTest(HTMLUIPerformanceTestSuite.suite());
		addTest(SSEUIPerformanceTestSuite.suite());
		addTest(XMLUIPerformanceTestSuite.suite());


	}

	public void testAll() {
		TestResult testResult = new TestResult();
		run(testResult);
	}

}
