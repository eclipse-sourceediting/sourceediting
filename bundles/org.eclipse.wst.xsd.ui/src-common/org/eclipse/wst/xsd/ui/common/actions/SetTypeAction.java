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
import org.eclipse.wst.xsd.ui.common.commands.SetTypeCommand;
import org.eclipse.xsd.XSDConcreteComponent;

public class SetTypeAction extends XSDBaseAction
{
  public static final String SET_NEW_TYPE_ID = "SetTypeAction_AddType";
  public static final String SELECT_EXISTING_TYPE_ID = "SetTypeAction_ExistingType";

  SetTypeCommand command;

  public SetTypeAction(String label, String ID, IWorkbenchPart part)
  {
    super(part);
    setText(label);
    setId(ID);
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      Object target = ((XSDBaseAdapter) selection).getTarget();

      if (target instanceof XSDConcreteComponent)
      {
        command = new SetTypeCommand(getText(), getId(), (XSDConcreteComponent) target);
        command.setAdapter((XSDBaseAdapter) selection);
        getCommandStack().execute(command);
      }
    }
  }
}
