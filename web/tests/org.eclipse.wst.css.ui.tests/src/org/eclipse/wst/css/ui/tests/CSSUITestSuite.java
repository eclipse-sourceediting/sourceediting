/*******************************************************************************
 * Copyright (c) 2005, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.css.ui.tests.contentassist.TestCSSContentAssistComputers;
import org.eclipse.wst.css.ui.tests.viewer.CSSCodeFoldingTest;
import org.eclipse.wst.css.ui.tests.viewer.TestViewerConfigurationCSS;

public class CSSUITestSuite extends TestSuite {
	public static Test suite() {
		return new CSSUITestSuite();
	}

	public CSSUITestSuite() {
		super("CSS UI Test Suite");
		addTest(new TestSuite(ExistenceTest.class, "CSS UI Existence Test"));
		addTest(new TestSuite(TestViewerConfigurationCSS.class));
		addTest(new TestSuite(TestEditorConfigurationCSS.class));
		addTest(CSSCodeFoldingTest.suite());
		addTest(TestCSSContentAssistComputers.suite());
	}
}	