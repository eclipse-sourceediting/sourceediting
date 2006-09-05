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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDAttributeGroupDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;

public class AddXSDAttributeGroupDefinitionAction extends XSDBaseAction
{
  public static String ID = "AddXSDAttributeGroupDefinitionAction"; //$NON-NLS-1$
  public static String REF_ID = "AddXSDAttributeGroupDefinitionRefAction"; //$NON-NLS-1$

  public AddXSDAttributeGroupDefinitionAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_ADD_ATTRIBUTE_GROUP);
    setId(ID);
  }
  
  public AddXSDAttributeGroupDefinitionAction(IWorkbenchPart part, String id)
  {
    super(part);
    if (id.equals(REF_ID))
    {
      setText(Messages._UI_ACTION_ADD_ATTRIBUTE_GROUP_REF);
    }
    else
    {
      setText(Messages._UI_ACTION_ADD_ATTRIBUTE_GROUP_DEFINITION);
    }   
    setId(id);
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
      command = new AddXSDAttributeGroupDefinitionCommand(Messages._UI_ACTION_ADD_ATTRIBUTE_GROUP_REF, (XSDComplexTypeDefinition) selection);
      getCommandStack().execute(command);
    }
    else if (selection instanceof XSDSchema)
    {
      command = new AddXSDAttributeGroupDefinitionCommand(Messages._UI_ACTION_ADD_ATTRIBUTE_GROUP_DEFINITION, (XSDSchema) selection);
      getCommandStack().execute(command);
    }

    if (command != null)
    {
      addedComponent = command.getAddedComponent();
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
      selectAddedComponent(adapter);
    }

  }
}
