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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDModelGroupCommand;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;

public class AddXSDModelGroupAction extends XSDBaseAction
{
  public static String SEQUENCE_ID = "AddXSDSequenceModelGroupAction"; //$NON-NLS-1$
  public static String CHOICE_ID = "AddXSDChoiceModelGroupAction"; //$NON-NLS-1$
  public static String ALL_ID = "AddXSDAllModelGroupAction"; //$NON-NLS-1$
  XSDCompositor xsdCompositor;

  public AddXSDModelGroupAction(IWorkbenchPart part, XSDCompositor compositor, String ID)
  {
    super(part);
    setText(getLabel(compositor));
    setId(ID);
    this.xsdCompositor = compositor;
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();
    if (selection instanceof XSDBaseAdapter)
    {
      XSDConcreteComponent xsdComponent = (XSDConcreteComponent) ((XSDBaseAdapter) selection).getTarget();
      AddXSDModelGroupCommand command = null;
      if (xsdComponent instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) xsdComponent;

        command = new AddXSDModelGroupCommand(getLabel(xsdCompositor), xsdElementDeclaration, xsdCompositor);
        getCommandStack().execute(command);
      }
      else if (xsdComponent instanceof XSDModelGroup)
      {
        XSDModelGroup xsdModelGroup = (XSDModelGroup) xsdComponent;

        command = new AddXSDModelGroupCommand(getLabel(xsdCompositor), xsdModelGroup, xsdCompositor);
        getCommandStack().execute(command);
      }
      else if (xsdComponent instanceof XSDComplexTypeDefinition
               || xsdComponent instanceof XSDModelGroupDefinition)
      {
        command = new AddXSDModelGroupCommand(getLabel(xsdCompositor), xsdComponent, xsdCompositor);
        getCommandStack().execute(command);
      }
     
      if (command != null)
      {
        Adapter adapter = XSDAdapterFactory.getInstance().adapt(command.getAddedComponent());
        if (adapter != null)
          provider.setSelection(new StructuredSelection(adapter));
      }

    }
  }

  private String getLabel(XSDCompositor compositor)
  {
    String result = XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SEQUENCE"); //$NON-NLS-1$
    if (compositor != null)
    {
      if (compositor == XSDCompositor.CHOICE_LITERAL)
      {
        result = XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CHOICE"); //$NON-NLS-1$
      }
      else if (compositor == XSDCompositor.ALL_LITERAL)
      {
        result = XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ALL");//$NON-NLS-1$
      }
    }
    return result;
  }
}
