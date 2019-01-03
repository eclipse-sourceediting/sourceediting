/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.core.xpath.tests;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	
	public void testcreateXPathFromNode() throws Exception {
		DOMImplementation domImpl = createDOMImpl();
		Document document = domImpl.createDocument(null, "test", null);
		assertEquals("Unexpected XPath value", "/test", XSLTXPathHelper.calculateXPathToNode(document.getFirstChild()));
	}

	private DOMImplementation createDOMImpl()
			throws ParserConfigurationException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = documentFactory.newDocumentBuilder();
		DOMImplementation domImpl =  builder.getDOMImplementation();
		return domImpl;
	}
	
	public void testCreateXPathFromNodeMultiple() throws Exception {
		DOMImplementation domImpl = createDOMImpl();
		Document document = domImpl.createDocument(null, "test", null);
		Element rootelem = document.getDocumentElement();
		Element elem1 = document.createElement("testNode1");
		Element elem2 = document.createElement("testNode1");
		rootelem.appendChild(elem1);
		rootelem.appendChild(elem2);
		assertEquals("Unexepected XPath value", "/test/testNode1[2]", XSLTXPathHelper.calculateXPathToNode(elem2));
	}
	
	public void testCreateXPathFromNodeAttribute() throws Exception {
		DOMImplementation domImpl = createDOMImpl();
		Document document = domImpl.createDocument(null, "test", null);
		Element rootelem = document.getDocumentElement();
		Element elem1 = document.createElement("testNode1");
		Element elem2 = document.createElement("testNode1");
		rootelem.appendChild(elem1);
		rootelem.appendChild(elem2);
		Attr attribute = document.createAttribute("attr");
		attribute.setValue("some value");
		elem2.setAttributeNode(attribute);
		assertEquals("Unexpected XPath value", "/test/testNode1[2]/@attr", XSLTXPathHelper.calculateXPathToNode(attribute));
	}

}
