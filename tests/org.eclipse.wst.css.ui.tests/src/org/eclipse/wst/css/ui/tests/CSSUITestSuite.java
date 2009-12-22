package org.eclipse.wst.css.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.css.ui.tests.viewer.CSSCodeFoldingTest;
import org.eclipse.wst.css.ui.tests.viewer.TestCSSContentAssist;
import org.eclipse.wst.css.ui.tests.viewer.TestViewerConfigurationCSS;

public class CSSUITestSuite extends TestSuite {
	public static Test suite() {
		return new CSSUITestSuite();
	}

	public CSSUITestSuite() {
		super("CSS UI Test Suite");
		addTest(new TestSuite(ExistenceTest.class, "CSS UI Existence Test"));
		addTest(new TestSuite(TestViewerConfigurationCSS.class));
		addTest(new TestSuite(TestEditorConfigurationCSS.class));
		addTest(new TestSuite(TestCSSContentAssist.class));
		addTest(CSSCodeFoldingTest.suite());
	}
}	