/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.validation.tests.internal;
import junit.framework.Test;
import junit.framework.TestSuite;
/**
 * Test class for the XML validator to test the XMLProject.
 * 
 * @author Lawrence Mandel, IBM
 */
public class XMLExamplesTest extends BaseTestCase
{ 
  private static final String XMLExamples_DIR = "XMLExamples/";
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XMLExamplesTest.class);
  }
  
  /**
   * Test /XMLExamples/Invoice/Invoice.xml.
   */
  public void testInvoice()
  {
  	String testname = "Invoice";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + XMLExamples_DIR + "Invoice/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + XMLExamples_DIR + "Invoice/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + XMLExamples_DIR + "Invoice/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /XMLExamples/PublicationCatalogue/Catalogue.xml.
   */
  public void testCatalogue()
  {
  	String testname = "Catalogue";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + XMLExamples_DIR + "PublicationCatalogue/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + XMLExamples_DIR + "PublicationCatalogue/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + XMLExamples_DIR + "PublicationCatalogue/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
}
