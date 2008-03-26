/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
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
  public static String WARN_NO_GRAMMAR = "WARN_NO_GRAMMAR"; //$NON-NLS-1$
  public static String INDICATE_NO_GRAMMAR = "INDICATE_NO_GRAMMAR"; //$NON-NLS-1$
  private boolean warn_no_grammar_value = false;
  private int indicate_no_grammar_value = 1;
  
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
	
	throw new IllegalArgumentException("Feature not recognized."); //$NON-NLS-1$
  }

}
