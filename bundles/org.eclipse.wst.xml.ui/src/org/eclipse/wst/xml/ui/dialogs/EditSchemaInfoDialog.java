/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.ui.dialogs;

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
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.nsedit.CommonEditNamespacesDialog;

public class EditSchemaInfoDialog extends Dialog implements UpdateListener {
	//  protected NamespaceInfoTable namespaceInfoTable;
	protected Label errorMessageLabel;
	protected List namespaceInfoList;
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
		CommonEditNamespacesDialog editNamespacesControl = new CommonEditNamespacesDialog(dialogArea, resourceLocation, XMLUIMessages._UI_NAMESPACE_DECLARATIONS, false, true); //$NON-NLS-1$
		editNamespacesControl.setNamespaceInfoList(namespaceInfoList);

		editNamespacesControl.updateErrorMessage(namespaceInfoList);

		return dialogArea;
	}

	protected Control getDialogArea(Composite parent) {
		return super.createDialogArea(parent);
	}

	public List getNamespaceInfoList() {
		return namespaceInfoList;
	}

	public void setNamespaceInfoList(List list) {
		namespaceInfoList = list;
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
