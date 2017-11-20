/*******************************************************************************
 * Copyright (c) 2011 Jesper Steen Moller and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Jesper Steen Moller - bug 348737 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.core.xpath.tests;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;

import org.eclipse.wst.xml.xpath.core.util.XPath20Helper;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestXPath20Helper extends TestCase {

	public void testInvalidXPath() {
		try {
			XPath20Helper.compile("starts-with('123', '123', '123)");
			fail("Compiled successfully");
		} catch (XPathExpressionException ex) {
			
		}
	}
	
	public void testValidXPath() throws Exception {
		try {
		   XPath20Helper.compile("concat('123', '123')");
		} catch (XPathExpressionException ex) {
			fail("Failed to compile.");
			throw new Exception(ex.getMessage());
		}
	}
	

	public void testEvalXPath() throws Exception {
		try {
		    XPath20Helper.XPath2Engine xpath2Engine = new XPath20Helper.XPath2Engine();
		    xpath2Engine.setNamespaceContext(new NamespaceContext() {
				
				public Iterator getPrefixes(String namespaceURI) {
					return Collections.EMPTY_LIST.iterator();
				}
				
				public String getPrefix(String namespaceURI) {
					if (namespaceURI.equals(""))
						return XMLConstants.DEFAULT_NS_PREFIX;
					else
						return null;
				}
				
				public String getNamespaceURI(String prefix) {
					if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX))
						return "urn:test";
					else
						return null;
				}
			});
		    xpath2Engine.parse("//testNode1");

			DOMImplementation domImpl = createDOMImpl();
			Document document = domImpl.createDocument("urn:test", "test", null);
			Element rootelem = document.getDocumentElement();
			Element elem1 = document.createElementNS("urn:test", "testNode1");
			Element elem2 = document.createElementNS("urn:test", "testNode1");
			rootelem.appendChild(elem1);
			rootelem.appendChild(elem2);
			Attr attribute = document.createAttribute("attr");
			attribute.setValue("some value");
			elem2.setAttributeNode(attribute);

			assertEquals(2, xpath2Engine.execute(document).getLength());
		} catch (XPathExpressionException ex) {
			fail("Failed to compile.");
			throw new Exception(ex.getMessage());
		}
	}

	private DOMImplementation createDOMImpl()
			throws ParserConfigurationException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = documentFactory.newDocumentBuilder();
		DOMImplementation domImpl =  builder.getDOMImplementation();
		return domImpl;
	}

}
