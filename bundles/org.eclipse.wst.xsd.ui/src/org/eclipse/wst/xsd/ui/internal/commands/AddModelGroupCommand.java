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
package org.eclipse.wst.xsd.ui.internal.commands;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;

public class AddModelGroupCommand extends AbstractCommand
{
  protected XSDCompositor compositor;

  public AddModelGroupCommand(XSDConcreteComponent parent, XSDCompositor compositor)
  {
    super(parent);
    this.compositor = compositor;
  }

  public void run()
  {
    XSDConcreteComponent parent = getParent();
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
        }
        owner = ed.getTypeDefinition();
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
    }
    else if (parent instanceof XSDModelGroup)
    {
      ((XSDModelGroup) parent).getContents().add(createModelGroup());
    }
    else if (parent instanceof XSDComplexTypeDefinition)
    {
      owner = ((XSDComplexTypeDefinition)parent);
    }
    if (owner != null)
    {
      XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle(); 
      XSDModelGroup modelGroup = createModelGroup();
      particle.setContent(modelGroup);
      XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)owner;
      ctd.setContent(particle);
      formatChild(parent.getElement());
    }  
  }

  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }
  
  protected XSDModelGroup createModelGroup()
  {
    XSDModelGroup modelGroup = XSDFactory.eINSTANCE.createXSDModelGroup();
    modelGroup.setCompositor(compositor);
    return modelGroup;
  }
}