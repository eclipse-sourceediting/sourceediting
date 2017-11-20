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
                                   
public class SimpleDirectEditPolicy extends DirectEditPolicy 
{
  protected DirectEditPolicyDelegate delegate;

  public void setDelegate(DirectEditPolicyDelegate delegate)
  {                                           
    this.delegate = delegate;
  }

  protected org.eclipse.gef.commands.Command getDirectEditCommand(final DirectEditRequest request) 
  { 
  	return new Command() //AbstractCommand()
    {
      public void execute()
      {                       
        if (delegate != null)
        {
          delegate.performEdit(request.getCellEditor());
        }  
      }     
  
      public void redo()
      {
      }  
  
      public void undo()
      {
      }     
  
      public boolean canExecute()
      {
        return true;
      }
    };
  }
  
  protected void showCurrentEditValue(DirectEditRequest request) 
  {      
  	//hack to prevent async layout from placing the cell editor twice.
  	getHostFigure().getUpdateManager().performUpdate();
  }
}