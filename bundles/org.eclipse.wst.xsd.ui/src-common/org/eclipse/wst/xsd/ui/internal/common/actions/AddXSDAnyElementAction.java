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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDAnyElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;

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
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }

    XSDModelGroup modelGroup = getModelGroup(selection);

    AddXSDAnyElementCommand command = new AddXSDAnyElementCommand(getText(), modelGroup);
    if (selection instanceof XSDComplexTypeDefinition)
    {
      command.setComplexType((XSDComplexTypeDefinition)selection);
    }
    command.setDoCreateModelGroupForComplexType(modelGroup == null);
    getCommandStack().execute(command);
    addedComponent = command.getAddedComponent();
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
    selectAddedComponent(adapter);
  }

  private XSDModelGroup getModelGroup(Object selection)
  {
    if (selection instanceof XSDModelGroup)
    {
      return (XSDModelGroup) selection;
    }
    else if (selection instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeContent content = ((XSDComplexTypeDefinition)selection).getContent();
      if (content instanceof XSDParticle)
      {
        XSDParticleContent particleContent = ((XSDParticle)content).getContent();
        if (particleContent instanceof XSDModelGroup)
        {
          return (XSDModelGroup)particleContent;
        }
      }
    }
    return null;
  }
}
