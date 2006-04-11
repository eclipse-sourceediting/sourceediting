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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class HeadingFigure extends Figure
{
  public static final Color headerColor = new Color(null, 224, 233, 246);
  Label label;
  Color[] gradientColor = {ColorConstants.white,  
                           ColorConstants.lightGray,
                           ColorConstants.lightBlue,
                           ColorConstants.gray};
  boolean isSelected = false;
  boolean isReadOnly = false;
  
  public HeadingFigure()
  {
    label = new Label();
    label.setBorder(new MarginBorder(2));
    ToolbarLayout toolbarLayout = new ToolbarLayout(false);
    toolbarLayout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
    setLayoutManager(toolbarLayout);
    add(label);
  }
  
  public void setGradientColors(Color[] colors)
  {
    this.gradientColor = colors;
  }
  
  public void setSelected(boolean isSelected)
  {
    this.isSelected = isSelected;
  }

  public void setIsReadOnly(boolean isReadOnly)
  {
    this.isReadOnly = isReadOnly;
  }
  
  public void paint(Graphics graphics)
  {
    super.paint(graphics);
    
    graphics.pushState();
    try
    {
      // Fill for the header section
      //
      Rectangle r = getBounds().getCopy();
      graphics.setBackgroundColor(ColorConstants.lightGray);
  
      Color gradient1 = isReadOnly ? gradientColor[1] : headerColor;
      if (isSelected && isReadOnly) gradient1 = gradientColor[3];
      else if (isSelected && !isReadOnly) gradient1 = gradientColor[2];
      Color gradient2 = gradientColor[0];
      graphics.setForegroundColor(gradient1);
      graphics.setBackgroundColor(gradient2);
      Rectangle labelBounds = label.getBounds();
      graphics.fillGradient(r.x+1, r.y+1, r.width-2, labelBounds.height - 2, true);    
      graphics.setForegroundColor(ColorConstants.darkGray);
      label.paint(graphics);    
    }
    finally
    {
      graphics.popState();
    }
  }

  public Label getLabel()
  {
    return label;
  }  
}
