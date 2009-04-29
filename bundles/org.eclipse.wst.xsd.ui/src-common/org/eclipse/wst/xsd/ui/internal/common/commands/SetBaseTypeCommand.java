/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;

public class SetBaseTypeCommand extends BaseCommand
{
  XSDConcreteComponent concreteComponent;
  XSDTypeDefinition baseType;
  XSDBaseAdapter adapter;
  

  public SetBaseTypeCommand(XSDConcreteComponent concreteComponent, XSDTypeDefinition baseType)
  {
    super(Messages._UI_ACTION_SET_BASE_TYPE);
    this.concreteComponent = concreteComponent;
    this.baseType = baseType;
  }

  public void execute()
  {
    try
    {
      beginRecording(concreteComponent.getElement());
      if (concreteComponent instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) concreteComponent;        
        XSDComplexTypeContent contentType = (baseType instanceof XSDComplexTypeDefinition) ? ((XSDComplexTypeDefinition)baseType).getContentType() : null;
        
        // Complex type simple content
        if (baseType instanceof XSDSimpleTypeDefinition || (contentType != null &&  contentType instanceof XSDSimpleTypeDefinition))
        {
          if (!(complexType.getContent() instanceof XSDSimpleTypeDefinition))
          {
            XSDSimpleTypeDefinition simpleContent = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
            complexType.setContent(simpleContent);
          }
        }
        // Complex type complex content
        else if(baseType instanceof XSDComplexTypeDefinition)
        {
        	if(!(complexType.getContent() instanceof XSDComplexTypeDefinition))
        	{
        		XSDComplexTypeDefinition complexContent = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
        		complexType.setContent(complexContent.getContent());
        	}
        }        
        complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
        complexType.setBaseTypeDefinition(baseType);
        // vb This call should not be needed. The XSD Infoset model should reconcile itself properly.
        complexType.updateElement(true);
        formatChild(complexType.getElement());
      }
      else if (concreteComponent instanceof XSDSimpleTypeDefinition)
      {
        XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) concreteComponent;
        if (baseType instanceof XSDSimpleTypeDefinition)
        {
          XSDVariety variety = simpleType.getVariety();
          if (variety.getValue() == XSDVariety.ATOMIC)
          {
            simpleType.setBaseTypeDefinition((XSDSimpleTypeDefinition) baseType);
          }
          else if (variety.getValue() == XSDVariety.UNION)
          {
            simpleType.getMemberTypeDefinitions().add(baseType);
          }
          else if (variety.getValue() == XSDVariety.LIST)
          {
            simpleType.setItemTypeDefinition((XSDSimpleTypeDefinition) baseType);
          }
        }
      }
    }
    finally
    {
      endRecording();
    }
  }
}
