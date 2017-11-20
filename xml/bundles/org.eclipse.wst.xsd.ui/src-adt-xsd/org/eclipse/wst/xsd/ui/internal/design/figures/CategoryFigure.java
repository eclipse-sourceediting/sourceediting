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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.ViewportLayout;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.HeadingFigure;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.design.layouts.FillLayout;

public class CategoryFigure extends Figure
{
  protected ScrollPane scrollpane;
  protected Figure outerPane;
  public HeadingFigure headingFigure;
  Figure contentPane;

  public CategoryFigure(int type)
  {
    super();

    outerPane = new Figure();
    outerPane.setBorder(new RoundedLineBorder(1, 6));

    ToolbarLayout layout = new ToolbarLayout(false);
    layout.setVertical(true);
    layout.setStretchMinorAxis(true);
    FillLayout fillLayout = new FillLayout(3);
    fillLayout.setHorizontal(false);

    FillLayout outerLayout = new FillLayout();
    outerPane.setLayoutManager(outerLayout);

    add(outerPane);

    headingFigure = new HeadingFigure();
    outerPane.add(headingFigure);

    Figure line = new Figure();
    line.setBorder(new LineBorder(1));
    ToolbarLayout lineLayout = new ToolbarLayout(false);
    lineLayout.setVertical(true);
    lineLayout.setStretchMinorAxis(true);
    line.setLayoutManager(lineLayout);
    outerPane.add(line);

    scrollpane = new ScrollPane();
    scrollpane.setForegroundColor(ColorConstants.black);
    scrollpane.setVerticalScrollBarVisibility(ScrollPane.AUTOMATIC);
    outerPane.add(scrollpane);

    Figure pane = new Figure();
    pane.setBorder(new MarginBorder(5, 8, 5, 8));
    ToolbarLayout toolbarLayout = new ToolbarLayout(false);
    toolbarLayout.setSpacing(3);
    pane.setLayoutManager(toolbarLayout); // good

    Viewport viewport = new Viewport();
    viewport.setContentsTracksHeight(true);
    ViewportLayout viewportLayout = new ViewportLayout();
    viewport.setLayoutManager(viewportLayout);

    scrollpane.setViewport(viewport);
    scrollpane.setContents(pane);
  }

  public HeadingFigure getHeadingFigure()
  {
    return headingFigure;
  }

  public ScrollPane getScrollPane()
  {
    return scrollpane;
  }

  public IFigure getContentPane()
  {
    return scrollpane.getContents();
  }

}
