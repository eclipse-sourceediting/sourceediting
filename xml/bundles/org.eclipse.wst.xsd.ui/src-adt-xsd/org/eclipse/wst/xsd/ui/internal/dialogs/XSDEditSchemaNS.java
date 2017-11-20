/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.dialogs;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.dialogs.EditSchemaInfoDialog;
import org.eclipse.wst.xml.ui.internal.nsedit.CommonEditNamespacesDialog;

public class XSDEditSchemaNS extends EditSchemaInfoDialog
{
  CommonEditNamespacesDialog editNamespacesControl;

  public XSDEditSchemaNS(Shell parentShell, IPath resourceLocation)
  {
    super(parentShell, resourceLocation);
  }

  protected Control createDialogArea(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    editNamespacesControl = new CommonEditNamespacesDialog(composite, resourceLocation, XMLUIMessages._UI_NAMESPACE_DECLARATIONS);

    editNamespacesControl.setNamespaceInfoList(namespaceInfoList);
    editNamespacesControl.updateErrorMessage(namespaceInfoList);

    return composite;
  }
}
