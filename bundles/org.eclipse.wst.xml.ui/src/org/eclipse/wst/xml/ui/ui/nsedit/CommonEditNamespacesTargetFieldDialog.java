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
package org.eclipse.wst.xml.ui.ui.nsedit;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.ui.ui.XMLCommonResources;


/*
 * This class is an extension of CommonEditNamespacesDialog.
 * This class adds the target namespaces dialog field.
 */
public class CommonEditNamespacesTargetFieldDialog extends CommonEditNamespacesDialog {
	protected Text targetNamespaceField;
	protected String targetNamespace;

	public CommonEditNamespacesTargetFieldDialog(Composite parent, IPath resourceLocation) {
		super(parent, resourceLocation, XMLCommonResources.getInstance().getString("_UI_NAMESPACE_DECLARATIONS")); //$NON-NLS-1$

		Composite targetComp = getTopComposite();
		targetComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridData gd = new GridData(GridData.FILL_BOTH);
		Label targetNamespaceLabel = new Label(targetComp, SWT.NONE);
		targetNamespaceLabel.setLayoutData(gd);
		targetNamespaceLabel.setText(XMLCommonResources.getInstance().getString("_UI_TARGET_NAMESPACE")); //$NON-NLS-1$

		targetNamespaceField = new Text(targetComp, SWT.BORDER);
		targetNamespaceField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		targetNamespaceField.addModifyListener(new TargetNamespaceModifyListener());

		//		createControlArea();
	}

	public void setTargetNamespace(String theTargetNamespace) {
		targetNamespace = theTargetNamespace != null ? theTargetNamespace : ""; //$NON-NLS-1$
		targetNamespaceField.setText(targetNamespace);
		//updateTargetNamespaceAndNamespaceInfo(targetNamespace);
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	class TargetNamespaceModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			String oldTargetNamespace = targetNamespace;
			targetNamespace = targetNamespaceField.getText();
			updateTargetNamespaceAndNamespaceInfo(oldTargetNamespace, targetNamespace);
		}
	}

	private void updateTargetNamespaceAndNamespaceInfo(String oldTargetNamespace, String newTargetNamespace) {
		NamespaceInfo info = getNamespaceInfo(newTargetNamespace);
		if (info == null) {
			info = getNamespaceInfo(oldTargetNamespace);
			if (info == null) {
				info = new NamespaceInfo(newTargetNamespace, "tns", null); //$NON-NLS-1$
				namespaceInfoList.add(info);
			}
			else {
				info.uri = targetNamespace;
			}
		}
		tableViewer.refresh();
	}
}
