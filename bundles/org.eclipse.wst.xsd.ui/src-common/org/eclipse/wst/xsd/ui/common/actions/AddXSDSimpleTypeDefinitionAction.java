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
import org.eclipse.wst.xsd.editor.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.common.commands.AddXSDSimpleTypeDefinitionCommand;
import org.eclipse.xsd.XSDSchema;

public class AddXSDSimpleTypeDefinitionAction extends XSDBaseAction
{
  public static final String ID = "org.eclipse.wst.xsd.editor.AddXSDSimpleTypeDefinitionAction";

  public AddXSDSimpleTypeDefinitionAction(IWorkbenchPart part)
  {
    super(part);
    setText("Add Simple Type");
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
      AddXSDSimpleTypeDefinitionCommand command = new AddXSDSimpleTypeDefinitionCommand("Add Simple Type", (XSDSchema) selection);
      getCommandStack().execute(command);
    }
  }

}
