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
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDAttributeGroupDefinitionCommand;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;

public class AddXSDAttributeGroupDefinitionAction extends XSDBaseAction
{
  public static String ID = "AddXSDAttributeGroupDefinitionAction";

  public AddXSDAttributeGroupDefinitionAction(IWorkbenchPart part)
  {
    super(part);
    setText("Add Attribute Group");
    setId(ID);
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }

    AddXSDAttributeGroupDefinitionCommand command = null;
    if (selection instanceof XSDComplexTypeDefinition)
    {
      command = new AddXSDAttributeGroupDefinitionCommand("Add Attribute Group Ref", (XSDComplexTypeDefinition) selection);
      getCommandStack().execute(command);
    }
    else if (selection instanceof XSDSchema)
    {
      command = new AddXSDAttributeGroupDefinitionCommand("Add Attribute Group Definition", (XSDSchema) selection);
      getCommandStack().execute(command);
    }

    if (command != null)
    {
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(command.getAddedComponent());
      if (adapter != null)
        provider.setSelection(new StructuredSelection(adapter));
    }

  }
}
