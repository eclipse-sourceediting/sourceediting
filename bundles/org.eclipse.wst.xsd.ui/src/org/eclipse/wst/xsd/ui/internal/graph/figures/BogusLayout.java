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

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;


/**
 * Figures using the StackLayout as their layout manager have
 * their children placed on top of one another. Order of 
 * placement is determined by the order in which the children
 * were added, first child added placed on the bottom.
 */
public class BogusLayout
	extends AbstractLayout
{
                             
protected int spacing;

public BogusLayout(){}

protected Dimension calculatePreferredSize(IFigure figure, int w, int h)
{	                 
  Dimension d = new Dimension();
  return d;
} 

/*
 * Returns the minimum size required by the input container.
 * This is the size of the largest child of the container, as all
 * other children fit into this size.
 */
/*jvh - final
public Dimension getMinimumSize(IFigure figure){
	return new Dimension();
}

public Dimension getPreferredSize(IFigure figure){
	return new Dimension();
}            */

/*
 * Lays out the children on top of each other with
 * their sizes equal to that of the available
 * paintable area of the input container figure.
 */
public void layout(IFigure figure){   
 
} 
 
}