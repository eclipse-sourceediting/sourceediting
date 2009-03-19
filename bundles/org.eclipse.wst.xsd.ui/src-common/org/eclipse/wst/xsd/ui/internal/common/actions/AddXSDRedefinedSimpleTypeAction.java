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
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentList;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddRedefinedComponentCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.RedefineSimpleTypeCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;


public class AddXSDRedefinedSimpleTypeAction extends AddXSDRedefinableContentAction
{
  public static final String ID = "org.eclipse.wst.xsd.ui.actions.RedefineSimpleType"; //$NON-NLS-1$

  public AddXSDRedefinedSimpleTypeAction(IWorkbenchPart part)
  {
    super(part, ID, Messages._UI_ACTION_REDEFINE_SIMPLE_TYPE);
  }

  public AddRedefinedComponentCommand getCommand(XSDRedefine redefine, XSDRedefinableComponent redefinableComponent)
  {
    AddRedefinedComponentCommand command = new RedefineSimpleTypeCommand(
      Messages._UI_ACTION_REDEFINE_SIMPLE_TYPE,
      redefine,
      redefinableComponent);
    return command;
  }

  protected void buildComponentsList(XSDRedefine xsdRedefine, Set redefinedComponentsNames, IComponentList componentList)
  {
    List typeDefinitions = xsdRedefine.getIncorporatedSchema().getTypeDefinitions();
    Iterator iterator = typeDefinitions.iterator();
    while (iterator.hasNext())
    {
      XSDTypeDefinition typeDefinition = (XSDTypeDefinition)iterator.next();
      String typeDefinitionName = typeDefinition.getName();
      if (typeDefinition instanceof XSDSimpleTypeDefinition && !redefinedComponentsNames.contains(typeDefinitionName))
      {
        componentList.add(typeDefinition);
      }
    }

  }
  
  protected Image getRedefinedComponentImage()
  {
    return XSDEditorPlugin.getXSDImage(Messages._UI_IMAGE_SIMPLE_TYPE);
  }
}
