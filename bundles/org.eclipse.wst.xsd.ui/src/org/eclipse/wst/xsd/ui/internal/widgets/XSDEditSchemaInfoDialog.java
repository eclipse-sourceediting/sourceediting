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
package org.eclipse.wst.xsd.ui.internal.widgets;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xml.ui.dialogs.EditSchemaInfoDialog;
import org.eclipse.wst.xml.ui.nsedit.CommonEditNamespacesTargetFieldDialog;

public class XSDEditSchemaInfoDialog extends EditSchemaInfoDialog {
	String targetNamespace;
	CommonEditNamespacesTargetFieldDialog editNamespacesControl;
	
	public XSDEditSchemaInfoDialog(Shell parentShell, IPath resourceLocation, String targetNamespace) {
		super(parentShell, resourceLocation);
		this.targetNamespace = targetNamespace;
	}
/*
	// in super
	protected CommonEditNamespacesDialog createCommonEditNamespacesDialog(Composite dialogArea)
	{
	  return new CommonEditNamespacesDialog(dialogArea, resourceLocation, XMLUIPlugin.getResourceString("%_UI_NAMESPACE_DECLARATIONS"), false, true); //$NON-NLS-1$				
	}
	
	// in super
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		CommonEditNamespacesDialog editNamespacesControl = createCommonEditNamespacesDialog(dialogArea); 
		editNamespacesControl.setNamespaceInfoList(namespaceInfoList);
		editNamespacesControl.updateErrorMessage(namespaceInfoList);
		return dialogArea;
	}
	
	// in this
	protected CommonEditNamespacesDialog createCommonEditNamespacesDialog(Composite dialogArea)
	{
	  return new CommonEditNamespacesTargetFieldDialog(dialogArea, resourceLocation); //$NON-NLS-1$				
	}	*/
	
	// this is copy of ....
    protected Control __internalCreateDialogArea(Composite parent) {
        // create a composite with standard margins and spacing
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(composite);
        return composite;
    }	
	
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) __internalCreateDialogArea(parent);
		editNamespacesControl = new CommonEditNamespacesTargetFieldDialog(dialogArea, resourceLocation); //$NON-NLS-1$
		if (targetNamespace != null)
		{	
			editNamespacesControl.setTargetNamespace(targetNamespace);
		}
		editNamespacesControl.setNamespaceInfoList(namespaceInfoList);
		editNamespacesControl.updateErrorMessage(namespaceInfoList);
		return dialogArea;
	}	
	
	public String getTargetNamespace() {
		return editNamespacesControl.getTargetNamespace();
	}
}
