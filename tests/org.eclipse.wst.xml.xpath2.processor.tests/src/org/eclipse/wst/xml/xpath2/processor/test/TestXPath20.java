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

import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSModel;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.xpath2.processor.DOMLoader;
import org.eclipse.wst.xml.xpath2.processor.DefaultDynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.JFlexCupParser;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.StaticChecker;
import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.StaticNameResolver;
import org.eclipse.wst.xml.xpath2.processor.XPathParser;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.eclipse.wst.xml.xpath2.processor.XercesLoader;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.function.FnFunctionLibrary;
import org.eclipse.wst.xml.xpath2.processor.function.XDTCtrLibrary;
import org.eclipse.wst.xml.xpath2.processor.function.XSCtrLibrary;
import org.eclipse.wst.xml.xpath2.processor.types.ElementType;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class TestXPath20 extends TestCase {

	Document domDoc = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Bundle bundle = Platform
				.getBundle("org.eclipse.wst.xml.xpath2.processor.tests");
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
			xpp.parse(xpath);
			fail("XPath parsing suceeded when it should have failed.");
		} catch (XPathParserException ex) {

		}
	}

	public void testParseValidXPathExpression() throws Exception {
		XPathParser xpp = new JFlexCupParser();
		String xpath = "some $x in /students/student/name satisfies $x = \"Fred\"";
		xpp.parse(xpath);
	}
	

	public void testProcessSimpleXpath() throws Exception {
	   // Get XML Schema Information for the Document
	   XSModel schema = getGrammar();

	   DynamicContext dc = setupDynamicContext(schema);
	  
	   String xpath = "/employees/employee[1]/location";

	   XPath path = compileXPath(dc, xpath);
	  
	   Evaluator eval = new DefaultEvaluator(dc, domDoc);
	   ResultSequence rs = eval.evaluate(path);
	  
	   ElementType result = (ElementType)rs.first();
	   String resultValue = result.node_value().getTextContent();
	  
	   assertEquals("Unexpected value returned", "Boston", resultValue);
	}

	private XSModel getGrammar() {
		ElementPSVI rootPSVI = (ElementPSVI) domDoc.getDocumentElement();
		XSModel schema = rootPSVI.getSchemaInformation();
		return schema;
	}

	private DynamicContext setupDynamicContext(XSModel schema) {
		DynamicContext dc = new DefaultDynamicContext(schema, domDoc);
		dc.add_namespace("xsd", "http://www.w3.org/2001/XMLSchema");
		dc.add_namespace("xdt", "http://www.w3.org/2004/10/xpath-datatypes");

		dc.add_function_library(new FnFunctionLibrary());
		dc.add_function_library(new XSCtrLibrary());
		dc.add_function_library(new XDTCtrLibrary());
		return dc;
	}

	private XPath compileXPath(DynamicContext dc, String xpath)
			throws XPathParserException, StaticError {
		XPathParser xpp = new JFlexCupParser();
		XPath path = xpp.parse(xpath);

		StaticChecker name_check = new StaticNameResolver(dc);
		name_check.check(path);
		return path;
	}

}
