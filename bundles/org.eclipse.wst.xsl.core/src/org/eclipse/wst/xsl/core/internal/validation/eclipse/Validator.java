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
package org.eclipse.wst.xsl.core.internal.validation.eclipse;

import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;

public class Validator extends AbstractNestedValidator {
	// protected HashMap xsdConfigurations = new HashMap();

	// protected void setupValidation(NestedValidatorContext context) {
	// XSLValidationConfiguration configuration = new
	// XSLValidationConfiguration();
	// xsdConfigurations.put(context, configuration);
	//
	// super.setupValidation(context);
	// }

	// protected void teardownValidation(NestedValidatorContext context) {
	// xsdConfigurations.remove(context);
	//
	// super.teardownValidation(context);
	// }

	public Validator() {
		System.out.println("Validator ctor");
	}

	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context) {
		XSLValidator validator = XSLValidator.getInstance();

		
//		IStructuredModel model = null;
//		IModelManager manager = StructuredModelManager.getModelManager();
//
//		try {
//			model = manager.getModelForRead(file);
//			// TODO.. HTML validator tries again to get a model a 2nd way
//		}
//		catch (Exception e) {
//			// e.printStackTrace();
//		}
//
//		return model instanceof IDOMModel ? (IDOMModel) model : null;

		
		
		// XSLValidationConfiguration configuration =
		// (XSLValidationConfiguration) xsdConfigurations
		// .get(context);

		ValidationReport valreport = null;

		// valreport = validator.validate(uri, inputstream, configuration);

		return valreport;
	}

	@Override
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return super.validate(resource, kind, state, monitor);
	}

	// protected void addInfoToMessage(ValidationMessage validationMessage,
	// IMessage message) {
	// String key = validationMessage.getKey();
	// if (key != null) {
	// XSLMessageInfoHelper messageInfoHelper = new XSLMessageInfoHelper();
	// String[] messageInfo = messageInfoHelper.createMessageInfo(key,
	// validationMessage.getMessage());
	//
	// message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(
	// validationMessage.getColumnNumber()));
	// message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE,
	// messageInfo[0]);
	// message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE,
	// messageInfo[1]);
	// }
	// }
}
