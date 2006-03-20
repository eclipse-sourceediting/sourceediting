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
package org.eclipse.wst.xsd.ui.common.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xsd.ui.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;
import org.w3c.dom.Text;

public class AddXSDElementCommand extends BaseCommand
{
  XSDComplexTypeDefinition xsdComplexTypeDefinition;
  XSDModelGroupDefinition xsdModelGroupDefinition;
  XSDModelGroup xsdModelGroup;
  XSDSchema xsdSchema;
  boolean isReference;

  public AddXSDElementCommand()
  {
    super();
  }

  public AddXSDElementCommand(String label, XSDComplexTypeDefinition xsdComplexTypeDefinition)
  {
    super(label);
    this.xsdComplexTypeDefinition = xsdComplexTypeDefinition;
    xsdModelGroup = getModelGroup(xsdComplexTypeDefinition);
  }

  public AddXSDElementCommand(String label, XSDModelGroupDefinition xsdModelGroupDefinition)
  {
    super(label);
    this.xsdModelGroupDefinition = xsdModelGroupDefinition;
    xsdModelGroup = getModelGroup(xsdModelGroupDefinition);
  }
  
  public AddXSDElementCommand(String label, XSDModelGroup xsdModelGroup, XSDComplexTypeDefinition xsdComplexTypeDefinition)
  {
    super(label);
    this.xsdModelGroup = xsdModelGroup;
    this.xsdComplexTypeDefinition = xsdComplexTypeDefinition;
  }

  public AddXSDElementCommand(String label, XSDModelGroup xsdModelGroup)
  {
    super(label);
    this.xsdModelGroup = xsdModelGroup;
  }

  public AddXSDElementCommand(String label, XSDSchema xsdSchema)
  {
    super(label);
    this.xsdSchema = xsdSchema;
  }

  public void setReference(boolean isReference)
  {
    this.isReference = isReference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.gef.commands.Command#execute()
   */
  public void execute()
  {
    if (xsdSchema != null)
    {
      XSDElementDeclaration element = createGlobalXSDElementDeclaration();
      Text textNode = xsdSchema.getDocument().createTextNode("\n");
      xsdSchema.getElement().appendChild(textNode);
      xsdSchema.getContents().add(element);
      addedXSDConcreteComponent = element;
    }
    else if (xsdModelGroupDefinition != null)
    {
      if (xsdModelGroup == null)
      {
        XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
        XSDParticle particle = factory.createXSDParticle();
        xsdModelGroup = factory.createXSDModelGroup();
        xsdModelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
        particle.setContent(xsdModelGroup);
      }
      if (!isReference)
      {
        xsdModelGroup.getContents().add(createXSDElementDeclaration());
      }
      else
      {
        xsdModelGroup.getContents().add(createXSDElementReference());
      }
    }
    else if (xsdComplexTypeDefinition == null && xsdModelGroup != null)
    {
      xsdSchema = xsdModelGroup.getSchema();
      if (!isReference)
      {
        xsdModelGroup.getContents().add(createXSDElementDeclaration());
      }
      else
      {
        xsdModelGroup.getContents().add(createXSDElementReference());
      }
      formatChild(xsdModelGroup.getElement());
    }
    else
    {
      if (xsdModelGroup == null)
      {
        XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
        XSDParticle particle = factory.createXSDParticle();
        xsdModelGroup = factory.createXSDModelGroup();
        xsdModelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
        particle.setContent(xsdModelGroup);
        xsdComplexTypeDefinition.setContent(particle);
      }
      xsdSchema = xsdComplexTypeDefinition.getSchema();
      
      if (!isReference)
      {
        xsdModelGroup.getContents().add(createXSDElementDeclarationForComplexType());
      }
      else
      {
        xsdModelGroup.getContents().add(createXSDElementReference());
      }
      formatChild(xsdModelGroup.getElement());
    }
    
  }
  
  protected XSDParticle createXSDElementDeclaration()
  {
    XSDSimpleTypeDefinition type = xsdModelGroup.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string");

    XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();

    ArrayList usedAttributeNames = new ArrayList();
    usedAttributeNames.addAll(XSDCommonUIUtils.getChildElements(xsdModelGroup));
    element.setName(XSDCommonUIUtils.createUniqueElementName("NewElement", usedAttributeNames));
    element.setTypeDefinition(type);

    XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
    particle.setContent(element);
    addedXSDConcreteComponent = element;
    return particle;
  }

  protected XSDParticle createXSDElementReference()
  {
    List list = xsdModelGroup.getSchema().getElementDeclarations();
    XSDElementDeclaration referencedElement = null;
    if (list.size() > 0)
    {
      referencedElement = (XSDElementDeclaration)list.get(0);
    }
    else
    {
      referencedElement = createGlobalXSDElementDeclaration();
      Text textNode = xsdSchema.getDocument().createTextNode("\n");
      xsdSchema.getElement().appendChild(textNode);
      xsdSchema.getContents().add(referencedElement);
    }

    XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();
    
    element.setResolvedElementDeclaration(referencedElement);
    XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
    particle.setContent(element);
    addedXSDConcreteComponent = element;
    return particle;
  }

  protected XSDParticle createXSDElementDeclarationForComplexType()
  {
    XSDSimpleTypeDefinition type = xsdModelGroup.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string");

    XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();

    ArrayList usedAttributeNames = new ArrayList();
    usedAttributeNames.addAll(XSDCommonUIUtils.getAllAttributes(xsdComplexTypeDefinition));
    usedAttributeNames.addAll(XSDCommonUIUtils.getInheritedAttributes(xsdComplexTypeDefinition));
    element.setName(XSDCommonUIUtils.createUniqueElementName("NewElement", usedAttributeNames));
    element.setTypeDefinition(type);

    XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
    particle.setContent(element);
    addedXSDConcreteComponent = element;
    return particle;
  }

  protected XSDElementDeclaration createGlobalXSDElementDeclaration()
  {
    XSDSimpleTypeDefinition type = xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("string");
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDElementDeclaration element = factory.createXSDElementDeclaration();

    element.setName(XSDCommonUIUtils.createUniqueElementName("NewElement", xsdSchema.getElementDeclarations()));
    element.setTypeDefinition(type);

    return element;
  }
  
  public XSDModelGroup getModelGroup(XSDModelGroupDefinition modelGroupDef)
  {
    return modelGroupDef.getModelGroup();
  }

  //PORT
  public XSDModelGroup getModelGroup(XSDComplexTypeDefinition cType)
  {
    XSDParticle particle = cType.getComplexType();

    if (particle == null)
    {
      return null;
    }
    
    Object particleContent = particle.getContent();
    XSDModelGroup group = null;

    if (particleContent instanceof XSDModelGroupDefinition)
    {
      group = ((XSDModelGroupDefinition) particleContent).getResolvedModelGroupDefinition().getModelGroup();
    }
    else if (particleContent instanceof XSDModelGroup)
    {
      group = (XSDModelGroup) particleContent;
    }

    if (group == null)
    {
      return null;
    }

    if (group.getContents().isEmpty() || group.eResource() != cType.eResource())
    {
      if (cType.getBaseType() != null)
      {
        XSDComplexTypeContent content = cType.getContent();
        if (content instanceof XSDParticle)
        {
          group = (XSDModelGroup) ((XSDParticle) content).getContent();
        }
      }
    }

    return group;
  }
  
  public XSDConcreteComponent getAddedComponent()
  {
    return super.getAddedComponent();
  }

}
