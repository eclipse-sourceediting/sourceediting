/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - bug 269833 - initial API and implementation
 *     Mukul Gandhi    - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.test;


import java.net.URL;


import org.apache.xerces.xs.XSModel;
import org.eclipse.wst.xml.xpath2.processor.DefaultEvaluator;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.Evaluator;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;



public class Bug269833 extends AbstractPsychoPathTest{
		
	public static void main(String[] args) {
		Bug269833 test = new Bug269833();        
	    try {        
            test.setUp();
            test.testKeywordAsNodeInXpath();
        } catch (Exception e) {            
            e.printStackTrace();
        }        
    }    
    
	protected void setUp() throws Exception {
		super.setUp();
        URL fileURL = new URL("http://resolve-locally/xml/note.xml");
        loadDOMDocument(fileURL);
     }
 	
 	public void testKeywordAsNodeInXpath() throws Exception {  
 		   // Get XML Schema Information for the Document  
 		   XSModel schema = getGrammar();  
 		  
 		   DynamicContext dc = setupDynamicContext(schema);  
 		    
 		     
 		   String xpath = "/note/to";
          compileXPath(xpath);
          ResultSequence rs = evaluate(domDoc);
  
 		    	    
 		   String actual = rs.first().string_value();
 		   
 		   assertEquals("Self", actual);	     
 		}  
}