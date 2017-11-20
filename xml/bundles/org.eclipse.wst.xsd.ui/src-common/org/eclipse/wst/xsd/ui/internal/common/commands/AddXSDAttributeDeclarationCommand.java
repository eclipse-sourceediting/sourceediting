/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Text;

public class AddXSDAttributeDeclarationCommand extends BaseCommand
{
  XSDComplexTypeDefinition xsdComplexTypeDefinition;
  XSDModelGroup xsdModelGroup;
  XSDConcreteComponent parent;
  boolean isReference;
  private String nameToAdd;
  // The index of the currently selected item.  If no item is selected, index will be less than 0
  private int index = -1;
  // Determines where the attribute should be inserted based on the currently selected attribute.  If no
  // attribute is selected, use the default behaviour of appending the attribute to the end
  private String addAttributeLocation;  
  
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
  
  public AddXSDAttributeDeclarationCommand(String label, XSDConcreteComponent parent, String addAttributeLocation, int index)
  {
    super(label);
    if (parent instanceof XSDComplexTypeDefinition)
    {
      this.xsdComplexTypeDefinition = (XSDComplexTypeDefinition) parent;
    }
    else
    {
      this.parent = parent;
    }
    this.addAttributeLocation = addAttributeLocation;
    this.index = index;
  }
  
  protected int getInsertionIndex()
  {
    if (index < 0)
      return -1;

    if (addAttributeLocation.equals(org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction.BEFORE_SELECTED_ID))
    {
      return index;
    }
    else if (addAttributeLocation.equals(org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction.AFTER_SELECTED_ID))
    {
      index++;
      return index;
    }
    else
    {
      return -1;
    }
  }
  
  public void execute()
  {
    XSDAttributeDeclaration attribute = XSDFactory.eINSTANCE.createXSDAttributeDeclaration();
    try
    {
      if (parent == null)
      {
        beginRecording(xsdComplexTypeDefinition.getElement());
        if (!isReference)
        {
          attribute.setName(getNewName(nameToAdd == null ? "NewAttribute" : nameToAdd)); //$NON-NLS-1$
          attribute.setTypeDefinition(xsdComplexTypeDefinition.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string")); //$NON-NLS-1$
        }
        else
        {
          attribute.setResolvedAttributeDeclaration(setGlobalAttributeReference(xsdComplexTypeDefinition.getSchema()));
        }
        XSDAttributeUse attributeUse = XSDFactory.eINSTANCE.createXSDAttributeUse();
        attributeUse.setAttributeDeclaration(attribute);
        attributeUse.setContent(attribute);

        if (xsdComplexTypeDefinition.getAttributeContents() != null)
        {
          index = getInsertionIndex();
          if (index >= 0 && index < xsdComplexTypeDefinition.getAttributeContents().size())
          {
            xsdComplexTypeDefinition.getAttributeContents().add(index, attributeUse);
          }
          else
          {
            xsdComplexTypeDefinition.getAttributeContents().add(attributeUse);
          }
          formatChild(xsdComplexTypeDefinition.getElement());
        }
      }
      else
      {
        beginRecording(parent.getElement());
        if (parent instanceof XSDSchema)
        {
          XSDSchema xsdSchema = (XSDSchema) parent;
          attribute = createGlobalXSDAttributeDeclaration(xsdSchema);
        }
        else if (parent instanceof XSDAttributeGroupDefinition)
        {
          if (!isReference)
          {
            attribute.setTypeDefinition(parent.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string")); //$NON-NLS-1$

            List list = new ArrayList();
            Iterator i = ((XSDAttributeGroupDefinition) parent).getResolvedAttributeGroupDefinition().getAttributeUses().iterator();
            while (i.hasNext())
            {
              XSDAttributeUse use = (XSDAttributeUse) i.next();
              list.add(use.getAttributeDeclaration());
            }
            attribute.setName(XSDCommonUIUtils.createUniqueElementName("NewAttribute", list)); //$NON-NLS-1$
          }
          else
          {
            attribute.setResolvedAttributeDeclaration(setGlobalAttributeReference(parent.getSchema()));
          }

          XSDAttributeUse attributeUse = XSDFactory.eINSTANCE.createXSDAttributeUse();
          attributeUse.setAttributeDeclaration(attribute);
          attributeUse.setContent(attribute);

          index = getInsertionIndex();
          if (index >= 0 && index < (((XSDAttributeGroupDefinition) parent).getResolvedAttributeGroupDefinition().getContents().size()))
          {
            ((XSDAttributeGroupDefinition) parent).getResolvedAttributeGroupDefinition().getContents().add(index, attributeUse);
          }
          else
          {
            ((XSDAttributeGroupDefinition) parent).getResolvedAttributeGroupDefinition().getContents().add(attributeUse);
          }
          formatChild(parent.getElement());
        }
      }
    }
    finally
    {
      endRecording();
    }
    addedXSDConcreteComponent = attribute;
  }

  ArrayList names;

  public void setNameToAdd(String name)
  {
    nameToAdd = name;
  }

  protected String getNewName(String description)
  {
    ArrayList usedAttributeNames = new ArrayList();
    usedAttributeNames.addAll(XSDCommonUIUtils.getAllAttributes(xsdComplexTypeDefinition));
    usedAttributeNames.addAll(XSDCommonUIUtils.getInheritedAttributes(xsdComplexTypeDefinition));
    return XSDCommonUIUtils.createUniqueElementName(description, usedAttributeNames); //$NON-NLS-1$
  }
  
  public void setReference(boolean isReference)
  {
    this.isReference = isReference;
  }
  
  protected XSDAttributeDeclaration createGlobalXSDAttributeDeclaration(XSDSchema xsdSchema)
  {
    ensureSchemaElement(xsdSchema);
    XSDAttributeDeclaration attribute = XSDFactory.eINSTANCE.createXSDAttributeDeclaration();
    attribute.setTypeDefinition(xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("string")); //$NON-NLS-1$
    attribute.setName(XSDCommonUIUtils.createUniqueElementName(nameToAdd == null ? "NewAttribute" : nameToAdd, xsdSchema.getAttributeDeclarations())); //$NON-NLS-1$
    Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
    xsdSchema.getElement().appendChild(textNode);
    xsdSchema.getContents().add(attribute);
    return attribute;
  }

  protected XSDAttributeDeclaration setGlobalAttributeReference(XSDSchema xsdSchema)
  {
    List list = xsdSchema.getAttributeDeclarations();
    XSDAttributeDeclaration referencedAttribute = null;
    boolean isUserDefined = false;
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      Object obj = i.next();
      if (obj instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration attr = (XSDAttributeDeclaration) obj;
        if (!XSDConstants.SCHEMA_INSTANCE_URI_2001.equals(attr.getTargetNamespace()))
        {
          referencedAttribute = attr;
          isUserDefined = true;
          break;
        }
      }
    }
    if (!isUserDefined)
    {
      referencedAttribute = createGlobalXSDAttributeDeclaration(xsdSchema);
      Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
      xsdSchema.getElement().appendChild(textNode);
      xsdSchema.getContents().add(referencedAttribute);
    }

    return referencedAttribute;
  }

}
