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
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.w3c.dom.Node;

public class MoveXSDElementAction extends MoveXSDBaseAction
{
  private static int INSERT_BEFORE = 0;
  private static int INSERT_AFTER = 1;
  private static int INSERT_DIRECT = 2;
  protected List selectedNodes;
  protected Node parentNode;
  protected Node previousRefChild, nextRefChild;
  int insertType;

  XSDModelGroup parentModelGroup;
  XSDConcreteComponent selected, previousRefComponent, nextRefComponent;
  boolean insertAtEnd = true;

  public MoveXSDElementAction(XSDModelGroup parentComponent, XSDConcreteComponent selected, XSDConcreteComponent previousRefChildComponent, XSDConcreteComponent nextRefChildComponent)
  {
    super();
    this.parentModelGroup = parentComponent;
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
      if (nextRefComponent.getContainer().getContainer() == parentModelGroup)
      {
        insertType = INSERT_BEFORE;
      }
    }
    if (previousRefComponent != null)
    {
      if (previousRefComponent.getContainer().getContainer() == parentModelGroup)
      {
        insertType = INSERT_AFTER;
      }
    }
    if (nextRefChildComponent == null && previousRefChildComponent == null)
    {
      insertType = INSERT_DIRECT;
    }
  }

  public MoveXSDElementAction(XSDModelGroup parentComponent, XSDConcreteComponent selected, XSDConcreteComponent previousRefChildComponent, XSDConcreteComponent nextRefChildComponent, boolean insertAtEnd)
  {
    this(parentComponent, selected, previousRefChildComponent, nextRefChildComponent);
    this.insertAtEnd = insertAtEnd;
  }
  
  public boolean canMove()
  {
    boolean result = true;
   
    if (nextRefComponent instanceof XSDAttributeDeclaration || previousRefComponent instanceof XSDAttributeDeclaration || parentModelGroup == null)
      return false;

    return result;
  }

  /*
   * @see IAction#run()
   */
  public void run()
  {
    int originalIndex = 0;
    for (Iterator particles = parentModelGroup.getContents().iterator(); particles.hasNext();)
    {
      XSDParticle particle = (XSDParticle) particles.next();
      XSDParticleContent particleContent = particle.getContent();
      if (particleContent == selected)
      {
        parentModelGroup.getContents().remove(particle);
        break;
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
        XSDConcreteComponent container2 = container.getContainer();
        if (container2 instanceof XSDModelGroup)
        {
          ((XSDModelGroup) container2).getContents().remove(container);
        }
        if (insertAtEnd)
          parentModelGroup.getContents().add(container);
        else
          parentModelGroup.getContents().add(0, container);
        addedBack = true;
      }
      return;
    }

    List particles = parentModelGroup.getContents();
    for (Iterator iterator = particles.iterator(); iterator.hasNext();)
    {
      XSDParticle particle = (XSDParticle) iterator.next();
      XSDParticleContent particleContent = particle.getContent();
      if (insertType == INSERT_BEFORE)
      {
        if (particleContent == nextRefComponent)
        {
          parentModelGroup.getContents().add(index, selected.getContainer());
          addedBack = true;
          break;
        }
        if (selected == nextRefComponent && originalIndex == index)
        {
          parentModelGroup.getContents().add(index, selected.getContainer());
          addedBack = true;
          break;
        }
      }
      else if (insertType == INSERT_AFTER)
      {
        if (particleContent == previousRefComponent)
        {
          parentModelGroup.getContents().add(index + 1, selected.getContainer());
          addedBack = true;
          break;
        }
        if (selected == previousRefComponent && originalIndex == index)
        {
          parentModelGroup.getContents().add(index, selected.getContainer());
          addedBack = true;
          break;
        }
      }
      index++;
    }
    if (particles.size() == 0)
    {
      parentModelGroup.getContents().add(selected.getContainer());
      addedBack = true;
    }

    if (!addedBack)
    {
      parentModelGroup.getContents().add(originalIndex, selected.getContainer());
    }
  }
}
