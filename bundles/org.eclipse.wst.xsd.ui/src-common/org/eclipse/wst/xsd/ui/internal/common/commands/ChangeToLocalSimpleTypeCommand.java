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
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class ChangeToLocalSimpleTypeCommand extends BaseCommand
{
  XSDFeature parent;
  XSDSimpleTypeDefinition anonymousSimpleType;
  XSDSimpleTypeDefinition currentType;

  public ChangeToLocalSimpleTypeCommand(String label, XSDFeature parent)
  {
    super(label);
    this.parent = parent;

//    if (parent instanceof XSDElementDeclaration)
//    {
//      XSDElementDeclaration element = (XSDElementDeclaration) parent;
//      XSDTypeDefinition aType = element.getResolvedElementDeclaration().getTypeDefinition();
//
//      if (aType instanceof XSDSimpleTypeDefinition)
//      {
//        currentType = (XSDSimpleTypeDefinition) aType;
//      }
//    }
  }

  public void execute()
  {
//    anonymousSimpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
//    anonymousSimpleType.setBaseTypeDefinition(currentType);
    try
    {
      beginRecording(parent.getElement());

      if (parent instanceof XSDElementDeclaration)
      {
        ((XSDElementDeclaration) parent).setAnonymousTypeDefinition(anonymousSimpleType);
      }
      else if (parent instanceof XSDAttributeDeclaration)
      {
        ((XSDAttributeDeclaration) parent).setAnonymousTypeDefinition(anonymousSimpleType);
      }
      formatChild(parent.getElement());
    }
    finally
    {
      endRecording();
    }    
  }
  
  public void setAnonymousSimpleType(XSDSimpleTypeDefinition anonymousSimpleType)
  {
    this.anonymousSimpleType = anonymousSimpleType;
  }

  public XSDSimpleTypeDefinition getAnonymousType()
  {
    if (anonymousSimpleType == null)
    {
      anonymousSimpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
      anonymousSimpleType.setBaseTypeDefinition(currentType);
    }
    return anonymousSimpleType;
  }
}
