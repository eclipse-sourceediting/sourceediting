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

import org.eclipse.wst.validation.internal.provisional.ValidationFactory;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * @author Mark Hutchinson
 * 
 */
public class DelegatingSourceValidatorForXML extends DelegatingSourceValidator {
	private final static String VALIDATOR_CLASS = "org.eclipse.wst.xml.validation.internal.ui.eclipse.Validator"; //$NON-NLS-1$

	public DelegatingSourceValidatorForXML() {
		super();
	}

	protected IValidator getDelegateValidator() {
		try {
			return ValidationFactory.instance.getValidator(VALIDATOR_CLASS);
		}
		catch (Exception e) { //
		}
		return null;
	}
}
