/*******************************************************************************
 * Copyright (c) 2009, 2010 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - initial api and implementation bug 262765 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.testsuite.functions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.xerces.xs.XSModel;
import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.function.FnCollection;
import org.eclipse.wst.xml.xpath2.processor.test.AbstractPsychoPathTest;
import org.w3c.dom.Document;

public class SeqCollectionFuncTest extends AbstractPsychoPathTest {

	private static final String COLLECTION2 = "http://www.w3.org/2005/xpath-functions/collection2";
	private static final String COLLECTION1 = "http://www.w3.org/2005/xpath-functions/collection1";

	// Evaluation of fn:collection function with wrong arity.
	public void test_fn_collection_1() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-1.xq";
		String expectedResult = "XPST0017";
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

	// Evaluation of fn:collection, for a non existent resource.
	public void test_fn_collection_2() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-2.xq";
		String expectedResult = "FODC0004";
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

	// Evaluation of fn:collection with argument set to an invalid URI.
	public void test_fn_collection_3() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-3.xq";
		String expectedResult = "FODC0002";
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

	// Count the number of nodes in the collection.
	public void test_fn_collection_4() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-4.xq";
		String resultFile = "/ExpectedTestResults/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);
		loadCollection1Default(dc);

		String xpath = "fn:count(fn:collection(\"" + COLLECTION1 + "\"))";
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

	// Count the number of nodes in the collection.
	public void test_fn_collection_4d() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-4d.xq";
		String resultFile = "/ExpectedTestResults/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);
		loadCollection1Default(dc);

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

	// Count the number of nodes in the collection.
	public void test_fn_collection_5() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-5.xq";
		String resultFile = "/ExpectedTestResults/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-5.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);
		loadCollection2Default(dc);

		String xpath = "fn:count(fn:collection(\"" + COLLECTION2 + "\"))";
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

	// Count the number of nodes in the collection.
	public void test_fn_collection_5d() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-5d.xq";
		String resultFile = "/ExpectedTestResults/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-5.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);
		loadCollection2Default(dc);

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

	// Return elements that immediately contain TCP/IP.
	public void test_fn_collection_7() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-7.xq";
		String resultFile = "/ExpectedTestResults/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-7.txt";
		String expectedResult = "<result>" + getExpectedResult(resultFile) + "</result>";
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);
		loadCollection2Default(dc);

		String xpath = "fn:collection(\"" + COLLECTION2 + "\")//*[text()[contains(.,\"TCP/IP\")]]";
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = "<result>" + buildXMLResultString(rs) + "</result>";

		} catch (XPathParserException ex) {
			actual = ex.code();
		} catch (StaticError ex) {
			actual = ex.code();
		} catch (DynamicError ex) {
			actual = ex.code();
		}

		assertXMLEqual("XPath Result Error " + xqFile + ":", expectedResult,
				actual);

	}

	// Return the first title element in each document.
	public void test_fn_collection_8() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-8.xq";
		String resultFile = "/ExpectedTestResults/Functions/NodeSeqFunc/SeqCollectionFunc/fn-collection-8.txt";
		String expectedResult = "<result>" + getExpectedResult(resultFile) + "</result>";
		URL fileURL = bundle.getEntry(inputFile);
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);
		loadCollection1Default(dc);

		String xpath = "for $d in fn:collection(\"" + COLLECTION1 + "\") return ($d//title)[1]";
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = "<result>" + buildXMLResultString(rs) + "</result>";

		} catch (XPathParserException ex) {
			actual = ex.code();
		} catch (StaticError ex) {
			actual = ex.code();
		} catch (DynamicError ex) {
			actual = ex.code();
		}

		assertXMLEqual("XPath Result Error " + xqFile + ":", expectedResult,
				actual);

	}

	private void loadCollection1Default(DynamicContext dc) throws Exception {
		DOMLoader domload = new XercesLoader();
		URL fileURL = bundle.getEntry("/TestSources/bib.xml");
		Document dom1 = domload.load(fileURL.openStream());
		fileURL = bundle.getEntry("/TestSources/reviews.xml");
		Document dom2 = domload.load(fileURL.openStream());
		ArrayList arraylist = new ArrayList();
		arraylist.add(dom1);
		arraylist.add(dom2);
		HashMap map = new HashMap();
		map.put(FnCollection.DEFAULT_COLLECTION_URI, arraylist);
		map.put(COLLECTION1, arraylist);
		dc.set_collections(map);
	}

	private void loadCollection2Default(DynamicContext dc) throws Exception {
		DOMLoader domload = new XercesLoader();
		URL fileURL = bundle.getEntry("/TestSources/bib.xml");
		Document dom1 = domload.load(fileURL.openStream());
		fileURL = bundle.getEntry("/TestSources/reviews.xml");
		Document dom2 = domload.load(fileURL.openStream());
		fileURL = bundle.getEntry("/TestSources/books.xml");
		Document dom3 = domload.load(fileURL.openStream());
		ArrayList arraylist = new ArrayList();
		arraylist.add(dom1);
		arraylist.add(dom2);
		arraylist.add(dom3);
		HashMap map = new HashMap();
		map.put(FnCollection.DEFAULT_COLLECTION_URI, arraylist);
		map.put(COLLECTION2, arraylist);
		dc.set_collections(map);
	}

}
