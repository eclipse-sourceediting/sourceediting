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

package org.eclipse.wst.xml.ui.internal.validation;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.provisional.ValidationFactory;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.ui.internal.Logger;

/**
 * @author Mark Hutchinson
 * 
 */
public class DelegatingSourceValidatorForXML extends DelegatingSourceValidator {
	private final static String VALIDATOR_CLASS = "org.eclipse.wst.xml.core.internal.validation.eclipse.Validator"; //$NON-NLS-1$

	public DelegatingSourceValidatorForXML() {
		super();
	}

	protected IValidator getDelegateValidator() {
		IValidator result = null;
		try {
			result = ValidationFactory.instance.getValidator(VALIDATOR_CLASS);
		}
		catch (InstantiationException e) {
			Logger.logException(e);
		}
		return result;
	}

	protected boolean isDelegateValidatorEnabled(IFile file) {
		boolean enabled = true;
		try {
			ProjectConfiguration configuration = ConfigurationManager.getManager().getProjectConfiguration(file.getProject());
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(VALIDATOR_CLASS);
			if (configuration.isBuildEnabled(vmd) || configuration.isManualEnabled(vmd)) {
				enabled = true;
			}
			else {
				enabled = false;
			}
		}
		catch (InvocationTargetException e) {
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}
		return enabled;
	}
}
