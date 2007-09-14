/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

public class AddXSDModelGroupCommand extends BaseCommand
{
  XSDConcreteComponent parent;
  XSDCompositor xsdCompositor;
  XSDModelGroup newModelGroup;

  public AddXSDModelGroupCommand(String label, XSDConcreteComponent parent, XSDCompositor xsdCompositor)
  {
    super(label);
    this.parent = parent;
    this.xsdCompositor = xsdCompositor;
  }

  public void execute()
  {
    try
    {
      beginRecording(parent.getElement());
      XSDConcreteComponent owner = getOwner();
      if (owner != null)
      {
        XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
        newModelGroup = createModelGroup();
        particle.setContent(newModelGroup);

        XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition) owner;
        ctd.setContent(particle);
      }
      formatChild(parent.getElement());
    }
    finally
    {
      endRecording();
    }
  }
  
  public void undo()
  {
    super.undo();
    
    if (parent instanceof XSDModelGroup)
    {
      XSDModelGroup model = (XSDModelGroup) parent;
      model.getContents().remove(newModelGroup.getContainer());
    }
  }
  
  private XSDConcreteComponent getOwner()
  {
    XSDConcreteComponent owner = null;
    if (parent instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration ed = (XSDElementDeclaration)parent;      
      if (ed.getTypeDefinition() != null) 
      {
        if (ed.getAnonymousTypeDefinition() == null)
        {
          ed.setTypeDefinition(null);
          XSDComplexTypeDefinition td = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
          ed.setAnonymousTypeDefinition(td);
          owner = ed.getTypeDefinition();
        }
        else
        {
          XSDComplexTypeDefinition td = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
          ed.setAnonymousTypeDefinition(td);
          owner = td;        
        }
      }        
      else if (ed.getAnonymousTypeDefinition() == null)
      {
        XSDComplexTypeDefinition td = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        ed.setAnonymousTypeDefinition(td);
        owner = td;        
      }
      else if (ed.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition)
      {
        owner = ed.getAnonymousTypeDefinition();
      }
      else if (ed.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition)
      {
        XSDComplexTypeDefinition td = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        ed.setAnonymousTypeDefinition(td);
        owner = td;        
      }
    }
    else if (parent instanceof XSDModelGroup)
    {
      newModelGroup = createModelGroup();
      ((XSDModelGroup) parent).getContents().add(newModelGroup.getContainer());
    }
    else if (parent instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)parent;
      owner = parent;
      if (ct.getContent() instanceof XSDParticle)
      {
        XSDParticle particle = (XSDParticle)ct.getContent();
        if (particle.getContent() instanceof XSDModelGroup)
        {
          owner = null;
          newModelGroup = createModelGroup();
          XSDModelGroup newParent = (XSDModelGroup)particle.getContent();
          newParent.getContents().add(newModelGroup.getContainer());
        }
        
      }
    }
    else if (parent instanceof XSDModelGroupDefinition)
    {
      XSDModelGroupDefinition modelGroupDefinition = (XSDModelGroupDefinition)parent;
      owner = null;
      newModelGroup = createModelGroup();
      if (modelGroupDefinition.getModelGroup() != null)
      {
        XSDModelGroup newParent = modelGroupDefinition.getModelGroup();
        newParent.getContents().add(newModelGroup.getContainer());
      }
      else
      {
        modelGroupDefinition.setModelGroup(newModelGroup);
      }
    }
    return owner;
  }
  

  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }
  
  protected XSDModelGroup createModelGroup()
  {
    
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDParticle particle = factory.createXSDParticle();
    XSDModelGroup modelGroup = factory.createXSDModelGroup();
    modelGroup.setCompositor(xsdCompositor);
    particle.setContent(modelGroup);
    addedXSDConcreteComponent = modelGroup;
    return modelGroup;
  }
}
