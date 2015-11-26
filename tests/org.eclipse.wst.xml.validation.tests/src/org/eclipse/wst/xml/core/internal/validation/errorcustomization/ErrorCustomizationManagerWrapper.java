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

/**
 * A wrapper for class ErrorCustomizationManager to facilitate testing.
 */
public class ErrorCustomizationManagerWrapper extends ErrorCustomizationManager 
{
  /**
   * Get the element information stack.
   * 
   * @return
   * 		The element information stack.
   */
  public Stack getElementInformationStack()
  {
	return elementInformationStack;
  }
  
  /**
   * Get the current message for consideration.
   * 
   * @return
   * 		The current message for consideration.
   */
  public ErrorMessageInformation getMessageForConsideration()
  {
	return messageForConsideration;
  }
  
  /**
   * Set the current message for consideration.
   * 
   * @param message
   * 		The current message for consideration.
   */
  public void setMessageForConsideration(ErrorMessageInformation message)
  {
	messageForConsideration = message;
  }
}
