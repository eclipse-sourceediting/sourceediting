package org.eclipse.wst.xsl.internal.core.xpath.tests;

import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;

public class TestXSLXPathHelper extends TestCase {

	public void testInvalidXPath() {
		try {
			XSLTXPathHelper.compile("starts-with('123', '123', '123)");
			fail("Compiled successfully");
		} catch (XPathExpressionException ex) {
			
		}
	}
	
	public void testValidXPath() throws Exception {
		try {
		   XSLTXPathHelper.compile("concat('123', '123')");
		} catch (XPathExpressionException ex) {
			fail("Failed to compile.");
			throw new Exception(ex.getMessage());
		}
	}
}
