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
public class CenterLayout
	extends AbstractLayout
{
                             
protected int spacing;

public CenterLayout(){}
public CenterLayout(int spacing){ this.spacing = spacing; }

/**
 * Calculates and returns the preferred size of the input container.
 * This is the size of the largest child of the container, as all
 * other children fit into this size.
 *
 * @param figure  Container figure for which preferred size is required.
 * @return  The preferred size of the input figure.
 * @since 2.0
 */
protected Dimension calculatePreferredSize(IFigure figure, int w, int h){
	Dimension d = calculatePreferredClientAreaSize(figure);
	/*d.expand(figure.getInsets().getWidth(),
	         figure.getInsets().getHeight()); */
        d.expand(w,
	         h);
	d.union(getBorderPreferredSize(figure));
	return d;
}

protected Dimension calculatePreferredClientAreaSize(IFigure figure){
	Dimension d = new Dimension();
	List children = figure.getChildren();
	for (Iterator i = children.iterator(); i.hasNext(); )
  {
		IFigure child = (IFigure)i.next();
    Dimension childSize = child.getPreferredSize();
    d.height += childSize.height;
    d.width = Math.max(childSize.width, d.width);
	}	                    
  int childrenSize = children.size();       
  if (childrenSize > 0)
  {
    d.height += spacing * children.size() - 1;
  }
	return d;
}

/*
 * Returns the minimum size required by the input container.
 * This is the size of the largest child of the container, as all
 * other children fit into this size.
 */
public Dimension getMinimumSize(IFigure figure, int wHint, int hHint){
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

public Dimension getPreferredSize(IFigure figure, int wHint, int hHint){
	return calculatePreferredSize(figure, wHint, hHint);
}


/*
 * Lays out the children on top of each other with
 * their sizes equal to that of the available
 * paintable area of the input container figure.
 */
public void layout(IFigure figure){
	Rectangle r = figure.getClientArea();
	List children = figure.getChildren();
  
  Dimension preferredClientAreaSize = calculatePreferredClientAreaSize(figure);
  int x = r.x + (r.width - preferredClientAreaSize.width) / 2;
  int y = r.y + (r.height - preferredClientAreaSize.height) / 2;

	for (Iterator i = children.iterator(); i.hasNext(); )
  {
		IFigure child = (IFigure)i.next();
    Dimension childSize = child.getPreferredSize();
		child.setBounds(new Rectangle(x, y, childSize.width, childSize.height));
    y += childSize.height + spacing;
	}
}
}