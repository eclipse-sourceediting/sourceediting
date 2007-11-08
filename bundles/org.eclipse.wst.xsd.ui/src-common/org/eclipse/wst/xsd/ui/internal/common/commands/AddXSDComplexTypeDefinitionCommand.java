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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.w3c.dom.Text;

public final class AddXSDComplexTypeDefinitionCommand extends BaseCommand
{
  protected XSDConcreteComponent parent;
  protected XSDComplexTypeDefinition createdComplexType;
  private String nameToAdd;
  
  public AddXSDComplexTypeDefinitionCommand(String label, XSDConcreteComponent parent)
  {
    super(label);
    this.parent = parent;
  }
  
  public void setNameToAdd(String nameToAdd)
  {
    this.nameToAdd = nameToAdd;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.gef.commands.Command#execute()
   */
  public void execute()
  {
    try
    {
      beginRecording(parent.getElement());
      XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
      XSDComplexTypeDefinition complexType = factory.createXSDComplexTypeDefinition();
      addedXSDConcreteComponent = complexType;
      String newName = getNewName(nameToAdd == null ? "NewComplexType" : nameToAdd, parent.getSchema()); //$NON-NLS-1$
      complexType.setName(newName);
      if (parent instanceof XSDSchema)
      {
        try
        {
          XSDSchema xsdSchema = (XSDSchema) parent;
          ensureSchemaElement(xsdSchema);
          Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
          xsdSchema.getElement().appendChild(textNode);
          xsdSchema.getContents().add(complexType);
        }
        catch (Exception e)
        {

        }
      }
      else if (parent instanceof XSDElementDeclaration)
      {
        ((XSDElementDeclaration) parent).setAnonymousTypeDefinition(complexType);
        formatChild(parent.getElement());
      }
      createdComplexType = complexType;
    }
    finally
    {
      endRecording();
    }
  }

  protected String getNewName(String description, XSDSchema schema)
  {
    String candidateName = description; //$NON-NLS-1$
    int i = 1;

    List list = schema.getTypeDefinitions();
    List listOfNames = new ArrayList();
    for (Iterator iter = list.iterator(); iter.hasNext();)
    {
      XSDTypeDefinition typeDef = (XSDTypeDefinition) iter.next();
      String name = typeDef.getName();
      if (name == null)
        name = ""; //$NON-NLS-1$
      if (typeDef.getTargetNamespace() == schema.getTargetNamespace())
        listOfNames.add(name);
    }

    boolean flag = true;
    while (flag)
    {
      if (!listOfNames.contains(candidateName))
      {
        flag = false;
        break;
      }
      candidateName = description + String.valueOf(i); //$NON-NLS-1$
      i++;
    }

    return candidateName;
  }

  public XSDComplexTypeDefinition getCreatedComplexType()
  {
    return createdComplexType;
  }
}
