/*******************************************************************************
 * Copyright (c) 2004, 2017 IBM Corporation and others.
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
package org.eclipse.wst.html.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.html.ui.tests.contentassist.TestEmbededCSSContentAssistComputers;
import org.eclipse.wst.html.ui.tests.contentassist.TestHTMLContentAssistComputers;
import org.eclipse.wst.html.ui.tests.validation.TestHTMLValidator;
import org.eclipse.wst.html.ui.tests.viewer.TestViewerConfigurationHTML;



public class HTMLUITestSuite extends junit.framework.TestSuite {
	public static Test suite() {
		return new HTMLUITestSuite();
	}

	public HTMLUITestSuite() {
		super("HTML UI TestSuite");
		addTest(new TestSuite(VerifyEditorPlugin.class));
		addTest(new TestSuite(HTMLUIPreferencesTest.class));
		addTest(new TestSuite(TestViewerConfigurationHTML.class));
		addTest(new TestSuite(TestEditorConfigurationHTML.class));
		addTest(TestHTMLValidator.suite());
		//		addTest(new SSEModelTestSuite());
		addTest(TestHTMLContentAssistComputers.suite());
		addTest(TestEmbededCSSContentAssistComputers.suite());
	}
}
