/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.w3c.dom.Node;

public class MoveXSDAttributeAction extends MoveXSDBaseAction
{
  private static int INSERT_BEFORE = 0;
  private static int INSERT_AFTER = 1;
  private static int INSERT_DIRECT = 2;
  protected List selectedNodes;
  protected Node parentNode;
  protected Node previousRefChild, nextRefChild;
  int insertType;

  XSDConcreteComponent parentComponent;
  XSDConcreteComponent selected, previousRefComponent, nextRefComponent;
  boolean insertAtEnd = true;
  
  public MoveXSDAttributeAction(XSDConcreteComponent parentComponent, XSDConcreteComponent selected, XSDConcreteComponent previousRefChildComponent, XSDConcreteComponent nextRefChildComponent)
  {
    super();
    this.parentComponent = parentComponent;
    this.selected = selected;
    this.previousRefComponent = previousRefChildComponent;
    this.nextRefComponent = nextRefChildComponent;

    if (parentComponent == null)
      return;
    parentNode = parentComponent.getElement();
    nextRefChild = nextRefChildComponent != null ? nextRefChildComponent.getElement() : null;
    previousRefChild = previousRefChildComponent != null ? previousRefChildComponent.getElement() : null;

    if (nextRefComponent != null)
    {
      if (nextRefComponent.getContainer().getContainer() == parentComponent)
      {
        insertType = INSERT_BEFORE;
      }
    }
    if (previousRefComponent != null)
    {
      if (previousRefComponent.getContainer().getContainer() == parentComponent)
      {
        insertType = INSERT_AFTER;
      }
    }
    if (nextRefChildComponent == null && previousRefChildComponent == null)
    {
      insertType = INSERT_DIRECT;
    }
  }
  
  public MoveXSDAttributeAction(XSDConcreteComponent parentComponent, XSDConcreteComponent selected, XSDConcreteComponent previousRefChildComponent, XSDConcreteComponent nextRefChildComponent, boolean insertAtEnd)
  {
    this(parentComponent, selected, previousRefChildComponent, nextRefChildComponent);
    this.insertAtEnd = insertAtEnd;
  }
  
  public boolean canMove()
  {
    boolean result = true;
   
    if (nextRefComponent instanceof XSDElementDeclaration || previousRefComponent instanceof XSDElementDeclaration || parentComponent == null)
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

  protected void moveUnderXSDAttributeGroupDefinition(XSDAttributeGroupDefinition parentGroup)
  {
    int originalIndex = 0;
    for (Iterator iterator = parentGroup.getContents().iterator(); iterator.hasNext();)
    {
      XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
      if (attributeGroupContent instanceof XSDAttributeUse)
      {
        XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
        if (attribute == selected)
        {
          parentGroup.getContents().remove(attribute.getContainer());
          break;
        }
      }
      originalIndex++;
    }
    int index = 0;
    boolean addedBack = false;
    if (insertType == INSERT_DIRECT)
    {
      XSDConcreteComponent container = selected.getContainer();
      if (container != null)
      {
        if (insertAtEnd)
          ((XSDAttributeGroupDefinition) parentComponent).getResolvedAttributeGroupDefinition().getContents().add(container);
        else
          ((XSDAttributeGroupDefinition) parentComponent).getResolvedAttributeGroupDefinition().getContents().add(0, container);
        addedBack = true;
      }
      return;
    }

    List attributeGroupContents = parentGroup.getContents();
    for (Iterator iterator = attributeGroupContents.iterator(); iterator.hasNext();)
    {
      XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
      if (attributeGroupContent instanceof XSDAttributeUse)
      {
        XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
        if (insertType == INSERT_BEFORE)
        {
          if (attribute == nextRefComponent)
          {
            parentGroup.getContents().add(index, selected.getContainer());
            addedBack = true;
            break;
          }
          if (selected == nextRefComponent && originalIndex == index)
          {
            parentGroup.getContents().add(index, selected.getContainer());
            addedBack = true;
            break;
          }
        }
        else if (insertType == INSERT_AFTER)
        {
          if (attribute == previousRefComponent)
          {
            parentGroup.getContents().add(index + 1, selected.getContainer());
            addedBack = true;
            break;
          }
          if (selected == previousRefComponent && originalIndex == index)
          {
            parentGroup.getContents().add(index, selected.getContainer());
            addedBack = true;
            break;
          }
        }
      }
      index++;
    }
    if (attributeGroupContents.size() == 0)
    {
      parentGroup.getContents().add(selected.getContainer());
      addedBack = true;
    }

    if (!addedBack)
    {
      parentGroup.getContents().add(originalIndex, selected.getContainer());
    }
  }

  protected void moveUnderXSDComplexTypeDefinition(XSDComplexTypeDefinition complexType)
  {
    int originalIndex = 0;
    for (Iterator iterator = complexType.getAttributeContents().iterator(); iterator.hasNext();)
    {
      XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
      if (attributeGroupContent instanceof XSDAttributeUse)
      {
        XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
        if (attribute == selected)
        {
          complexType.getAttributeContents().remove(attribute.getContainer());
          break;
        }
      }
      originalIndex++;
    }
    int index = 0;
    boolean addedBack = false;
    List attributeGroupContents = complexType.getAttributeContents();
    for (Iterator iterator = attributeGroupContents.iterator(); iterator.hasNext();)
    {
      XSDAttributeGroupContent attributeGroupContent = (XSDAttributeGroupContent) iterator.next();
      if (attributeGroupContent instanceof XSDAttributeUse)
      {
        XSDAttributeDeclaration attribute = ((XSDAttributeUse) attributeGroupContent).getContent();
        if (insertType == INSERT_AFTER)
        {
          if (attribute == previousRefComponent)
          {
            complexType.getAttributeContents().add(index + 1, selected.getContainer());
            addedBack = true;
            break;
          }
        }
        else if (insertType == INSERT_BEFORE)
        {
          if (attribute == nextRefComponent)
          {
            complexType.getAttributeContents().add(index, selected.getContainer());
            addedBack = true;
            break;
          }
        }
      }
      index++;
    }
    if (attributeGroupContents.size() == 0)
    {
      complexType.getAttributeContents().add(selected.getContainer());
      addedBack = true;
    }

    if (!addedBack)
    {
      complexType.getAttributeContents().add(originalIndex, selected.getContainer());
    }
  }

}
