package org.eclipse.wst.xsl.internal.core.tests;


import org.eclipse.wst.xsl.core.XSLCore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestXSLCore {

	@Test
	public void testisXSLNamespaceNullFalse() {
		assertFalse(XSLCore.isXSLNamespace(null));
	}
		
}
