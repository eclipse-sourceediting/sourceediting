/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDElementCommand;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDModelGroupImpl;

//revisit this and see if we can reuse AddFieldAction??

public class AddXSDElementAction extends XSDBaseAction
{
  public static String ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction"; //$NON-NLS-1$
  public static String BEFORE_SELECTED_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction.BEFORE_SELECTED_ID"; //$NON-NLS-1$
  public static String AFTER_SELECTED_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction.AFTER_SELECTED_ID"; //$NON-NLS-1$
  public static String REF_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementReferenceAction"; //$NON-NLS-1$
  boolean isReference;
  
  public AddXSDElementAction(IWorkbenchPart part, String id, String label, boolean isReference)
  {
    super(part);
    setText(label);
    setId(id);
    this.isReference = isReference;
    doDirectEdit = !isReference;
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }
    AddXSDElementCommand command = null;
    if (selection instanceof XSDComplexTypeDefinition)
    {
      command = new AddXSDElementCommand(getText(), (XSDComplexTypeDefinition) selection);
      command.setReference(isReference);
      getCommandStack().execute(command);
    }
    else if (selection instanceof XSDModelGroupDefinition)
    {
      command = new AddXSDElementCommand(getText(), (XSDModelGroupDefinition) selection);
      command.setReference(isReference);
      getCommandStack().execute(command);
    }
    else if (selection instanceof XSDSchema)
    {
      command = new AddXSDElementCommand(getText(), (XSDSchema) selection);
      getCommandStack().execute(command);
    }
    else if (selection instanceof XSDModelGroup)
    {
      XSDModelGroup modelGroup = (XSDModelGroup) selection;
      XSDConcreteComponent component = modelGroup.getContainer();
      XSDComplexTypeDefinition ct = null;
      while (component != null)
      {
        if (component instanceof XSDComplexTypeDefinition)
        {
          ct = (XSDComplexTypeDefinition) component;
          break;
        }
        component = component.getContainer();
      }

      if (ct != null)
      {
        command = new AddXSDElementCommand(getText(), (XSDModelGroup) selection, ct);
      }
      else
      {
        command = new AddXSDElementCommand(getText(), (XSDModelGroup) selection);
      }
      command.setReference(isReference);
      getCommandStack().execute(command);
    }
    else if (selection instanceof XSDElementDeclaration || selection instanceof XSDAttributeUse)
    {
      XSDConcreteComponent xsdConcreteComponent = (XSDConcreteComponent) selection;
      XSDConcreteComponent parent = null;
      XSDComplexTypeDefinition ct = null;
      XSDModelGroupDefinition group = null;
      XSDModelGroupImpl ctGroup = null;

      for (parent = xsdConcreteComponent.getContainer(); parent != null; )
      {
        if (parent instanceof XSDComplexTypeDefinition)
        {
          ct = (XSDComplexTypeDefinition)parent;
          break;
        }
        else if (parent instanceof XSDModelGroupDefinition)
        {
          group = (XSDModelGroupDefinition)parent;
          break;
        }
        else if (parent instanceof XSDModelGroupImpl)
        {
          ctGroup = (XSDModelGroupImpl) parent;
          break;
        }
        parent = parent.getContainer();
      }
      if (ct != null)
      {
        command = new AddXSDElementCommand(getText(), ct);
        command.setReference(isReference);
        getCommandStack().execute(command);
      }
      else if (ctGroup != null)
      {
        XSDElementDeclaration sel = (XSDElementDeclaration) selection;
        int index = ctGroup.getContents().indexOf(sel.eContainer());
        command = new AddXSDElementCommand(getText(), ctGroup, getId(), index);
        command.setReference(isReference);
        getCommandStack().execute(command);
      }
      else if (group != null)
      {
        command = new AddXSDElementCommand(getText(), group);
        command.setReference(isReference);
        getCommandStack().execute(command);
      }
    }
    
    if (command != null)
    {
      addedComponent = command.getAddedComponent();
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
      selectAddedComponent(adapter);
    }
  }

}
