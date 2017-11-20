/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.tests;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.tests.JFaceTextTestSuite;
import org.eclipse.text.tests.EclipseTextTestSuite;
import org.eclipse.wst.sse.ui.tests.viewer.TestViewerConfiguration;

public class SSEUITestSuite extends TestSuite {
	public static Test suite() {
		return new SSEUITestSuite();
	}

	public SSEUITestSuite() {
		super("SSE UI Test Suite");
		addTest(new TestSuite(VerifyEditorPlugin.class));
		addTest(new TestSuite(CommonEditorPreferencesTest.class));
		addTest(new TestSuite(TestViewerConfiguration.class));
		addTest(new TestSuite(TestStructuredTextEditor.class));
		addTest(JFaceTextTestSuite.suite());
		addTest(EclipseTextTestSuite.suite());
	}
}
