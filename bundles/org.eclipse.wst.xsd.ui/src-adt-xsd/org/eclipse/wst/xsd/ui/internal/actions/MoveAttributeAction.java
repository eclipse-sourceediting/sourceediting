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
package org.eclipse.wst.xsd.ui.internal.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.w3c.dom.Node;

// TODO COMMON AND CLEAN UP THIS CODE
public class MoveAttributeAction extends Action
{

  protected List selectedNodes;
  protected Node parentNode;
  protected Node previousRefChild, nextRefChild;
  boolean doInsertBefore;

  List selectedComponentsList;
  XSDConcreteComponent parentComponent;
  XSDConcreteComponent previousRefComponent, nextRefComponent;

  public MoveAttributeAction(XSDConcreteComponent parentComponent, List selectedComponents, XSDConcreteComponent previousRefChildComponent, XSDConcreteComponent nextRefChildComponent)
  {
    this.parentComponent = parentComponent;
    this.selectedComponentsList = selectedComponents;
    this.previousRefComponent = previousRefChildComponent;
    this.nextRefComponent = nextRefChildComponent;

    selectedNodes = new ArrayList(selectedComponents.size());
    for (Iterator i = selectedComponents.iterator(); i.hasNext();)
    {
      XSDConcreteComponent concreteComponent = (XSDConcreteComponent) i.next();
      selectedNodes.add(concreteComponent.getElement());
    }
    parentNode = parentComponent.getElement();
    nextRefChild = nextRefChildComponent != null ? nextRefChildComponent.getElement() : null;
    previousRefChild = previousRefChildComponent != null ? previousRefChildComponent.getElement() : null;

    doInsertBefore = (nextRefChild != null);

    if (nextRefComponent != null)
    {
      if (nextRefComponent.getContainer().getContainer() == parentComponent)
      {
        doInsertBefore = true;
      }
    }
    if (previousRefComponent != null)
    {
      if (previousRefComponent.getContainer().getContainer() == parentComponent)
      {
        doInsertBefore = false;
      }
    }

  }

  public boolean canMove()
  {
    boolean result = true;

    if (nextRefComponent instanceof XSDElementDeclaration || previousRefComponent instanceof XSDElementDeclaration)
      return false;

    return result;
  }

  /*
   * @see IAction#run()
   */
  public void run()
  {
    if (parentComponent instanceof XSDAttributeGroupDefinition)
    {
      moveUnderXSDAttributeGroupDefinition((XSDAttributeGroupDefinition) parentComponent);
    }
    else if (parentComponent instanceof XSDComplexTypeDefinition)
    {
      moveUnderXSDComplexTypeDefinition((XSDComplexTypeDefinition) parentComponent);
    }
  }

  protected void moveUnderXSDAttributeGroupDefinition(XSDAttributeGroupDefinition parentModelGroup)
  {
    try
    {
      for (Iterator i = selectedComponentsList.iterator(); i.hasNext();)
      {
        XSDConcreteComponent concreteComponent = (XSDConcreteComponent) i.next();

        if (doInsertBefore)
        {
          if (concreteComponent == nextRefComponent)
            continue;
        }
        else
        {
          if (concreteComponent == previousRefComponent)
            continue;
        }

        for (Iterator iterator = parentModelGroup.getContents().iterator(); iterator.hasNext();)
        {
          XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
          if (attributeGroupContent instanceof XSDAttributeUse)
          {
            XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
            if (attribute == concreteComponent)
            {
              parentModelGroup.getContents().remove(attribute.getContainer());
              break;
            }
          }
        }
        int index = 0;
        List attributeGroupContents = parentModelGroup.getContents();
        for (Iterator iterator = attributeGroupContents.iterator(); iterator.hasNext();)
        {
          XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
          if (attributeGroupContent instanceof XSDAttributeUse)
          {
            XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
            if (doInsertBefore)
            {
              if (attribute == nextRefComponent)
              {
                parentModelGroup.getContents().add(index, concreteComponent.getContainer());
                break;
              }
            }
            else
            {
              if (attribute == previousRefComponent)
              {
                parentModelGroup.getContents().add(index + 1, concreteComponent.getContainer());
                break;
              }
            }
          }
          index++;
        }
        if (attributeGroupContents.size() == 0)
        {
          parentModelGroup.getContents().add(concreteComponent.getContainer());
        }

      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  protected void moveUnderXSDComplexTypeDefinition(XSDComplexTypeDefinition complexType)
  {
    try
    {
      for (Iterator i = selectedComponentsList.iterator(); i.hasNext();)
      {
        XSDConcreteComponent concreteComponent = (XSDConcreteComponent) i.next();

        if (doInsertBefore)
        {
          if (concreteComponent == nextRefComponent)
            continue;
        }
        else
        {
          if (concreteComponent == previousRefComponent)
            continue;
        }

        for (Iterator iterator = complexType.getAttributeContents().iterator(); iterator.hasNext();)
        {
          XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
          if (attributeGroupContent instanceof XSDAttributeUse)
          {
            XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
            if (attribute == concreteComponent)
            {
              complexType.getAttributeContents().remove(attribute.getContainer());
              break;
            }
          }
        }
        int index = 0;
        List attributeGroupContents = complexType.getAttributeContents();
        for (Iterator iterator = attributeGroupContents.iterator(); iterator.hasNext();)
        {
          XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
          if (attributeGroupContent instanceof XSDAttributeUse)
          {
            XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
            if (doInsertBefore)
            {
              if (attribute == nextRefComponent)
              {
                complexType.getAttributeContents().add(index, concreteComponent.getContainer());
                break;
              }
            }
            else
            {
              if (attribute == previousRefComponent)
              {
                complexType.getAttributeContents().add(index + 1, concreteComponent.getContainer());
                break;
              }
            }
          }
          index++;
        }
        if (attributeGroupContents.size() == 0)
        {
          complexType.getAttributeContents().add(concreteComponent.getContainer());
        }

      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

  }
}
