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

import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalAttributeGroupCleanup extends BaseGlobalCleanup
{
  /**
   * Constructor for GlobalAttributeGroupCleanup.
   * @param deletedItem
   */
  public GlobalAttributeGroupCleanup(XSDConcreteComponent deletedItem)
  {
    super(deletedItem);
  }
  
  
  protected String replacementName = null;
  
  protected String getReplacementElementName()
  {
    if (replacementName == null)
    {
      TypesHelper helper = new TypesHelper(schema);
      
      List elements = helper.getGlobalAttributeGroups();
      
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
   * @see org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor#visitComplexTypeDefinition(XSDComplexTypeDefinition)
   */
  public void visitComplexTypeDefinition(XSDComplexTypeDefinition type)
  {
    super.visitComplexTypeDefinition(type);
    if (type.getAttributeContents() != null)
    {
      for (Iterator iter = type.getAttributeContents().iterator(); iter.hasNext(); )
      {
        XSDAttributeGroupContent attrGroupContent = (XSDAttributeGroupContent) iter.next();

        if (attrGroupContent instanceof XSDAttributeGroupDefinition)
        {
          XSDAttributeGroupDefinition attrGroupDef = (XSDAttributeGroupDefinition) attrGroupContent;
          
          if (deletedItem.equals(attrGroupDef.getResolvedAttributeGroupDefinition()))
          {
            if (getReplacementElementName() != null)
            {
			  String msg = XSDEditorPlugin.getXSDString("_INFO_RESET_ATTRIBUTE_GROUP_REFERENCE") + " <" + getReplacementElementName() + ">";
              addMessage(msg, attrGroupDef);
              attrGroupDef.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getReplacementElementName());
            }
            else
            {
              // remove the attribute group reference
              String name = getNamedComponentName(type);
			  String msg = XSDEditorPlugin.getXSDString("_INFO_REMOVE_ATTRIBUTE_GROUP_REFERENCE") + " <" + name + ">";  
              addMessage(msg, attrGroupDef.getContainer());
              
              childrenToRemove.add(attrGroupDef.getElement());
            }
          }
        }
      }
    }
    
  }

}
