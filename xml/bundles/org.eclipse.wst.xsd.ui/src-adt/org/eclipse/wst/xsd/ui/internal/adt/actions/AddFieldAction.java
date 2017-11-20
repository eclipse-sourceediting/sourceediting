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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;


public class AddFieldAction extends BaseSelectionAction
{   
  public static String ID = "AddFieldAction";  //$NON-NLS-1$
  
  public AddFieldAction(IWorkbenchPart part)
  {
    super(part);
    setId(ID);
    setText(Messages._UI_ACTION_ADD_FIELD);  
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
        Command command = type.getAddNewFieldCommand(""); //$NON-NLS-1$
        if (command != null)
        {  
          getCommandStack().execute(command);
          Adapter adapter = XSDAdapterFactory.getInstance().adapt(((BaseCommand)command).getAddedComponent());
          selectAddedComponent(adapter);
        }
        else
        {
           //TODO ... pop up a command not implemented message
        }
      }
    }  
  }
  
  protected void doEdit(Object obj, IWorkbenchPart part)
  {
    if (obj instanceof BaseFieldEditPart)
    {
      BaseFieldEditPart editPart = (BaseFieldEditPart)obj;
      editPart.doEditName(!(part instanceof ContentOutline));
    }
  }
}
