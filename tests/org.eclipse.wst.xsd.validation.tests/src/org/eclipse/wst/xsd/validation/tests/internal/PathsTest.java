/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.validation.tests.internal;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests that test various types of variations in file paths.
 */
public class PathsTest extends BaseTestCase
{
  private String PATHS_DIR = "Paths/";
  
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(PathsTest.class);
  }
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
  {
    super.setUp();
  }
  
  /**
   * Test /Paths/Space InPath/SpaceInPathValid.xsd
   */
  public void testSpaceInPathValid()
  {
    String testname = "SpaceInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Space InPath/SpaceInPathInvalid.xsd
   */
  public void testSpaceInPathInvalid()
  {
    String testname = "SpaceInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracket)InPath/CloseBracketInPathValid.xsd
   */
  public void testCloseBracketInPathValid()
  {
    String testname = "CloseBracketInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracket)InPath/CloseBracketInPathInvalid.xsd
   */
  public void testCloseBrackettInPathInvalid()
  {
    String testname = "CloseBracketInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracketInFilename/CloseBracket)InFilenameValid.xsd
   */
  public void testCloseBracketInFilenameValid()
  {
    String testname = "CloseBracket)InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracketInFilename/CloseBracket)InFilenameInvalid.xsd
   */
  public void testCloseBracketInFilenameInvalid()
  {
    String testname = "CloseBracket)InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Dash-InPath/DashInPathValid.xsd
   */
  public void testDashInPathValid()
  {
    String testname = "DashInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Dash-InPath/DashInPathInvalid.xsd
   */
  public void testDashInPathInvalid()
  {
    String testname = "DashInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/DashInFilename/Dash-InFilenameValid.xsd
   */
  public void testDashInFilenameValid()
  {
    String testname = "Dash-InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/DashInFilename/Dash-InFilenameInvalid.xsd
   */
  public void testDashInFilenameInvalid()
  {
    String testname = "Dash-InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Exclamation!InPath/ExclamationInPathValid.xsd
   */
  public void testExclamationInPathValid()
  {
    String testname = "ExclamationInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Exclamation!InPath/ExclamationInPathInvalid.xsd
   */
  public void testExclamationInPathInvalid()
  {
    String testname = "ExclamationInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/ExclamationInFilename/Exclamation!InFilenameValid.xsd
   */
  public void testExclamationInFilenameValid()
  {
    String testname = "Exclamation!InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/ExclamationInFilename/Exclamation!InFilenameInvalid.xsd
   */
  public void testExclamationInFilenameInvalid()
  {
    String testname = "Exclamation!InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracket(InPath/OpenBracketInPathValid.xsd
   */
  public void testOpenBracketInPathValid()
  {
    String testname = "OpenBracketInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracket(InPath/OpenBracketInPathInvalid.xsd
   */
  public void testOpenBracketInPathInvalid()
  {
    String testname = "OpenBracketInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracketInFilename/OpenBracket(InFilenameValid.xsd
   */
  public void testOpenBracketInFilenameValid()
  {
    String testname = "OpenBracket(InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracketInFilename/OpenBracket(InFilenameInvalid.xsd
   */
  public void testOpenBracketInFilenameInvalid()
  {
    String testname = "OpenBracket(InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Period.InPath/PeriodInPathValid.xsd
   */
  public void testPeriodInPathValid()
  {
    String testname = "PeriodInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Period.InPath/PeriodInPathInvalid.xsd
   */
  public void testPeriodInPathInvalid()
  {
    String testname = "PeriodInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/PeriodInFilename/Period.InFilenameValid.xsd
   */
  public void testPeriodInFilenameValid()
  {
    String testname = "Period.InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/PeriodInFilename/Period.InFilenameInvalid.xsd
   */
  public void testPeriodInFilenameInvalid()
  {
    String testname = "Period.InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Quote'InPath/QuoteInPathValid.xsd
   */
  public void testQuoteInPathValid()
  {
    String testname = "QuoteInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Quote'InPath/QuoteInPathInvalid.xsd
   */
  public void testQuoteInPathInvalid()
  {
    String testname = "QuoteInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/QuoteInFilename/Quote'InFilenameValid.xsd
   */
  public void testQuoteInFilenameValid()
  {
    String testname = "Quote'InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/QuoteInFilename/Quote'InFilenameInvalid.xsd
   */
  public void testQuoteInFilenameInvalid()
  {
    String testname = "Quote'InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/SpaceInFilename/Space InFilenameValid.xsd
   */
  public void testSpaceInFilenameValid()
  {
    String testname = "Space InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/SpaceInFilename/Space InFilenameInvalid.xsd
   */
  public void testSpaceInFilenameInvalid()
  {
    String testname = "Space InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Tilde~InPath/TildeInPathValid.xsd
   */
  public void testTildeInPathValid()
  {
    String testname = "TildeInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Tilde~InPath/TildeInPathInvalid.xsd
   */
  public void testTildeInPathInvalid()
  {
    String testname = "TildeInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/TildeInFilename/Tilde~InFilenameValid.xsd
   */
  public void testTildeInFilenameValid()
  {
    String testname = "Tilde~InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/TildeInFilename/Tilde~InFilenameInvalid.xsd
   */
  public void testTildeInFilenameInvalid()
  {
    String testname = "Tilde~InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Underscore_InPath/UnderscoreInPathValid.xsd
   */
  public void testUnderscoreInPathValid()
  {
    String testname = "UnderscoreInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Underscore_InPath/UnderscoreInPathInvalid.xsd
   */
  public void testUnderscoreInPathInvalid()
  {
    String testname = "UnderscoreInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/UnderscoreInFilename/Underscore_InFilenameValid.xsd
   */
  public void testUnderscoreInFilenameValid()
  {
    String testname = "Underscore_InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/UnderscoreInFilename/Underscore_InFilenameInvalid.xsd
   */
  public void testUnderscoreInFilenameInvalid()
  {
    String testname = "Underscore_InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHat^InPath/AngleHatInPathValid.xsd
   */
  public void testAngleHatInPathValid()
  {
    String testname = "AngleHatInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHat^InPath/AngleHatInPathInvalid.xsd
   */
  public void testAngleHatInPathInvalid()
  {
    String testname = "AngleHatInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHatInFilename/AngleHat^InFilenameValid.xsd
   */
  public void testAngleHatInFilenameValid()
  {
    String testname = "AngleHat^InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHatInFilename/AngleHat^InFilenameInvalid.xsd
   */
  public void testAngleHatInFilenameInvalid()
  {
    String testname = "AngleHat^InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /*****************
   * Test Imports
   *****************/
  
  /**
   * Test /Paths/AngleHatInFilename/ImportAngleHat^InFilenameValid.xsd
   */
  public void testImportAngleHatInFilenameValid()
  {
    String testname = "ImportAngleHat^InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHatInFilename/ImportAngleHat^InFilenameInvalid.xsd
   */
  public void testImportAngleHatInFilenameInvalid()
  {
    String testname = "ImportAngleHat^InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHatInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHat^InPath/ImportAngleHatInPathValid.xsd
   */
  public void testImportAngleHatInPathValid()
  {
    String testname = "ImportAngleHatInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/AngleHat^InPath/ImportAngleHatInPathInvalid.xsd
   */
  public void testImportAngleHatInPathInvalid()
  {
    String testname = "ImportAngleHatInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "AngleHat^InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  

  /**
   * Test /Paths/Space InPath/ImportSpaceInPathValid.xsd
   */
  public void testImportSpaceInPathValid()
  {
    String testname = "ImportSpaceInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Space InPath/ImportSpaceInPathInvalid.xsd
   */
  public void testImportSpaceInPathInvalid()
  {
    String testname = "ImportSpaceInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Space InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracket)InPath/ImportCloseBracketInPathValid.xsd
   */
  public void testImportCloseBracketInPathValid()
  {
    String testname = "ImportCloseBracketInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracket)InPath/ImportCloseBracketInPathInvalid.xsd
   */
  public void testImportCloseBrackettInPathInvalid()
  {
    String testname = "ImportCloseBracketInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracket)InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracketInFilename/ImportCloseBracket)InFilenameValid.xsd
   */
  public void testImportCloseBracketInFilenameValid()
  {
    String testname = "ImportCloseBracket)InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/CloseBracketInFilename/ImportCloseBracket)InFilenameInvalid.xsd
   */
  public void testImportCloseBracketInFilenameInvalid()
  {
    String testname = "ImportCloseBracket)InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "CloseBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Dash-InPath/ImportDashInPathValid.xsd
   */
  public void testImportDashInPathValid()
  {
    String testname = "ImportDashInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Dash-InPath/ImportDashInPathInvalid.xsd
   */
  public void testImportDashInPathInvalid()
  {
    String testname = "ImportDashInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Dash-InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/DashInFilename/ImportDash-InFilenameValid.xsd
   */
  public void testImportDashInFilenameValid()
  {
    String testname = "ImportDash-InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/DashInFilename/ImportDash-InFilenameInvalid.xsd
   */
  public void testImportDashInFilenameInvalid()
  {
    String testname = "ImportDash-InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "DashInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Exclamation!InPath/ImportExclamationInPathValid.xsd
   */
  public void testImportExclamationInPathValid()
  {
    String testname = "ImportExclamationInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Exclamation!InPath/ImportExclamationInPathInvalid.xsd
   */
  public void testImportExclamationInPathInvalid()
  {
    String testname = "ImportExclamationInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Exclamation!InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/ExclamationInFilename/ImportExclamation!InFilenameValid.xsd
   */
  public void testImportExclamationInFilenameValid()
  {
    String testname = "ImportExclamation!InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/ExclamationInFilename/ImportExclamation!InFilenameInvalid.xsd
   */
  public void testImportExclamationInFilenameInvalid()
  {
    String testname = "ImportExclamation!InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "ExclamationInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracket(InPath/ImportOpenBracketInPathValid.xsd
   */
  public void testImportOpenBracketInPathValid()
  {
    String testname = "ImportOpenBracketInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracket(InPath/ImportOpenBracketInPathInvalid.xsd
   */
  public void testImportOpenBracketInPathInvalid()
  {
    String testname = "ImportOpenBracketInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracket(InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracketInFilename/ImportOpenBracket(InFilenameValid.xsd
   */
  public void testImportOpenBracketInFilenameValid()
  {
    String testname = "ImportOpenBracket(InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/OpenBracketInFilename/ImportOpenBracket(InFilenameInvalid.xsd
   */
  public void testImportOpenBracketInFilenameInvalid()
  {
    String testname = "ImportOpenBracket(InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "OpenBracketInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Period.InPath/ImportPeriodInPathValid.xsd
   */
  public void testImportPeriodInPathValid()
  {
    String testname = "ImportPeriodInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Period.InPath/ImportPeriodInPathInvalid.xsd
   */
  public void testImportPeriodInPathInvalid()
  {
    String testname = "ImportPeriodInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Period.InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/PeriodInFilename/ImportPeriod.InFilenameValid.xsd
   */
  public void testImportPeriodInFilenameValid()
  {
    String testname = "ImportPeriod.InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/PeriodInFilename/ImportPeriod.InFilenameInvalid.xsd
   */
  public void testImportPeriodInFilenameInvalid()
  {
    String testname = "ImportPeriod.InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "PeriodInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Quote'InPath/ImportQuoteInPathValid.xsd
   */
  public void testImportQuoteInPathValid()
  {
    String testname = "ImportQuoteInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Quote'InPath/ImportQuoteInPathInvalid.xsd
   */
  public void testImportQuoteInPathInvalid()
  {
    String testname = "ImportQuoteInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Quote'InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/QuoteInFilename/ImportQuote'InFilenameValid.xsd
   */
  public void testImportQuoteInFilenameValid()
  {
    String testname = "ImportQuote'InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/QuoteInFilename/ImportQuote'InFilenameInvalid.xsd
   */
  public void testImportQuoteInFilenameInvalid()
  {
    String testname = "ImportQuote'InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "QuoteInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/SpaceInFilename/ImportSpace InFilenameValid.xsd
   */
  public void testImportSpaceInFilenameValid()
  {
    String testname = "ImportSpace InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/SpaceInFilename/ImportSpace InFilenameInvalid.xsd
   */
  public void testImportSpaceInFilenameInvalid()
  {
    String testname = "ImportSpace InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "SpaceInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Tilde~InPath/ImportTildeInPathValid.xsd
   */
  public void testImportTildeInPathValid()
  {
    String testname = "ImportTildeInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Tilde~InPath/ImportTildeInPathInvalid.xsd
   */
  public void testImportTildeInPathInvalid()
  {
    String testname = "ImportTildeInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Tilde~InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/TildeInFilename/ImportTilde~InFilenameValid.xsd
   */
  public void testImportTildeInFilenameValid()
  {
    String testname = "ImportTilde~InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/TildeInFilename/ImportTilde~InFilenameInvalid.xsd
   */
  public void testImportTildeInFilenameInvalid()
  {
    String testname = "ImportTilde~InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "TildeInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Underscore_InPath/ImportUnderscoreInPathValid.xsd
   */
  public void testImportUnderscoreInPathValid()
  {
    String testname = "ImportUnderscoreInPathValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/Underscore_InPath/ImportUnderscoreInPathInvalid.xsd
   */
  public void testImportUnderscoreInPathInvalid()
  {
    String testname = "ImportUnderscoreInPathInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "Underscore_InPath/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/UnderscoreInFilename/ImportUnderscore_InFilenameValid.xsd
   */
  public void testImportUnderscoreInFilenameValid()
  {
    String testname = "ImportUnderscore_InFilenameValid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
  /**
   * Test /Paths/UnderscoreInFilename/ImportUnderscore_InFilenameInvalid.xsd
   */
  public void testImportUnderscoreInFilenameInvalid()
  {
    String testname = "ImportUnderscore_InFilenameInvalid";
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd";
    String loglocation = PLUGIN_ABSOLUTE_PATH + GENERATED_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    String idealloglocation = PLUGIN_ABSOLUTE_PATH + IDEAL_RESULTS_DIR + PATHS_DIR + "UnderscoreInFilename/" + testname + ".xsd-log";
    
    runTest(testfile, loglocation, idealloglocation);
  }
  
}
