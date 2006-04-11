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
package org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;

public class RoundedLineBorder extends LineBorder
{
  protected int arcLength;   
  protected int lineStyle = Graphics.LINE_SOLID;

  public RoundedLineBorder(Color c, int width, int arcLength)
  {
    super(c, width);     
    this.arcLength = arcLength;
  }

  public RoundedLineBorder(int width, int arcLength)
  {
    super(width);     
    this.arcLength = arcLength;
  }
  
  public RoundedLineBorder(Color c, int width, int arcLength, int lineStyle)
  {
    super(c, width);
    this.arcLength = arcLength;
    this.lineStyle = lineStyle;
  }

  public RoundedLineBorder(int width, int arcLength, int lineStyle)
  {
    super(width);
    this.arcLength = arcLength;
    this.lineStyle = lineStyle;
  }

  public void paint(IFigure figure, Graphics graphics, Insets insets)
  {
    int rlbWidth = getWidth();
    tempRect.setBounds(getPaintRectangle(figure, insets));
    if (rlbWidth%2 == 1)
    {
      tempRect.width--;
      tempRect.height--;
    }
    tempRect.shrink(rlbWidth/2,rlbWidth/2);
    graphics.setLineWidth(rlbWidth);
    graphics.setLineStyle(lineStyle);
    if (getColor() != null)
      graphics.setForegroundColor(getColor());
    graphics.drawRoundRectangle(tempRect, arcLength, arcLength);
  }
}
