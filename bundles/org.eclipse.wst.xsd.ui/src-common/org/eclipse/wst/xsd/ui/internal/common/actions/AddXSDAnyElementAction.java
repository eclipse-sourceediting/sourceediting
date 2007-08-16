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
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDAnyElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDModelGroup;

public class AddXSDAnyElementAction extends XSDBaseAction
{
  public static String ID = "org.eclipse.wst.xsd.ui.AddXSDAnyElementAction"; //$NON-NLS-1$

  public AddXSDAnyElementAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_ADD_ANY_ELEMENT);
    setId(ID);
  }

  public void run()
  {
    XSDModelGroup modelGroup = getModelGroup();
    if (modelGroup != null)
    {
      AddXSDAnyElementCommand command = new AddXSDAnyElementCommand(getText(), modelGroup);
      getCommandStack().execute(command);
    }
  }

  private XSDModelGroup getModelGroup()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }
    if (selection instanceof XSDModelGroup)
    {
      return (XSDModelGroup) selection;
    }
    return null;
  }

  protected boolean calculateEnabled()
  {
    return super.calculateEnabled();
  }

}
