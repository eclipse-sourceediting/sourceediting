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

package org.eclipse.wst.dtd.ui.internal.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.dtd.core.internal.validation.DTDValidationMessages;
import org.eclipse.wst.dtd.core.internal.validation.Validator;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.ui.internal.validation.core.ValidateAction;


/**
 * This class managers the 'UI' related details of validation Here's a quick
 * overview of the details : - manages Marker creation based on the results of
 * the validation - (optionally) displays dialog to summarize the results of
 * validation - temporarily copies XML to a temp file to workaround a xerces
 * bug (InputSource from InputStream bug)
 */
public class ValidateDTDAction extends ValidateAction {
	/**
	 * Constructor.
	 * 
	 * @param f
	 *            The file to validate.
	 * @param doShowDialog
	 *            Whether or not to show dialogs during validation.
	 */
	public ValidateDTDAction(IFile f, boolean doShowDialog) {
		super(f, doShowDialog);
	}

	protected void validate(final IFile fileToValidate) {
		final ValidationOutcome validationOutcome = new ValidationOutcome();
		IPath path = fileToValidate.getLocation();
		final String uri = createURIForFilePath(path.toString());
		Validator dtdValidator = Validator.getInstance();

		clearMarkers(fileToValidate);

		ValidationReport valreport = dtdValidator.validate(uri);
		validationOutcome.isValid = valreport.isValid();
		if (valreport.getValidationMessages().length == 0) {
			validationOutcome.hasMessages = false;
		}
		else {
			validationOutcome.hasMessages = true;
		}
		createMarkers(fileToValidate, valreport.getValidationMessages());

		try {
			fileToValidate.setSessionProperty(ValidationMessage.ERROR_MESSAGE_MAP_QUALIFIED_NAME, valreport.getNestedMessages());
		}
		catch (CoreException e) {
		}


		if (showDialog) {
			if (!validationOutcome.isValid) {
				String title = DTDValidationMessages._UI_DIALOG_DTD_INVALID_TITLE;
				String message = DTDValidationMessages._UI_DIALOG_DTD_INVALID_TEXT;
				openErrorDialog(title, message);
			}
			else {
				String title = DTDValidationMessages._UI_DIALOG_DTD_VALID_TITLE;
				String message = DTDValidationMessages._UI_DIALOG_DTD_VALID_TEXT;
				openValidDialog(title, message);
			}
		}
	}
}
