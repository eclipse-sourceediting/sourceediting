/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.SetTypeCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDConcreteComponent;

public class SetTypeAction extends XSDBaseAction
{
  public static final String SET_NEW_TYPE_ID = "SetTypeAction_AddType"; //$NON-NLS-1$
  public static final String SELECT_EXISTING_TYPE_ID = "SetTypeAction_ExistingType"; //$NON-NLS-1$

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
        command = new SetTypeCommand(Messages._UI_ACTION_SET_TYPE, getId(), (XSDConcreteComponent) target);
        command.setAdapter((XSDBaseAdapter) selection);
        getCommandStack().execute(command);
      }
    }
  }
}
