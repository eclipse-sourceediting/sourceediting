package org.eclipse.wst.xsl.internal.core.xpath.tests;

import java.io.InputStream;
import java.io.StringBufferInputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;

import org.apache.commons.jxpath.ri.parser.SimpleCharStream;
import org.apache.commons.jxpath.ri.parser.Token;
import org.apache.commons.jxpath.ri.parser.XPathParser;
import org.apache.commons.jxpath.ri.parser.XPathParserConstants;
import org.apache.commons.jxpath.ri.parser.XPathParserTokenManager;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.XPath;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.XPathImpl;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathFactoryImpl;

import junit.framework.TestCase;


public class TestXPathParser extends TestCase {
		
	protected XPathParser parser = null;
	protected final static String xpathvariable = "$test";

	public TestXPathParser() {
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	public void testXPathTokenizer() throws Exception {
		parser = new XPathParser(new StringBufferInputStream(xpathvariable));
		assertNotNull("Token is Null", parser.token);
		Token token = parser.getNextToken();
		assertEquals("Variable name doesn't match.", "$", token.toString());
		token = token.next;
		assertEquals("Variable", "test", token.toString());
	}
	
	public void testXPathTokenizerVariableKind() throws Exception {
		parser = new XPathParser(new StringBufferInputStream(xpathvariable));
		assertNotNull("Token is Null", parser.token);
		Token token = parser.getNextToken();
		assertEquals("Token is not a Variable", XPathParserConstants.VARIABLE, token.kind);
	    token = token.next;
	    assertEquals("Token is not a NCName:", XPathParserConstants.NCName, token.kind);
	    assertEquals("Variable Name is not test:", "test", token.toString());
	}
	
	public void testCreateXPathRoot() throws Exception {
		XPath xpath = new xpathFactoryImpl().createXPath();
		assertNotNull("Failed to create xpath class", xpath);
	}
	
}
