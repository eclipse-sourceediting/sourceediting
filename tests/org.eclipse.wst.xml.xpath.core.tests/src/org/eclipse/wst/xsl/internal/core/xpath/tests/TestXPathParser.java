/********************************************************************************
 *  Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      David Carver - initial API and implementation
 *
 * $Id: TestXPathParser.java,v 1.3 2008/04/23 23:28:51 dacarver Exp $
 *********************************************************************************/
package org.eclipse.wst.xsl.internal.core.xpath.tests;

import java.io.StringBufferInputStream;


import org.apache.commons.jxpath.ri.parser.Token;
import org.apache.commons.jxpath.ri.parser.XPathParser;
import org.apache.commons.jxpath.ri.parser.XPathParserConstants;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component;

import junit.framework.TestCase;


@SuppressWarnings({ "deprecation", "unused", "restriction" })
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
	
}
