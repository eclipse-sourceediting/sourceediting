/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *	   David Schneider, david.schneider@unisys.com - [142500] WTP properties pages fonts don't follow Eclipse preferences
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.widgets;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xml.ui.internal.dialogs.EditSchemaInfoDialog;
import org.eclipse.wst.xml.ui.internal.nsedit.CommonEditNamespacesTargetFieldDialog;

public class XSDEditSchemaInfoDialog extends EditSchemaInfoDialog implements SelectionListener {
	String targetNamespace;
	CommonEditNamespacesTargetFieldDialog editNamespacesControl;
  /**
   * @deprecated - These have been moved to the Advanced Properties Tab 
   */
  Combo elementFormCombo, attributeFormCombo;
  /**
   * @deprecated 
   */
  String elementFormQualified = "", attributeFormQualified = ""; //$NON-NLS-1$ //$NON-NLS-2$
  
	public XSDEditSchemaInfoDialog(Shell parentShell, IPath resourceLocation, String targetNamespace) {
		super(parentShell, resourceLocation);
		this.targetNamespace = targetNamespace;
	}
	
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

    applyDialogFont(parent);
		return dialogArea;
	}	
	
	public String getTargetNamespace() {
		return editNamespacesControl.getTargetNamespace();
	}
  
  /**
   * @deprecated 
   */
  public void setIsElementQualified(String state)
  {
    elementFormCombo.setText(state);
    elementFormQualified = state;
  }
  
  /**
   * @deprecated 
   */
  public void setIsAttributeQualified(String state)
  {
    attributeFormCombo.setText(state);
    attributeFormQualified = state;
  }
  
  /**
   * @deprecated 
   */
  public String getElementFormQualified()
  {
    return elementFormQualified;
  }
  
  /**
   * @deprecated 
   */
  public String getAttributeFormQualified()
  {
    return attributeFormQualified;
  }
  
  /**
   * @deprecated 
   */
  public void widgetDefaultSelected(SelectionEvent e)
  {
   
  }
  
  /**
   * @deprecated 
   */
  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == attributeFormCombo)
    {
      attributeFormQualified = attributeFormCombo.getText();
    }
    else if (e.widget == elementFormCombo)
    {
      elementFormQualified = elementFormCombo.getText();
    }

  }

}
