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
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;


public class AddFieldAction extends BaseSelectionAction
{   
  public static String ID = "AddFieldAction"; 
  
  public AddFieldAction(IWorkbenchPart part)
  {
    super(part);
    setId(ID);
    setText("Add Field");  
  }
  
  public void run()
  {
    if (getSelectedObjects().size() > 0)
    {
      Object o = getSelectedObjects().get(0);
      IComplexType type = null;
      
      if (o instanceof IComplexType)
      {  
        type = (IComplexType)o; 
      }
      else if (o instanceof IField)
      {
        IField field = (IField)o;
        type = field.getContainerType();
      }
      if (type != null)
      {
        Command command = ((IComplexType)type).getAddNewFieldCommand("");
        if (command != null)
        {  
          getCommandStack().execute(command);
          Adapter adapter = XSDAdapterFactory.getInstance().adapt(((BaseCommand)command).getAddedComponent());
          if (adapter != null)
            provider.setSelection(new StructuredSelection(adapter));
        }
        else
        {
           //TODO ... pop up a command not implemented message
        }
      }
    }  
  }
}
