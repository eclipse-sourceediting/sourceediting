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
package org.eclipse.wst.xml.ui.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.xml.ui.nls.ResourceHandler;
import org.eclipse.wst.xml.ui.ui.XMLCommonResources;
import org.eclipse.wst.xml.ui.ui.XMLCommonUIContextIds;
import org.w3c.dom.Element;



public class EditElementDialog extends Dialog implements ModifyListener {
	protected Element element;
	protected Text elementNameField;
	protected Button okButton;
	protected String elementName;
	protected Label errorMessageLabel;

	public EditElementDialog(Shell parentShell, Element element) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.element = element;
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		elementNameField.forceFocus();
		elementNameField.selectAll();
		updateErrorMessage();
		return control;
	}


	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}


	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		WorkbenchHelp.setHelp(dialogArea, XMLCommonUIContextIds.XCUI_ELEMENT_DIALOG);

		Composite composite = new Composite(dialogArea, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label elementNameLabel = new Label(composite, SWT.NONE);
		elementNameLabel.setText(XMLCommonResources.getInstance().getString("_UI_LABEL_ELEMENT_NAME")); //$NON-NLS-1$

		elementNameField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 200;
		elementNameField.setLayoutData(gd);
		elementNameField.setText(getDisplayValue(element != null ? element.getNodeName() : "")); //$NON-NLS-1$
		elementNameField.addModifyListener(this);

		// error message
		errorMessageLabel = new Label(composite, SWT.NONE);
		errorMessageLabel.setText(ResourceHandler.getString("error_message_goes_here")); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		errorMessageLabel.setLayoutData(gd);
		Color color = new Color(errorMessageLabel.getDisplay(), 200, 0, 0);
		errorMessageLabel.setForeground(color);

		return dialogArea;
	}

	public void modifyText(ModifyEvent e) {
		updateErrorMessage();
	}

	protected void updateErrorMessage() {
		String errorMessage = null;
		String name = elementNameField.getText().trim();
		if (name.length() > 0) {
			// TODO use checkName from model level                              
			// errorMessage = ValidateHelper.checkXMLName(name);
		}
		else {
			errorMessage = ""; //$NON-NLS-1$
		}

		errorMessageLabel.setText(errorMessage != null ? errorMessage : ""); //$NON-NLS-1$
		okButton.setEnabled(errorMessage == null);
	}

	protected String getDisplayValue(String string) {
		return string != null ? string : ""; //$NON-NLS-1$
	}

	protected String getModelValue(String string) {
		String result = null;
		if (string != null && string.trim().length() > 0) {
			result = string;
		}
		return result;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			elementName = getModelValue(elementNameField.getText());
		}
		super.buttonPressed(buttonId);
	}

	public String getElementName() {
		return elementName;
	}
}



