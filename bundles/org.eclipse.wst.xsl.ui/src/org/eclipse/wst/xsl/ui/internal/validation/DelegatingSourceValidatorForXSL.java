/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.validation;

import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator;

/**
 * This performs the as-you-type validation for xsl files
 * 
 */
public class DelegatingSourceValidatorForXSL extends DelegatingSourceValidator {
	private final static String Id = "org.eclipse.wst.xsl.core.xsl"; //$NON-NLS-1$

	private Validator _validator;

	/**
	 * Constructor
	 */
	public DelegatingSourceValidatorForXSL() {
	}

	private Validator getValidator() {
		if (_validator == null)
			_validator = ValidationFramework.getDefault().getValidator(Id);
		return _validator;
	}

	protected IValidator getDelegateValidator() {
		Validator v = getValidator();
		if (v == null)
			return null;
		return v.asIValidator();
	}
	
	@Override
	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		// TODO Auto-generated method stub
		super.validate(helper, reporter);
	}
}
