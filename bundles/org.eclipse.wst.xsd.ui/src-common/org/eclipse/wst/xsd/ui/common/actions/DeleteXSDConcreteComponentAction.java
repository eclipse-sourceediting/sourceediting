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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.common.commands.DeleteCommand;
import org.eclipse.xsd.XSDConcreteComponent;

public class DeleteXSDConcreteComponentAction extends XSDBaseAction
{
  public static final String DELETE_XSD_COMPONENT_ID = "org.eclipse.wst.xsd.ui.common.actions.DeleteXSDConcreteComponentAction";   //$NON-NLS-1$

  public DeleteXSDConcreteComponentAction(IWorkbenchPart part)
  {
    super(part);
    setText("Delete");
    setId(DELETE_XSD_COMPONENT_ID);
    setImageDescriptor(XSDEditorPlugin.getImageDescriptor("icons/delete_obj.gif") );
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }

    if (selection instanceof XSDConcreteComponent)
    {
      DeleteCommand command = new DeleteCommand(getText(), (XSDConcreteComponent) selection);
      getCommandStack().execute(command);
    }

  }
}
