/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.graph.figures;
            
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
              

//  ------------------------------
//  | GraphNodeFigure            |
//  |                            |
//  | -------------------------  |
//  | | vertical group        |  |
//  | | --------------------- |  |
//  | | | outlined area     | |  |
//  | | | ----------------- | |  |
//  | | | | icon area     | | |  |
//  | | | ----------------- | |  |
//  | | | ----------------- | |  |
//  | | | | inner content | | |  |
//  | | | ----------------- | |  |
//  | | --------------------- |  |
//  | -------------------------  |
//  ------------------------------

public class GraphNodeFigure extends ContainerFigure
{                        
  protected ContainerFigure verticalGroup; 
  protected ContainerFigure outlinedArea;
  protected ContainerFigure iconArea;
  protected ContainerFigure innerContentArea; 
         
  protected boolean isConnected;
  protected boolean isIsolated;

  protected List childGraphNodeFigures = new ArrayList();
  protected GraphNodeFigure parentGraphNodeFigure; 

  public GraphNodeFigure()
  { 
    isIsolated = false;
    createFigure();
  }      

  public boolean isExpanded()
  {
    return true;
  }    

  public boolean isConnected()
  {
    return isConnected;
  }                    

  public void setConnected(boolean isConnected)
  {
    this.isConnected = isConnected;
  }

  public void setIsIsolated(boolean isIsolated)
  {
    this.isIsolated = isIsolated; 
  }

  public boolean getIsIsolated()
  {
    return isIsolated; 
  }

  protected void createFigure()
  {
    createVerticalGroup(this);
    createOutlinedArea(verticalGroup); 
  }

  protected void createVerticalGroup(IFigure parent)
  {
    verticalGroup = new ContainerFigure();
    verticalGroup.getContainerLayout().setHorizontal(false);
    parent.add(verticalGroup);
  }

  protected void createOutlinedArea(IFigure parent)
  { 
    outlinedArea = new ContainerFigure();
    outlinedArea.getContainerLayout().setHorizontal(false);
    parent.add(outlinedArea);      
     
    iconArea = new ContainerFigure();
    outlinedArea.add(iconArea);

    innerContentArea = new ContainerFigure();
    innerContentArea.getContainerLayout().setHorizontal(false);
    outlinedArea.add(innerContentArea);  
  }   

  public ContainerFigure getIconArea()
  {
    return iconArea;
  }

  public ContainerFigure getOutlinedArea()
  {
    return outlinedArea;
  }

  public ContainerFigure getInnerContentArea()
  {
    return innerContentArea;
  } 

  public IFigure getConnectionFigure()
  {
    return outlinedArea;
  }

  public Rectangle getConnectionRectangle()
  {
    return outlinedArea.getBounds();
  }

  public List getChildGraphNodeFigures()
  {
    return childGraphNodeFigures;
  }

  public void addNotify()                     
  {
    super.addNotify();                 
    if (isConnected())
    {
      parentGraphNodeFigure = computeParentGraphNodeFigure(this);
      if (parentGraphNodeFigure != null)
      {   
        parentGraphNodeFigure.getChildGraphNodeFigures().add(this);
      }   
    }
  } 

  public void removeNotify()
  {     
    super.removeNotify();
    if (parentGraphNodeFigure != null)
    {   
      parentGraphNodeFigure.getChildGraphNodeFigures().remove(this);
    }  
  }

  public GraphNodeFigure getParentGraphNodeFigure()
  {
    return parentGraphNodeFigure;
  }  

  public GraphNodeFigure computeParentGraphNodeFigure(IFigure figure)
  {                   
    GraphNodeFigure result = null;
    IFigure parent = figure != null ? figure.getParent() : null;
    while (parent != null)
    {
      if (parent instanceof GraphNodeFigure)
      {    
        GraphNodeFigure parentGraphNodeFigure = (GraphNodeFigure)parent;
        if (parentGraphNodeFigure.isConnected())
        {
          result = (GraphNodeFigure)parent;
          break;
        }
      }
      parent = parent.getParent();
    }   
    return result;
  }
}
