/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple input dialog for soliciting an input string
 * from the user.
 * <p>
 * This concete dialog class can be instantiated as is, 
 * or further subclassed as required.
 * 
 * NOTE: This class was mainly copied from org.eclipse.jface.dialogs.InputDialog
 * except the error message label was turned into a read-only text widget to
 * make the dialog accessible.  (https://bugs.eclipse.org/bugs/show_bug.cgi?id=61069)
 * </p>
 */
public class AccessibleInputDialog extends Dialog {

	
	/**
	 * The title of the dialog.
	 */
	private String title;
	 
	/**
	 * The message to display, or <code>null</code> if none.
	 */
	private String message;

	/**
	 * The input value; the empty string by default.
	 */
	private String value= "";//$NON-NLS-1$

	/**
	 * The input validator, or <code>null</code> if none.
	 */
	private IInputValidator validator;

	/**
	 * Ok button widget.
	 */
	private Button okButton;

	/**
	 * Input text widget.
	 */
	private Text text;

	/**
	 * Error message read-only text widget.
	 */
	private Text errorMessageLabel;
/**
 * Creates an input dialog with OK and Cancel buttons.
 * Note that the dialog will have no visual representation (no widgets)
 * until it is told to open.
 * <p>
 * Note that the <code>open</code> method blocks for input dialogs.
 * </p>
 *
 * @param parentShell the parent shell
 * @param dialogTitle the dialog title, or <code>null</code> if none
 * @param dialogMessage the dialog message, or <code>null</code> if none
 * @param initialValue the initial input value, or <code>null</code> if none
 *  (equivalent to the empty string)
 * @param validator an input validator, or <code>null</code> if none
 */
public AccessibleInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue, IInputValidator validator) {
	super(parentShell);
	this.title = dialogTitle;
	message = dialogMessage;
	if (initialValue == null)
		value = "";//$NON-NLS-1$
	else
		value = initialValue;
	this.validator = validator;
}
/* (non-Javadoc)
 * Method declared on Dialog.
 */
protected void buttonPressed(int buttonId) {
	if (buttonId == IDialogConstants.OK_ID) {
		value= text.getText();
	} else {
		value= null;
	}
	super.buttonPressed(buttonId);
}
/* (non-Javadoc)
 * Method declared in Window.
 */
protected void configureShell(Shell shell) {
	super.configureShell(shell);
	if (title != null)
		shell.setText(title);
}
/* (non-Javadoc)
 * Method declared on Dialog.
 */
protected void createButtonsForButtonBar(Composite parent) {
	// create OK and Cancel buttons by default
	okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

	//do this here because setting the text will set enablement on the ok button
	text.setFocus();
	if (value != null) {
		text.setText(value);
		text.selectAll();
	}
}
/* (non-Javadoc)
 * Method declared on Dialog.
 */
protected Control createDialogArea(Composite parent) {
	// create composite
	Composite composite = (Composite)super.createDialogArea(parent);

	// create message
	if (message != null) {
		Label label = new Label(composite, SWT.WRAP);
		label.setText(message);
		GridData data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_FILL |
			GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());
	}

	text= new Text(composite, SWT.SINGLE | SWT.BORDER);
	text.setLayoutData(new GridData(
		GridData.GRAB_HORIZONTAL |
		GridData.HORIZONTAL_ALIGN_FILL));
	text.addModifyListener(
		new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		}
	);
	text.setFont(parent.getFont());

	errorMessageLabel = new Text(composite, SWT.READ_ONLY);
	errorMessageLabel.setLayoutData(new GridData(
		GridData.GRAB_HORIZONTAL |
		GridData.HORIZONTAL_ALIGN_FILL));
	errorMessageLabel.setFont(parent.getFont());
	
	return composite;
}
/**
 * Returns the error message text widget.
 *
 * @return the error message text widget
 */
protected Text getErrorMessageLabel() {
	return errorMessageLabel;
}
/**
 * Returns the ok button.
 *
 * @return the ok button
 */
protected Button getOkButton() {
	return okButton;
}
/**
 * Returns the text area.
 *
 * @return the text area
 */
protected Text getText() {
	return text;
}
/**
 * Returns the validator.
 *
 * @return the validator
 */
protected IInputValidator getValidator() {
	return validator;
}
/**
 * Returns the string typed into this input dialog.
 *
 * @return the input string
 */
public String getValue() {
	return value;
}
/**
 * Validates the input.
 * <p>
 * The default implementation of this framework method
 * delegates the request to the supplied input validator object;
 * if it finds the input invalid, the error message is displayed
 * in the dialog's message line.
 * This hook method is called whenever the text changes in the
 * input field.
 * </p>
 */
protected void validateInput() {

	String errorMessage = null;

	if (validator != null) {
		errorMessage = validator.isValid(text.getText());
	}

	// Bug 16256: important not to treat "" (blank error) the same as null (no error)
	errorMessageLabel.setText(errorMessage == null ? "" : errorMessage); //$NON-NLS-1$
	okButton.setEnabled(errorMessage == null);

	errorMessageLabel.getParent().update();
}
}
