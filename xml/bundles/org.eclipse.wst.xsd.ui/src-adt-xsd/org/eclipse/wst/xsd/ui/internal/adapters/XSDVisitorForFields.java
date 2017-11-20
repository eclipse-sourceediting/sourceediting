/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;

public class XSDVisitorForFields extends XSDVisitor
{
  public XSDVisitorForFields()
  {
    super();
  }

  public List concreteComponentList = new ArrayList();
  public List thingsWeNeedToListenTo = new ArrayList();
  
  public void visitComplexTypeDefinition(XSDComplexTypeDefinition type)
  {
    if (type.getAttributeContents() != null)
    {
      for (Iterator iter = type.getAttributeContents().iterator(); iter.hasNext(); )
      {
        XSDAttributeGroupContent attrGroupContent = (XSDAttributeGroupContent)iter.next();
        if (attrGroupContent instanceof XSDAttributeUse)
        {
          XSDAttributeUse attrUse = (XSDAttributeUse)attrGroupContent;
          
          visitAttributeDeclaration(attrUse.getContent());

//          if (attrUse.getAttributeDeclaration() != attrUse.getContent())
//          {
//            visitAttributeDeclaration(attrUse.getContent());
//          }
//          else
//          {
//            thingsWeNeedToListenTo.add(attrUse.getAttributeDeclaration());
//            concreteComponentList.add(attrUse.getAttributeDeclaration());
//          }
        }
        else if (attrGroupContent instanceof XSDAttributeGroupDefinition)
        {
          XSDAttributeGroupDefinition attrGroup = (XSDAttributeGroupDefinition)attrGroupContent;
          thingsWeNeedToListenTo.add(attrGroup);
          if (attrGroup.isAttributeGroupDefinitionReference())
          {
            attrGroup = attrGroup.getResolvedAttributeGroupDefinition();
            visitAttributeGroupDefinition(attrGroup);
          }
        }
      }
    }
    if (type.getAttributeWildcard() != null)
    {
      thingsWeNeedToListenTo.add(type.getAttributeWildcard());
      concreteComponentList.add(type.getAttributeWildcard());
    }
    super.visitComplexTypeDefinition(type);
  }
  
  public void visitComplexTypeContent(XSDSimpleTypeDefinition content)
  {
    thingsWeNeedToListenTo.add(content);
    
    super.visitComplexTypeContent(content);   
  }

  
  public void visitModelGroupDefinition(XSDModelGroupDefinition modelGroupDef)
  {
    // listen to definition in case it changes
    XSDModelGroupDefinition resolvedModelGroupDef = modelGroupDef.getResolvedModelGroupDefinition();
    if (visitedGroups.contains(resolvedModelGroupDef.getModelGroup())) return;
   
    if (modelGroupDef.isModelGroupDefinitionReference())
    {
      // if it's a reference we need to listen to the reference incase it changes
      if (!thingsWeNeedToListenTo.contains(modelGroupDef))
        thingsWeNeedToListenTo.add(modelGroupDef);      
    }
    super.visitModelGroupDefinition(modelGroupDef);
  }
  
  public void visitModelGroup(XSDModelGroup modelGroup)
  {
    super.visitModelGroup(modelGroup);
    thingsWeNeedToListenTo.add(modelGroup); 
  }
  
  public void visitAttributeGroupDefinition(XSDAttributeGroupDefinition attributeGroup)
  {
    for (Iterator it = attributeGroup.getContents().iterator(); it.hasNext(); )
    {
      Object o = it.next();
      if (o instanceof XSDAttributeUse)
      {
        XSDAttributeUse attributeUse = (XSDAttributeUse)o;
        concreteComponentList.add(attributeUse.getAttributeDeclaration());
        thingsWeNeedToListenTo.add(attributeUse.getAttributeDeclaration());
      }
      else if (o instanceof XSDAttributeGroupDefinition)
      {
        XSDAttributeGroupDefinition attrGroup = (XSDAttributeGroupDefinition)o;
        thingsWeNeedToListenTo.add(attrGroup);
        if (attrGroup.isAttributeGroupDefinitionReference())
        {
          attrGroup = attrGroup.getResolvedAttributeGroupDefinition();
          visitAttributeGroupDefinition(attrGroup);
        }
      }
    }
    
    XSDWildcard anyAttribute = attributeGroup.getAttributeWildcardContent();
    if (anyAttribute != null)
    {
      concreteComponentList.add(anyAttribute);
      thingsWeNeedToListenTo.add(anyAttribute);
    }
    
  }
  
  public void visitParticle(XSDParticle particle)
  {
    thingsWeNeedToListenTo.add(particle);
    super.visitParticle(particle);
  }
  
  public void visitWildcard(XSDWildcard wildcard)
  {
    concreteComponentList.add(wildcard);
  }

  public void visitElementDeclaration(XSDElementDeclaration element)
  {
    if (element.isElementDeclarationReference())
    {
      thingsWeNeedToListenTo.add(element);
      thingsWeNeedToListenTo.add(element.getResolvedElementDeclaration());
      // now, add the reference as a field
      concreteComponentList.add(element);
    }
    else
    {
      concreteComponentList.add(element.getResolvedElementDeclaration());
      // note... we intentionally ommit the call to super.visitElementDeclaration()
      // since we don't want to delve down deeper than the element      
    }
  }
  
  public void visitAttributeDeclaration(XSDAttributeDeclaration attr)
  {
    if (attr.isAttributeDeclarationReference())
    {
      thingsWeNeedToListenTo.add(attr);
      thingsWeNeedToListenTo.add(attr.getResolvedAttributeDeclaration());
      concreteComponentList.add(attr);
    }
    else
    {
      concreteComponentList.add(attr.getResolvedAttributeDeclaration());
      thingsWeNeedToListenTo.add(attr.getResolvedAttributeDeclaration());
    }
  }
}