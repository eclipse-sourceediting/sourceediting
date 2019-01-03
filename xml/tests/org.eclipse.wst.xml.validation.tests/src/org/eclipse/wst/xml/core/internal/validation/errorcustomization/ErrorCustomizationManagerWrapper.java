/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
