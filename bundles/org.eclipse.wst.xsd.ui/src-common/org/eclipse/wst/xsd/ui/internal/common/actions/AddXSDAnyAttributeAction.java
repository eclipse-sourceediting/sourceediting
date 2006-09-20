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
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDAnyAttributeCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;

public class AddXSDAnyAttributeAction extends XSDBaseAction
{
  public static String ID = "org.eclipse.wst.xsd.ui.AddXSDAnyAttributeAction"; //$NON-NLS-1$
  protected XSDComplexTypeDefinition xsdComplexTypeDefinition;
  
  public AddXSDAnyAttributeAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_ADD_ANY_ATTRIBUTE);
    setId(ID);
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }
    AddXSDAnyAttributeCommand command = null;
    if (selection instanceof XSDComplexTypeDefinition)
    {
      command = new AddXSDAnyAttributeCommand(Messages._UI_ACTION_ADD_ANY_ATTRIBUTE, (XSDComplexTypeDefinition) selection);
      getCommandStack().execute(command);
    }
    else if (selection instanceof XSDAttributeGroupDefinition)
    {
      command = new AddXSDAnyAttributeCommand(Messages._UI_ACTION_ADD_ANY_ATTRIBUTE, (XSDAttributeGroupDefinition)selection);
      getCommandStack().execute(command);
    }
    
    if (command != null)
    {
      addedComponent = command.getAddedComponent();
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
      selectAddedComponent(adapter);
    }
  }


  protected boolean calculateEnabled()
  {
    boolean rc = super.calculateEnabled();
    if (rc)
    {
      Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

      if (selection instanceof XSDBaseAdapter)
      {
        selection = ((XSDBaseAdapter) selection).getTarget();
      }
      if (selection instanceof XSDComplexTypeDefinition)
      {
        return ((XSDComplexTypeDefinition)selection).getAttributeWildcardContent() == null;
      }
      else if (selection instanceof XSDAttributeGroupDefinition)
      {
        return ((XSDAttributeGroupDefinition)selection).getAttributeWildcardContent() == null;
      }
      
    }
    return rc;
  }



}
