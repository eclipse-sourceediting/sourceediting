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
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.ViewportLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.HeadingFigure;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.design.layouts.ContainerLayout;
import org.eclipse.wst.xsd.ui.internal.design.layouts.FillLayout;

public class CategoryEditPart extends BaseEditPart
{
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;
  // CategoryFigure figure;
  Figure outerPane;
  HeadingFigure headingFigure;
  protected ScrollPane scrollpane;

  public int getType()
  {
    return ((CategoryAdapter) getModel()).getGroupType();
  }

  protected IFigure createFigure()
  {
    // figure = new CategoryFigure(getType());
    outerPane = new Figure();
    outerPane.setBorder(new RoundedLineBorder(1, 6));

    headingFigure = new HeadingFigure();
    outerPane.add(headingFigure);
    headingFigure.getLabel().setText(((CategoryAdapter) getModel()).getText());
    headingFigure.getLabel().setIcon(((CategoryAdapter) getModel()).getImage());

    int minHeight = 250;
    switch (getType())
    {
    case CategoryAdapter.DIRECTIVES:
    {
      minHeight = 80;
      break;
    }
    case CategoryAdapter.ATTRIBUTES:
    case CategoryAdapter.GROUPS:
    {
      minHeight = 100;
      break;
    }
    }

    final int theMinHeight = minHeight;
    FillLayout outerLayout = new FillLayout()
    {
      protected Dimension calculatePreferredSize(IFigure parent, int width, int height)
      {
        Dimension d = super.calculatePreferredSize(parent, width, height);
        d.union(new Dimension(250, theMinHeight));
        return d;
      }
    };
    outerPane.setLayoutManager(outerLayout);

    RectangleFigure line = new RectangleFigure()
    {
      public Dimension getPreferredSize(int wHint, int hHint)
      {
        Dimension d = super.getPreferredSize(wHint, hHint);
        d.width += 20;
        d.height = 1;
        return d;
      }
    };
    ToolbarLayout lineLayout = new ToolbarLayout(false);
    lineLayout.setVertical(true);
    lineLayout.setStretchMinorAxis(true);
    line.setLayoutManager(lineLayout);
    outerPane.add(line);

    scrollpane = new ScrollPane();
    scrollpane.setVerticalScrollBarVisibility(ScrollPane.AUTOMATIC); // ScrollPane.ALWAYS);
    outerPane.add(scrollpane);

    Figure pane = new Figure();
    pane.setBorder(new MarginBorder(5, 8, 5, 8));
    ContainerLayout layout = new ContainerLayout();
    layout.setHorizontal(false);
    layout.setSpacing(0);
    pane.setLayoutManager(layout);

    Viewport viewport = new Viewport();
    viewport.setContentsTracksHeight(true);
    ViewportLayout viewportLayout = new ViewportLayout()
    {
      protected Dimension calculatePreferredSize(IFigure parent, int width, int height)
      {
        Dimension d = super.calculatePreferredSize(parent, width, height);
        d.height = Math.min(d.height, theMinHeight - 25); // getViewer().getControl().getBounds().height);
        d.width = Math.min(d.width, 300);
        return d;
      }
    };
    viewport.setLayoutManager(viewportLayout);

    scrollpane.setViewport(viewport);
    scrollpane.setContents(pane);

    return outerPane;
  }

  public void refreshVisuals()
  {
    super.refreshVisuals();

    RoundedLineBorder border = (RoundedLineBorder) outerPane.getBorder();
    border.setWidth(isSelected ? 2 : 1);
    headingFigure.setSelected(isSelected);
    outerPane.repaint();

    headingFigure.getLabel().setText(((CategoryAdapter) getModel()).getText());
  }

  public IFigure getContentPane()
  {
    return scrollpane.getContents();
  }

  public Label getNameLabel()
  {
    return headingFigure.getLabel();
  }

  public HeadingFigure getHeadingFigure()
  {
    return headingFigure;
  }

  protected EditPart createChild(Object model)
  {
    EditPart editPart = new TopLevelComponentEditPart();
    editPart.setModel(model);
    editPart.setParent(this);
    return editPart;
  }

  protected void createEditPolicies()
  {
    selectionHandlesEditPolicy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionHandlesEditPolicy);
  }

  protected List getModelChildren()
  {
    CategoryAdapter adapter = (CategoryAdapter) getModel();
    List children = new ArrayList(Arrays.asList(adapter.getChildren()));
    return children;
  }

  public void scrollTo(AbstractGraphicalEditPart topLevel)
  {
    Rectangle topLevelBounds = topLevel.getFigure().getBounds();
    Rectangle categoryBounds = figure.getBounds();
    int scrollValue = scrollpane.getVerticalScrollBar().getValue();
    int location = topLevelBounds.y + scrollValue - categoryBounds.y;
    scrollpane.scrollVerticalTo(location - categoryBounds.height / 2);
  }
}
