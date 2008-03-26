/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation.eclipse;

import java.io.InputStream;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationReport;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;

public class Validator extends AbstractNestedValidator
{
  protected int indicateNoGrammar = 0;
  
  /**
   * Set any preferences for XML validation.
   * 
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#setupValidation(org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext)
   */
  protected void setupValidation(NestedValidatorContext context) 
  {
	super.setupValidation(context);
	indicateNoGrammar = XMLCorePlugin.getDefault().getPluginPreferences().getInt(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR);
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#validate(java.lang.String, java.io.InputStream, org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext)
   */
  public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context)
  {  
	XMLValidator validator = XMLValidator.getInstance();

	XMLValidationConfiguration configuration = new XMLValidationConfiguration();
	try
	{
	  configuration.setFeature(XMLValidationConfiguration.INDICATE_NO_GRAMMAR, indicateNoGrammar);
	}
	catch(Exception e)
	{
	  // TODO: Unable to set the preference. Log this problem.
	}
	
	XMLValidationReport valreport = null;
	if (inputstream != null)
	{
	  valreport = validator.validate(uri, inputstream, configuration);
	}
	else
	{
	  valreport = validator.validate(uri, null, configuration);
	}
		        
	return valreport;
  }
	  
  /**
   * Store additional information in the message parameters. For XML validation there
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
      XMLMessageInfoHelper messageInfoHelper = new XMLMessageInfoHelper();
      String[] messageInfo = messageInfoHelper.createMessageInfo(key, validationMessage.getMessageArguments());
      
      message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
      message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, messageInfo[0]);
      message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, messageInfo[1]);
	}
  }
}
