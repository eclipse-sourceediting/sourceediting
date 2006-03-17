/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.common.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDBaseAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class XSDBaseAction extends BaseSelectionAction
{

  public XSDBaseAction(IWorkbenchPart part)
  {
    super(part);
  }

  protected boolean calculateEnabled()
  {
    if (getWorkbenchPart() instanceof IEditorPart)
    {
      IEditorPart owningEditor = (IEditorPart)getWorkbenchPart();
      
      Object selection = ((IStructuredSelection) getSelection()).getFirstElement();
      if (selection instanceof XSDBaseAdapter)
      {
        selection = ((XSDBaseAdapter) selection).getTarget();
      }
      XSDSchema xsdSchema = null;
      if (selection instanceof XSDConcreteComponent)
      {
        xsdSchema = ((XSDConcreteComponent)selection).getSchema();
      }
      
      if (xsdSchema != null && xsdSchema == owningEditor.getAdapter(XSDSchema.class))
      {
        return true;
      }
    }
    return false;
  }

}
