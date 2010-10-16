package org.eclipse.wst.jsdt.web.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.web.core.tests.translation.TestHtmlTranslation;

public class AllWebCoreTests extends TestCase {
	public AllWebCoreTests() {
		super("JSDT Web Core Tests");
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("JSDT Web Core Tests");
		//$JUnit-BEGIN$

		//$JUnit-END$
		suite.addTestSuite(TestHtmlTranslation.class);
		return suite;
	}

}
