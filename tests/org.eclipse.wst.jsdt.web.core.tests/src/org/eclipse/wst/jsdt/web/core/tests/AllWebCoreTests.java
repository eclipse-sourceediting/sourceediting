/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.web.core.tests.translation.TestHtmlTranslation;

public class AllWebCoreTests extends TestSuite {
	public AllWebCoreTests() {
		super("JSDT Web Core Tests");
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("JSDT Web Core Tests");
		//$JUnit-BEGIN$

		//$JUnit-END$
		suite.addTestSuite(TestHtmlTranslation.class);
		suite.addTestSuite(PathUtilsTests.class);
		return suite;
	}

}
