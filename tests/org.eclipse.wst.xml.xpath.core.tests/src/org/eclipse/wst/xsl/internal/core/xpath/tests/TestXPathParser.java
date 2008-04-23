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
 * $Id: TestXPathParser.java,v 1.2 2008/04/23 20:36:57 dacarver Exp $
 *********************************************************************************/
package org.eclipse.wst.xsl.internal.core.xpath.tests;

import java.io.StringBufferInputStream;


import org.apache.commons.jxpath.ri.parser.Token;
import org.apache.commons.jxpath.ri.parser.XPathParser;
import org.apache.commons.jxpath.ri.parser.XPathParserConstants;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.Component;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.XPath;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.DataTypes.Variable;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.DataTypes.impl.DataTypesFactoryImpl;
import org.eclipse.wst.xml.xpath.core.internal.model.XPath.impl.xpathFactoryImpl;

import junit.framework.Assert;
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
	
	public void testCreateXPathRoot() throws Exception {
		XPath xpath = new xpathFactoryImpl().createXPath();
		assertNotNull("Failed to create xpath class", xpath);
	}
	
	@SuppressWarnings("unchecked")
	public void testLoadXPathModel() throws Exception {
		EList components = new BasicEList();
		XPath xpath = new xpathFactoryImpl().createXPath();
		Variable variable = new DataTypesFactoryImpl().createVariable();
		variable.setName("test");
		variable.setBeginColumn(1);
		variable.setEndColumn(4);
		variable.setBeginLineNumber(1);
		variable.setEndLineNumber(1);
		components.add(variable);
		xpath.setComponents(components);
		assertNotNull("XPath Components array not set.", xpath.getComponentsList());
		EList xpathComp = xpath.getComponentsList();
		assertEquals("Number of components not equal to 1.", 1, xpathComp.size());
		if (!(xpathComp.get(0) instanceof Variable)) {
			Assert.fail("Component that was not a variable.");
		}
		variable = (Variable)xpathComp.get(0);
		assertEquals("Name does not equal test", "test", variable.getName());
		assertEquals("Beginning Column Number not 1", 1, variable.getBeginColumn());
		assertEquals("Ending Column Number not 4", 4, variable.getEndColumn());
		assertEquals("Beggning Line Number incorrect.", 1, variable.getBeginLineNumber());
		assertEquals("Ending Line Number incorrect.", 1, variable.getEndLineNumber());
	}
	
}
