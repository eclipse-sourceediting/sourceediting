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

import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

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
    if (concreteComponent instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) concreteComponent;
      
      if (baseType instanceof XSDSimpleTypeDefinition)
      {
        if (!(complexType.getContent() instanceof XSDSimpleTypeDefinition))
        {
          XSDSimpleTypeDefinition simpleContent = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
          complexType.setContent(simpleContent);
        }
      }
      else
      {
        if (!(complexType.getContent() instanceof XSDParticle))
        {
          XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
          XSDModelGroup group = XSDFactory.eINSTANCE.createXSDModelGroup();
          group.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
          particle.setContent(group);
          complexType.setContent(particle);
        }
      }
      
      complexType.setBaseTypeDefinition(baseType);
      complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);      
      formatChild(complexType.getElement());
    }
  }
}
