/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.actions;


import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddRedefinedComponentCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.RedefineSimpleTypeCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;


public class AddXSDRedefinedSimpleTypeAction extends AddXSDRedefinableContentAction
{

  public AddXSDRedefinedSimpleTypeAction(IWorkbenchPart part, String ID, String text)
  {
    super(part, ID, text);
    this.operationType = SIMPLE_TYPE_REDEFINE;
  }

  public AddRedefinedComponentCommand getCommand(XSDRedefine redefine, XSDRedefinableComponent redefinableComponent)
  {
    AddRedefinedComponentCommand command = new RedefineSimpleTypeCommand(
      Messages._UI_ACTION_REDEFINE_SIMPLE_TYPE,
      redefine,
      redefinableComponent);
    return command;
  }

  protected void buildComponentsList(XSDRedefine xsdRedefine, List names, IComponentList componentList)
  {
    List typeDefinitions = xsdRedefine.getIncorporatedSchema().getTypeDefinitions();
    Iterator iterator = typeDefinitions.iterator();
    while (iterator.hasNext())
    {
      XSDTypeDefinition typeDefinition = (XSDTypeDefinition)iterator.next();
      String typeDefinitionName = typeDefinition.getName();
      if (typeDefinition instanceof XSDSimpleTypeDefinition && !names.contains(typeDefinitionName))
      {
        componentList.add(typeDefinition);
      }
    }

  }

}
