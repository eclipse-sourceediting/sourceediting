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
