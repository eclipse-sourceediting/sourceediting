/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver, Standards for Technology in Automotive Retail, bug 1147033
 *******************************************************************************/

package org.eclipse.wst.xsd.core.internal.validation.eclipse;

import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsd.core.internal.XSDCorePlugin;
import org.eclipse.wst.xsd.core.internal.preferences.XSDCorePreferenceNames;
import org.eclipse.wst.xsd.core.internal.validation.XSDValidationConfiguration;

public class Validator extends AbstractNestedValidator
{
  protected HashMap xsdConfigurations = new HashMap();
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#setupValidation(org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext)
   */
  protected void setupValidation(NestedValidatorContext context) 
  {
	XSDValidationConfiguration configuration = new XSDValidationConfiguration();
	boolean honourAllSchemaLocations = XMLCorePlugin.getDefault().getPluginPreferences().getBoolean(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS);
	boolean fullSchemaConformance = XSDCorePlugin.getDefault().getPluginPreferences().getBoolean(XSDCorePreferenceNames.FULL_SCHEMA_CONFORMANCE);
	try
	{
	  configuration.setFeature(XSDValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS, honourAllSchemaLocations);
	  configuration.setFeature(XSDValidationConfiguration.FULL_SCHEMA_CONFORMANCE, fullSchemaConformance);
	}
	catch(Exception e)
	{
	  // Unable to set the honour all schema locations option. Do nothing.
	}
	xsdConfigurations.put(context, configuration);
	
	super.setupValidation(context);
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#teardownValidation(org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext)
   */
  protected void teardownValidation(NestedValidatorContext context) 
  {
	xsdConfigurations.remove(context);
	
	super.teardownValidation(context);
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#validate(java.lang.String, java.io.InputStream, org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext)
   */
  public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context)
  {  
	XSDValidator validator = XSDValidator.getInstance();
	
	XSDValidationConfiguration configuration = (XSDValidationConfiguration)xsdConfigurations.get(context);

	ValidationReport valreport = null;
	
	valreport = validator.validate(uri, inputstream, configuration);
		        
	return valreport;
  }
	  
  /**
   * Store additional information in the message parameters. For XSD validation there
   * are three additional pieces of information to store:
   * param[0] = the column number of the error
   * param[1] = the 'squiggle selection strategy' for which DOM part to squiggle
   * param[2] = the name or value of what is to be squiggled
   * 
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#addInfoToMessage(org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage, org.eclipse.wst.validation.internal.provisional.core.IMessage)
   */
  protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message)
  { 
	String key = validationMessage.getKey();
	if(key != null)
	{
	  XSDMessageInfoHelper messageInfoHelper = new XSDMessageInfoHelper();
	  String[] messageInfo = messageInfoHelper.createMessageInfo(key, validationMessage.getMessage());

	  message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
	  message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, messageInfo[0]);
	  message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, messageInfo[1]);
	}
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#getValidatorID()
   */
  protected String getValidatorID()
  {
    // Because this class is used as a delegate, return the id of the validator
    // which delegates to this class.

    return XSDDelegatingValidator.class.getName();
  }
}
