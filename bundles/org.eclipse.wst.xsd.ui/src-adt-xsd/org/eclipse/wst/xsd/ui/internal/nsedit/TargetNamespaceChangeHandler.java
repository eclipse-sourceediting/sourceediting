/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.nsedit;

import java.util.Iterator;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;


public class TargetNamespaceChangeHandler
{
  String newNS;
  String oldNS;
  XSDSchema xsdSchema;

  public TargetNamespaceChangeHandler(XSDSchema xsdSchema, String oldNS, String newNS)
  {
    this.xsdSchema = xsdSchema;
    this.oldNS= oldNS;
    this.newNS= newNS;
  }
  
  public void resolve()
  {
    ElementReferenceRenamer elementReferenceRenamer = new ElementReferenceRenamer();
    elementReferenceRenamer.visitSchema(xsdSchema);
    AttributeReferenceRenamer attributeReferenceRenamer = new AttributeReferenceRenamer();
    attributeReferenceRenamer.visitSchema(xsdSchema);
  }

  class ElementReferenceRenamer extends XSDVisitor
  {
    public ElementReferenceRenamer()
    {
      super();
    }
    
    public void visitElementDeclaration(XSDElementDeclaration element)
    {
      super.visitElementDeclaration(element);
      if (element.isElementDeclarationReference())
      {
      	if (element.getResolvedElementDeclaration().getTargetNamespace() != null)
      	{
      	  if (element.getResolvedElementDeclaration().getTargetNamespace().equals(oldNS))
          {
            // set the resolved element's declaration to new ns
            // this is defect 237518 - target namespace rename creates a new namespace
            element.getResolvedElementDeclaration().setTargetNamespace(newNS);
          }
        }
        else
        {
        	if (oldNS == null || (oldNS != null && oldNS.equals("")))
        	{
						element.getResolvedElementDeclaration().setTargetNamespace(newNS);
        	}
        }
      }
    }
  }
  
  // Similar to defect 237518 but for attributes
  class AttributeReferenceRenamer extends XSDVisitor
  {
    public AttributeReferenceRenamer()
    {
      super();
    }
    
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
            
            if (attrDecl != null && attrDecl.isAttributeDeclarationReference())
            {
							if (attrDecl.getResolvedAttributeDeclaration().getTargetNamespace() != null)
							{
                if (attrDecl.getResolvedAttributeDeclaration().getTargetNamespace().equals(oldNS))
                {
                  attrDecl.getResolvedAttributeDeclaration().setTargetNamespace(newNS);
                }
              }
              else
              {
								if (oldNS == null || (oldNS != null && oldNS.equals("")))
								{
									attrDecl.getResolvedAttributeDeclaration().setTargetNamespace(newNS);
								}
              }
            }
          }
        }
      }
    }
  
    public void visitAttributeGroupDefinition(XSDAttributeGroupDefinition attributeGroup)
    {
      super.visitAttributeGroupDefinition(attributeGroup);
      EList list = attributeGroup.getAttributeUses();
      if (list != null)
      {
        for (Iterator iter = list.iterator(); iter.hasNext(); )
        {
          XSDAttributeUse attrUse = (XSDAttributeUse)iter.next();
          XSDAttributeDeclaration attrDecl = attrUse.getContent();

          if (attrDecl != null && attrDecl.isAttributeDeclarationReference())
          {
						if (attrDecl.getResolvedAttributeDeclaration().getTargetNamespace() != null)
						{
              if (attrDecl.getResolvedAttributeDeclaration().getTargetNamespace().equals(oldNS))
              {
                attrDecl.getResolvedAttributeDeclaration().setTargetNamespace(newNS);
              }
            }
            else
            {
							if (oldNS == null || (oldNS != null && oldNS.equals("")))
							{
								attrDecl.getResolvedAttributeDeclaration().setTargetNamespace(newNS);
							}
            }
          }
        }
      }
    }
  }
}
