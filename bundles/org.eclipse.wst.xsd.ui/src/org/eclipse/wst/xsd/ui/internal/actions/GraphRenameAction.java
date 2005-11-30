/*******************************************************************************
* Copyright (c) 2004, 2005 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     IBM Corporation - Initial API and implementation
*******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.commands.RenameCommand;
import org.eclipse.xsd.XSDNamedComponent;


public class GraphRenameAction extends Action
{
  protected RenameCommand command;
  
  public GraphRenameAction(XSDNamedComponent namedComponent, GraphicalEditPart editPart)
  {
    command = new RenameCommand(namedComponent, editPart);
    setText(XSDEditorPlugin.getXSDString("_UI_LABEL_RENAME"));
  }

  public void run()
  {
    command.run();
  }
}
