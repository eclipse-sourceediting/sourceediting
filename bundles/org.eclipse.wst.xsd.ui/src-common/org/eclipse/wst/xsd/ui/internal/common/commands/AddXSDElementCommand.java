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
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
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
  private String nameToAdd;
  // The index of the currently selected item.  If no item is selected, index will be less than 0
  private int index = -1;  
  // Determines where the element should be inserted based on the currently selected element.  If no
  // element is selected, use the default behaviour of appending the element to the end
  private String addElementLocation; 
  
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
  
  public AddXSDElementCommand(String label, XSDModelGroup xsdModelGroup, String ID, int index)
  {
    super(label);
    this.xsdModelGroup = xsdModelGroup;
    this.index = index;
    this.addElementLocation = ID;
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
  
  public void setNameToAdd(String name)
  {
    nameToAdd = name;
  }
  
  public void setAddElementLocation(String LocationType)
  {
    addElementLocation = LocationType;
  }
  
  protected int getInsertionIndex()
  {
    if (index < 0)
      return -1;

    if (addElementLocation.equals(org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction.BEFORE_SELECTED_ID))
    {
      return index;
    }
    else if (addElementLocation.equals(org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction.AFTER_SELECTED_ID))
    {
      index++;
      return index;
    }
    else
    {
      return -1;
    }
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
      if (xsdSchema != null)
      {
        beginRecording(xsdSchema.getElement());
        XSDElementDeclaration element = createGlobalXSDElementDeclaration();
        Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
        xsdSchema.getElement().appendChild(textNode);
        xsdSchema.getContents().add(element);
        addedXSDConcreteComponent = element;
      }
      else if (xsdModelGroupDefinition != null)
      {
        beginRecording(xsdModelGroupDefinition.getElement());
        if (xsdModelGroup == null)
        {
          XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
          XSDParticle particle = factory.createXSDParticle();
          xsdModelGroup = factory.createXSDModelGroup();
          xsdModelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
          particle.setContent(xsdModelGroup);
          xsdModelGroupDefinition.setModelGroup(xsdModelGroup);
        }
        xsdSchema = xsdModelGroupDefinition.getSchema();
        if (!isReference)
        {
          xsdModelGroup.getContents().add(createXSDElementDeclarationForModelGroupDefinitions());
        }
        else
        {
          xsdModelGroup.getContents().add(createXSDElementReference());
        }
        formatChild(xsdModelGroupDefinition.getElement());
      }
      else if (xsdModelGroup != null && (xsdComplexTypeDefinition == null || xsdModelGroupDefinition == null) )
      {
        xsdSchema = xsdModelGroup.getSchema();
        beginRecording(xsdSchema.getElement());
        if (!isReference)
        {
        	index = getInsertionIndex();
        	if(index >= 0 && index < xsdModelGroup.getContents().size())
        	{        		
        		xsdModelGroup.getContents().add(index,createXSDElementDeclaration());
        	}
        	else
        	{
        		xsdModelGroup.getContents().add(createXSDElementDeclaration());
        	}
        }
        else
        {
          xsdModelGroup.getContents().add(createXSDElementReference());
        }
        formatChild(xsdModelGroup.getElement());
      }
      else
      {
        xsdSchema = xsdComplexTypeDefinition.getSchema();
        beginRecording(xsdSchema.getElement());
        if (xsdModelGroup == null)
        {
          XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
          XSDParticle particle = factory.createXSDParticle();
          xsdModelGroup = factory.createXSDModelGroup();
          xsdModelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
          particle.setContent(xsdModelGroup);
          xsdComplexTypeDefinition.setContent(particle);
        }
        if (!isReference)
        {
          xsdModelGroup.getContents().add(createXSDElementDeclarationForComplexType());
        }
        else
        {
          xsdModelGroup.getContents().add(createXSDElementReference());
        }
        formatChild(xsdComplexTypeDefinition.getElement());
      }
    }
    finally
    {
      endRecording();
    }
  }
  
  protected XSDParticle createXSDElementDeclaration()
  {
    XSDSimpleTypeDefinition type = xsdModelGroup.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string"); //$NON-NLS-1$

    XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();

    XSDConcreteComponent comp = xsdModelGroup.getContainer();
    ArrayList usedAttributeNames = new ArrayList();
    XSDCommonUIUtils.resetVisitedGroupsStack();
    usedAttributeNames.addAll(XSDCommonUIUtils.getChildElements(xsdModelGroup));    
    while (comp != null)
    {
      if (comp instanceof XSDModelGroupDefinition)
      {
        usedAttributeNames.addAll(XSDCommonUIUtils.getAllAttributes((XSDModelGroupDefinition)comp));
        break;
      }
      else if (comp instanceof XSDComplexTypeDefinition)
      {
        usedAttributeNames.addAll(XSDCommonUIUtils.getAllAttributes((XSDComplexTypeDefinition)comp));
        usedAttributeNames.addAll(XSDCommonUIUtils.getInheritedAttributes((XSDComplexTypeDefinition)comp));
        break;
      }
      comp = comp.getContainer();
    }
    element.setName(XSDCommonUIUtils.createUniqueElementName(
    		nameToAdd == null ? "NewElement" : nameToAdd , usedAttributeNames)); //$NON-NLS-1$
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
      Text textNode = xsdSchema.getDocument().createTextNode("\n"); //$NON-NLS-1$
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
    XSDSimpleTypeDefinition type = xsdModelGroup.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string"); //$NON-NLS-1$

    XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();

    ArrayList usedAttributeNames = new ArrayList();
    usedAttributeNames.addAll(XSDCommonUIUtils.getAllAttributes(xsdComplexTypeDefinition));
    usedAttributeNames.addAll(XSDCommonUIUtils.getInheritedAttributes(xsdComplexTypeDefinition));
    element.setName(XSDCommonUIUtils.createUniqueElementName(
    		nameToAdd == null ? "NewElement" : nameToAdd , usedAttributeNames)); //$NON-NLS-1$
    element.setTypeDefinition(type);

    XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
    particle.setContent(element);
    addedXSDConcreteComponent = element;
    return particle;
  }
  
  protected XSDParticle createXSDElementDeclarationForModelGroupDefinitions()
  {
    XSDSimpleTypeDefinition type = xsdModelGroup.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition("string"); //$NON-NLS-1$

    XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();

    ArrayList usedAttributeNames = new ArrayList();
    usedAttributeNames.addAll(XSDCommonUIUtils.getAllAttributes(xsdModelGroupDefinition));
    element.setName(XSDCommonUIUtils.createUniqueElementName(
        nameToAdd == null ? "NewElement" : nameToAdd , usedAttributeNames)); //$NON-NLS-1$
    element.setTypeDefinition(type);

    XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
    particle.setContent(element);
    addedXSDConcreteComponent = element;
    return particle;
  }

  protected XSDElementDeclaration createGlobalXSDElementDeclaration()
  {
    ensureSchemaElement(xsdSchema);
    XSDSimpleTypeDefinition type = xsdSchema.getSchemaForSchema().resolveSimpleTypeDefinition("string"); //$NON-NLS-1$
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDElementDeclaration element = factory.createXSDElementDeclaration();

    element.setName(XSDCommonUIUtils.createUniqueElementName(
    		nameToAdd == null ? "NewElement" : nameToAdd , xsdSchema.getElementDeclarations())); //$NON-NLS-1$
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
    XSDParticle particle = null;

    XSDComplexTypeContent xsdComplexTypeContent = cType.getContent();
    if (xsdComplexTypeContent instanceof XSDParticle)
    {
      particle = (XSDParticle)xsdComplexTypeContent;
    }
    
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

//    if (group.getContents().isEmpty() || group.eResource() != cType.eResource())
//    {
//      if (cType.getBaseType() != null)
//      {
//        XSDComplexTypeContent content = cType.getContent();
//        if (content instanceof XSDParticle)
//        {
//          group = (XSDModelGroup) ((XSDParticle) content).getContent();
//        }
//      }
//    }

    return group;
  }
  
  public XSDConcreteComponent getAddedComponent()
  {
    return super.getAddedComponent();
  }

}
