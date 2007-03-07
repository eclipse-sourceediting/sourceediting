/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.design.figures.CenteredIconFigure;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class ADTFloatingToolbar extends GraphicalViewerImpl
{
  protected IModel model;
  protected boolean isDrilledDown;
  protected ADTToolbarButton backToSchema;
  protected ADTFloatingToolbarEditPart editPart;

  public ADTFloatingToolbar(IModel model)
  {
    this.model = model;
    editPart = new ADTFloatingToolbarEditPart();
    editPart.setModel(model);
    setContents(editPart);
  }

  public void setModel(IModel model)
  {
    this.model = model;
    editPart.setModel(model);
  }

  public Control createControl(Composite composite)
  {
    Canvas canvas = new Canvas(composite, SWT.NONE);
    canvas.setBackground(ColorConstants.white);
    setControl(canvas);
    return getControl();
  }

  public void refresh(boolean isDrilledDown)
  {
    this.isDrilledDown = isDrilledDown;
    getContents().refresh();
  }

  protected void addToToolbar(IFigure figure)
  {
    
  }

  protected class ADTFloatingToolbarEditPart extends BaseEditPart
  {
    protected IFigure createFigure()
    {
      Figure figure = new Figure();
      ToolbarLayout tb = new ToolbarLayout(true);
      tb.setStretchMinorAxis(false);
      tb.setSpacing(3);
      figure.setLayoutManager(tb);
      
      backToSchema = new ADTToolbarButton(XSDEditorPlugin.getPlugin().getIcon("elcl16/schemaview_co.gif"));
      backToSchema.setToolTipText(Messages._UI_HOVER_BACK_TO_SCHEMA);
      backToSchema.setBackgroundColor(ColorConstants.white);
      figure.add(backToSchema);

      addToToolbar(figure);   

      return figure;
    }
    
    protected void refreshVisuals()
    {
      super.refreshVisuals();
      backToSchema.isEnabled = isDrilledDown;
      if (isDrilledDown)
      {
        backToSchema.image = XSDEditorPlugin.getPlugin().getIcon("elcl16/schemaview_co.gif");
      }
      else
      {
        backToSchema.image = XSDEditorPlugin.getPlugin().getIcon("dlcl16/schemaview_co.gif");
      }
      backToSchema.refresh();
    }
  }

  protected void doAction(MouseEvent me)
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
 
    if (backToSchema.getBounds().contains(me.getLocation()))
    {
      SetInputToGraphView action = new SetInputToGraphView(editorPart, model);
      action.run();
    }
  }

  protected class ADTToolbarButton extends CenteredIconFigure
  {
    protected MouseListener mouseListener;
    public boolean isEnabled;
    
    public ADTToolbarButton(Image img)
    {
      super();
      this.image = img;

      mouseListener = new MouseListener.Stub()
      {
        public void mousePressed(org.eclipse.draw2d.MouseEvent me)
        {
          if (isEnabled)
          {
            addFeedback();
          }
        }

        public void mouseReleased(org.eclipse.draw2d.MouseEvent me)
        {
          if (isEnabled)
          {
            removeFeedback();
            doAction(me);
          }
        }
      };
      addMouseListener(mouseListener);
      addMouseMotionListener(new MouseMotionListener.Stub()
      {
        public void mouseExited(MouseEvent me)
        {
          removeFeedback();
        }
      });
    }

    public void addFeedback()
    {
      setMode(CenteredIconFigure.SELECTED);
      refresh();
    }

    public void removeFeedback()
    {
      setMode(CenteredIconFigure.NORMAL);
      refresh();
    }

    public Rectangle getBounds()
    {
      Rectangle r = super.getBounds();
      org.eclipse.swt.graphics.Rectangle rect = image.getBounds();
      return new Rectangle(r.x, r.y, rect.width + 8, rect.height + 8);
    }

    public Dimension getPreferredSize(int hint, int hint2)
    {
      org.eclipse.swt.graphics.Rectangle rect = image.getBounds();
      return new Dimension(rect.width + 8, rect.height + 8);
    }
  }
}
