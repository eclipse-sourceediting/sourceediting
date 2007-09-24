/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @deprecated.  Extenders should specialize...ie. XSDTabbedPropertySheetPage
 */
public class ADTTabbedPropertySheetPage extends TabbedPropertySheetPage
{
  public ADTTabbedPropertySheetPage(ITabbedPropertySheetPageContributor tabbedPropertySheetPageContributor)
  {
    super(tabbedPropertySheetPageContributor);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged(IWorkbenchPart part, ISelection selection) {

//      Object selected = ((StructuredSelection)selection).getFirstElement();
//      if (selected instanceof EditPart)
//      {
//        Object model = ((EditPart)selected).getModel();
//        selection = new StructuredSelection(model);
//      }
      super.selectionChanged(part, selection);
  }

}
