/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.delete;

import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalGroupCleanup extends BaseGlobalCleanup
{
  /**
   * Constructor for GlobalGroupCleanup.
   * @param deletedItem
   */
  public GlobalGroupCleanup(XSDConcreteComponent deletedItem)
  {
    super(deletedItem);
  }

  protected String replacementName = null;
  
  protected String getReplacementElementName()
  {
    if (replacementName == null)
    {
      TypesHelper helper = new TypesHelper(schema);
      
      List elements = helper.getModelGroups();
      
      String deletedName = getDeletedQName();
      for (Iterator i = elements.iterator(); i.hasNext();)
      {
        String name = (String) i.next();
        if (!name.equals(deletedName))
        {
          replacementName = name;
          break;
        }
      }
    }
    return replacementName;
  }

  /**
   * @see org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor#visitModelGroupDefinition(XSDModelGroupDefinition)
   */
  public void visitModelGroupDefinition(XSDModelGroupDefinition modelGroup)
  {
    super.visitModelGroupDefinition(modelGroup);
    if (modelGroup.isModelGroupDefinitionReference())
    {
      if (deletedItem.equals(modelGroup.getResolvedModelGroupDefinition()))
      {
        if (getReplacementElementName() != null)
        {
          // KCPort String msg = XSDPlugin.getSchemaString("_INFO_RESET_GROUP_REFERENCE") + " <" + getReplacementElementName() + ">";
					String msg = "_INFO_RESET_GROUP_REFERENCE" + " <" + getReplacementElementName() + ">";
          addMessage(msg, modelGroup);
          modelGroup.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getReplacementElementName());
        }
        else
        {
          String name = getNamedComponentName(modelGroup);
          // KCPort String msg = XSDPlugin.getSchemaString("_INFO_REMOVE_GROUP_REFERENCE") + " <" + name + ">";
					String msg = "_INFO_REMOVE_GROUP_REFERENCE" + " <" + name + ">";
          addMessage(msg, modelGroup.getContainer());
          childrenToRemove.add(modelGroup.getElement());
        }
      }
    }      
  }
}
