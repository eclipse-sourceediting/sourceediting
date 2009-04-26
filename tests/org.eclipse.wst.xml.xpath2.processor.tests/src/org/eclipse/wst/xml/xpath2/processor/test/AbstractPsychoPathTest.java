/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import java.io.*;
import java.net.URL;

import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSModel;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.ast.*;
import org.eclipse.wst.xml.xpath2.processor.function.*;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;

import junit.framework.TestCase;

public class AbstractPsychoPathTest extends TestCase {

	protected Document domDoc = null;
	protected Bundle bundle = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bundle = Platform
				.getBundle("org.eclipse.wst.xml.xpath2.processor.tests");
	}

	protected void loadDOMDocument(URL fileURL) throws IOException,
			DOMLoaderException {
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

	protected XSModel getGrammar() {
		ElementPSVI rootPSVI = (ElementPSVI) domDoc.getDocumentElement();
		XSModel schema = rootPSVI.getSchemaInformation();
		return schema;
	}

	protected DynamicContext setupDynamicContext(XSModel schema) {
		DynamicContext dc = new DefaultDynamicContext(schema, domDoc);
		dc.add_namespace("xsd", "http://www.w3.org/2001/XMLSchema");
		dc.add_namespace("xdt", "http://www.w3.org/2004/10/xpath-datatypes");

		dc.add_function_library(new FnFunctionLibrary());
		dc.add_function_library(new XSCtrLibrary());
		dc.add_function_library(new XDTCtrLibrary());
		return dc;
	}

	protected XPath compileXPath(DynamicContext dc, String xpath)
			throws XPathParserException, StaticError {
		XPathParser xpp = new JFlexCupParser();
		XPath path = xpp.parse(xpath);

		StaticChecker name_check = new StaticNameResolver(dc);
		name_check.check(path);
		return path;
	}

}
