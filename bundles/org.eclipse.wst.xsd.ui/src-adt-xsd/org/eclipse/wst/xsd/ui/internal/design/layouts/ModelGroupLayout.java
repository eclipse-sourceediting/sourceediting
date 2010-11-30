/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.design.layouts;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.wst.xsd.ui.internal.design.figures.ModelGroupFigure;
import org.eclipse.wst.xsd.ui.internal.design.figures.SpacingFigure;

public class ModelGroupLayout extends AbstractLayout
{
  protected boolean isHorizontal;
  protected int spacing = 10;
  protected int border = 0;

  public ModelGroupLayout()
  {
    this(0);
  }

  public ModelGroupLayout(boolean isHorizontal)
  {
    this.isHorizontal = isHorizontal;
  }

  public ModelGroupLayout(boolean isHorizontal, int spacing)
  {
    this.isHorizontal = isHorizontal;
    this.spacing = spacing;
  }

  public ModelGroupLayout(int spacing)
  {
    super();
    this.spacing = spacing;
  }

  protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint)
  {
    Dimension preferred = new Dimension();
    List children = container.getChildren();

    for (int i = 0; i < children.size(); i++)
    {
      IFigure child = (IFigure) children.get(i);

      Dimension childSize = child.getPreferredSize();

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
    preferred.width += container.getInsets().getWidth();
    preferred.height += container.getInsets().getHeight();

    return preferred;
  }

  public void layout(IFigure container)
  {
    List children = container.getChildren();

    Dimension dimension = new Dimension();

    for (int i = 0; i < children.size(); i++)
    {
      IFigure child = (IFigure) children.get(i);
      Dimension childSize = child.getPreferredSize();
      if (isHorizontal)
      {
        dimension.height = Math.max(dimension.height, childSize.height);
      }
      else
      {
        dimension.width = Math.max(dimension.width, childSize.width);
      }
    }

    if (isHorizontal)
    {
      dimension.height += border * 2;
      dimension.width += border;
    }
    else
    {
      dimension.width += border * 2;
      dimension.height += border;
    }

    Rectangle r = container.getClientArea();
    dimension = new Dimension(r.width, r.height);
    Point p = new Point(0, 0);

    for (Iterator i = children.iterator(); i.hasNext();)
    {
      IFigure child = (IFigure) i.next();
      Dimension childSize = child.getPreferredSize();

      if (isHorizontal)
      {
        Rectangle rectangle = new Rectangle(p.x, 0, childSize.width, childSize.height);

        // last child
        if (!i.hasNext())
        {
          rectangle.width = dimension.width - rectangle.x;
        }

        if (p.x == 0)
        {
          rectangle.y = r.height / 2 - childSize.height / 2;
        }
        else
        {
          rectangle.y = r.height / 2 - childSize.height / 2;
        }

        rectangle.translate(container.getClientArea().getLocation());
        child.setBounds(rectangle);
        p.x += childSize.width;
        p.x += spacing;

      }
      else
      {
        Rectangle rectangle = new Rectangle(0, p.y, childSize.width, childSize.height);

        if (child instanceof SpacingFigure)
        {
          rectangle.x = dimension.width + 6;
        }
        else if (child instanceof ModelGroupFigure)
        {
          rectangle.width = dimension.width - rectangle.x;
        }
        else
        {
          rectangle.width = dimension.width - rectangle.x;
        }

        rectangle.translate(container.getClientArea().getLocation());
        child.setBounds(rectangle);
        p.y += childSize.height;
        p.y += spacing;
      }
    }
  }

  public void setSpacing(int spacing)
  {
    this.spacing = spacing;
  }

  protected int alignFigure(IFigure parent, IFigure child)
  {
    return -1;
  }
}
