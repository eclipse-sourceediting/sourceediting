/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver (STAR) - bug 297005 - Some static constants not made final.
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation;

/**
 * An XML validation configuration allows setting specific configuration
 * information for a WTP XML validation run. Any features and properties
 * set on this configuration should not be confused with those from
 * parsers such as Xerces. (This object does not by default wrap features
 * and properties from specific parsers.)
 */
public class XMLValidationConfiguration 
{
  /**
   * @deprecated
   */
  public static final String WARN_NO_GRAMMAR = "WARN_NO_GRAMMAR"; //$NON-NLS-1$
  public static final String INDICATE_NO_GRAMMAR = "INDICATE_NO_GRAMMAR"; //$NON-NLS-1$
  public static final String INDICATE_NO_DOCUMENT_ELEMENT = "INDICATE_NO_DOCUMENT_ELEMENT"; //$NON-NLS-1$
  public static final String USE_XINCLUDE = "USE_XINCLUDE"; //$NON-NLS-1$
  public static final String HONOUR_ALL_SCHEMA_LOCATIONS = "HONOUR_ALL_SCHEMA_LOCATIONS"; //$NON-NLS-1$

  private boolean warn_no_grammar_value = false;
  private int indicate_no_grammar_value = 1;
  private boolean use_xinclude = false;
  private boolean honour_all_schema_locations_value = false;
  private int indicate_no_document_value = 0;
  
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
	if(WARN_NO_GRAMMAR.equals(feature))
	  warn_no_grammar_value = value;
    else if(USE_XINCLUDE.equals(feature))
      use_xinclude = value;
    else if(HONOUR_ALL_SCHEMA_LOCATIONS.equals(feature))
      honour_all_schema_locations_value = value;
	else
	  throw new Exception("Feature not recognized."); //$NON-NLS-1$
	
  }
  
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
  public void setFeature(String feature, int value) throws Exception
  {
	if(INDICATE_NO_GRAMMAR.equals(feature))
	  indicate_no_grammar_value = value;
	else if (INDICATE_NO_DOCUMENT_ELEMENT.equals(feature))
	  indicate_no_document_value = value;
	else
	  throw new IllegalArgumentException("Feature not recognized."); //$NON-NLS-1$
	
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
	if(WARN_NO_GRAMMAR.equals(feature))
	  return warn_no_grammar_value;
	else if(USE_XINCLUDE.equals(feature))
      return use_xinclude;
    if(HONOUR_ALL_SCHEMA_LOCATIONS.equals(feature))
      return honour_all_schema_locations_value;
			
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
  public int getIntFeature(String feature) throws Exception
  {
	if(INDICATE_NO_GRAMMAR.equals(feature))
	  return indicate_no_grammar_value;
	else if (INDICATE_NO_DOCUMENT_ELEMENT.equals(feature))
      return indicate_no_document_value;

	throw new IllegalArgumentException("Feature not recognized."); //$NON-NLS-1$
  }

}
