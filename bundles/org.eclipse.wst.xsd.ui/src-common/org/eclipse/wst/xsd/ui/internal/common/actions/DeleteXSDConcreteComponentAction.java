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
package org.eclipse.wst.xsd.ui.internal.common.actions;

import java.util.Iterator;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

/**
 * @deprecated Use org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction
 *
 */
public class DeleteXSDConcreteComponentAction extends XSDBaseAction
{
  public static final String DELETE_XSD_COMPONENT_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.DeleteXSDConcreteComponentAction";   //$NON-NLS-1$

  public DeleteXSDConcreteComponentAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_DELETE);
    setId(DELETE_XSD_COMPONENT_ID);
    setImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/delete_obj.gif") ); //$NON-NLS-1$
  }

  public void run()
  {
    for (Iterator i = ((IStructuredSelection) getSelection()).iterator(); i.hasNext();)
    {
      Object selection = i.next();
      boolean doReselect = false;
      
      if (selection instanceof XSDBaseAdapter)
      {
        selection = ((XSDBaseAdapter) selection).getTarget();
      }

      if (selection instanceof XSDConcreteComponent)
      {
        XSDConcreteComponent xsdComponent = (XSDConcreteComponent) selection;
        XSDSchema model = xsdComponent.getSchema();
        
        doReselect = xsdComponent.eContainer() instanceof XSDSchema;
        
        DeleteCommand command = new DeleteCommand(getText(), xsdComponent);
        getCommandStack().execute(command);
        
        if (model != null && doReselect)
        {
          Adapter adapter = XSDAdapterFactory.getInstance().adapt(model);
          if (adapter != null)
            provider.setSelection(new StructuredSelection(adapter));
        }
       }
    }
  }
}
