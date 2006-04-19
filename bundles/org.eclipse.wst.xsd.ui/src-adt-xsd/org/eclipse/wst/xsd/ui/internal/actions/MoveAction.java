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
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.w3c.dom.Node;

public class MoveAction extends Action
{
  protected List selectedNodes;
  protected Node parentNode;
  protected Node previousRefChild, nextRefChild;
  boolean doInsertBefore;

  List selectedComponentsList;
  XSDModelGroup parentModelGroup;
  XSDConcreteComponent previousRefComponent, nextRefComponent;

  public MoveAction(XSDModelGroup parentComponent, List selectedComponents, XSDConcreteComponent previousRefChildComponent, XSDConcreteComponent nextRefChildComponent)
  {
    this.parentModelGroup = parentComponent;
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
      if (nextRefComponent.getContainer().getContainer() == parentModelGroup)
      {
        doInsertBefore = true;
      }
    }
    if (previousRefComponent != null)
    {
      if (previousRefComponent.getContainer().getContainer() == parentModelGroup)
      {
        doInsertBefore = false;
      }
    }
  }

  public boolean canMove()
  {
    boolean result = true;

    if (nextRefComponent instanceof XSDAttributeDeclaration || previousRefComponent instanceof XSDAttributeDeclaration)
      return false;

    return result;
  }

  /*
   * @see IAction#run()
   */
  public void run()
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

        for (Iterator particles = parentModelGroup.getContents().iterator(); particles.hasNext();)
        {
          XSDParticle particle = (XSDParticle) particles.next();
          XSDParticleContent particleContent = particle.getContent();
          if (particleContent == concreteComponent)
          {
            parentModelGroup.getContents().remove(particle);
            break;
          }
        }
        int index = 0;
        List particles = parentModelGroup.getContents();
        for (Iterator iterator = particles.iterator(); iterator.hasNext();)
        {
          XSDParticle particle = (XSDParticle) iterator.next();
          XSDParticleContent particleContent = particle.getContent();
          if (doInsertBefore)
          {
            if (particleContent == nextRefComponent)
            {
              parentModelGroup.getContents().add(index, concreteComponent.getContainer());
              break;
            }
          }
          else
          {
            if (particleContent == previousRefComponent)
            {
              parentModelGroup.getContents().add(index + 1, concreteComponent.getContainer());
              break;
            }
          }
          index++;
        }
        if (particles.size() == 0)
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
}
