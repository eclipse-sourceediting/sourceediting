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

import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Text;

public final class AddXSDSimpleTypeDefinitionCommand extends BaseCommand
{
  XSDConcreteComponent parent;
  XSDSimpleTypeDefinition createdSimpleType;
  private String nameToAdd;
  
  public AddXSDSimpleTypeDefinitionCommand(String label, XSDConcreteComponent parent)
  {
    super(label);
    this.parent = parent;
  }

  public void setNameToAdd(String nameToAdd)
  {
    this.nameToAdd = nameToAdd;
  }
  
  public void execute()
  {
    if (parent instanceof XSDSchema)
    {
      ensureSchemaElement((XSDSchema)parent);
    }
    
    try
    {
      beginRecording(parent.getElement());
      XSDSimpleTypeDefinition typeDef = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
      typeDef.setBaseTypeDefinition(parent.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "string")); //$NON-NLS-1$

      if (parent instanceof XSDSchema)
      {
        typeDef.setName(XSDCommonUIUtils.createUniqueElementName(nameToAdd == null ? "NewSimpleType" : nameToAdd, ((XSDSchema) parent).getTypeDefinitions())); //$NON-NLS-1$
        createdSimpleType = typeDef;
        try
        {
          XSDSchema xsdSchema = (XSDSchema) parent;
          Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
          xsdSchema.getElement().appendChild(textNode);
          xsdSchema.getContents().add(typeDef);
        }
        catch (Exception e)
        {

        }
      }
      else if (parent instanceof XSDElementDeclaration)
      {
        ((XSDElementDeclaration) parent).setAnonymousTypeDefinition(typeDef);
      }
      else if (parent instanceof XSDAttributeDeclaration)
      {
        ((XSDAttributeDeclaration) parent).setAnonymousTypeDefinition(typeDef);
      }
      formatChild(createdSimpleType.getElement());

      addedXSDConcreteComponent = createdSimpleType;
    }
    finally
    {
      endRecording();
    }
  }

  public XSDSimpleTypeDefinition getCreatedSimpleType()
  {
    return createdSimpleType;
  }
}
