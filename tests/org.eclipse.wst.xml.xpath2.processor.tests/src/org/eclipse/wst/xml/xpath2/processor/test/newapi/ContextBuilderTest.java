package org.eclipse.wst.xml.xpath2.processor.test.newapi;

import junit.framework.TestCase;

import org.eclipse.wst.xml.xpath2.processor.util.StaticContextBuilder;

public class ContextBuilderTest extends TestCase {

	
	public void testReasonableDefaults() {
		StaticContextBuilder scb = new StaticContextBuilder();
		
		assertFalse(scb.isXPath1Compatible());
		assertEquals("", scb.getDefaultNamespace());
		assertEquals(null, scb.getBaseUri());
	}
	
}
