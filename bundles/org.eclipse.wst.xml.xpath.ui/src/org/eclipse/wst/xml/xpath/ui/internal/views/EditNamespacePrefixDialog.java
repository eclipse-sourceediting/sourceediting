/*******************************************************************************
 * Copyright (c) 2001, 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors (adapted from EditSchemaInfoDialog):
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Moller - Extracted for use in XPath Navigator view
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.ui.internal.dialogs.NamespaceInfoErrorHelper;
import org.eclipse.wst.xml.ui.internal.dialogs.UpdateListener;
import org.eclipse.wst.xml.ui.internal.nsedit.CommonEditNamespacesDialog;
import org.eclipse.wst.xml.xpath.ui.internal.Messages;

public class EditNamespacePrefixDialog extends Dialog implements UpdateListener {
	// protected NamespaceInfoTable namespaceInfoTable;
	protected Label errorMessageLabel;
	protected List<NamespaceInfo> namespaceInfoList;
	protected IPath resourceLocation;

	public EditNamespacePrefixDialog(Shell parentShell, IPath resourceLocation) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.resourceLocation = resourceLocation;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(dialogArea,
		// XMLCommonUIContextIds.XCUI_SCHEMA_INFO_DIALOG);

		CommonEditNamespacesDialog editNamespacesControl = new CommonEditNamespacesDialog(
				dialogArea, resourceLocation,
				Messages.XPathNavigator_Namespace_Prefixes, false, true);
		editNamespacesControl.setNamespaceInfoList(namespaceInfoList);

		editNamespacesControl.updateErrorMessage(namespaceInfoList);

		getShell().setText(Messages.XPathNavigator_Namespace_Prefixes);
		return dialogArea;
	}

	protected Control getDialogArea(Composite parent) {
		return super.createDialogArea(parent);
	}

	public List<NamespaceInfo> getNamespaceInfoList() {
		return namespaceInfoList;
	}

	public void setNamespaceInfoList(List<NamespaceInfo> list) {
		namespaceInfoList = list;
	}

	public void updateErrorMessage(List<NamespaceInfo> namespaceInfoList) {
		NamespaceInfoErrorHelper helper = new NamespaceInfoErrorHelper();
		String errorMessage = helper.computeErrorMessage(namespaceInfoList,
				null);
		errorMessageLabel.setText(errorMessage != null ? errorMessage : ""); //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	public void updateOccured(Object object, Object arg) {
		updateErrorMessage((List<NamespaceInfo>) arg);
	}
}
