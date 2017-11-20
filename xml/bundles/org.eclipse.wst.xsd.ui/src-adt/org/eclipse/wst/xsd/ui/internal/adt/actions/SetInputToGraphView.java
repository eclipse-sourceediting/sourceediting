/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;

public class SetInputToGraphView extends BaseSelectionAction
{
  public static String ID = "SetAsFocus"; //$NON-NLS-1$
  protected IEditorPart editorPart;
  protected Object input;

  public SetInputToGraphView(IWorkbenchPart part)
  {
    this(part, null);
  }
  
  public SetInputToGraphView(IWorkbenchPart part, Object input)
  {
    super(part);
    this.input = input;
    setId(ID);
    setText(Messages._UI_ACTION_SET_AS_FOCUS);
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
    Object selection = input;   
    if (selection == null)
    {  
      selection = ((IStructuredSelection) getSelection()).getFirstElement();
    }  
    Object adapter = getWorkbenchPart().getAdapter(GraphicalViewer.class);

    if (selection instanceof IADTObject)
    {
      IADTObject obj = (IADTObject) selection;
      if (adapter instanceof DesignViewGraphicalViewer)
      {
        DesignViewGraphicalViewer graphicalViewer = (DesignViewGraphicalViewer) adapter;  
        EditPart editPart = graphicalViewer.getInputEditPart();
        if (editPart instanceof RootContentEditPart)
        {
          graphicalViewer.setInputAndMarkLocation(obj);
        }
      }
    }
  }
}
