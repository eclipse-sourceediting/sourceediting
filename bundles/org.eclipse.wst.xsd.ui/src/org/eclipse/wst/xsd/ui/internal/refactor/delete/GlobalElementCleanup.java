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
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalElementCleanup extends BaseGlobalCleanup
{
    
  /**
   * Constructor for GlobalElementCleanup.
   * @param deletedItem
   */
  public GlobalElementCleanup(XSDConcreteComponent deletedItem)
  {
    super(deletedItem);
  }

  protected String replacementName = null;
  
  protected String getReplacementElementName()
  {
    if (replacementName == null)
    {
      TypesHelper helper = new TypesHelper(schema);
      
      List elements = helper.getGlobalElements();
      
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
   * @see org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor#visitElementDeclaration(XSDElementDeclaration)
   */
  public void visitElementDeclaration(XSDElementDeclaration element)
  {
    if (element.isElementDeclarationReference())
    {
      if (deletedItem.equals(element.getResolvedElementDeclaration()))
      {
        if (getReplacementElementName() != null)
        {
          // KCPort String msg = XSDPlugin.getSchemaString("_INFO_RESET_ELEMENT_REFERENCE") + " <" + getReplacementElementName() + ">";
					String msg = "_INFO_RESET_ELEMENT_REFERENCE" + " <" + getReplacementElementName() + ">";
          addMessage(msg, element);
          element.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getReplacementElementName());
        }
        else
        {
          // KCPort  String msg = XSDPlugin.getSchemaString("_INFO_REMOVE_ELEMENT_REFERENCE") + " <" + getNamedComponentName(element.getContainer()) + ">";
					String msg = "_INFO_REMOVE_ELEMENT_REFERENCE" + " <" + getNamedComponentName(element.getContainer()) + ">";
          addMessage(msg, element.getContainer());
          childrenToRemove.add(element.getElement());
        }
      }
    }
    super.visitElementDeclaration(element);
  }
}
