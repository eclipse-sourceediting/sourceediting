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


/**
 * Figures using the StackLayout as their layout manager have
 * their children placed on top of one another. Order of 
 * placement is determined by the order in which the children
 * were added, first child added placed on the bottom.
 */
public class FillLayout extends AbstractLayout
{
         
protected boolean isHorizontal = false;                    
protected int spacing = 0;
public Dimension min;  

public FillLayout(){}     
public FillLayout(boolean dummy){} 

public FillLayout(int spacing)
{ 
  this.spacing = spacing; 
}

public void setHorizontal(boolean isHorizontal)
{
 this.isHorizontal = isHorizontal;
}

/**
 * Calculates and returns the preferred size of the input container.
 * This is the size of the largest child of the container, as all
 * other children fit into this size.
 *
 * @param figure  Container figure for which preferred size is required.
 * @return  The preferred size of the input figure.
 * @since 2.0
 */
protected Dimension calculatePreferredSize(IFigure figure, int w, int h)
{	                 
  Dimension d = calculatePreferredClientAreaSize(figure);
	d.expand(figure.getInsets().getWidth(),
	         figure.getInsets().getHeight());
	//d.union(getBorderPreferredSize(figure));
	return d;
} 

protected Dimension calculatePreferredClientAreaSize(IFigure figure)
{
  Dimension d = new Dimension();
	List children = figure.getChildren(); 


	for (Iterator i = children.iterator(); i.hasNext(); )
  {
		IFigure child = (IFigure)i.next();
    Dimension childSize = child.getPreferredSize();

    if (isHorizontal)
    {
      d.width += childSize.width;
      d.height = Math.max(childSize.height, d.height);
    }
    else
    {
      d.height += childSize.height;
      d.width = Math.max(childSize.width, d.width);
    }
	}	 

                   
  int childrenSize = children.size();       
  if (childrenSize > 0)
  {
    if (isHorizontal)
      d.width += spacing * (childrenSize - 1); 
    else
      d.height += spacing * (childrenSize - 1);
  }       
    
  if (min != null)
  {
    d.width = Math.max(d.width, min.width);
    d.height = Math.max(d.height, min.height);
  }
  return d;
}

/*
 * Returns the minimum size required by the input container.
 * This is the size of the largest child of the container, as all
 * other children fit into this size.
 */
public Dimension getMinimumSize(IFigure figure, int w, int h)
{
	Dimension d = new Dimension();
	List children = figure.getChildren();
	IFigure child;
	for (int i=0; i < children.size(); i++){
		child = (IFigure)children.get(i);
		d.union(child.getMinimumSize());
	}
	d.expand(figure.getInsets().getWidth(),
	         figure.getInsets().getHeight());
	return d;
}

public Dimension getPreferredSize(IFigure figure, int w, int h)
{
	return calculatePreferredSize(figure, w, h);
}

/*
 * Lays out the children on top of each other with
 * their sizes equal to that of the available
 * paintable area of the input container figure.
 */
public void layout(IFigure figure){   
                     
  Dimension	preferredSize = calculatePreferredClientAreaSize(figure);
	Rectangle r = figure.getClientArea().getCopy(); 

	List children = figure.getChildren();
                                        
	for (Iterator i = children.iterator(); i.hasNext(); )
  {
		IFigure child = (IFigure)i.next();
    Dimension childSize = child.getPreferredSize();

    if (isHorizontal)
    {
		  child.setBounds(new Rectangle(r.x, r.y, childSize.width, Math.max(preferredSize.height, r.height)));
      r.x += childSize.width + spacing;
    }
    else
    {            
		  child.setBounds(new Rectangle(r.x, r.y, Math.max(preferredSize.width, r.width), childSize.height));
      r.y += childSize.height + spacing;
    }
	}
}
} 
 