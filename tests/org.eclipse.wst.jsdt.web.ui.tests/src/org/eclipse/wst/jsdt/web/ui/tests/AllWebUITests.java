/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui.tests;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.web.ui.tests.format.FormattingTests;
import org.eclipse.wst.jsdt.web.ui.tests.style.StyleTests;

public class AllWebUITests extends TestSuite {
	public AllWebUITests() {
		super("JSDT Web UI Tests");
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("JSDT Web UI Tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(StyleTests.class);
		suite.addTest(FormattingTests.suite());
		// $JUnit-END$
		return suite;
	}

}
