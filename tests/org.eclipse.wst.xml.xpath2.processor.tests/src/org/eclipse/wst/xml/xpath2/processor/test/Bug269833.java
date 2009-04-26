/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - bug 269833 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;


import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSModel;
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
import org.w3c.dom.Document;

public class Bug269833 extends TestCase{
	
	Document domDoc;
	
	public static void main(String[] args) {
		Bug269833 test = new Bug269833();        
	    try {        
            test.setUp();
            test.testProcessSimpleXpath();
        } catch (Exception e) {            
            e.printStackTrace();
        }        
    }    
    
    protected void setUp() throws Exception {
        URL fileURL = new URL("http://www.w3schools.com/xml/note.xml");
        InputStream is = fileURL.openStream();  
        DOMLoader domloader = new XercesLoader();  
        domloader.set_validating(false);  
        domDoc = domloader.load(is);  
     }

    private XSModel getGrammar() {  
 	   ElementPSVI rootPSVI = (ElementPSVI)domDoc.getDocumentElement();  
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
 	
 	public void testProcessSimpleXpath() throws Exception {  
 		   // Get XML Schema Information for the Document  
 		   XSModel schema = getGrammar();  
 		  
 		   DynamicContext dc = setupDynamicContext(schema);  
 		    
 		     
 		   String xpath = "/note/to";
 		   XPath path = compileXPath(dc, xpath);  
 		    
 		   Evaluator eval = new DefaultEvaluator(dc, domDoc);  
 		   ResultSequence rs = eval.evaluate(path);  
 		    
 		   ElementType result = (ElementType)rs.first();  
 		   String resultValue = result.node_value().getNodeValue();  
 	    
 		   String actual = rs.first().string_value();
 		   
 		   assertEquals("Tove", actual); 		     
 		}  
}