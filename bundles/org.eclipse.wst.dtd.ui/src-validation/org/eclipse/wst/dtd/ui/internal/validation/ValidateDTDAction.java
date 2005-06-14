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
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.dtd.core.internal.validation.DTDValidationMessages;
import org.eclipse.wst.dtd.core.internal.validation.Validator;
import org.eclipse.wst.xml.validation.internal.core.ValidateAction;
import org.eclipse.wst.xml.validation.internal.core.ValidationMessage;
import org.eclipse.wst.xml.validation.internal.core.ValidationReport;


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
	 * @param file
	 *            The file to validate.
	 * @param showDialog
	 *            Whether or not to show dialogs during validation.
	 */
	public ValidateDTDAction(IFile file, boolean showDialog) {
		super(file, showDialog);
	}

	protected void validate(final IFile file) {
		final ValidationOutcome validationOutcome = new ValidationOutcome();
		IPath path = file.getLocation();
		final String uri = createURIForFilePath(path.toString());
		IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor progressMonitor) throws CoreException {
				Validator dtdValidator = Validator.getInstance();

				clearMarkers(file);

				ValidationReport valreport = dtdValidator.validate(uri);
				validationOutcome.isValid = valreport.isValid();
				if (valreport.getValidationMessages().length == 0) {
					validationOutcome.hasMessages = false;
				}
				else {
					validationOutcome.hasMessages = true;
				}
				createMarkers(file, valreport.getValidationMessages());

				file.setSessionProperty(ValidationMessage.ERROR_MESSAGE_MAP_QUALIFIED_NAME, valreport.getNestedMessages());

			}
		};


		try {
			ResourcesPlugin.getWorkspace().run(op, null);

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
		catch (CoreException e) {
		}
	}
}
