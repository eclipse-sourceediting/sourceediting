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
 *     Mukul Gandhi - bug 274725 - improvements to fn:base-uri function
 *     Mukul Gandhi - bug 274731 - improvements to fn:document-uri function
 *     Mukul Gandhi - bug 274784 - improvements to xs:boolean data type
 *     Mukul Gandhi - bug 274805 - improvements to xs:integer data type
 *     Mukul Gandhi - bug 274952 - implements xs:long data type
 *     Mukul Gandhi - bug 277599 - implements xs:nonPositiveInteger data type
 *     Mukul Gandhi - bug 277602 - implements xs:negativeInteger data type
 *     Mukul Gandhi - bug 277599 - implements xs:nonPositiveInteger data type
 *     Mukul Gandhi - bug 277608   implements xs:short data type
 *                    bug 277609   implements xs:nonNegativeInteger data type
 *                    bug 277629   implements xs:unsignedLong data type
 *                    bug 277632   implements xs:positiveInteger data type
 *                    bug 277639   implements xs:byte data type
 *                    bug 277642   implements xs:unsignedInt data type
 *                    bug 277645   implements xs:unsignedShort data type
 *                    bug 277650   implements xs:unsignedByte data type
 *                    bug 279373   improvements to multiply operation on xs:yearMonthDuration
 *                                 data type.
 *                    bug 279376   improvements to xs:yearMonthDuration division operation
 *                    bug 281046   implementation of xs:base64Binary data type                                
 *  Jesper S Moller - bug 286061   correct handling of quoted string 
 *  Jesper S Moller - bug 280555 - Add pluggable collation support
 *  Jesper S Moller - bug 297958   Fix fn:nilled for elements
 *  Mukul Gandhi    - bug 298519   improvements to fn:number implementation, catering
 *                                 to node arguments.
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import java.net.URL;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.xs.XSModel;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.xpath2.processor.CollationProvider;
import org.eclipse.wst.xml.xpath2.processor.DefaultDynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSBoolean;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDecimal;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDouble;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDuration;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSFloat;
import org.osgi.framework.Bundle;

public class TestBugs extends AbstractPsychoPathTest {

	private static final String URN_X_ECLIPSE_XPATH20_FUNKY_COLLATOR = "urn:x-eclipse:xpath20:funky-collator";

	private Bundle bundle;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		bundle = Platform
				.getBundle("org.eclipse.wst.xml.xpath2.processor.tests");

	}

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

		assertEquals("false", actual);
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
		// Bug 274725

		// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		loadDOMDocument(new URL("http://www.w3schools.com/xml/note.xml"));

		// for testing this bug, we read the XML document from the web.
		// this ensures, that base-uri property of DOM is not null.
		// domDoc = docBuilder.parse("http://www.w3schools.com/xml/note.xml");

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
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
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
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
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

	public void testNilled() throws Exception {
		// This is a terrible shortcoming in the test suite, I'd say
		URL fileURL = bundle.getEntry("/bugTestFiles/bugNilled.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/bugNilled.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		assertTrue(evaluateBoolean(dc, "empty( nilled( / ) )"));
		assertTrue(evaluateBoolean(dc, "empty( nilled( /root/@attr1 ) )"));
		assertTrue(evaluateBoolean(dc, "empty( nilled( /root/element1/text() ) )"));

		assertFalse(evaluateBoolean(dc, "nilled(/root/element1)"));
		assertTrue(evaluateBoolean(dc, "nilled(/root/element2)"));
		assertFalse(evaluateBoolean(dc, "nilled(/root/element3)"));
		assertFalse(evaluateBoolean(dc, "nilled(/root/element4)"));
	}

	// I can't stand to see so much duplicated code!!!
	private boolean evaluateBoolean(DynamicContext dc, String xpath) throws Exception {
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		return result.value();
	}

	public void testXSNonPositiveInteger() throws Exception {
		// Bug 277599
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:nonPositiveInteger is -INF
		// max value is 0
		String xpath = "xs:nonPositiveInteger('0') eq 0";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSNegativeInteger() throws Exception {
		// Bug 277602
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:negativeInteger is -INF
		// max value is -1
		String xpath = "xs:negativeInteger('-1') eq -1";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSShort() throws Exception {
		// Bug 277608
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:short is -32768
		// max value of xs:short is 32767
		String xpath = "xs:short('-32768') eq -32768";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSNonNegativeInteger() throws Exception {
		// Bug 277609
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:nonNegativeInteger is 0
		// max value of xs:nonNegativeInteger is INF
		String xpath = "xs:nonNegativeInteger('0') eq 0";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSUnsignedLong() throws Exception {
		// Bug 277629
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:unsignedLong is 0
		// max value of xs:unsignedLong is 18446744073709551615
		String xpath = "xs:unsignedLong('0') eq 0";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSPositiveInteger() throws Exception {
		// Bug 277632
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:positiveInteger is 1
		// max value of xs:positiveInteger is INF
		String xpath = "xs:positiveInteger('1') eq 1";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSByte() throws Exception {
		// Bug 277639
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:byte is -128
		// max value of xs:byte is 127
		String xpath = "xs:byte('-128') eq -128";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSUnsignedInt() throws Exception {
		// Bug 277642
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");

		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:unsignedInt is 0
		// max value of xs:unsignedInt is 4294967295
		String xpath = "xs:unsignedInt('4294967295') eq xs:unsignedInt('4294967295')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSUnsignedShort() throws Exception {
		// Bug 277645
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:unsignedShort is 0
		// max value of xs:unsignedShort is 65535
		String xpath = "xs:unsignedShort('65535') eq 65535";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSYearMonthDurationMultiply() throws Exception {
		// Bug 279373
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:yearMonthDuration('P2Y11M') * 2.3";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSDuration result = (XSDuration) rs.first();

		String actual = result.string_value();

		assertEquals("P6Y9M", actual);
	}

	public void testXSYearMonthDurationDivide1() throws Exception {
		// Bug 279376
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:yearMonthDuration('P2Y11M') div 1.5";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSDuration result = (XSDuration) rs.first();

		String actual = result.string_value();

		assertEquals("P1Y11M", actual);
	}

	public void testXSYearMonthDurationDivide2() throws Exception {
		// Bug 279376
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:yearMonthDuration('P3Y4M') div xs:yearMonthDuration('-P1Y4M')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSDecimal result = (XSDecimal) rs.first();

		String actual = result.string_value();

		assertEquals("-2.5", actual);
	}

	public void testXSDayTimeDurationMultiply() throws Exception {
		// Bug 279377
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:dayTimeDuration('PT2H10M') * 2.1";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSDuration result = (XSDuration) rs.first();

		String actual = result.string_value();

		assertEquals("PT4H33M", actual);
	}

	public void testXSDayTimeDurationDivide() throws Exception {
		// Bug 279377
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:dayTimeDuration('P1DT2H30M10.5S') div 1.5";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSDuration result = (XSDuration) rs.first();

		String actual = result.string_value();

		assertEquals("PT17H40M7S", actual);
	}

	public void testNegativeZeroDouble() throws Exception {
		// Bug 279406
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "-(xs:double('0'))";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSDouble result = (XSDouble) rs.first();

		String actual = result.string_value();

		assertEquals("-0", actual);
	}

	public void testNegativeZeroFloat() throws Exception {
		// Bug 279406
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "-(xs:float('0'))";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSFloat result = (XSFloat) rs.first();

		String actual = result.string_value();

		assertEquals("-0", actual);
	}

	public void testXSUnsignedByte() throws Exception {
		// Bug 277650
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		// min value of xs:unsignedByte is 0
		// max value of xs:unsignedByte is 255
		String xpath = "xs:unsignedByte('255') eq 255";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSBase64Binary() throws Exception {
		// Bug 281046
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:base64Binary('cmxjZ3R4c3JidnllcmVuZG91aWpsbXV5Z2NhamxpcmJkaWFhbmFob2VsYXVwZmJ1Z2dmanl2eHlzYmhheXFtZXR0anV2dG1q') eq xs:base64Binary('cmxjZ3R4c3JidnllcmVuZG91aWpsbXV5Z2NhamxpcmJkaWFhbmFob2VsYXVwZmJ1Z2dmanl2eHlzYmhheXFtZXR0anV2dG1q')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testXSHexBinary() throws Exception {
		// Bug 281054
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "xs:hexBinary('767479716c6a647663') eq xs:hexBinary('767479716c6a647663')";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void testElementTypedValue() throws Exception {
		// test for fix in ElementType.java, involving incorrectly computing
		// typed value of element node, in case of validating element node,
		// with a user defined simple XSD type.
		URL fileURL = bundle.getEntry("/bugTestFiles/elementTypedValueBug.xml");
		URL schemaURL = bundle
				.getEntry("/bugTestFiles/elementTypedValueBug.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/Transportation/mode eq 'air'";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);

	}

	public void testBug286061_quoted_string_literals_no_normalize()
			throws Exception {

		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "'\"\"'"; // the expression '""' contains no escapes

		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		String resultValue = rs.first().string_value();

		assertEquals("\"\"", resultValue);
	}

	public void testBug286061_quoted_string_literals() throws Exception {

		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "concat(  'Don''t try this' ,  \" at \"\"home\"\",\"  ,  ' she said'  )";

		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		String resultValue = rs.first().string_value();

		assertEquals("Don't try this at \"home\", she said", resultValue);
	}

	public void testBug280555_collations() throws Exception {
		// Setup context
		DefaultDynamicContext dc = setupDynamicContext(null);
		dc.set_collation_provider(createLengthCollatorProvider());
		Evaluator eval = new DefaultEvaluator(dc, domDoc);

		// Parse expression
		XPath path = compileXPath(dc, " 'abc' < 'de' ");

		// Evaluate once
		XSBoolean bval = (XSBoolean) eval.evaluate(path).first();
		assertTrue("'abc' < 'def' for normal collations", bval.value());

		// Evaluate again with the funny collator
		dc.set_default_collation(URN_X_ECLIPSE_XPATH20_FUNKY_COLLATOR);
		XSBoolean bval2 = (XSBoolean) eval.evaluate(path).first();
		assertFalse("'abc' < 'def' for normal collations", bval2.value());
	}

	public void testXPathDefaultNamespace() throws Exception {
		// Test for the fix, for xpathDefaultNamespace
		URL fileURL = bundle
				.getEntry("/bugTestFiles/xpathDefNamespaceTest.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		// set up XPath default namespace in Dynamic Context
		DynamicContext dc = setupDynamicContext(schema);
		addXPathDefaultNamespace("http://xyz");

		String xpath = "X/message = 'hello'";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testXPathInstanceOf1() throws Exception {
		// Bug 298267
		URL fileURL = bundle.getEntry("/bugTestFiles/elementTypedValueBug.xml");
		URL schemaURL = bundle
				.getEntry("/bugTestFiles/elementTypedValueBug.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/Transportation/mode instance of element()";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testXPathInstanceOf2() throws Exception {
		// Bug 298267
		URL fileURL = bundle.getEntry("/bugTestFiles/elementTypedValueBug.xml");
		URL schemaURL = bundle
				.getEntry("/bugTestFiles/elementTypedValueBug.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/Transportation/mode instance of element(mode)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testXPathInstanceNonExistantElement() throws Exception {
		// Bug 298267
		URL fileURL = bundle.getEntry("/bugTestFiles/elementTypedValueBug.xml");
		URL schemaURL = bundle
				.getEntry("/bugTestFiles/elementTypedValueBug.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/Transportation/mode instance of element(x)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("false", actual);
	}
	
	public void testXPathInstanceOf4() throws Exception {
		// Bug 298267
		URL fileURL = bundle.getEntry("/bugTestFiles/elementTypedValueBug.xml");
		URL schemaURL = bundle
				.getEntry("/bugTestFiles/elementTypedValueBug.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/Transportation/mode instance of element(mode, abc)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("false", actual);
	}
	
	public void testFnNumber_Evaluation1() throws Exception {
		// Bug 298519
		URL fileURL = bundle.getEntry("/bugTestFiles/fnNumberBug.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/fnNumberBug.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "number(Example/x) ge 18";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testFnNumber_Evaluation2() throws Exception {
		// Bug 298519
		bundle = Platform.getBundle("org.w3c.xqts.testsuite");
		URL fileURL = bundle.getEntry("/TestSources/emptydoc.xml");
		loadDOMDocument(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = getGrammar();

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "number(xs:unsignedByte('20')) ge 18";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testAttrNode_Test1() throws Exception {
		// Bug 298535
		URL fileURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/x[1]/@mesg instance of attribute()";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testAttrNode_Test2() throws Exception {
		// Bug 298535
		URL fileURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/x[1]/@mesg instance of attribute(xx)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("false", actual);
	}
	
	public void testAttrNode_Test3() throws Exception {
		// Bug 298535
		URL fileURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/x[1]/@mesg instance of attribute(*, mesg_Type)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testAttrNode_Test4() throws Exception {
		// Bug 298535
		URL fileURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/x[1]/@mesg instance of attribute(*, abc)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("false", actual);
	}
	
	public void testAttrNode_Test5() throws Exception {
		// Bug 298535
		URL fileURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/x[1]/@mesg instance of attribute(mesg, mesg_Type)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}
	
	public void testAttrNode_Test6() throws Exception {
		// Bug 298535
		URL fileURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/x[1]/@mesg instance of attribute(mesg, abc)";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("false", actual);
	}
	
	public void testAttrNode_Test7() throws Exception {
		// Bug 298535
		URL fileURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xml");
		URL schemaURL = bundle.getEntry("/bugTestFiles/attrNodeTest.xsd");

		loadDOMDocument(fileURL, schemaURL);

		// Get XSModel object for the Schema
		XSModel schema = getGrammar(schemaURL);

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = "Example/x/@mesg instance of attribute(mesg, mesg_Type)*";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		XSBoolean result = (XSBoolean) rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	private CollationProvider createLengthCollatorProvider() {
		return new CollationProvider() {
			@SuppressWarnings("unchecked")
			public Comparator get_collation(String name) {
				if (name.equals(URN_X_ECLIPSE_XPATH20_FUNKY_COLLATOR)) {
					return new Comparator<String>() {
						public int compare(String o1, String o2) {
							return o1.length() - o2.length();
						}
					};
				}
				return null;
			}
		};
	}
}
