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
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalAttributeCleanup extends BaseGlobalCleanup
{
  /**
   * Constructor for GlobalAttributeCleanup.
   * @param deletedItem
   */
  public GlobalAttributeCleanup(XSDConcreteComponent deletedItem)
  {
    super(deletedItem);
  }
  
  protected String replacementName = null;
  
  protected String getReplacementElementName()
  {
    if (replacementName == null)
    {
      TypesHelper helper = new TypesHelper(schema);
      
      List elements = helper.getGlobalAttributes();
      
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
        if (attrGroupContent instanceof XSDAttributeUse)
        {
          XSDAttributeUse attrUse = (XSDAttributeUse) attrGroupContent;
          XSDAttributeDeclaration attrDecl = attrUse.getContent();
          
          // now is this a reference?
          if (attrDecl != null && attrDecl.isAttributeDeclarationReference())
          {
            if (deletedItem.equals(attrDecl.getResolvedAttributeDeclaration()))
            {
              if (getReplacementElementName() != null)
              {
                // String msg = XSDPlugin.getSchemaString("_INFO_RESET_ATTRIBUTE_REFERENCE") + " <" + getReplacementElementName() + ">";
								String msg = "Reset attribute reference " + " <" + getReplacementElementName() + ">";
                addMessage(msg, attrUse);
                attrUse.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getReplacementElementName());
              }
              else
              {
                String name = getNamedComponentName(type);
                // String msg = XSDPlugin.getSchemaString("_INFO_REMOVE_ATTRIBUTE_REFERENCE") +
								String msg = "Remove attribute reference " +
                         " <" + name + ">";
                addMessage(msg, attrUse.getContainer());
                
                childrenToRemove.add(attrDecl.getElement());
              }
            }
          }
        }
      }
    }
  }


  
}
