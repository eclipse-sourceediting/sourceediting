/*******************************************************************************
 *Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.xpath2.processor.DOMLoader;
import org.eclipse.wst.xml.xpath2.processor.DefaultDynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.JFlexCupParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.eclipse.wst.xml.xpath2.processor.XercesLoader;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.function.FnFunctionLibrary;
import org.eclipse.wst.xml.xpath2.processor.function.XDTCtrLibrary;
import org.eclipse.wst.xml.xpath2.processor.function.XSCtrLibrary;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class TestXPath20 extends TestCase {

	Document domDoc = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Bundle bundle = Platform.getBundle("org.eclipse.wst.xml.xpath2.processor.tests");
		URL fileURL = bundle.getEntry("/TestSources/acme_corp.xml");
		InputStream is = fileURL.openStream();
		DOMLoader domloader = new XercesLoader();
		domloader.set_validating(false);
		domDoc = domloader.load(is);
	}
	
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		domDoc = null;
	}

	public void testLoadXML() throws Exception {
		assertNotNull(domDoc);
	}
	
	public void testSetupNullContenxt() throws Exception {
		DynamicContext dc = new DefaultDynamicContext(null, domDoc);
		dc.add_namespace("xsd", "http://www.w3.org/2001/XMLSchema");
		dc.add_namespace("xdt", "http://www.w3.org/2004/10/xpath-datatypes");
	}
	
	public void testAddLibraries() throws Exception {
		DynamicContext dc = new DefaultDynamicContext(null, domDoc);
		dc.add_namespace("xsd", "http://www.w3.org/2001/XMLSchema");
		dc.add_namespace("xdt", "http://www.w3.org/2004/10/xpath-datatypes");
		
		dc.add_function_library(new FnFunctionLibrary());
		dc.add_function_library(new XSCtrLibrary());
		dc.add_function_library(new XDTCtrLibrary());
	}
	
	public void testParseInvalidXPathExpression() throws Exception {
		try {
			XPathParser xpp = new JFlexCupParser();
			String xpath = "for  in /order/item return $x/price * $x/quantity";
			XPath path = xpp.parse(xpath);
			fail("XPath parsing suceeded when it should have failed.");
		} catch (XPathParserException ex) {
			
		}
	}
	
	public void testParseValidXPathExpression() throws Exception {
		XPathParser xpp = new JFlexCupParser();
		String xpath = "some $x in /students/student/name satisfies $x = \"Fred\"";
		XPath path = xpp.parse(xpath);
	}
	
	
	
}

