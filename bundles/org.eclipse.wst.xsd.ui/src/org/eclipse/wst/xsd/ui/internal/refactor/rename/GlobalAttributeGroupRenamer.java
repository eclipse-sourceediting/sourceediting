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
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.util.Iterator;

import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalAttributeGroupRenamer extends BaseRenamer
{
  /**
   * Constructor for GlobalAttributeGroupRenamer.
   * @param globalComponent
   */
  public GlobalAttributeGroupRenamer(XSDNamedComponent globalComponent, String newName)
  {
    super(globalComponent, newName);
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
          
          if (globalComponent.equals(attrGroupDef.getResolvedAttributeGroupDefinition()))
          {
            attrGroupDef.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getNewQName());
          }
        }
      }
    }
    
  }
}
