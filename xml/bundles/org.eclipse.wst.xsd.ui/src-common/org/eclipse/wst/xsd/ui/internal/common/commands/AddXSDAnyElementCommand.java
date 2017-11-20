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

import org.eclipse.core.runtime.Assert;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

public class AddXSDAnyElementCommand extends BaseCommand
{
  XSDModelGroup parent;
  XSDComplexTypeDefinition complexType;
  boolean doCreateModelGroupForComplexType = false;

  public AddXSDAnyElementCommand(String label, XSDModelGroup parent)
  {
    super(label);
    this.parent = parent;
  }
  
  public void setComplexType(XSDComplexTypeDefinition complexType)
  {
    this.complexType = complexType;
  }
  
  public void setDoCreateModelGroupForComplexType(boolean doCreate)
  {
    this.doCreateModelGroupForComplexType = doCreate;
  }
  
  private void createModelGroup()
  {
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDParticle particle = factory.createXSDParticle();
    parent = factory.createXSDModelGroup();
    parent.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
    particle.setContent(parent);
    complexType.setContent(particle);
  }

  public void execute()
  {
    try
    {
      if (doCreateModelGroupForComplexType)
      {
        Assert.isNotNull(complexType);
        beginRecording(complexType.getElement());
        createModelGroup();
      }
      else
      {
        beginRecording(parent.getElement());
      }
      XSDWildcard wildCard = XSDFactory.eINSTANCE.createXSDWildcard();
      XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
      particle.setContent(wildCard);
      parent.getContents().add(particle);
      addedXSDConcreteComponent = wildCard;
      formatChild(parent.getElement());
    }
    finally
    {
      endRecording();
    }
  }
}
