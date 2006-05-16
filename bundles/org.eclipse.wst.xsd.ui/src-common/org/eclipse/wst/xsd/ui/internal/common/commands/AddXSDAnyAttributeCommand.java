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

import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDWildcard;

public class AddXSDAnyAttributeCommand extends BaseCommand
{
  XSDComplexTypeDefinition xsdComplexTypeDefinition;
  XSDAttributeGroupDefinition xsdAttributeGroupDefinition;

  public AddXSDAnyAttributeCommand(String label, XSDComplexTypeDefinition xsdComplexTypeDefinition)
  {
    super(label);
    this.xsdComplexTypeDefinition = xsdComplexTypeDefinition;
  }
  
  public AddXSDAnyAttributeCommand(String label, XSDAttributeGroupDefinition xsdAttributeGroupDefinition)
  {
    super(label);
    this.xsdAttributeGroupDefinition = xsdAttributeGroupDefinition;
  }
  
  public void execute()
  {
    XSDWildcard anyAttribute = XSDFactory.eINSTANCE.createXSDWildcard();
    if (xsdComplexTypeDefinition != null)
    {
        xsdComplexTypeDefinition.setAttributeWildcardContent(anyAttribute);
        formatChild(xsdComplexTypeDefinition.getElement());
    }
    else if (xsdAttributeGroupDefinition != null)
    {
      xsdAttributeGroupDefinition.setAttributeWildcardContent(anyAttribute);
      formatChild(xsdAttributeGroupDefinition.getElement());
    }
    addedXSDConcreteComponent = anyAttribute;
  }

}
