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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Text;

public class AddXSDAttributeGroupDefinitionCommand extends BaseCommand
{
  XSDComplexTypeDefinition xsdComplexTypeDefinition;
  XSDSchema xsdSchema;

  public AddXSDAttributeGroupDefinitionCommand(String label, XSDComplexTypeDefinition xsdComplexTypeDefinition)
  {
    super(label);
    this.xsdComplexTypeDefinition = xsdComplexTypeDefinition;
  }

  public AddXSDAttributeGroupDefinitionCommand(String label, XSDSchema xsdSchema)
  {
    super(label);
    this.xsdSchema = xsdSchema;
  }

  public void execute()
  {
    XSDAttributeGroupDefinition attributeGroup = XSDFactory.eINSTANCE.createXSDAttributeGroupDefinition();
    try
    {
      if (xsdSchema == null)
      {
        beginRecording(xsdComplexTypeDefinition.getElement());
        attributeGroup.setName(getNewName("AttributeGroup")); //$NON-NLS-1$

        List list = xsdComplexTypeDefinition.getSchema().getAttributeGroupDefinitions();
        if (list.size() > 0)
        {
          attributeGroup.setResolvedAttributeGroupDefinition((XSDAttributeGroupDefinition) list.get(0));
        }
        else
        {
          attributeGroup.setName(null);
          XSDAttributeGroupDefinition attributeGroup2 = XSDFactory.eINSTANCE.createXSDAttributeGroupDefinition();
          attributeGroup2.setName(XSDCommonUIUtils.createUniqueElementName("NewAttributeGroup", xsdComplexTypeDefinition.getSchema().getAttributeGroupDefinitions())); //$NON-NLS-1$
          xsdComplexTypeDefinition.getSchema().getContents().add(attributeGroup2);
          attributeGroup.setResolvedAttributeGroupDefinition(attributeGroup2);
        }

        if (xsdComplexTypeDefinition.getAttributeContents() != null)
        {
          xsdComplexTypeDefinition.getAttributeContents().add(attributeGroup);
        }
        addedXSDConcreteComponent = attributeGroup;
      }
      else
      {
        ensureSchemaElement(xsdSchema);
        // put this after, since we don't have a DOM node yet
        beginRecording(xsdSchema.getElement());
        attributeGroup.setName(XSDCommonUIUtils.createUniqueElementName("NewAttributeGroup", xsdSchema.getAttributeGroupDefinitions())); //$NON-NLS-1$
        Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
        xsdSchema.getElement().appendChild(textNode);
        xsdSchema.getContents().add(attributeGroup);
        addedXSDConcreteComponent = attributeGroup;
      }
    }
    finally
    {
      endRecording();
    }
  }

  ArrayList names;

  protected String getNewName(String description)
  {
    String candidateName = "New" + description; //$NON-NLS-1$
    XSDConcreteComponent parent = xsdComplexTypeDefinition;
    names = new ArrayList();
    int i = 1;
    if (parent instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition) parent;
      walkUpInheritance(ct);

      boolean ready = false;
      while (!ready)
      {
        ready = true;
        for (Iterator iter = names.iterator(); iter.hasNext();)
        {
          String attrName = (String) iter.next();
          if (candidateName.equals(attrName))
          {
            ready = false;
            candidateName = "New" + description + String.valueOf(i); //$NON-NLS-1$
            i++;
          }
        }
      }
    }
    return candidateName;
  }

  private void walkUpInheritance(XSDComplexTypeDefinition ct)
  {
    updateNames(ct);
    XSDTypeDefinition typeDef = ct.getBaseTypeDefinition();
    if (ct != ct.getRootType())
    {
      if (typeDef instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition ct2 = (XSDComplexTypeDefinition) typeDef;
        walkUpInheritance(ct2);
      }
    }
  }

  private void updateNames(XSDComplexTypeDefinition ct)
  {
    Iterator iter = ct.getAttributeContents().iterator();
    while (iter.hasNext())
    {
      Object obj = iter.next();
      if (obj instanceof XSDAttributeUse)
      {
        XSDAttributeUse use = (XSDAttributeUse) obj;
        XSDAttributeDeclaration attr = use.getAttributeDeclaration();
        String attrName = attr.getName();
        if (attrName != null)
        {
          names.add(attrName);
        }
      }
    }

  }

}
