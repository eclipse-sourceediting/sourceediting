package org.eclipse.wst.xml.xpath2.processor.test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.apache.xerces.xs.XSModel;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.StaticError;
import org.eclipse.wst.xml.xpath2.processor.XPathParserException;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TestWTPDOMXPath2 extends AbstractPsychoPathTest {
	IDOMModel model = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (model != null) {
			model.releaseFromRead();
		}
	}

	public void testSimpleWTPDOM() throws Exception {
		// Test for the fix, for xpathDefaultNamespace
		bundle = Platform
				.getBundle("org.eclipse.wst.xml.xpath2.processor.tests");

		URL fileURL = bundle.getEntry("/bugTestFiles/bug273719.xml");
		super.domDoc = load(fileURL);

		// set up XPath default namespace in Dynamic Context
		DynamicContext dc = setupDynamicContext(null);

		String xpath = "string-length(x) > 2";
		XPath path = compileXPath(dc, xpath);

		Evaluator eval = new DefaultEvaluator(dc, domDoc);
		ResultSequence rs = eval.evaluate(path);

		AnyType result = rs.first();

		String actual = result.string_value();

		assertEquals("true", actual);
	}

	public void test_ForExpr005() throws Exception {
		String inputFile = "/TestSources/fsx.xml";
		String xqFile = "/Queries/XQuery/Expressions/FLWORExpr/ForExpr/ForExpr005.xq";
		String resultFile = "/ExpectedTestResults/Expressions/FLWORExpr/ForExpr/ForExpr005.xml";
		String expectedResult = getExpectedResult(resultFile);

		URL fileURL = bundle.getEntry(inputFile);
		super.domDoc = load(fileURL);
		DynamicContext dc = setupDynamicContext(null);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Type declaration containing multiple 'as' keywords.
	public void test_ForExprType013() throws Exception {
		String inputFile = "/TestSources/orderData.xml";
		String xqFile = "/Queries/XQuery/Expressions/FLWORExpr/ForExprType/ForExprType013.xq";
		String expectedResult = "XPST0003";
		URL fileURL = bundle.getEntry(inputFile);
		super.domDoc = load(fileURL);

		DynamicContext dc = setupDynamicContext(null);

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

	// No children for child::*.
	public void test_Axes001_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes001.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes001-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		super.domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Simple filter using data query from xml source and the "gt" operator.
	public void test_filterexpressionhc1() throws Exception {
		String inputFile = "/TestSources/works.xml";
		String xqFile = "/Queries/XQuery/Expressions/SeqExpr/FilterExpr/filterexpressionhc1.xq";
		String resultFile = "/ExpectedTestResults/Expressions/SeqExpr/FilterExpr/filterexpressionhc1.txt";
		String expectedResult = "<result>" + getExpectedResult(resultFile)
				+ "</result>";
		URL fileURL = bundle.getEntry(inputFile);
		super.domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
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

	// No children, child::name gets none.
	public void test_Axes002_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes002.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes002-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluates abbreviated syntax, "hours". Selects the "hours" element
	// children of the context node.
	public void test_abbreviatedSyntax_1() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-1.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates abbreviated syntax, "text()". Selects all text node children of
	// the context node.
	public void test_abbreviatedSyntax_2() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-2.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluates abbreviated syntax, "@name". Selects the name attribute of the
	// context node.
	public void test_abbreviatedSyntax_3() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-3.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-3.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates abbreviated syntax, "employee[1]". Selects the first employee
	// child of the context node.
	public void test_abbreviatedSyntax_5() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-5.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/AbbrAxes/abbreviatedSyntax-5.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: empty Sequence is Single Node Element.
	public void test_nodeexpression3() throws Exception {
		String inputFile = "/TestSources/works.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeSame/nodeexpression3.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeSame/nodeexpression3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: empty Sequence is Sequence of single Element Node.
	public void test_nodeexpression4() throws Exception {
		String inputFile = "/TestSources/staff.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeSame/nodeexpression4.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeSame/nodeexpression4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: Single Node Element is empty Sequence.
	public void test_nodeexpression9() throws Exception {
		String inputFile = "/TestSources/works.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeSame/nodeexpression9.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeSame/nodeexpression9.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: Single Node Element is Single Node Element.
	public void test_nodeexpression11() throws Exception {
		String inputFile = "/TestSources/works.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeSame/nodeexpression11.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeSame/nodeexpression11.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a simple predicate with a "true" value (uses "fn:true").
	public void test_predicates_1() throws Exception {
		String inputFile = "/TestSources/atomicns.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Predicates/predicates-1.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Predicates/predicates-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluation of a simple predicate with a "false" value (uses "fn:false").
	// Use "fn:count" to avoid empty file.
	public void test_predicates_2() throws Exception {
		String inputFile = "/TestSources/atomicns.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Predicates/predicates-2.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Predicates/predicates-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a simple predicate with a "true" value (uses "fn:true" and
	// fn:not()).
	public void test_predicates_3() throws Exception {
		String inputFile = "/TestSources/atomicns.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Predicates/predicates-3.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Predicates/predicates-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluation of a simple predicate set to a boolean expression ("and"
	// operator), returns true.
	public void test_predicates_4() throws Exception {
		String inputFile = "/TestSources/atomicns.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Predicates/predicates-4.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Predicates/predicates-4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluation of a simple predicate set to a boolean expression ("or"
	// operator), return true.
	public void test_predicates_5() throws Exception {
		String inputFile = "/TestSources/atomicns.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Predicates/predicates-5.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Predicates/predicates-5.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluation of a simple predicate set to a boolean expression ("and"
	// operator), returns false. Use "fn:count" to avoid empty file.
	public void test_predicates_6() throws Exception {
		String inputFile = "/TestSources/atomicns.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Predicates/predicates-6.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Predicates/predicates-6.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// No children of any kind, elem//child::* gets none.
	public void test_Axes074_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes074.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes074-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::* gets nothing when no element children.
	public void test_Axes074_2() throws Exception {
		String inputFile = "/TestSources/Tree1Text.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes074.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes074-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::* gets 1 child element.
	public void test_Axes074_3() throws Exception {
		String inputFile = "/TestSources/Tree1Child.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes074.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes074-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::* gets several nodes.
	public void test_Axes074_4() throws Exception {
		String inputFile = "/TestSources/TreeRepeat.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes074.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes074-4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::name gets nothing when no sub-tree.
	public void test_Axes075_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes075.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes075-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::name gets none when no elements have that name.
	public void test_Axes075_2() throws Exception {
		String inputFile = "/TestSources/Tree1Child.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes075.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes075-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::name gets the 1 descendant of that name.
	public void test_Axes075_3() throws Exception {
		String inputFile = "/TestSources/TreeCompass.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes075.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes075-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::name gets several elements.
	public void test_Axes075_4() throws Exception {
		String inputFile = "/TestSources/TreeStack.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes075.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes075-4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// No children, elem//child::node() gets none.
	public void test_Axes076_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes076.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes076-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::node() gets 1 child element.
	public void test_Axes076_2() throws Exception {
		String inputFile = "/TestSources/Tree1Child.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes076.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes076-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//child::node() gets 1 child text node.
	public void test_Axes076_3() throws Exception {
		String inputFile = "/TestSources/Tree1Text.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes076.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes076-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Several children for elem//child::node().
	public void test_Axes076_4() throws Exception {
		String inputFile = "/TestSources/TreeRepeat.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes076.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes076-4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// No children for elem//*.
	public void test_Axes077_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes077.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes077-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// 1 child for elem//*.
	public void test_Axes077_2() throws Exception {
		String inputFile = "/TestSources/Tree1Child.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes077.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes077-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Many children for elem//*.
	public void test_Axes077_3() throws Exception {
		String inputFile = "/TestSources/TreeRepeat.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes077.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes077-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//name gets none when there are no children.
	public void test_Axes078_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes078.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes078-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//name gets none when no elements have that name.
	public void test_Axes078_2() throws Exception {
		String inputFile = "/TestSources/Tree1Child.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes078.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes078-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//name gets the 1 descendant of that name.
	public void test_Axes078_3() throws Exception {
		String inputFile = "/TestSources/TreeCompass.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes078.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes078-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//name gets several elements.
	public void test_Axes078_4() throws Exception {
		String inputFile = "/TestSources/TreeStack.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes078.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes078-4.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// No children, elem//node() gets none.
	public void test_Axes079_1() throws Exception {
		String inputFile = "/TestSources/TreeTrunc.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes079.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes079-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//node() gets 1 child element.
	public void test_Axes079_2() throws Exception {
		String inputFile = "/TestSources/Tree1Child.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes079.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes079-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// elem//node() gets 1 child text node.
	public void test_Axes079_3() throws Exception {
		String inputFile = "/TestSources/Tree1Text.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/Steps/Axes/Axes079.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/Steps/Axes/Axes079-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: empty Sequence "<<" empty Sequence.
	public void test_nodeexpression17() throws Exception {
		String inputFile = "/TestSources/emptydoc.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeBefore/nodeexpression17.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeBefore/nodeexpression17.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: empty Sequence "<<" Single Node Element.
	public void test_nodeexpression19() throws Exception {
		String inputFile = "/TestSources/works.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeBefore/nodeexpression19.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeBefore/nodeexpression19.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: empty Sequence "<<" Sequence of single Element Node.
	public void test_nodeexpression20() throws Exception {
		String inputFile = "/TestSources/staff.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeBefore/nodeexpression20.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeBefore/nodeexpression20.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: Single Node Element "<<" empty Sequence.
	public void test_nodeexpression25() throws Exception {
		String inputFile = "/TestSources/works.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeBefore/nodeexpression25.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeBefore/nodeexpression25.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluation of a Node expression With the operands/operator set with the
	// following format: Single Node Element "<<" Single Node Element.
	public void test_nodeexpression27() throws Exception {
		String inputFile = "/TestSources/works.xml";
		String xqFile = "/Queries/XQuery/Expressions/Operators/NodeOp/NodeBefore/nodeexpression27.xq";
		String resultFile = "/ExpectedTestResults/Expressions/Operators/NodeOp/NodeBefore/nodeexpression27.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	public Document load(java.net.URL url) throws Exception {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		InputStream inStream = url.openStream();
		if (inStream == null)
			throw new FileNotFoundException("Can't file resource stream "
					+ url.getFile());
		model = (IDOMModel) modelManager.getModelForRead(url.getFile(),
				inStream, null);

		return model.getDocument();
	}

	// Evaluates unabbreviated syntax - child::empnum - select empnum children
	// of the context node.
	public void test_unabbreviatedSyntax_1() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-1.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-1.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax - child::* - select all element children
	// of the context node.
	public void test_unabbreviatedSyntax_2() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-2.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-2.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax - child::text() - select all text node
	// children of the context node.
	public void test_unabbreviatedSyntax_3() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-3.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-3.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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
	
	// Evaluates unabbreviated syntax - parent::node() - Selects the parent of
	// the context node.
	public void test_unabbreviatedSyntax_8() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-8.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-8.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax - descendant::empnum - Selects the
	// "empnum" descendants of the context node.
	public void test_unabbreviatedSyntax_9() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-9.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-9.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax - descendant-or-self::employee - Selects
	// all the "employee" descendant of the context node (selects employee, if
	// the context node is "employee").
	public void test_unabbreviatedSyntax_12() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-12.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-12.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax - self::employee - Selects the context
	// node, if it is an "employee" element, otherwise returns empty sequence.
	// This test retuns an "employee" element.
	public void test_unabbreviatedSyntax_13() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-13.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-13.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax - self::hours - Selects the context node,
	// if it is an "hours" element, otherwise returns empty sequence. This test
	// retuns the empty sequence.
	public void test_unabbreviatedSyntax_14() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-14.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-14.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

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

	// Evaluates unabbreviated syntax child::employee/descendant:empnum- Selects
	// the empnum element descendants of the employee element children of the
	// context node.
	public void test_unabbreviatedSyntax_15() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-15.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-15.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax child::*/child:pnum)- Selects the pnum
	// grandchildren of the context node.
	public void test_unabbreviatedSyntax_16() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-16.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-16.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate /descendant::pnum - Selects all
	// the pnum elements in the same document as the context node.
	public void test_unabbreviatedSyntax_18() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-18.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-18.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// /descendant::employee/child::pnum selects all the pnum elements that have
	// an "employee" parent and that are in the same document as the context
	// node.
	public void test_unabbreviatedSyntax_19() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-19.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-19.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::employee[fn:position() = 1]". Selects the first employee child of
	// the context node.
	public void test_unabbreviatedSyntax_20() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-20.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-20.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::employee[fn:position() = fn:last()]". Selects the last "employee"
	// child of the context node.
	public void test_unabbreviatedSyntax_21() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-21.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-21.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::employee[fn:position() = fn:last()-1]. Selects the previous to the last one "employee"
	// child of the context node.
	public void test_unabbreviatedSyntax_22() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-22.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-22.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::hours[fn:position() > 1]". Selects all the para children of the
	// context node other than the first "hours" child of the context node.
	public void test_unabbreviatedSyntax_23() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-23.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-23.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "/descendant::employee[fn:position() = 12]". Selects the twelfth employee
	// element in the document containing the context node.
	public void test_unabbreviatedSyntax_26() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-26.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-26.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "/child::works/child::employee[fn:position() = 5]/child::hours[fn:position() = 2]".
	// Selects the second "hours" of the fifth "employee" of the "works" whose
	// parent is the document node that contains the context node.
	public void test_unabbreviatedSyntax_27() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-27.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-27.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::employee[attribute::name eq "Jane Doe 11"]". Selects all
	// "employee" children of the context node that have a "name" attribute with
	// value "Jane Doe 11".
	public void test_unabbreviatedSyntax_28() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-28.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-28.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::employee[attribute::gender eq 'female'][fn:position() = 5]".
	// Selects the fifth employee child of the context node that has a gender
	// attribute with value "female".
	public void test_unabbreviatedSyntax_29() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-29.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-29.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::employee[child::empnum = 'E3']". Selects the employee children of
	// the context node that have one or more empnum children whose typed value
	// is equal to the string "E3".
	public void test_unabbreviatedSyntax_30() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-30.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-30.txt";
		String expectedResult = "<result>" + getExpectedResult(resultFile)
				+ "</result>";
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::employee[child::status]". Selects the employee children of the
	// context node that have one or more status children.
	public void test_unabbreviatedSyntax_31() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-31.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-31.txt";
		String expectedResult = formatResultString(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::*[self::pnum or self::empnum]". Selects the pnum and empnum
	// children of the context node.
	public void test_unabbreviatedSyntax_32() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-32.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-32.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	// Evaluates unabbreviated syntax. Evaluate
	// "child::*[self::empnum or self::pnum][fn:position() = fn:last()]".
	// Selects the last empnum or pnum child of the context node.
	public void test_unabbreviatedSyntax_33() throws Exception {
		String inputFile = "/TestSources/works-mod.xml";
		String xqFile = "/Queries/XQuery/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-33.xq";
		String resultFile = "/ExpectedTestResults/Expressions/PathExpr/UnabbrAxes/unabbreviatedSyntax-33.txt";
		String expectedResult = getExpectedResult(resultFile);
		URL fileURL = bundle.getEntry(inputFile);
		domDoc = load(fileURL);

		// Get XML Schema Information for the Document
		XSModel schema = null;

		DynamicContext dc = setupDynamicContext(schema);

		String xpath = extractXPathExpression(xqFile, inputFile);
		String actual = null;
		try {
			XPath path = compileXPath(dc, xpath);

			Evaluator eval = new DefaultEvaluator(dc, domDoc);
			ResultSequence rs = eval.evaluate(path);

			actual = buildXMLResultString(rs);

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

	  //FLWOR expression returns a constructed sequence.
	   public void test_ReturnExpr011() throws Exception {
	      String inputFile = "/TestSources/fsx.xml";
	      String xqFile = "/Queries/XQuery/Expressions/FLWORExpr/ReturnExpr/ReturnExpr011.xq";
	      String resultFile = "/ExpectedTestResults/Expressions/FLWORExpr/ReturnExpr/ReturnExpr011.xml";
	      String expectedResult = getExpectedResult(resultFile);
	      URL fileURL = bundle.getEntry(inputFile);
	      domDoc = load(fileURL);
	      
	      // Get XML Schema Information for the Document
	      XSModel schema = null;

	      DynamicContext dc = setupDynamicContext(schema);

	      String xpath = extractXPathExpression(xqFile, inputFile);
	      String actual = null;
	      try {
		   	  XPath path = compileXPath(dc, xpath);
		
		      Evaluator eval = new DefaultEvaluator(dc, domDoc);
		      ResultSequence rs = eval.evaluate(path);
	         
	          actual = buildXMLResultString(rs);
		
	      } catch (XPathParserException ex) {
	    	 actual = ex.code();
	      } catch (StaticError ex) {
	         actual = ex.code();
	      } catch (DynamicError ex) {
	         actual = ex.code();
	      }

	      assertEquals("XPath Result Error " + xqFile + ":", expectedResult, actual);
	        

	   }
	
	@Override
	protected String buildXMLResultString(ResultSequence rs) throws Exception {
		Iterator<NodeType> iterator = rs.iterator();
		StringBuffer buffer = new StringBuffer();
		while (iterator.hasNext()) {
			AnyType aat = iterator.next();
			if (aat instanceof NodeType) {
				NodeType nodeType = (NodeType) aat;
				IDOMNode node = (IDOMNode) nodeType.node_value();
				buffer = buffer.append(node.getSource());
			} else {
				buffer = buffer.append(aat.string_value());
			}
		}

		return buffer.toString();
	}

}
