/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures;

import java.util.Iterator;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.layouts.ColumnData;

public class BoxFigure extends Figure
{
  protected ColumnData columnData = new ColumnData();  
  public HeadingFigure headingFigure;
  protected Figure contentPane;
  
  public boolean isSelected = false;

  public BoxFigure()
  {
    super();
 
    boolean highContrast = false;
    try
    {
      highContrast = Display.getDefault().getHighContrast();
    }
    catch (Exception e)
    {
    }
    if (!highContrast)
    {
      setBackgroundColor(ColorConstants.white);
    }
    
    headingFigure = new HeadingFigure();   
    add(headingFigure);

    contentPane = new Figure()
    {
      public void paint(Graphics graphics)
      {
        graphics.fillRectangle(getBounds());
        super.paint(graphics);
        boolean isFirst = false;
        for (Iterator i = getChildren().iterator(); i.hasNext();)
        {
          Figure figure = (Figure) i.next();
          if (isFirst)
          {
            isFirst = false;
          }
          else
          {
            Rectangle r = figure.getBounds();
            graphics.drawLine(r.x, r.y + 1, r.x + r.width, r.y + 1);
          }
        }
      }
    };
    contentPane.setLayoutManager(new ToolbarLayout());
    add(contentPane);
    headingFigure.setForegroundColor(ColorConstants.black); 
  }

  public void paint(Graphics graphics)
  {
    super.paint(graphics);
    /*
    // Fill for the header section
    //
    Rectangle r = getBounds().getCopy();
    graphics.setBackgroundColor(ColorConstants.darkGray);
    Color gradient1 = ColorConstants.lightGray;
    if (isSelected)
    {
      gradient1 = ColorConstants.lightBlue;
    }
    Color gradient2 = ColorConstants.white;
    graphics.setForegroundColor(gradient1);
    graphics.setBackgroundColor(gradient2);
    graphics.fillGradient(r.x + 1, r.y + 1, r.width - 2, nodeNameLabel.getBounds().height - 1, true);
    nodeNameLabel.paint(graphics);
    */
  }

  public IFigure getContentPane()
  {
    return contentPane;
  }

  public Label getNameLabel()
  {
    return headingFigure.getLabel();
  }
  
  public HeadingFigure getHeadingFigure()
  {
    return headingFigure;
  }
  
  public ColumnData getColumnData()
  {
    return columnData;
  }  
}
