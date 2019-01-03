/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text.tests;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Test Suite for org.eclipse.jface.text.
 *
 * @since 3.0
 */
public class JFaceTextTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite= new TestSuite("org.eclipse.jface.text Test Suite using BasicStructuredDocument"); //$NON-NLS-1$
//		suite.addTest(HTML2TextReaderTester.suite());
//		suite.addTest(TextHoverPopupTest.suite());
//		suite.addTest(TextPresentationTest.suite());
		suite.addTest(DefaultUndoManagerTest.suite());
		suite.addTest(TextViewerUndoManagerTest.suite());
//		suite.addTest(RulesTestSuite.suite());
//		suite.addTest(ReconcilerTestSuite.suite());
//		suite.addTest(DefaultPairMatcherTest.suite());
		return suite;
	}
}
