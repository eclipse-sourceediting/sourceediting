/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.validation.tests.internal;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;
/**
 * Test class for the XML validator to test bug fixes.
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
	List keys = new ArrayList();
	int numErrors = 0;
	int numWarnings = 0;

	runTest(testfile, keys, numErrors, numWarnings);
  }
  
  /**
   * Test /bugfixes/EmptyFile/Empty.xml.
   */
  public void testEmpty()
  {
  	String testname = "Empty";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "EmptyFile/" + testname + ".xml";
	List keys = new ArrayList();
	int numErrors = 0;
	int numWarnings = 0;

	runTest(testfile, keys, numErrors, numWarnings);
  }
  
  /**
   * Test /bugfixes/ValidateWithDTD/ValidateWithDTDValid.xml.
   */
  public void testValidateWithDTDValid()
  {
  	String testname = "ValidateWithDTDValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml";
	List keys = new ArrayList();
	int numErrors = 0;
	int numWarnings = 0;

	runTest(testfile, keys, numErrors, numWarnings);
  }
  
  /**
   * Test /bugfixes/ValidateWithDTD/ValidateWithDTDInvalid.xml.
   */
  public void testValidateWithDTDInvalid()
  {
  	String testname = "ValidateWithDTDInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "ValidateWithDTD/" + testname + ".xml";
	List keys = new ArrayList();
	keys.add("MSG_CONTENT_INVALID");
	int numErrors = 1;
	int numWarnings = 0;

	runTest(testfile, keys, numErrors, numWarnings);
  }
  
  /**
   * Test /bugfixes/NotWellFormed/NotWellFormed.xml.
   */
  public void testNotWellFormed()
  {
  	String testname = "NotWellFormed";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "NotWellFormed/" + testname + ".xml";
	List keys = new ArrayList();
	keys.add("ETagRequired");
	int numErrors = 1;
	int numWarnings = 0;

	runTest(testfile, keys, numErrors, numWarnings);
  }
  
  /**
   * Test /bugfixes/CannotLocateDTD/InvalidHost.xml.
   */
  public void testInvalidHost()
  {
  	String testname = "InvalidHost";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "CannotLocateDTD/" + testname + ".xml";
	List keys = new ArrayList();
	keys.add("FILE_NOT_FOUND");
	int numErrors = 0;
	int numWarnings = 1;

	runTest(testfile, keys, numErrors, numWarnings);
  }
  
  /**
   * Test /bugfixes/CannotLocateDTD/InvalidLocation.xml.
   */
  public void testInvalidLocation()
  {
  	String testname = "InvalidLocation";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "CannotLocateDTD/" + testname + ".xml";
	List keys = new ArrayList();
	keys.add("FILE_NOT_FOUND");
	int numErrors = 0;
	int numWarnings = 1;

	runTest(testfile, keys, numErrors, numWarnings);
  }
  
  /**
   * Test /bugfixes/NoGrammar/NoGrammar.xml.
   */
  public void testNoGrammar()
  {
  	String testname = "NoGrammar";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "NoGrammar/" + testname + ".xml";
	List keys = new ArrayList();
	keys.add("NO_GRAMMAR_FOUND");
	int numErrors = 0;
	int numWarnings = 1;

	try
	{
	  configuration.setFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR, true);
	  configuration.setFeature(XMLValidationConfiguration.INDICATE_NO_GRAMMAR, 1);
	}
	catch(Exception e)
	{
	  fail("Unable to set configuration WARN_NO_GRAMMAR.");
	}
	runTest(testfile, keys, numErrors, numWarnings);
	try
	{
	  configuration.setFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR, false);
	  configuration.setFeature(XMLValidationConfiguration.INDICATE_NO_GRAMMAR, 0);
	}
	catch(Exception e)
	{
	  fail("Unable to set configuration WARN_NO_GRAMMAR.");
	}
  }
  
  /**
   * Test /bugfixes/NoNamespaceSchema/NoNamespaceSchema.xml.
   */
  public void testNoNamespaceSchema()
  {
  	String testname = "NoNamespaceSchema";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + BUGFIXES_DIR + "NoNamespaceSchema/" + testname + ".xml";
	List keys = new ArrayList();
	keys.add("cvc-complex-type.2.4.b");
	int numErrors = 1;
	int numWarnings = 0;

	runTest(testfile, keys, numErrors, numWarnings);
  }
}
