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
package org.eclipse.wst.xsd.ui.internal.graph;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.RangeModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.CategoryEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.TopLevelComponentEditPart;

public class FigureCanvasKeyboardHandler extends KeyAdapter
{
  public static final int H_SCROLL_INCREMENT = 5;
  public static final int V_SCROLL_INCREMENT = 30;

  BaseGraphicalViewer viewer;
  
  /**
   * Constructor for FigureCanvasKeyboardHandler.
   */
  public FigureCanvasKeyboardHandler(BaseGraphicalViewer viewer)
  {
    super();
    this.viewer = viewer;
  }

  public void keyPressed(KeyEvent e)
  {
    Widget w = e.widget;
    if (w instanceof FigureCanvas)
    {
      processKey(e.keyCode, (FigureCanvas)w);
      update();
    }
  }

  private void processKey(int keyCode, FigureCanvas figureCanvas)
  {
    switch (keyCode)
    {
      case SWT.ARROW_DOWN :
        scrollVertical(figureCanvas, false);
        break;
      case SWT.ARROW_UP :
        scrollVertical(figureCanvas, true);
        break;
      case SWT.ARROW_LEFT :
        scrollHorizontal(figureCanvas, true);
        break;
      case SWT.ARROW_RIGHT :
        scrollHorizontal(figureCanvas, false);
        break;
      case SWT.PAGE_UP :
        scrollPage(figureCanvas, true);
        break;
      case SWT.PAGE_DOWN :
        scrollPage(figureCanvas, false);
        break;
    }
  }

  private int verifyScrollBarOffset(RangeModel model, int value)
  {
    value = Math.max(model.getMinimum(), value);
    return Math.min(model.getMaximum() - model.getExtent(), value);
  }

  private void scrollVertical(FigureCanvas figureCanvas, boolean up)
  {
    Point location = figureCanvas.getViewport().getViewLocation();
    int vOffset = up ? -V_SCROLL_INCREMENT : V_SCROLL_INCREMENT;
    int x = verifyScrollBarOffset(figureCanvas.getViewport().getHorizontalRangeModel(), location.x);
    int y = verifyScrollBarOffset(figureCanvas.getViewport().getVerticalRangeModel(), location.y + vOffset);
    figureCanvas.scrollSmoothTo(x, y);
  }

  private void scrollHorizontal(FigureCanvas figureCanvas, boolean left)
  {
    Point location = figureCanvas.getViewport().getViewLocation();
    int hOffset = left ? -H_SCROLL_INCREMENT : H_SCROLL_INCREMENT;
    int x = verifyScrollBarOffset(figureCanvas.getViewport().getHorizontalRangeModel(), location.x + hOffset);
    int y = verifyScrollBarOffset(figureCanvas.getViewport().getVerticalRangeModel(), location.y);
    figureCanvas.scrollSmoothTo(x, y);
  }

  private void scrollPage(FigureCanvas figureCanvas, boolean up)
  {
    Rectangle clientArea = figureCanvas.getClientArea();
    int increment = up ? -clientArea.height : clientArea.height;
    Point location = figureCanvas.getViewport().getViewLocation();
    int x = verifyScrollBarOffset(figureCanvas.getViewport().getHorizontalRangeModel(), location.x);
    int y = verifyScrollBarOffset(figureCanvas.getViewport().getVerticalRangeModel(), location.y + increment);
    figureCanvas.scrollSmoothTo(x, y);
  }
  
  private void update()
  {
    StructuredSelection s = (StructuredSelection)viewer.getSelection();
    Object newSelectedEditPart = s.getFirstElement();

    if (newSelectedEditPart instanceof TopLevelComponentEditPart)
    {
      TopLevelComponentEditPart topLevel = (TopLevelComponentEditPart) newSelectedEditPart;
      CategoryEditPart categoryEP = (CategoryEditPart) topLevel.getParent();
      categoryEP.scrollTo(topLevel);
      viewer.reveal((TopLevelComponentEditPart)newSelectedEditPart);
    }                           
  }


}
