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
package org.eclipse.wst.xsd.ui.internal.design.figures;
            
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ReferenceConnection;

public class CenteredIconFigure extends RoundedRectangle
{                         
  public static final int NORMAL = 0;
  public static final int SELECTED = 1;
  public static final int HOVER = 2;
  public Image image;
  protected Label toolTipLabel;
  protected int mode = 0;
  
  public CenteredIconFigure()
  {
    super();
    setFill(true);   
    toolTipLabel = new Label();
    setCornerDimensions(new Dimension(5,5));
  }
  
  public CenteredIconFigure(Image img)
  {
    this();
    this.image = img;
  }
  
  public void refresh()
  {
    repaint();
  }
  
  protected void outlineShape(Graphics graphics)
  {
    graphics.pushState();
    try
    {
      if (mode == NORMAL)
      { // TODO: common up and organize colors....
        graphics.setForegroundColor(ReferenceConnection.inactiveConnection);
      }
      else if (mode == SELECTED)
      {
        boolean highContrast = false;
        try
        {
          highContrast = Display.getDefault().getHighContrast();
        }
        catch (Exception e)
        {
        }
        if (highContrast)
        {
          graphics.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
        }
        else
        {
          graphics.setForegroundColor(ColorConstants.black);
        }
      }
      super.outlineShape(graphics);
    }
    finally
    {
      graphics.popState();
    }
  }

  protected void fillShape(Graphics g)
  {    
    super.fillShape(g);
    if (image != null)
    {                         
      Rectangle r = getBounds();
      Dimension imageSize = new Dimension(15, 15);
      g.drawImage(image, r.x + (r.width - imageSize.width)/2, r.y + (r.height - imageSize.height)/2 - 1);
    }
  }

  public Label getToolTipLabel()
  {
    return toolTipLabel;
  }
  
  public void setMode(int mode)
  {
    this.mode = mode;  
  }
  
  public void setToolTipText(String text)
  {
    if (text.length() > 0)
    {
      setToolTip(toolTipLabel);
      toolTipLabel.setText(text);
    }
    else
    {
      setToolTip(null);
    }
  }
}
