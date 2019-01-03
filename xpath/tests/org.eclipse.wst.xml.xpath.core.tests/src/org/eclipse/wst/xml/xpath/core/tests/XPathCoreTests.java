/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
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
package org.eclipse.wst.xml.xpath.core.tests;

import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXPath20Helper;
import org.eclipse.wst.xsl.internal.core.xpath.tests.TestXSLXPathHelper;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XPathCoreTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.xpath.core.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestXSLXPathHelper.class);
		suite.addTestSuite(TestXPath20Helper.class);
		//$JUnit-END$
		return suite;
	}

}
