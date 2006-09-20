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
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

public class UpdateTypeReferenceCommand extends BaseCommand
{
  XSDConcreteComponent concreteComponent;
  XSDTypeDefinition newType;
  
  public UpdateTypeReferenceCommand(XSDConcreteComponent concreteComponent, XSDTypeDefinition newType)
  {
    this.concreteComponent = concreteComponent;
    this.newType = newType;
  }
   
  public void execute()
  {
    try
    {
      beginRecording(concreteComponent.getElement());

      if (concreteComponent instanceof XSDElementDeclaration)
      {
        setElementType((XSDElementDeclaration) concreteComponent);
      }
      else if (concreteComponent instanceof XSDAttributeUse)
      {
        setAttributeType((XSDAttributeUse) concreteComponent);
      }
      else if (concreteComponent instanceof XSDAttributeDeclaration)
      {
        setAttributeType((XSDAttributeDeclaration) concreteComponent);
      }
    }
    finally
    {
      endRecording();
    }
  }
 
  protected void setElementType(XSDElementDeclaration ed)
  {
    ed = ed.getResolvedElementDeclaration();
    if (ed != null)
    {  
      ed.setTypeDefinition(newType);
    }      
  }
  
  protected void setAttributeType(XSDAttributeUse attributeUse)
  {
    setAttributeType(attributeUse.getAttributeDeclaration());
  }
  
  protected void setAttributeType(XSDAttributeDeclaration ad)
  {
    ad = ad.getResolvedAttributeDeclaration();
    if (ad != null && newType instanceof XSDSimpleTypeDefinition)
    {
      ad.setTypeDefinition((XSDSimpleTypeDefinition)newType);
    }  
  }
}
