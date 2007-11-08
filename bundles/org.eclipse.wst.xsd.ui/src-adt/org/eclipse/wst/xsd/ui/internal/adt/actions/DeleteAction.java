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
import org.eclipse.wst.xsd.ui.internal.adt.design.DesignViewGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
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
      boolean doReselect = false;
      IModel model = null;
      if (selection instanceof IComplexType)
      {
        command = ((IComplexType)selection).getDeleteCommand();
        model = ((IComplexType)selection).getModel();
        doReselect = !((IComplexType)selection).isAnonymous();
      }
      else if (selection instanceof IField)
      {
        model = ((IField)selection).getModel();
        if ( ((IField)selection).isGlobal())
        {
          doReselect = true;
        }
        command = ((IField)selection).getDeleteCommand();
      }  
      else if (selection instanceof IStructure)
      {
        // Fallback for model groups and attribute groups.
        IStructure structure = (IStructure)selection; 
        model = structure.getModel();
        command = structure.getDeleteCommand();
        doReselect = true;
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
                  doReselect = false;
                }
              }
            }
          }
        }
        command.execute();
        if (model != null && doReselect)
          provider.setSelection(new StructuredSelection(model));
      }  
    }
    
  }
}
