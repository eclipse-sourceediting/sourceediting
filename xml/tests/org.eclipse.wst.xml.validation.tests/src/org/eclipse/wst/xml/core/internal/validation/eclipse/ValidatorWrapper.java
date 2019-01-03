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
package org.eclipse.wst.xml.core.internal.validation.eclipse;

import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;

/**
 * This class extends validator to expose the protected methods
 * for testing.
 */
public class ValidatorWrapper extends Validator 
{
  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.eclipse.Validator#addInfoToMessage(org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage, org.eclipse.wst.validation.internal.provisional.core.IMessage)
   */
  protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message) 
  {
	super.addInfoToMessage(validationMessage, message);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.xml.core.internal.validation.eclipse.Validator#setupValidation(org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext)
   */
  public void setupValidation(NestedValidatorContext context) 
  {
	super.setupValidation(context);
  }

/**
   * Export the warn no grammar preference for testing.
   * 
   * @return The warn no grammar preference.
   */
  public int getIndicateNoGrammarPreference()
  {
	return indicateNoGrammar;
  }
}
