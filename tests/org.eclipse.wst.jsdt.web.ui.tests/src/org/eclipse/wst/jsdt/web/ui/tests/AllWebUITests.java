/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
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
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.web.ui.tests.conversion.IncludePathTests;
import org.eclipse.wst.jsdt.web.ui.tests.format.FormattingTests;
import org.eclipse.wst.jsdt.web.ui.tests.style.StyleTests;

public class AllWebUITests extends TestCase {
	public AllWebUITests() {
		super("JSDT Web UI Tests");
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("JSDT Web UI Tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(IncludePathTests.class);
		suite.addTest(FormattingTests.suite());
		suite.addTestSuite(StyleTests.class);
		// $JUnit-END$
		return suite;
	}

}
