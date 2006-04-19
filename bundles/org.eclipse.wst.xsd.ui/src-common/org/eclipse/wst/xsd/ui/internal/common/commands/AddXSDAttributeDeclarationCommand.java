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
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Text;

public class AddXSDAttributeDeclarationCommand extends BaseCommand
{
  XSDComplexTypeDefinition xsdComplexTypeDefinition;
  XSDModelGroup xsdModelGroup;
//  XSDSchema xsdSchema;
  XSDConcreteComponent parent;

  public AddXSDAttributeDeclarationCommand(String label, XSDComplexTypeDefinition xsdComplexTypeDefinition)
  {
    super(label);
    this.xsdComplexTypeDefinition = xsdComplexTypeDefinition;
  }
  
  public AddXSDAttributeDeclarationCommand(String label, XSDConcreteComponent parent)
  {
    super(label);
    this.parent = parent;
  }

  public void execute()
  {
    XSDAttributeDeclaration attribute = XSDFactory.eINSTANCE.createXSDAttributeDeclaration();
    if (parent == null)
    {
      attribute.setName(getNewName("Attribute")); //$NON-NLS-1$
      attribute.setTypeDefinition(xsdComplexTypeDefinition.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string")); //$NON-NLS-1$

      XSDAttributeUse attributeUse = XSDFactory.eINSTANCE.createXSDAttributeUse();
      attributeUse.setAttributeDeclaration(attribute);
      attributeUse.setContent(attribute);

      if (xsdComplexTypeDefinition.getAttributeContents() != null)
      {
        xsdComplexTypeDefinition.getAttributeContents().add(attributeUse);
        formatChild(xsdComplexTypeDefinition.getElement());
      }
      addedXSDConcreteComponent = attributeUse;
    }
    else
    {
      attribute.setTypeDefinition(parent.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string")); //$NON-NLS-1$
      if (parent instanceof XSDSchema)
      {
        attribute.setName(XSDCommonUIUtils.createUniqueElementName("NewAttribute", parent.getSchema().getAttributeDeclarations())); //$NON-NLS-1$
        XSDSchema xsdSchema = (XSDSchema)parent;
        Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
        xsdSchema.getElement().appendChild(textNode);
        xsdSchema.getContents().add(attribute);
      }
      else if (parent instanceof XSDAttributeGroupDefinition)
      {
        List list = new ArrayList();
        Iterator i = ((XSDAttributeGroupDefinition)parent).getResolvedAttributeGroupDefinition().getAttributeUses().iterator();
        while (i.hasNext())
        {
          XSDAttributeUse use = (XSDAttributeUse)i.next();
          list.add(use.getAttributeDeclaration());
        }
        attribute.setName(XSDCommonUIUtils.createUniqueElementName("NewAttribute", list)); //$NON-NLS-1$
        XSDAttributeUse attributeUse = XSDFactory.eINSTANCE.createXSDAttributeUse();
        attributeUse.setAttributeDeclaration(attribute);
        attributeUse.setContent(attribute);
 
        ((XSDAttributeGroupDefinition)parent).getResolvedAttributeGroupDefinition().getContents().add(attributeUse);
        formatChild(parent.getElement());
      }
      addedXSDConcreteComponent = attribute;
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
