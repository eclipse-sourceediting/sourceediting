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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.xml.ui.ui.XMLCommonResources;
import org.eclipse.wst.xml.ui.ui.XMLCommonUIContextIds;
import org.w3c.dom.ProcessingInstruction;



public class EditProcessingInstructionDialog extends Dialog {
	protected Text targetField;
	protected Text dataField;
	protected String target;
	protected String data;

	public EditProcessingInstructionDialog(Shell parentShell, String target, String data) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.target = target;
		this.data = data;
	}

	public EditProcessingInstructionDialog(Shell parentShell, ProcessingInstruction pi) {
		this(parentShell, pi.getTarget(), pi.getData());
	}

	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		WorkbenchHelp.setHelp(dialogArea, XMLCommonUIContextIds.XCUI_PROCESSING_DIALOG);

		Composite composite = new Composite(dialogArea, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));


		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 250;

		Label targetLabel = new Label(composite, SWT.NONE);
		targetLabel.setText(XMLCommonResources.getInstance().getString("_UI_LABEL_TARGET_COLON")); //$NON-NLS-1$

		targetField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		targetField.setLayoutData(gd);
		targetField.setText(getDisplayValue(target));

		Label dataLabel = new Label(composite, SWT.NONE);
		dataLabel.setText(XMLCommonResources.getInstance().getString("_UI_LABEL_DATA_COLON")); //$NON-NLS-1$

		dataField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		dataField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dataField.setText(getDisplayValue(data));

		return dialogArea;
	}

	protected Label createMessageArea(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		//label.setText(message);
		return label;
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
		target = getModelValue(targetField.getText());
		data = getModelValue(dataField.getText());
		super.buttonPressed(buttonId);
	}


	public String getTarget() {
		return target;
	}

	public String getData() {
		return data;
	}
}



