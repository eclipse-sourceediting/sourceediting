/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *     Mukul Gandhi - bug 273719 - improvements to fn:string-length function
 *     Mukul Gandhi - bug 273795 - improvements to fn:substring function
 *     Mukul Gandhi - bug 274471 - improvements to fn:string function
 *     Mukul Gandhi - bug 274725 - improvements to fn:base-uri function.
 *     Mukul Gandhi - bug 274731 - improvements to fn:document-uri function.
 *     Mukul Gandhi - bug 274784 - improvements to xs:boolean data type
 *     Mukul Gandhi - bug 274805 - improvements to xs:integer data type
 *     Mukul Gandhi - bug 274952 - implements xs:long data type
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.xs.XSModel;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSBoolean;

public class TestBugs extends AbstractPsychoPathTest {

	public void testStringLengthWithElementArg() throws Exception {
		// Bug 273719
		URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "string-length(x) > 2";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testBug273795Arity2() throws Exception {
		// Bug 273795
		URL fileURL = bundle.getEntry("/bugTestFiles/bug273795.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// test with arity 2
		String xpath = "substring(x, 3) = 'happy'";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testBug273795Arity3() throws Exception {
		// Bug 273795
		URL fileURL = bundle.getEntry("/bugTestFiles/bug273795.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// test with arity 3
		String xpath = "substring(x, 3, 4) = 'happ'";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testStringFunctionBug274471() throws Exception {
		// Bug 274471
		URL fileURL = bundle.getEntry("/bugTestFiles/bug274471.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "x/string() = 'unhappy'";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testStringLengthFunctionBug274471() throws Exception {
		// Bug 274471. string-length() with arity 0
		URL fileURL = bundle.getEntry("/bugTestFiles/bug274471.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "x/string-length() = 7";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testNormalizeSpaceFunctionBug274471() throws Exception {
		// Bug 274471. normalize-space() with arity 0
		URL fileURL = bundle.getEntry("/bugTestFiles/bug274471.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "x/normalize-space() = 'unhappy'";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testAnyUriEqualityBug() throws Exception {
		// Bug 274719
		// reusing the XML document from another bug
		URL fileURL = bundle.getEntry("/bugTestFiles/bug274471.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:anyURI('abc') eq xs:anyURI('abc')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testBaseUriBug() throws Exception {
		// Bug 274725 - Mukul Ghandi

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();

		// for testing this bug, we read the XML document from the web.
		// this ensures, that base-uri property of DOM is not null.
		domDoc = docBuilder.parse("http://www.w3schools.com/xml/note.xml");

		// we pass XSModel as null for this test case. Otherwise, we would
		// get an exception.
		DynamicContext dc = setupDynamicContext(null);

		String xpath = "base-uri(note) eq xs:anyURI('http://www.w3schools.com/xml/note.xml')";

		// please note: The below XPath would also work, with base-uri using
		// arity 0.
		// String xpath =
		// "note/base-uri() eq xs:anyURI('http://www.w3schools.com/xml/note.xml')";

		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testDocumentUriBug() throws Exception {
		// Bug 274731
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();

		domDoc = docBuilder.parse("http://www.w3schools.com/xml/note.xml");

		DynamicContext dc = setupDynamicContext(null);

		String xpath = "document-uri(/) eq xs:anyURI('http://www.w3schools.com/xml/note.xml')";

		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = "false";

		if (result != null) {
			actual = result.string_value();
		}

		assertEquals("true", actual);
	}

	public void testBooleanTypeBug() throws Exception {
		// Bug 274784
		// reusing the XML document from another bug
		URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:boolean('1') eq xs:boolean('true')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testDateConstructorBug() throws Exception {
		// Bug 274792
		URL fileURL = bundle.getEntry("/bugTestFiles/bug274792.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:date(x) eq xs:date('2009-01-01')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testIntegerDataTypeBug() throws Exception {
		// Bug 274805
		URL fileURL = bundle.getEntry("/bugTestFiles/bug274805.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:integer(x) gt 100";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testLongDataType() throws Exception {
		// Bug 274952
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// long min value is -9223372036854775808
		// and max value can be 9223372036854775807
		String xpath = "xs:long('9223372036854775807') gt 0";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testIntDataType() throws Exception {
		// Bug 275105
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// int min value is -2147483648
		// and max value can be 2147483647
		String xpath = "xs:int('2147483647') gt 0";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testSchemaAwarenessForAttributes() throws Exception {
		// Bug 276134
		URL fileURL = bundle.getEntry("/bugTestFiles/bug276134.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/bug276134.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "person/@dob eq xs:date('2006-12-10')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);

	}

	public void testSchemaAwarenessForElements() throws Exception {
		// Bug 276134
		URL fileURL = bundle.getEntry("/bugTestFiles/bug276134_2.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/bug276134_2.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "person/dob eq xs:date('2006-12-10')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
}
