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
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDComplexTypeDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDSchema;

public class AddXSDComplexTypeDefinitionAction extends XSDBaseAction
{
  public static final String ID = "org.eclipse.wst.xsd.ui.internal.editor.AddXSDComplexTypeDefinitionAction"; //$NON-NLS-1$

  public AddXSDComplexTypeDefinitionAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_ADD_COMPLEX_TYPE);
    setId(ID);
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }

    if (selection instanceof XSDSchema)
    {
      AddXSDComplexTypeDefinitionCommand command = new AddXSDComplexTypeDefinitionCommand(Messages._UI_ACTION_ADD_COMPLEX_TYPE, (XSDSchema) selection);
      getCommandStack().execute(command);
      
      addedComponent = command.getAddedComponent();
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
      selectAddedComponent(adapter);
    }
  }
}
