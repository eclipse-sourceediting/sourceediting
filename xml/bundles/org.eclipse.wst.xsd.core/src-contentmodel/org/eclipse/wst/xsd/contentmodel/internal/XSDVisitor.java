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
package org.eclipse.wst.xsd.contentmodel.internal;

import java.util.Iterator;
import java.util.Stack;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNotationDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;

public class XSDVisitor
{
  public XSDVisitor()
  {
  }
  
  protected XSDSchema schema;
  protected Stack particleStack = new Stack();
  
  public void visitSchema(XSDSchema schema)
  {
    this.schema = schema;
    for (Iterator iterator = schema.getAttributeDeclarations().iterator(); iterator.hasNext();)
    {
      XSDAttributeDeclaration attr = (XSDAttributeDeclaration) iterator.next();
      visitAttributeDeclaration(attr);
    }
    for (Iterator iterator = schema.getTypeDefinitions().iterator(); iterator.hasNext();)
    {
      XSDTypeDefinition type = (XSDTypeDefinition) iterator.next();
      visitTypeDefinition(type);
    }
    for (Iterator iterator = schema.getElementDeclarations().iterator(); iterator.hasNext();)
    {
      XSDElementDeclaration element = (XSDElementDeclaration) iterator.next();
      visitElementDeclaration(element);
    }
    for (Iterator iterator = schema.getIdentityConstraintDefinitions().iterator(); iterator.hasNext();)
    {
      XSDIdentityConstraintDefinition identityConstraint = (XSDIdentityConstraintDefinition) iterator.next();
      visitIdentityConstraintDefinition(identityConstraint);
    }
    for (Iterator iterator = schema.getModelGroupDefinitions().iterator(); iterator.hasNext();)
    {
      XSDModelGroupDefinition modelGroup = (XSDModelGroupDefinition) iterator.next();
      visitModelGroupDefinition(modelGroup);
    }
    for (Iterator iterator = schema.getAttributeGroupDefinitions().iterator(); iterator.hasNext();)
    {
      XSDAttributeGroupDefinition attributeGroup = (XSDAttributeGroupDefinition) iterator.next();
      visitAttributeGroupDefinition(attributeGroup);
    }
    for (Iterator iterator = schema.getNotationDeclarations().iterator(); iterator.hasNext();)
    {
      XSDNotationDeclaration element = (XSDNotationDeclaration) iterator.next();
      visitNotationDeclaration(element);
    }
    
  }
  
  public void visitAttributeDeclaration(XSDAttributeDeclaration attr)
  {
  }
  
  public void visitTypeDefinition(XSDTypeDefinition type)
  {
    if (type instanceof XSDSimpleTypeDefinition)
    {
      visitSimpleTypeDefinition((XSDSimpleTypeDefinition)type);
    }
    else if (type instanceof XSDComplexTypeDefinition)
    {
      visitComplexTypeDefinition((XSDComplexTypeDefinition)type);
    }
  }
  
  public void visitElementDeclaration(XSDElementDeclaration element)
  {
    if (element.isElementDeclarationReference())
    {
    }
    else if (element.getAnonymousTypeDefinition() != null)
    {
      visitTypeDefinition(element.getAnonymousTypeDefinition());
    }
  }
  
  public void visitIdentityConstraintDefinition(XSDIdentityConstraintDefinition identityConstraint)
  {
  }
  
  public void visitModelGroupDefinition(XSDModelGroupDefinition modelGroupDef)
  {
    if (!modelGroupDef.isModelGroupDefinitionReference())
    {
      if (modelGroupDef.getModelGroup() != null)
      {
        visitModelGroup(modelGroupDef.getModelGroup());
      }
    }
  }
  
  public void visitAttributeGroupDefinition(XSDAttributeGroupDefinition attributeGroup)
  {
  }
  
  public void visitNotationDeclaration(XSDNotationDeclaration notation)
  {
  }
  
  public void visitSimpleTypeDefinition(XSDSimpleTypeDefinition type)
  {
  }
  
  public void visitComplexTypeDefinition(XSDComplexTypeDefinition type)
  {
    if (type.getContentType() != null)
    {
      XSDComplexTypeContent complexContent = type.getContentType();
      if (complexContent instanceof XSDSimpleTypeDefinition)
      {
        visitSimpleTypeDefinition((XSDSimpleTypeDefinition)complexContent);
      }
      else if (complexContent instanceof XSDParticle)
      {
        visitParticle((XSDParticle) complexContent);
      }
    }
  }
  
  public void visitParticle(XSDParticle particle)
  {
    visitParticleContent(particle.getContent());
  }
  
  public void visitParticleContent(XSDParticleContent particleContent)
  {
    if (particleContent instanceof XSDModelGroupDefinition)
    {
      XSDModelGroupDefinition modelGroupDef = (XSDModelGroupDefinition) particleContent;

      if (particleStack.contains(modelGroupDef))
      {
        return;
      }
        
      particleStack.push(modelGroupDef);
      
      visitModelGroupDefinition(modelGroupDef);
      
      particleStack.pop();
    }
    else if (particleContent instanceof XSDModelGroup)
    {
      visitModelGroup((XSDModelGroup)particleContent);
    }
    else if (particleContent instanceof XSDElementDeclaration)
    {
      visitElementDeclaration((XSDElementDeclaration)particleContent);
    }
    else if (particleContent instanceof XSDWildcard)
    {
      visitWildcard((XSDWildcard)particleContent);
    }
  }
  
  public void visitModelGroup(XSDModelGroup modelGroup)
  {
    if (modelGroup.getContents() != null)
    {
      for (Iterator iterator = modelGroup.getContents().iterator(); iterator.hasNext();)
      {
        XSDParticle particle = (XSDParticle) iterator.next();
        visitParticle(particle);
      }
    }
  }
  
  public void visitWildcard(XSDWildcard wildcard)
  {
  }
}
