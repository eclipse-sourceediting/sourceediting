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

	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		editNamespacesControl = new CommonEditNamespacesTargetFieldDialog(dialogArea, resourceLocation);
		editNamespacesControl.setNamespaceInfoList(namespaceInfoList);
		if (targetNamespace != null)
			editNamespacesControl.setTargetNamespace(targetNamespace);
		editNamespacesControl.updateErrorMessage(namespaceInfoList);

		return dialogArea;
	}
	
	public String getTargetNamespace() {
		return editNamespacesControl.getTargetNamespace();
	}
}
