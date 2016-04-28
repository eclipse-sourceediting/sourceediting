/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
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

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.jsdt.web.ui.tests.contentassist.AllContentAssistTests;
import org.eclipse.wst.jsdt.web.ui.tests.format.FormattingTests;
import org.eclipse.wst.jsdt.web.ui.tests.format.TestJSPContentFormatter;
import org.eclipse.wst.jsdt.web.ui.tests.style.StyleTests;

public class AllWebUITests extends TestSuite {
	public AllWebUITests() {
		super("JSDT Web UI Tests");
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("JSDT Web UI Tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(StyleTests.class);
//		suite.addTest(AllContentAssistTests.suite());
		System.err.println(AllContentAssistTests.class.getName() + " has been disabled while JSDT is in flux"); //$NON-NLS-1$
		suite.addTest(FormattingTests.suite());
		
		if (Platform.getBundle("org.eclipse.jst.jsp.ui") != null) {
			suite.addTest(TestJSPContentFormatter.suite());
		}
		// $JUnit-END$
		return suite;
	}

}
