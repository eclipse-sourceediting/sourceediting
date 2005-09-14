package org.eclipse.wst.css.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;


public class CSSUITestSuite extends TestSuite {
	public static Test suite() {
		return new CSSUITestSuite();
	}

	public CSSUITestSuite() {
		super("CSS UI Test Suite");
		addTest(new TestSuite(ExistenceTest.class, "CSS UI Existence Test"));
	}
}	