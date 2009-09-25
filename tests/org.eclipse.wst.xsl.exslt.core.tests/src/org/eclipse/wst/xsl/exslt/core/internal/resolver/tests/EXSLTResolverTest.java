package org.eclipse.wst.xsl.exslt.core.internal.resolver.tests;

import org.eclipse.wst.xsl.exslt.core.internal.EXSLTCore;
import org.eclipse.wst.xsl.exslt.core.internal.resolver.EXSLTResolverExtension;

import junit.framework.TestCase;

public class EXSLTResolverTest extends TestCase {
	
	private EXSLTResolverExtension resolver = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		resolver = new EXSLTResolverExtension();
	}
	

	public void testCommonNamespace() {
		String namespace = EXSLTCore.EXSLT_COMMON_NAMESPACE;
		String uri = resolver.resolve(null, null, namespace, null);
		assertTrue("Did not find http://exslt.org/common", uri.contains("/schemas/common.xsd"));
	}
	
	public void testURINotResolved() {
		String namespace = "http://www.example.org/";
		String uri = resolver.resolve(null, null, namespace, null);
		assertNull("Found http://www.example.org/", uri);
	}
}
