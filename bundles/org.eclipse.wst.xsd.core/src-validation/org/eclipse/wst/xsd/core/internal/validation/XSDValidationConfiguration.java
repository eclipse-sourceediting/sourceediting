/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver, Standards for Technology in Automotive Retail, bug 1147033
 *******************************************************************************/
package org.eclipse.wst.xsd.core.internal.validation;

/**
 * An XSD validation configuration allows setting specific configuration
 * information for a WTP XSD validation run. Any features and properties
 * set on this configuration should not be confused with those from
 * parsers such as Xerces. (This object does not by default wrap features
 * and properties from specific parsers.)
 */
public class XSDValidationConfiguration 
{
  public static String HONOUR_ALL_SCHEMA_LOCATIONS = "HONOUR_ALL_SCHEMA_LOCATIONS"; //$NON-NLS-1$
  public static String FULL_SCHEMA_CONFORMANCE = "FULL_SCHEMA_CONFORMANCE"; //$NON-NLS-1$
  private boolean honour_all_schema_locations = false;
  private boolean fullSchemaConformance = true;
  
  /**
   * Set a feature of this configuration.
   * 
   * @param feature
   * 		The feature to set.
   * @param value
   * 		The value to set for the feature.
   * @throws 
   * 		An exception is thrown if the feature is not recognized.
   */
  public void setFeature(String feature, boolean value) throws Exception
  {
	if(HONOUR_ALL_SCHEMA_LOCATIONS.equals(feature))
	  honour_all_schema_locations = value;
	else if (FULL_SCHEMA_CONFORMANCE.equals(feature))
		fullSchemaConformance = value;
	else
	  throw new Exception("Feature not recognized."); //$NON-NLS-1$
	
  }
  
  
  /**
   * Get the value for a given feature. If the feature is not defined
   * this method will throw an exception.
   * 
   * @param feature
   * 		The feature for which to retrieve the value.
   * @return
   * 		The feature's value, true or false.
   * @throws 
   * 		An exception is thrown if the feature is not recognized.
   */
  public boolean getFeature(String feature) throws Exception
  {
	if(HONOUR_ALL_SCHEMA_LOCATIONS.equals(feature))
	  return honour_all_schema_locations;
	else if (FULL_SCHEMA_CONFORMANCE.equals(feature))
      return fullSchemaConformance;
	
	throw new Exception("Feature not recognized."); //$NON-NLS-1$
  }

}
