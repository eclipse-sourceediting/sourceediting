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
 * Test class for the XML validator to test bug fixes.
 * 
 * @author Lawrence Mandel, IBM
 */
public class BugFixesTest extends BaseTestCase
{ 
  private static final String BUGFIXES_DIR = "bugfixes/";

  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(BugFixesTest.class);
  }
  
  /**
   * Test /bugfixes/RootNoNSChildNS/RootNoNSChildNS.xml.
   */
  public void testRootNoNSChildNS()
  {
  	String testname = "RootNoNSChildNS";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "RootNoNSChildNS/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "RootNoNSChildNS/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "RootNoNSChildNS/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /bugfixes/EmptyFile/Empty.xml.
   */
  public void testEmpty()
  {
  	String testname = "Empty";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "EmptyFile/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "EmptyFile/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "EmptyFile/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /bugfixes/ValidateWithDTD/ValidateWithDTDValid.xml.
   */
  public void testValidateWithDTDValid()
  {
  	String testname = "ValidateWithDTDValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /bugfixes/ValidateWithDTD/ValidateWithDTDInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testValidateWithDTDInvalid()
//  {
//  	String testname = "ValidateWithDTDInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
}
