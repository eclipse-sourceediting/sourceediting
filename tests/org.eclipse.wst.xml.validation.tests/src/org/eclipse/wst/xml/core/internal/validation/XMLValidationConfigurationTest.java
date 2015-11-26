/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test the XMLValidationConfiguration class.
 */
public class XMLValidationConfigurationTest extends TestCase
{
  XMLValidationConfiguration configuration;
	
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XMLValidationConfigurationTest.class);
  }
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception 
  {
	super.setUp();
	configuration = new XMLValidationConfiguration();
  }


  protected void tearDown() throws Exception 
  {
	configuration = null;
	super.tearDown();
  }


  /**
   * Test the default setting of the WARN_NO_GRAMMAR feature.
   */
  public void testSetWarnNoGrammarFeatureDefault()
  {
	try
	{
		assertFalse("The WARN_NO_GRAMMAR feature is not set by default to false.", configuration.getFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR));
	}
	catch(Exception e)
	{
	  fail("Unable to set read the WARN_NO_GRAMMAR feature: " + e);
	}
  }
  
  /**
   * Test setting the WARN_NO_GRAMMAR feature to true.
   */
  public void testSetWarnNoGrammarFeatureTrue()
  {
	try
	{
	  configuration.setFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR, true);
	  assertTrue("The WARN_NO_GRAMMAR feature is not set to true.", configuration.getFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR));
	}
	catch(Exception e)
	{
	  fail("Unable to set WARN_NO_GRAMMAR to true: " + e);
	}
  }
  
  /**
   * Test setting the WARN_NO_GRAMMAR feature to false.
   */
  public void testSetWarnNoGrammarFeatureFalse()
  {
	try
	{
	  configuration.setFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR, false);
	  assertFalse("The WARN_NO_GRAMMAR feature is not set to false.", configuration.getFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR));
	}
	catch(Exception e)
	{
	  fail("Unable to set WARN_NO_GRAMMAR to false: " + e);
	}
  }
  
  /**
   * Test setting a feature that doesn't exist.
   */
  public void testSetNotExistantFeature()
  {
	try
	{
	  configuration.setFeature("NON_EXISTANT_FEATURE", false);
	  fail("Setting a non existant feature did not produce an exception.");
	}
	catch(Exception e)
	{
	  // The test succeeds if the exception is caught.
	}
  }
  
  /**
   * Test getting a feature that doesn't exist.
   */
  public void testGetNotExistantFeature()
  {
	try
	{
	  configuration.getFeature("NON_EXISTANT_FEATURE");
	  fail("Getting a non existant feature did not produce an exception.");
	}
	catch(Exception e)
	{
	  // The test succeeds if the exception is caught.
	}
  }
  
  /**
   * Test the default setting of the WARN_NO_GRAMMAR feature.
   */
  public void testIndicateNoGrammarFeatureDefault()
  {
	try
	{
		assertEquals("The INDICATE_NO_GRAMMAR feature is not set by default to Ignore.", 1, configuration.getIntFeature(XMLValidationConfiguration.INDICATE_NO_GRAMMAR));
	}
	catch(Exception e)
	{
	  fail("Unable to set read the WARN_NO_GRAMMAR feature: " + e);
	}
  }
}
