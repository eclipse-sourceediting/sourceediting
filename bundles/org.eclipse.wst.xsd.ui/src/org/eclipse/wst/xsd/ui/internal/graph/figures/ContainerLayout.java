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

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.wst.xsd.ui.internal.gef.util.figures.SpacingFigure;

public class ContainerLayout extends AbstractLayout implements PostLayoutManager
{                                         
  protected boolean isHorizontal;
  protected int spacing = 0;
  protected int border = 0;
 
  public ContainerLayout()
  { 
    this(true, 0); 
  }             

  public ContainerLayout(boolean isHorizontal, int spacing)
  {
    this.isHorizontal = isHorizontal;   
    this.spacing = spacing;
  }  

  public void setHorizontal(boolean isHorizontal)
  {
    this.isHorizontal = isHorizontal;
  }  

  public void setSpacing(int spacing)
  {
    this.spacing = spacing;
  }  

  public void setBorder(int border)
  {
    this.border = border;
  }   

  /**
   * Calculates and returns the preferred size of the container 
   * given as input.
   * 
   * @param figure  Figure whose preferred size is required.
   * @return  The preferred size of the passed Figure.
   */
  protected Dimension calculatePreferredSize(IFigure parent, int w, int h)
  { 
    Dimension preferred = null;                                              
                                  
    // Here we ensure that an unexpanded container is given a size of (0,0)
    //
    if (parent instanceof GraphNodeContainerFigure)
    {
      GraphNodeContainerFigure graphNodeContainerFigure = (GraphNodeContainerFigure)parent;
      if (!graphNodeContainerFigure.isExpanded())
      {
        preferred = new Dimension(); 
      }
    }   
    
    if (preferred == null)
    {
      preferred = new Dimension();
      List children = parent.getChildren();
                                            
      for (int i=0; i < children.size(); i++)
      {
        IFigure child = (IFigure)children.get(i);      
    
        Dimension childSize = child.getPreferredSize();
        /*
        if (child instanceof Interactor)
        {          
          childSize.width = 9;
          childSize.height = 9;
        }*/  
    
        if (isHorizontal)
        {
          preferred.width += childSize.width;
          preferred.height = Math.max(preferred.height, childSize.height);
        }
        else
        {  
          preferred.height += childSize.height;
          preferred.width = Math.max(preferred.width, childSize.width);
        }
      }                                                 

      int childrenSize = children.size();
      if (childrenSize > 1)
      {                      
        if (isHorizontal)    
        {
          preferred.width += spacing * (childrenSize - 1);
        }
        else
        {
          preferred.height += spacing * (childrenSize - 1);
        } 
      }
                            
      preferred.width += border * 2;
      preferred.height += border * 2;
      preferred.width += parent.getInsets().getWidth();
      preferred.height += parent.getInsets().getHeight();      
    }
    return preferred;
  }

                
  protected int alignFigure(IFigure parent, IFigure child)
  {         
    int y = -1;                                                            
    return y;
  }

  public void layout(IFigure parent)
  {                        
    List children = parent.getChildren();
    Dimension preferred = new Dimension();

    int rx = 0;
    for (int i=0; i < children.size(); i++)
    {
      IFigure child = (IFigure)children.get(i);
      Dimension childSize = child.getPreferredSize();
      if (isHorizontal)
      {   
        preferred.height = Math.max(preferred.height, childSize.height);
        rx += childSize.width;
      }
      else
      {
        preferred.width = Math.max(preferred.width, childSize.width);
      }
    }

    if (isHorizontal)
    {
      preferred.height += border*2;
      preferred.width += border;
    }
    else
    {
      preferred.width += border*2;
      preferred.height += border;
    }

    int childrenSize = children.size(); 
    for (int i=0; i < childrenSize; i++)
    {
      IFigure child = (IFigure)children.get(i);
      Dimension childSize = child.getPreferredSize();
      
      if (isHorizontal)
      {   
        int y = alignFigure(parent, child);             

      
        if (y == -1)
        {
           y = (preferred.height - childSize.height) / 2;                                      
        }      

        Rectangle rectangle = new Rectangle(preferred.width, y, childSize.width, childSize.height);   
        rectangle.translate(parent.getClientArea().getLocation());

        child.setBounds(rectangle);         
        preferred.width += childSize.width; 
        preferred.width += spacing;   
        
        if (child instanceof SpacingFigure)
        {          
          int availableHorizontalSpace = parent.getClientArea().width - rx;
          preferred.width += availableHorizontalSpace;
        } 
      }
      else
      {
        Rectangle rectangle = new Rectangle(0, preferred.height, childSize.width, childSize.height);
        rectangle.translate(parent.getClientArea().getLocation());
        child.setBounds(rectangle);  
        preferred.height += childSize.height;
        preferred.height += spacing;
      }
    }     
  }      
  
  public void postLayout(IFigure figure)
  {       
    // This method attempts to align a 'FloatableFigure' (e.g. an Element node) 
    // with any content that it is connected to.  This way a chain of connected figures
    // will be vertically aligned (e.g. element -> modelgroup -> element).  Any visible clipping of the
    // figures in the graph is probably a result of a bug in this code that results in a 'FloatableFigure' being
    // repositioned beyond the bounds of its parent.
    //     
    for (Iterator i = figure.getChildren().iterator(); i.hasNext();)
    {
      IFigure child = (IFigure)i.next();
      if (child instanceof FloatableFigure)       
      {        
        FloatableFigure floatableFigure = (FloatableFigure)child;
        GraphNodeFigure graphNodeFigure = floatableFigure.getGraphNodeFigure(); 
        if (graphNodeFigure.isExpanded() && graphNodeFigure.getChildGraphNodeFigures().size() == 1)
        {
          GraphNodeFigure alignedChild = (GraphNodeFigure)graphNodeFigure.getChildGraphNodeFigures().get(0);      
          Rectangle alignedChildBounds = alignedChild.getConnectionRectangle();                            
          Rectangle childBounds = child.getBounds();
        
          int l = childBounds.y + childBounds.height/2;
          int r = alignedChildBounds.y + alignedChildBounds.height/2;       
          int delta = r - l;

          fixUp(child, delta);
        }          
      }
    }      
  }      
  
  protected void fixUp(IFigure figure, int delta)
  { 
    Rectangle bounds = figure.getBounds();
    bounds.y += delta;
    figure.setBounds(bounds);
      
    for (Iterator i = figure.getChildren().iterator(); i.hasNext(); )
    {
      IFigure child = (IFigure)i.next();
      fixUp(child, delta);
    }                       
  }
}
