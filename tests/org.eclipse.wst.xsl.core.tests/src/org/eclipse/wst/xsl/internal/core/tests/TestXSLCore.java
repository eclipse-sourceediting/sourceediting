package org.eclipse.wst.xsl.internal.core.tests;

import junit.framework.TestCase;
import org.eclipse.wst.xsl.core.XSLCore;

public class TestXSLCore extends TestCase {

	public TestXSLCore() {
		super();
	}

	public TestXSLCore(String name) {
		super(name);
	}
	
	public void testisXSLNamespaceNullFalse() {
		assertFalse(XSLCore.isXSLNamespace(null));
	}
	
	public void testFakeTestToTestTests() {
		assertFalse(true);
	}
	
}
