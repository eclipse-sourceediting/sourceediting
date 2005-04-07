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
public class PathsTest extends BaseTestCase
{ 
  private static final String PATHS_DIR = "Paths/";

  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(PathsTest.class);
  }
  
  /**
   * Test /Paths/Space InPath/SpaceInPathValid.xml.
   */
  public void testSpaceInPathValid()
  {
  	String testname = "SpaceInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Space InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Space InPath/SpaceInPathInvalid.xml.TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testSpaceInPathInvalid()
//  {
//  	String testname = "SpaceInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Space InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/CloseBracket)InPath/CloseBracketInPathValid.xml.
   */
  public void testCloseBracketInPathValid()
  {
  	String testname = "CloseBracketInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracket)InPath/CloseBracketInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testCloseBracketInPathInvalid()
//  {
//  	String testname = "CloseBracketInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/CloseBracketInFilename/CloseBracket)InFilenameValid.xml.
   */
  public void testCloseBracketInFilenameValid()
  {
  	String testname = "CloseBracket)InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracketInFilename/CloseBracket)InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testCloseBracketInFilenameInvalid()
//  {
//  	String testname = "CloseBracket)InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/Dash-InPath/DashInPathValid.xml.
   */
  public void testDashInPathValid()
  {
  	String testname = "DashInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Dash-InPath/DashInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testDashInPathInvalid()
//  {
//  	String testname = "DashInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/DashInFilename/Dash-InFilenameValid.xml.
   */
  public void testDashInFilenameValid()
  {
  	String testname = "Dash-InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/DashInFilename/Dash-InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testDashInFilenameInvalid()
//  {
//  	String testname = "Dash-InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/Exclamation!InPath/ExclamationInPathValid.xml.
   */
  public void testExclamationInPathValid()
  {
  	String testname = "ExclamationInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Exclamation!InPath/ExclamationInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testExclamationInPathInvalid()
//  {
//  	String testname = "ExclamationInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/ExclamationInFilename/Exclamation!InFilenameValid.xml.
   */
  public void testExclamationInFilenameValid()
  {
  	String testname = "Exclamation!InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/ExclamationInFilename/Exclamation!InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testExclamationInFilenameInvalid()
//  {
//  	String testname = "Exclamation!InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/OpenBracket(InPath/OpenBracketInPathValid.xml.
   */
  public void testOpenBracketInPathValid()
  {
  	String testname = "OpenBracketInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracket(InPath/OpenBracketInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testOpenBracketInPathInvalid()
//  {
//  	String testname = "OpenBracketInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/OpenBracketInFilename/OpenBracket(InFilenameValid.xml.
   */
  public void testOpenBracketInFilenameValid()
  {
  	String testname = "OpenBracket(InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracketInFilename/OpenBracket(InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testOpenBracketInFilenameInvalid()
//  {
//  	String testname = "OpenBracket(InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/Period.InPath/PeriodInPathValid.xml.
   */
  public void testPeriodBracketInPathValid()
  {
  	String testname = "PeriodInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Period.InPath/PeriodInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testPeriodBracketInPathInvalid()
//  {
//  	String testname = "PeriodInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/PeriodInFilename/Period.InFilenameValid.xml.
   */
  public void testPeriodBracketInFilenameValid()
  {
  	String testname = "Period.InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/PeriodInFilename/Period.InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */ 
//  public void testPeriodBracketInFilenameInvalid()
//  {
//  	String testname = "Period.InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/Quote'InPath/QuoteInPathValid.xml.
   */
  public void testQuoteInPathValid()
  {
  	String testname = "QuoteInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Quote'InPath/QuoteInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testQuoteInPathInvalid()
//  {
//  	String testname = "QuoteInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/QuoteInFilename/Quote'InFilenameValid.xml.
   */
  public void testQuoteInFilenameValid()
  {
  	String testname = "Quote'InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/QuoteInFilename/Quote'InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testQuoteInFilenameInvalid()
//  {
//  	String testname = "Quote'InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/SpaceInFilename/Space InFilenameValid.xml.
   */
  public void testSpaceInFilenameValid()
  {
  	String testname = "Space InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/SpaceInFilename/Space InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testSpaceInFilenameInvalid()
//  {
//  	String testname = "Space InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/Tilde~InPath/TildeInPathValid.xml.
   */
  public void testTildeInPathValid()
  {
  	String testname = "TildeInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Tilde~InPath/TildeInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testTildeInPathInvalid()
//  {
//  	String testname = "TildeInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/TildeInFilename/Tilde~InFilenameValid.xml.
   */
  public void testTildeInFilenameValid()
  {
  	String testname = "Tilde~InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/TildeInFilename/Tilde~InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testTildeInFilenameInvalid()
//  {
//  	String testname = "Tilde~InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/Underscore_InPath/UnderscoreInPathValid.xml.
   */
  public void testUnderscoreInPathValid()
  {
  	String testname = "UnderscoreInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Underscore_InPath/UnderscoreInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testUnderscoreInPathInvalid()
//  {
//  	String testname = "UnderscoreInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/UnderscoreInFilename/Underscore_InFilenameValid.xml.
   */
  public void testUnderscoreInFilenameValid()
  {
  	String testname = "Underscore_InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/UnderscoreInFilename/Underscore_InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testUnderscoreInFilenameInvalid()
//  {
//  	String testname = "Underscore_InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/AngleHat^InPath/AngleHatInPathValid.xml.
   */
  public void testAngleHatInPathValid()
  {
  	String testname = "AngleHatInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHat^InPath/AngleHatInPathInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testAngleHatInPathInvalid()
//  {
//  	String testname = "AngleHatInPathInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
  
  /**
   * Test /Paths/AngleHatInFilename/AngleHat^InFilenameValid.xml.
   */
  public void testAngleHatInFilenameValid()
  {
  	String testname = "AngleHat^InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xml";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xml-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xml-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHatInFilename/AngleHat^InFilenameInvalid.xml. TODO: Re-enable the test once the source of the linux problem is found.
   */
//  public void testAngleHatInFilenameInvalid()
//  {
//  	String testname = "AngleHat^InFilenameInvalid";
//    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xml";
//    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xml-log";
//    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xml-log";
//    
//    runTest(testfile, loglocation, idealloglocation);
//  }
}
