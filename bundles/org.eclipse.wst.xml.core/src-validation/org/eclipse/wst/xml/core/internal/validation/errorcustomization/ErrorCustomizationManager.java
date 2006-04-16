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
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

import java.util.Stack;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;

/**
 * This class tracks the context of the parsed document to help identify error
 * conditions
 */
public class ErrorCustomizationManager
{
  protected Stack elementInformationStack = new Stack();
  protected ErrorMessageInformation messageForConsideration;

  /**
   * This method should be called in the start element method of the XML validator's
   * content handler.
   * 
   * @param uri
   * 		The namespace of the element.
   * @param localName
   * 		The local name of the element.
   */
  public void startElement(String uri, String localName)
  { 
    ElementInformation elementInformation = new ElementInformation(uri, localName);
    if (elementInformationStack.size() > 0)
    {
      ElementInformation parent = (ElementInformation) elementInformationStack.peek();
      parent.children.add(elementInformation);
    }
    elementInformationStack.push(elementInformation);
  }

  /**
   * This method should be called in the end element method of the XML validator's
   * content handler.
   * 
   * @param uri
   * 		The namespace of the element.
   * @param localName
   * 		The local name of the element.
   */
  public void endElement(String uri, String localName)
  {   
    if (elementInformationStack.size() > 0)
    {
      ElementInformation elementInformation = (ElementInformation)elementInformationStack.pop();
      if (messageForConsideration != null)
      {  
        IErrorMessageCustomizer[] customizers = ErrorCustomizationRegistry.getInstance().getCustomizers(elementInformation.getNamespace());
        int numCustomizers = customizers.length;
        for(int i = 0; i < numCustomizers; i++)
        {
          String message = customizers[i].customizeMessage(elementInformation, messageForConsideration.key, messageForConsideration.arguments);
          if(message != null)
          {
        	messageForConsideration.message.setMessage(message);
        	break;
          }
        }
      }  
    }
  }

  /**
   * Consider the reported error for customization.
   * 
   * @param valInfo
   * 		The current ValidationInfo object containing validation specific information.
   * @param key
   * 		The key related to the message.
   * @param arguments
   * 		Any message arguments.
   */
  public void considerReportedError(ValidationInfo valInfo, String key, Object[] arguments)
  {
    messageForConsideration = null;
    ValidationMessage[] messages = valInfo.getValidationMessages();
    if (messages.length > 0)
    {  
      messageForConsideration = new ErrorMessageInformation();
      messageForConsideration.key = key;
      messageForConsideration.arguments = arguments;
      messageForConsideration.message = messages[messages.length - 1]; 
    }
  }
  
  /**
   * A simple class to hold error message information.
   */
  public class ErrorMessageInformation
  {
	public String key = null;
	public Object[] arguments = null;
	public ValidationMessage message = null;
  }
}
