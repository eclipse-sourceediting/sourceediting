package org.eclipse.wst.jsdt.web.core.tests.translation;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllWebCoreTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("JSDT Web Core Tests");
		//$JUnit-BEGIN$

		//$JUnit-END$
		suite.addTestSuite(TestHtmlTranslation.class);
		return suite;
	}

}
