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

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xml.ui.ui.nsedit.CommonEditNamespacesTargetFieldDialog;


public class EditSchemaInfoDialog extends Dialog implements UpdateListener {
	protected List namespaceInfoList;
	//  protected NamespaceInfoTable namespaceInfoTable;
	protected Label errorMessageLabel;
	protected IPath resourceLocation;

	public EditSchemaInfoDialog(Shell parentShell, IPath resourceLocation) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.resourceLocation = resourceLocation;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		CommonEditNamespacesTargetFieldDialog editNamespacesControl = new CommonEditNamespacesTargetFieldDialog(dialogArea, resourceLocation);
		editNamespacesControl.setNamespaceInfoList(namespaceInfoList);

		editNamespacesControl.updateErrorMessage(namespaceInfoList);

		return dialogArea;
	}

	public void setNamespaceInfoList(List list) {
		namespaceInfoList = list;
	}

	public List getNamespaceInfoList() {
		return namespaceInfoList;
	}

	public void updateErrorMessage(List namespaceInfoList) {
		NamespaceInfoErrorHelper helper = new NamespaceInfoErrorHelper();
		String errorMessage = helper.computeErrorMessage(namespaceInfoList, null);
		errorMessageLabel.setText(errorMessage != null ? errorMessage : ""); //$NON-NLS-1$
	}

	public void updateOccured(Object object, Object arg) {
		updateErrorMessage((List) arg);
	}
}
