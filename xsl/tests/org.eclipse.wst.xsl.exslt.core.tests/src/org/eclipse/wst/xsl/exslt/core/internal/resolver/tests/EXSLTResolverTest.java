package org.eclipse.wst.xsl.exslt.core.internal.resolver.tests;

import org.eclipse.wst.xsl.exslt.core.internal.EXSLTCore;
import org.eclipse.wst.xsl.exslt.core.internal.resolver.EXSLTResolverExtension;

import org.junit.*;
import static org.junit.Assert.*;

public class EXSLTResolverTest {
	
	private EXSLTResolverExtension resolver = null;
	
	@Before
	public void setUp() throws Exception {
		resolver = new EXSLTResolverExtension();
	}
	
	@After
	public void tearDown() throws Exception {
		resolver = null;
	}
	

	@Test
	public void testCommonNamespace() {
		String namespace = EXSLTCore.EXSLT_COMMON_NAMESPACE;
		String uri = resolver.resolve(null, null, namespace, null);
		assertTrue("Did not find http://exslt.org/common", uri.contains("/schemas/common.xsd"));
	}
	
	@Test
	public void testURINotResolved() {
		String namespace = "http://www.example.org/";
		String uri = resolver.resolve(null, null, namespace, null);
		assertNull("Found http://www.example.org/", uri);
	}
}
