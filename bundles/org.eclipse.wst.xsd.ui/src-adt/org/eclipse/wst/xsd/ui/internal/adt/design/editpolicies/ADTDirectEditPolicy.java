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
package org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.wst.xsd.ui.internal.adt.design.directedit.ComboBoxCellEditorManager;

public class ADTDirectEditPolicy extends DirectEditPolicy
{
  protected ComboBoxCellEditorManager delegate;
  protected IADTUpdateCommand command;

  public ADTDirectEditPolicy()
  {
    super();
  }

  
  public void setUpdateCommand(IADTUpdateCommand command)
  {
    this.command = command;
  }
  
  protected void showCurrentEditValue(DirectEditRequest request) 
  {      
    getHostFigure().getUpdateManager().performUpdate();
  }

  protected Command getDirectEditCommand(DirectEditRequest request)
  {
    command.setRequest(request);
    return (Command)command; 
  }


}
