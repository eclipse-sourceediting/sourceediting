/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.reconcile;

import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;

/**
 * @author Mark Hutchinson
 *
 */
public class DelegatingReconcileValidatorForXML extends DelegatingReconcileValidator
{
 private final static String VALIDATOR_CLASS = "org.eclipse.wst.xml.validation.internal.ui.eclipse.Validator";
 
  public DelegatingReconcileValidatorForXML()
  { super();
  }
 
  protected IValidator getDelegateValidator()
  {
    try
    {
      //Get the validator:
      ValidatorMetaData validatorData = ValidationRegistryReader.getReader().getValidatorMetaData(VALIDATOR_CLASS);
      return validatorData.getValidator();
    }
    catch (Exception e)
    { //
    }
    return null;
  }
}
