/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator;

/**
 * This performs the as-you-type validation for schema files
 *
 */
public class DelegatingSourceValidatorForXSD extends DelegatingSourceValidator {                                                
  private final static String Id = "org.eclipse.wst.xsd.core.xsd";

  private Validator _validator;
  
  public DelegatingSourceValidatorForXSD() { 
  }
  
  private Validator getValidator(){
	  if (_validator == null)_validator = ValidationFramework.getDefault().getValidator(Id);
	  return _validator;
  }
  
  protected IValidator getDelegateValidator() {
	  Validator v = getValidator();
	  if (v == null)return null;
	  return v.asIValidator();
  }
  
	protected boolean isDelegateValidatorEnabled(IFile file) {
		Validator v = getValidator();
		if (v == null)return false;
		if (!v.shouldValidate(file, false, false))return false;
		return v.isBuildValidation() || v.isManualValidation();
	}
}
