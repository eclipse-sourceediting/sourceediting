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
package org.eclipse.wst.xsd.ui.internal.design.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

public class HyperLinkLabel extends Label
{
  protected void paintFigure(Graphics graphics)
  {
    super.paintFigure(graphics);   
    graphics.setFont(getFont());
        
    // TODO (cs) this lookup to find " :" is a hack
    // that's specialized for element and type label text
    // we need to make the TopLevelComponent use two labels in this case
    //    
    String string = getText();
    int index = string.indexOf(" :");
    if (index != -1)
    {
      string = string.substring(0, index);
    }
    // end hack
    
    Point p = getTextLocation();      
    Dimension textSize =  FigureUtilities.getTextExtents(string, getFont());
    int textWidth = textSize.width;
    int textHeight = textSize.height;
    int descent = graphics.getFontMetrics().getDescent();
    int lineY = bounds.y + p.y + textHeight - descent + 1;      
    int lineX = bounds.x + p.x;
    graphics.drawLine(lineX, lineY, lineX + textWidth, lineY); 
  }
}
