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
import org.eclipse.wst.xsd.ui.internal.common.commands.RedefineAttributeGroupCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;


public class AddXSDRedefinedAttributeGroupAction extends AddXSDRedefinableContentAction
{

  public AddXSDRedefinedAttributeGroupAction(IWorkbenchPart part, String ID, String text)
  {
    super(part, ID, text);
    this.operationType = ATTRIBUTE_GROUP_REDEFINE;
  }

  protected AddRedefinedComponentCommand getCommand(XSDRedefine redefine, XSDRedefinableComponent redefinableComponent)
  {
    AddRedefinedComponentCommand command = new RedefineAttributeGroupCommand(
      Messages._UI_ACTION_REDEFINE_ATTRIBUTE_GROUP,
      redefine,
      redefinableComponent);
    return command;
  }

  protected void buildComponentsList(XSDRedefine xsdRedefine, List names, IComponentList componentList)
  {
    List attributeGroups = xsdRedefine.getIncorporatedSchema().getAttributeGroupDefinitions();
    Iterator iterator = attributeGroups.iterator();
    while (iterator.hasNext())
    {
      XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition)iterator.next();
      String attributeGroupDefinitionName = attributeGroupDefinition.getName();
      if (!names.contains(attributeGroupDefinitionName))
      {
        componentList.add(attributeGroupDefinition);
      }
    }
  }
}
