/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDComplexTypeBaseTypeEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class SetBaseTypeAction extends XSDBaseAction
{
  public static String ID = "org.eclipse.wst.xsd.ui.internal.common.actions.setBaseType"; //$NON-NLS-1$

  public SetBaseTypeAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_SET_BASE_TYPE + "..."); //$NON-NLS-1$
    setId(ID);
  }
  
  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
      
      boolean complexType = selection instanceof XSDComplexTypeDefinition;
      boolean simpleType = selection instanceof XSDSimpleTypeDefinition;
      
      if (complexType || simpleType)
      {
        
        if (getWorkbenchPart() instanceof IEditorPart)
        {
          IEditorPart editor = (IEditorPart)getWorkbenchPart();

          ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDComplexTypeBaseTypeEditManager.class);
          ComponentSpecification newValue;
          IComponentDialog dialog = null;
          dialog = manager.getBrowseDialog();
          if (dialog != null)
          {
        	if(simpleType)
        	{
        		((XSDSearchListDialogDelegate) dialog).showComplexTypes(false);
        	}
            if (dialog.createAndOpen() == Window.OK)
            {
              newValue = dialog.getSelectedComponent();
              manager.modifyComponentReference(selection, newValue);
            }
          }
        }
      }
    }
  }

}
