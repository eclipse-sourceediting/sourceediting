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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

public class RootEditPart extends ScalableRootEditPart implements org.eclipse.gef.RootEditPart
{  
  public void activate()
  {
    super.activate();
    // Set up Connection layer with a router, if it doesn't already have one
    ConnectionLayer connectionLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
    if (connectionLayer != null)
    {  
      connectionLayer.setConnectionRouter(new BendpointConnectionRouter());
    }

    Figure figure = (Figure)getLayer(LayerConstants.FEEDBACK_LAYER);       
    if (figure != null)
    {      
      if (getViewer() instanceof ScrollingGraphicalViewer)
      {  
        //ScrollingGraphicalViewer sgv = (ScrollingGraphicalViewer)getViewer();
        //IndexFigure indexFigure = new IndexFigure(sgv);
        //figure.add(indexFigure);
        //getViewer().addPropertyChangeListener(indexFigure);
      }  
    }           
    refresh();
  }
  
  
  class IndexFigure extends RectangleFigure implements PropertyChangeListener
  {
    EditPart editPart;
    ScrollingGraphicalViewer sgv;
    public IndexFigure(ScrollingGraphicalViewer sgv)
    {
      this.sgv = sgv;      
      ((FigureCanvas)sgv.getControl()).getViewport().getHorizontalRangeModel().addPropertyChangeListener(this);
      ((FigureCanvas)sgv.getControl()).getViewport().getVerticalRangeModel().addPropertyChangeListener(this);
      Rectangle bounds = new Rectangle(0, 0, 40, 40);
      translateToAbsolute(bounds);      
      setBounds(bounds);       
    }
    public void propertyChange(PropertyChangeEvent evt)
    {
      System.out.println("scroll-change");
      Rectangle bounds = new Rectangle(0, 0, 40, 40);
      Point p = ((FigureCanvas)sgv.getControl()).getViewport().getViewLocation();
      bounds.translate(p);
      setBounds(bounds); 
    }
    
    public Rectangle getBounds()
    {
      Point p = ((FigureCanvas)sgv.getControl()).getViewport().getViewLocation();
      bounds.translate(p);      
      return super.getBounds().getCopy().translate(p);
    }
  }
}
