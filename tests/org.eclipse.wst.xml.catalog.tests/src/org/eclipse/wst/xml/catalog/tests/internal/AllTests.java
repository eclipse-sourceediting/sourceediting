/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.catalog.tests.internal;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.wst.xml.catalog.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(CatalogReaderTest.class);
		suite.addTestSuite(CatalogResolverTest.class);
		suite.addTestSuite(CatalogWriterTest.class);
		suite.addTestSuite(CatalogContributorRegistryReaderTest.class);
		suite.addTestSuite(CatalogTest.class);
		//$JUnit-END$
		return suite;
	}
    


}
