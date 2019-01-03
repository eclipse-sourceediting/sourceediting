/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
