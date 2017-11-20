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
package org.eclipse.wst.xsd.core.internal.validation;

import junit.framework.TestCase;

/**
 * Test the XSDValidationConfiguration class.
 */
public class XSDValidationConfigurationTest extends TestCase
{
  XSDValidationConfiguration configuration;
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception 
  {
	super.setUp();
	configuration = new XSDValidationConfiguration();
  }


  protected void tearDown() throws Exception 
  {
	configuration = null;
	super.tearDown();
  }


  /**
   * Test the default setting of the HONOUR_ALL_SCHEMA_LOCATIONS feature.
   */
  public void testSetHonourAllSchemaLocationsFeatureDefault()
  {
	try
	{
		assertFalse("The HONOUR_ALL_SCHEMA_LOCATIONS feature is not set by default to false.", configuration.getFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS));
	}
	catch(Exception e)
	{
	  fail("Unable to set read the HONOUR_ALL_SCHEMA_LOCATIONS feature: " + e);
	}
  }
  
  /**
   * Test setting the HONOUR_ALL_SCHEMA_LOCATIONS feature to true.
   */
  public void testSetHonourAllSchemaLocationsFeatureTrue()
  {
	try
	{
	  configuration.setFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, true);
	  assertTrue("The HONOUR_ALL_SCHEMA_LOCATIONS feature is not set to true.", configuration.getFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS));
	}
	catch(Exception e)
	{
	  fail("Unable to set HONOUR_ALL_SCHEMA_LOCATIONS to true: " + e);
	}
  }
  
  /**
   * Test setting the HONOUR_ALL_SCHEMA_LOCATIONS feature to false.
   */
  public void testSetHonourAllSchemaLocationsFeatureFalse()
  {
	try
	{
	  configuration.setFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, false);
	  assertFalse("The HONOUR_ALL_SCHEMA_LOCATIONS feature is not set to false.", configuration.getFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS));
	}
	catch(Exception e)
	{
	  fail("Unable to set HONOUR_ALL_SCHEMA_LOCATIONS to false: " + e);
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
  
}
