/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import java.awt.event.KeyEvent;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.KeyListener;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.design.figures.CenteredIconFigure;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class ADTFloatingToolbarEditPart extends BaseEditPart
{
  protected IModel model;
  protected boolean isDrilledDown;
  protected ADTToolbarButton backToSchema;
  
  public ADTFloatingToolbarEditPart(IModel model)
  {
    this.model = model;
  }

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
    backToSchema.setFocusTraversable(true);    
    figure.add(backToSchema);
    figure.setBounds(new Rectangle(0,0,24,24));    
    addToToolbar(figure);
    return figure;
  }
  
  protected void addToToolbar(IFigure figure)
  {
    
  }
  
  public void setIsDrilledDown(boolean isDrilledDown)
  {
    this.isDrilledDown = isDrilledDown;
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
  
  protected void doAction(MouseEvent me)
  {
    IEditorPart editorPart = getActionEditorPart();
 
    if (backToSchema.getBounds().contains(me.getLocation()))
    {
      SetInputToGraphView action = new SetInputToGraphView(editorPart, model);
      action.run();
    }
  }
  
  protected void doAction(org.eclipse.draw2d.KeyEvent ke)
  {
	  IEditorPart editorPart = getActionEditorPart(); 
	  SetInputToGraphView action = new SetInputToGraphView(editorPart, model);
	  action.run();    
  }
  
  private IEditorPart getActionEditorPart()
  {
	  IWorkbench workbench = PlatformUI.getWorkbench();
	  IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	  IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
	  return editorPart;
  }
  
  
  protected class ADTToolbarButton extends CenteredIconFigure
  {
    protected MouseListener mouseListener;
    protected org.eclipse.draw2d.KeyListener keyListener;    
    protected FocusListener focusListener;
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
      
      keyListener = new KeyListener.Stub()
      {
    	  public void keyPressed(org.eclipse.draw2d.KeyEvent ke)
    	  {
    		  boolean isValidKey = (ke.keycode == KeyEvent.VK_SPACE);
    		  isValidKey = isValidKey || (ke.character == SWT.CR || ke.character == SWT.LF);
    		  if (isEnabled && isValidKey)
    		  {
    			  addFeedback();
    		  }
    	  }

    	  public void keyReleased(org.eclipse.draw2d.KeyEvent ke)
    	  {
    		  boolean isValidKey = (ke.keycode == KeyEvent.VK_SPACE);
    		  isValidKey = isValidKey || (ke.character == SWT.CR || ke.character == SWT.LF);
    		  if (isEnabled && isValidKey)
    		  {
    			  removeFeedback();
    			  doAction(ke);
    		  }            
    	  }
      };
      addKeyListener(keyListener);    
      
      focusListener = new FocusListener(){

		public void focusGained(FocusEvent fe) {
			setMode(CenteredIconFigure.HOVER);
			refresh();
		}

		public void focusLost(FocusEvent fe) {
			setMode(CenteredIconFigure.NORMAL);
			refresh();
		}
	};
	
	addFocusListener(focusListener);
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
