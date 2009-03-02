/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import java.util.Iterator;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class DeleteAction extends BaseSelectionAction
{
  public final static String ID = "org.eclipse.wst.xsd.ui.internal.editor.DeleteAction";  //$NON-NLS-1$
  public DeleteAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_DELETE);
    setId(ID);
    setImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/delete_obj.gif") ); //$NON-NLS-1$
  }
  
  public void run()
  {
    for (Iterator i = ((IStructuredSelection) getSelection()).iterator(); i.hasNext(); )
    {
      Object selection = i.next();
      Command command = null;
      boolean doSetInput = false;
      boolean doSetModelAsInput = false;
      IADTObject topLevelContainer = null;
      IModel model = null;
      
      if (selection instanceof IGraphElement)
      {
        IGraphElement xsdObj = (IGraphElement)selection;
 
        if (xsdObj instanceof XSDBaseAdapter)
        {
          XSDBaseAdapter baseAdapter = (XSDBaseAdapter)xsdObj;
          
          // Do not delete selected item if it is read-only, or if the item selected
          // is null and the read only check cannot be completed
          if (baseAdapter == null || baseAdapter.isReadOnly())
          {
            continue;
          }
          
          topLevelContainer = xsdObj.getTopContainer();
          if (topLevelContainer == selection)
          {
            doSetInput = true;
            doSetModelAsInput = true;
          }
          command = xsdObj.getDeleteCommand();
          model = xsdObj.getModel();         
        }
      }
      
      if (command != null)
      {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench != null)
        {
          IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
          if (workbenchWindow != null && workbenchWindow.getActivePage() != null)
          {
            IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
            if (editorPart != null)
            {
              Object viewer = editorPart.getAdapter(GraphicalViewer.class);
              if (viewer instanceof DesignViewGraphicalViewer)
              { 
                Object input = ((DesignViewGraphicalViewer)viewer).getInput();
                if (input != selection)
                {
                  // Bug 86218 : Don't switch to top level view if the object we're deleting
                  // is not the input to the viewer
                  doSetInput = false;
                }
              }
            }
          }
        }
        command.execute();
        if (doSetInput)
        {
          if (model != null && doSetModelAsInput)
            provider.setSelection(new StructuredSelection(model));   
          else if (topLevelContainer != null && !doSetModelAsInput)
            provider.setSelection(new StructuredSelection(topLevelContainer));
        }
      }  
    }
    
  }
}
