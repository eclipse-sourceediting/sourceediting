/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (STAR) - initial API and implementation
 *     Jin Mingjan - bug 262765 -  extractXPathExpression and getExpectedResults
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.xerces.jaxp.validation.XSGrammarPoolContainer;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSModel;
import org.custommonkey.xmlunit.XMLTestCase;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.ast.*;
import org.eclipse.wst.xml.xpath2.processor.function.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.DocType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.ElementType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class AbstractPsychoPathTest extends XMLTestCase {

	protected Document domDoc = null;
	protected Bundle bundle = null;

	private static final String INPUT_CONTEXT = "input-context";
	private static final String INPUT_CONTEXT1 = "input-context1";
	private static final String INPUT_CONTEXT2 = "input-context2";
	// private static final String S_COMMENT1 = "(:";
	private static final String S_COMMENT2 = ":)";
	private static final String DECLARE_NAMESPACE = "declare namespace";
	private static final String DECLARE_VARIABLE = "declare variable";
	private static final String REGEX_DN = "declare namespace\\s+(\\w[-_\\w]*)\\s*=\\s*['\"]([^;]*)['\"];";
	private static HashMap<String, String> namespaceMap = new HashMap<String, String>(
			3);
	private static HashMap<String, String> inputMap = new HashMap<String, String>(
			3);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bundle = Platform
				.getBundle("org.eclipse.wst.xml.xpath2.processor.tests");

		if (bundle == null) {
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		}
		System.setProperty("javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema","org.apache.xerces.jaxp.validation.XMLSchemaFactory");
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
		super.tearDown();
		domDoc = null;
	}

	protected XSModel getGrammar() {
		ElementPSVI rootPSVI = (ElementPSVI) domDoc.getDocumentElement();
		XSModel schema = rootPSVI.getSchemaInformation();
		return schema;
	}

	protected void loadDOMDocument(URL fileURL, URL schemaURL)
			throws IOException, DOMLoaderException, SAXException {
		InputStream is = fileURL.openStream();
		InputStream schemaIs = schemaURL.openStream();
		Schema jaxpSchema = getSchema(schemaIs);
		DOMLoader domloader = new XercesLoader(jaxpSchema);
		domloader.set_validating(false);
		domDoc = domloader.load(is);
	}

	private Schema getSchema(InputStream schemaIs) throws SAXException {
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new StreamSource(schemaIs));
		return schema;
	}

	protected XSModel getGrammar(URL schemaURL) throws IOException,
			SAXException {
		InputStream schemaIs = schemaURL.openStream();
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new StreamSource(schemaIs));
		XSGrammarPoolContainer poolContainer = (XSGrammarPoolContainer) schema;
		XMLGrammarPool pool = poolContainer.getGrammarPool();
		Grammar[] grammars = pool
				.retrieveInitialGrammarSet(XMLGrammarDescription.XML_SCHEMA);

		XSGrammar[] xsGrammars = new XSGrammar[grammars.length];
		System.arraycopy(grammars, 0, xsGrammars, 0, grammars.length);

		return xsGrammars[0].toXSModel(xsGrammars);
	}

	protected DynamicContext setupDynamicContext(XSModel schema) {
		DynamicContext dc = new DefaultDynamicContext(schema, domDoc);
		dc.add_namespace("xs", "http://www.w3.org/2001/XMLSchema");
		dc.add_namespace("xsd", "http://www.w3.org/2001/XMLSchema");
		dc.add_namespace("fn", "http://www.w3.org/2005/xpath-functions");
		dc.add_namespace("xml", "http://www.w3.org/XML/1998/namespace");

		dc.add_function_library(new FnFunctionLibrary());
		dc.add_function_library(new XSCtrLibrary());
		setupVariables(dc);
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

	protected String getExpectedResult(String xqFile) {
		String resultFile = xqFile;
		//
		if (resultFile.length() < 10) { // <9 enough? like XPST0001
			return resultFile;
		}
		String content = "";
		//
		InputStream isrf;
		try {
			isrf = bundle.getEntry(resultFile).openStream();
			BufferedReader rfreader = new BufferedReader(new InputStreamReader(
					isrf));
			// XXX:assume char buffer 2048 is long enough;1024 maybe enough
			// Exception: Axes085, NodeTest003/04/05,...
			int bufferLength = 2048;
			if ((resultFile.indexOf("Axes085") != -1)
					|| (resultFile.indexOf("NodeTest003") != -1)
					|| (resultFile.indexOf("NodeTest004") != -1)
					|| (resultFile.indexOf("NodeTest005") != -1)) {
				bufferLength = 40000;
			} else if (resultFile.indexOf("ForExpr013") != -1) {
				bufferLength = 433500;
			} else if (resultFile.indexOf("ForExpr016") != -1
					|| (resultFile.indexOf("ReturnExpr011") != -1)
					|| (resultFile.indexOf("sgml-queries-results-q1") != -1)
					|| (resultFile.indexOf("sgml-queries-results-q2") != -1)) {
				bufferLength = 10240;
			}
			char[] cbuf = new char[bufferLength];
			int nByte = rfreader.read(cbuf);
			assertTrue(resultFile, nByte < bufferLength);// assert nice buffer
			// length

			content = new String(cbuf).trim();
			rfreader.close();
			isrf.close();

		} catch (IOException e) {
			// e.printStackTrace();
			content = "XPST0003";
		}
		return content;
	}

	public String extractXPathExpression(String xqFile, String inputFile) {
		// get the xpath2 expr from xq file, first
		char[] cbuf = new char[2048];//
		String content = null;
		String xpath2Expr = null;

		try {
			InputStream isxq = bundle.getEntry(xqFile).openStream();
			BufferedReader xqreader = new BufferedReader(new InputStreamReader(
					isxq));
			int nByte = xqreader.read(cbuf);
			assertTrue(xqFile, nByte < 2048);
			content = new String(cbuf).trim();
			//
			if (content.indexOf(INPUT_CONTEXT) != -1
					&& content.indexOf(INPUT_CONTEXT1) == -1
					&& content.indexOf(INPUT_CONTEXT2) == -1) {
				inputMap.put(INPUT_CONTEXT, inputFile);
			} else if (content.indexOf(INPUT_CONTEXT1) == -1) {
				inputMap.put(INPUT_CONTEXT1, inputFile);
			} else if (content.indexOf(INPUT_CONTEXT2) != -1) {
				inputMap.put(INPUT_CONTEXT2, inputFile);
			}
			//	        
			if (content.indexOf(DECLARE_NAMESPACE) != -1) {
				setupNamespace(content);
			}
			//
			assertTrue(content.lastIndexOf(S_COMMENT2) != -1);// assert to get
			xpath2Expr = content.substring(content.lastIndexOf(S_COMMENT2) + 2)
					.trim();

			xqreader.close();
			isxq.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xpath2Expr;
	}

	protected void setupNamespace(String content) {
		Pattern p = Pattern.compile(REGEX_DN);
		Matcher m = p.matcher(content);
		while (m.find()) {
			assertTrue(m.groupCount() == 2);//
			namespaceMap.put(m.group(1), m.group(2));
		}

	}

	protected DynamicContext setupVariables(DynamicContext dc) {
		dc.add_variable(new QName("x"));
		dc.add_variable(new QName("var"));
		AnyType docType = new DocType(domDoc, 0);
		ElementType elementType = new ElementType(domDoc.getDocumentElement(),
				0);
		dc.set_variable(new QName("input-context1"), docType);
		dc.set_variable(new QName("input-context"), docType);

		return dc;
	}

	protected String buildResultString(ResultSequence rs) {
		String actual = new String();
		Iterator<AnyType> iterator = rs.iterator();
		while (iterator.hasNext()) {
			AnyType anyType = iterator.next();
			
			actual = actual + anyType.string_value() + " ";
		}

		return actual.trim();
	}
	
	protected String buildXMLResultString(ResultSequence rs) throws Exception {
        DOMImplementationLS domLS = (DOMImplementationLS) domDoc.getImplementation().getFeature("LS", "3.0");
        LSOutput outputText = domLS.createLSOutput();
        LSSerializer serializer = domLS.createLSSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputText.setByteStream(outputStream);
        
		String actual = new String();
		Iterator<NodeType> iterator = rs.iterator();
		while (iterator.hasNext()) {
			NodeType nodeType = iterator.next();
			Node node = nodeType.node_value();
			serializer.write(node, outputText);
		}

		actual = outputStream.toString();
		actual = actual.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		actual = actual.replace("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", "");
		outputStream.close();
		return actual.trim();
	}
	
	protected String formatResultString(String resultFile) throws Exception {
		DOMLoader domloader = new XercesLoader(null);
		domloader.set_validating(false);
		InputStream is = bundle.getEntry(resultFile).openStream();		
		Document resultDoc = domloader.load(is);

        DOMImplementationLS domLS = (DOMImplementationLS) resultDoc.getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = domLS.createLSSerializer();
        
        String actual = serializer.writeToString(resultDoc.getDocumentElement());

		actual = actual.replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "");
		return actual.trim();
	}


}
