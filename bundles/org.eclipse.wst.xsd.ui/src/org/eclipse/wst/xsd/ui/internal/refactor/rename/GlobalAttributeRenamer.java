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

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalAttributeRenamer extends BaseRenamer
{
  /**
   * Constructor for GlobalAttributeRenamer.
   * @param globalComponent
   */
  public GlobalAttributeRenamer(XSDNamedComponent globalComponent, String newName)
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
        if (attrGroupContent instanceof XSDAttributeUse)
        {
          XSDAttributeUse attrUse = (XSDAttributeUse) attrGroupContent;
          XSDAttributeDeclaration attrDecl = attrUse.getContent();
          
          // now is this a reference?
          if (attrDecl != null && attrDecl.isAttributeDeclarationReference())
          {
            if (globalComponent.equals(attrDecl.getResolvedAttributeDeclaration()))
            {
              attrDecl.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getNewQName());           
            }
          }
        }
      }
    }
  }


  

  
}
