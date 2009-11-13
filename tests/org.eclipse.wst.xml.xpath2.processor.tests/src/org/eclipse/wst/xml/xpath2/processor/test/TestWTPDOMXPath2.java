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

	  //Simple filter using data query from xml source and the "gt" operator.
	   public void test_filterexpressionhc1() throws Exception {
	      String inputFile = "/TestSources/works.xml";
	      String xqFile = "/Queries/XQuery/Expressions/SeqExpr/FilterExpr/filterexpressionhc1.xq";
	      String resultFile = "/ExpectedTestResults/Expressions/SeqExpr/FilterExpr/filterexpressionhc1.txt";
	      String expectedResult = "<result>" + getExpectedResult(resultFile) + "</result>";
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

	      assertXMLEqual("XPath Result Error " + xqFile + ":", expectedResult, actual);
	        

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
