package org.eclipse.wst.xsl.internal.core.xpath.tests;

import org.apache.commons.jxpath.ri.parser.Token;
import org.apache.commons.jxpath.ri.parser.XPathParserConstants;
import org.eclipse.wst.xml.xpath.core.internal.parser.XPathParser;

import junit.framework.TestCase;

public class TestXPathCoreXPathParser extends TestCase {

	protected String xpathSingleLine = "$linksFile='' and (normalize-space(translate($searchIncludedSchemas, 'TRUE', 'true'))='true' or normalize-space(translate($searchImportedSchemas, 'TRUE', 'true'))='true')";
	protected String xpathMultiLine = "$linksFile='' and\n" +
	                                  "(normalize-space(translate($searchIncludedSchemas, 'TRUE', 'true'))='true'\n" +
	                                  "or normalize-space(translate($searchImportedSchemas, 'TRUE', 'true'))='true')";
	protected String xpathPartial = "starts-with(, )";
	protected String xpathSpaceCheck = "  and starts-with('pre', )";
	
	
	
	public TestXPathCoreXPathParser() {
		// TODO Auto-generated constructor stub
	}
	
	public void testCoreXPathParser() {
		XPathParser parser = new XPathParser(xpathSingleLine);
		assertNotNull(parser);
		parser = null;
		parser = new XPathParser(xpathMultiLine);
		assertNotNull(parser);
	}
	
	public void testgetTokenStartOffset() {
		XPathParser parser = new XPathParser(xpathSingleLine);
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 2, parser.getTokenStartOffset(1, 2));
	}
	
	public void testgetTokenStartOffset2() {
		XPathParser parser = new XPathParser(xpathSingleLine);
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 20, parser.getTokenStartOffset(1, 20));
		assertEquals("Unexpected token value:", "normalize-space", parser.getCurrentToken().image);
		assertEquals("Expected Function normalize-space", XPathParserConstants.FUNCTION_NORMALIZE_SPACE, parser.getCurrentToken().kind);
	}
	
	public void testgetTokenStartOffset3() {
		XPathParser parser = new XPathParser(xpathSingleLine);
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 15, parser.getTokenStartOffset(1, 15));
		assertEquals("Unexpected token value:", "and", parser.getCurrentToken().image);
		assertEquals("Expected AND kind", XPathParserConstants.AND, parser.getCurrentToken().kind);
		
	}
	
	public void testgetTokenStartOffset4() {
		XPathParser parser = new XPathParser(xpathSingleLine);
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 19, parser.getTokenStartOffset(1, 19));
		assertEquals("Unexpected token value:", "(", parser.getCurrentToken().image);
	}
	
	public void testgetTokenStartOffset5() {
		XPathParser parser = new XPathParser(xpathSingleLine);
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 164, parser.getTokenStartOffset(1, 167));
		assertEquals("Unexpected token value:", "'true'", parser.getCurrentToken().image);
	}
	
	public void testgetTokenStartOffsetNotEqual() {
		XPathParser parser = new XPathParser(xpathSingleLine);
		assertNotNull(parser);
		assertFalse("Value of token offset is 1 should be 164:", parser.getTokenStartOffset(1, 167) == 1);
	}
	
	public void testXPathPartial() {
		XPathParser parser = new XPathParser(xpathPartial);
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 13, parser.getTokenStartOffset(1, 13));
	}

	public void testXPathSpaceCheck() {
		XPathParser parser = new XPathParser(xpathSpaceCheck);
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 2, parser.getTokenStartOffset(1, 2));
	}
	
	public void testEmptyString() {
		XPathParser parser = new XPathParser("");
		assertNotNull(parser);
		assertEquals("Value of token offset is wrong:", 0, parser.getTokenStartOffset(1, 1));
	}
	
	
}
