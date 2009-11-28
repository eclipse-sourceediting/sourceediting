/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - initial api and implementation bug 262765 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.testsuite.functions;

import java.net.URL;

import org.apache.xerces.xs.XSModel;
import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.test.AbstractPsychoPathTest;

public class InScopePrefixesFuncTest extends AbstractPsychoPathTest {

	// Evaluation of "in-scope-prefixes" function with incorrect argument type.
	public void test_fn_in_scope_prefixes_2() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeFunc/InScopePrefixesFunc/fn-in-scope-prefixes-2.xq";
		String expectedResult = "XPTY0004";
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildResultString(rs);

		} catch (XPathParserException ex) {
			actual = ex.code();
		} catch (StaticError ex) {
			actual = ex.code();
		} catch (DynamicError ex) {
			actual = ex.code();
		}

		assertEquals("XPath Result Error " + xqFile + ":", expectedResult,
				actual);
	}

	public void testInScopePrefixesRoot() throws Exception {
		// Test for the fix, for xpathDefaultNamespace
		String inputFile = "/TestSources/fsx_NS.xml";
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		// set up XPath default namespace in Dynamic Context
		DynamicContext dc = setupDynamicContext(schema);
		dc.add_namespace("fsx", "http://www.example.com/filesystem");

		String xpath = "fn:in-scope-prefixes(./fsx:MyComputer)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);
		
		String actual = buildResultString(rs);
		
		assertEquals("XPath Result Error: ", "fs", actual);

	}

	public void testInScopePrefixesChildNode() throws Exception {
		// Test for the fix, for xpathDefaultNamespace
		String inputFile = "/TestSources/fsx_NS.xml";
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		// set up XPath default namespace in Dynamic Context
		DynamicContext dc = setupDynamicContext(schema);
		dc.add_namespace("fsx", "http://www.example.com/filesystem");

		String xpath = "fn:in-scope-prefixes(./fsx:MyComputer/fsx:Drive[1])";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);
		
		String actual = buildResultString(rs);
		
		assertEquals("XPath Result Error: ", "fs", actual);

	}
}
