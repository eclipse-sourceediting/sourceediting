/*******************************************************************************
 * Copyright (c) 2004, 2019 IBM Corporation and others.
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
package org.eclipse.wst.json.core.tests;

import org.eclipse.wst.json.core.catalog.CatalogTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JsonCoreTestSuite extends TestSuite {
	public static Test suite() {
		return new JsonCoreTestSuite();
	}

	public JsonCoreTestSuite() {
		super("JSON Core Test Suite");

		String noninteractive = System.getProperty("wtp.autotest.noninteractive");
		String wtp_autotest_noninteractive = null;
		if (noninteractive != null)
			wtp_autotest_noninteractive = noninteractive;
		System.setProperty("wtp.autotest.noninteractive", "true");
		addTest(new TestSuite(CatalogTests.class));

//		addTest(new TestSuite(JSONTokenizerTest.class));
//		addTest(new TestSuite(JSONArrayCreationTest.class));
//		addTest(new TestSuite(JSONArrayDocumentChangesTest.class));
//		addTest(new TestSuite(JSONDocumentTest.class));
//		addTest(new TestSuite(JSONGlossaryDocumentTest.class));
//		addTest(new TestSuite(JSONObjectCreationTest.class));
//		addTest(new TestSuite(JSONObjectDocumentChangesTest.class));
//		addTest(new TestSuite(JSONSampleDocumentTest.class));

		if (wtp_autotest_noninteractive != null)
			System.setProperty("wtp.autotest.noninteractive", wtp_autotest_noninteractive);
	}
}
