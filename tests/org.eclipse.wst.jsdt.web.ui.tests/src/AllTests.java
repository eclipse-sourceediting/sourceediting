

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Tests for org.eclipse.wst.jsdt.web.core");
		//$JUnit-BEGIN$

		//$JUnit-END$
		suite.addTest(new TestSuite(TestHtmlTranslation.class, "TestHtmlTranslation"));
		return suite;
	}

}
