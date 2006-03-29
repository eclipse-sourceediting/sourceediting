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
package org.eclipse.wst.xsd.adt.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.adt.facade.IComplexType;

public class SetInputToGraphView extends BaseSelectionAction
{
  public static String ID = "SetAsFocus";
  IEditorPart editorPart;

  public SetInputToGraphView(IWorkbenchPart part)
  {
    super(part);
    setId(ID);
    setText("Set As Focus");
    if (part instanceof IEditorPart)
    {
      editorPart = (IEditorPart)part;
    }  
  }

  protected boolean calculateEnabled()
  {
    return true;
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();
    Object adapter = getWorkbenchPart().getAdapter(GraphicalViewer.class);

    if (selection instanceof IComplexType)
    {
      IComplexType type = (IComplexType) selection;

      if (adapter instanceof DesignViewGraphicalViewer)
      {
        DesignViewGraphicalViewer graphicalViewer = (DesignViewGraphicalViewer) adapter;
        RootEditPart rootEditPart = graphicalViewer.getRootEditPart();
        EditPart editPart = rootEditPart.getContents();
        if (editPart instanceof RootContentEditPart)
        {
          if (editorPart != null)
          {          
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getNavigationHistory().markLocation(editorPart);
          }  
          ((RootContentEditPart) editPart).setInput(type);
          ((RootContentEditPart) editPart).refresh();          
          if (editorPart != null)
          {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getNavigationHistory().markLocation(editorPart);
          }
        }
      }
    }
  }
}
