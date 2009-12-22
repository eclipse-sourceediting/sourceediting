/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.dtd.ui.tests.plugin.TestPluginXMLRequirements;
import org.eclipse.wst.dtd.ui.tests.viewer.DTDCodeFoldingTest;
import org.eclipse.wst.dtd.ui.tests.viewer.TestViewerConfigurationDTD;



public class DTDUITestSuite extends TestSuite {
	public static Test suite() {
		return new DTDUITestSuite();
	}

	public DTDUITestSuite() {
		super("DTD UI TestSuite");
		addTest(new TestSuite(VerifyUIPlugin.class));
		addTest(new TestSuite(DTDUIPreferencesTest.class));
		addTest(new TestSuite(TestViewerConfigurationDTD.class));
		addTest(new TestSuite(TestEditorConfigurationDTD.class));
		addTestSuite(TestPluginXMLRequirements.class);
		addTest(DTDCodeFoldingTest.suite());
	}
}
