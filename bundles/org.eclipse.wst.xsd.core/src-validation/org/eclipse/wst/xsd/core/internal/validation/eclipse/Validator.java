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

package org.eclipse.wst.xsd.core.internal.validation.eclipse;

import java.io.InputStream;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsd.core.internal.validation.XSDValidationMessages;

public class Validator extends AbstractNestedValidator
{
	 
  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#getValidatorName()
   */
  protected String getValidatorName() 
  {
	return XSDValidationMessages.Message_XSD_validation_message_ui;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator#validate(java.lang.String, java.io.InputStream, org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext)
   */
  public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context)
  {  
	XSDValidator validator = XSDValidator.getInstance();

	ValidationReport valreport = null;
	if (inputstream != null)
	{
	  valreport = validator.validate(uri, inputstream);
	}
	else
	{
	  valreport = validator.validate(uri);
	}
		        
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
}
