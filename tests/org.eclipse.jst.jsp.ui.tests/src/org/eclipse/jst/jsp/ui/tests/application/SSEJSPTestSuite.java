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
package org.eclipse.jst.jsp.ui.tests.application;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jst.jsp.ui.tests.OtherTests;



public class SSEJSPTestSuite extends TestSuite {
	public static Test suite() {
		return new SSEJSPTestSuite();
	}

	public SSEJSPTestSuite() {
		super("SSEJSPTestSuite");

		addTest(OtherTests.suite());
		// pa_TODO fix this test
		//addTest(new TestSuite(JSPSearchTests.class));
	}
}