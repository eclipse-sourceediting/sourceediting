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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.design.layouts.ModelGroupLayout;

public class GenericGroupFigure extends Figure
{
  protected CenteredIconFigure centeredIconFigure;
  protected Figure contentFigure;
  protected String text;
  protected boolean hasText = false;
  protected Label textFigure;
  
  public GenericGroupFigure()
  {
    super();
    setLayoutManager(new ModelGroupLayout(true));
   
    centeredIconFigure = new CenteredIconFigure();
    centeredIconFigure.setPreferredSize(new Dimension(15, 15));

    add(centeredIconFigure);
    contentFigure = new Figure();
    contentFigure.setLayoutManager(new ModelGroupLayout(false, 0));
    add(contentFigure);
  }
  
  public void setText(String text)
  {
    this.text = text;
    hasText = false;
    if (text != null && text.length() > 0)
    {
      hasText = true;
    }
  }
  
  public boolean hasText()
  {
    return hasText;
  }
  
  public String getText()
  {
    return text;
  }
  
  public Point getTextCoordinates()
  {
    Rectangle rect = centeredIconFigure.getBounds();
    return new Point(rect.x, rect.y + 14);
  }
  
  public void setIconFigure(Image image)
  {
    centeredIconFigure.image = image;
  }

  public CenteredIconFigure getTargetFigure()
  {
    return centeredIconFigure;
  }
  
  public CenteredIconFigure getIconFigure()
  {
    return centeredIconFigure;
  }
  
  public Figure getContentFigure()
  {
    return contentFigure;
  }
  
  public void setToolTipText(String text)
  {
    centeredIconFigure.setToolTipText(text);
  }
}
