/*******************************************************************************
 * Copyright (c) 2009, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
