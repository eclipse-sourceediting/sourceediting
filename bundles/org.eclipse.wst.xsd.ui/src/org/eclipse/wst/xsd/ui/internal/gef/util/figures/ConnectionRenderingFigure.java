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
package org.eclipse.wst.xsd.ui.internal.gef.util.figures;
        
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
              

public class ConnectionRenderingFigure extends RectangleFigure
{               
  protected boolean isOutlined = true;
  protected IFigure primaryLayer;

  public ConnectionRenderingFigure(IFigure primaryLayer)
  {
    setOpaque(false);                   
    this.primaryLayer = primaryLayer;
    //setFocusTraversable(false); 
    //setEnabled(false); 
  }
           
  protected boolean isMouseEventTarget()
  {
    return false;
  }        

  public boolean containsPoint(int x, int y)
  {
    return false;
  }

  protected void fillShape(Graphics graphics)
  { 
    graphics.setForegroundColor(ColorConstants.black);
    drawLines(graphics, primaryLayer);
  }

  protected void outlineShape(Graphics graphics)
  { 
    if (isOutlined)
    {
      super.outlineShape(graphics);
    }
  } 

  protected void drawLines(Graphics graphics, IFigure figure)
  {      
    if (figure instanceof IConnectedEditPartFigure)
    {
      IConnectedEditPartFigure graphNodeFigure = (IConnectedEditPartFigure)figure;         
      List connectedFigures = graphNodeFigure.getConnectedFigures(IConnectedEditPartFigure.RIGHT_CONNECTION);
      int connectedFiguresSize = connectedFigures.size();              

      if (connectedFiguresSize > 0) 
      {                                         
        IConnectedEditPartFigure firstGraphNodeFigure = (IConnectedEditPartFigure)connectedFigures.get(0);
        Rectangle r = graphNodeFigure.getConnectionFigure().getBounds();    
          
        int x1 = r.x + r.width;
        int y1 = r.y + r.height/2;
                                                                                   
        int startOfChildBox = firstGraphNodeFigure.getConnectionFigure().getBounds().x;
        int x2 = x1 + (startOfChildBox - x1) / 3;
        int y2 = y1;
      
        if (connectedFiguresSize == 1)
        {
          graphics.drawLine(x1, y1, startOfChildBox, y2);   
        }
        else // (connectedFigures.length > 1)
        { 
          graphics.drawLine(x1, y1, x2, y2);

          int minY = Integer.MAX_VALUE;
          int maxY = -1;

          for (Iterator i = connectedFigures.iterator(); i.hasNext(); )
          {                                 
            IConnectedEditPartFigure connectedFigure = (IConnectedEditPartFigure)i.next();
            Rectangle childConnectionRectangle = connectedFigure.getConnectionFigure().getBounds();
            int y = childConnectionRectangle.y + childConnectionRectangle.height / 2;
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            graphics.drawLine(x2, y, childConnectionRectangle.x, y);
          }                   
          graphics.drawLine(x2, minY, x2, maxY);
        }                          
      }                             
    }            

    //boolean visitChildren = true;
    List children = figure.getChildren();
    for (Iterator i = children.iterator(); i.hasNext(); )
    {
      IFigure child = (IFigure)i.next();
      drawLines(graphics, child);
    }
  }    

  // This method supports the preview connection line function related to drag and drop
  //     
  public PointList getConnectionPoints(GraphicalEditPart parentEditPart, GraphicalEditPart childRefEditPart, Rectangle draggedFigureBounds)
  {           
    PointList pointList = new PointList();                         
    int[] data = new int[1];
    Point a = getConnectionPoint(parentEditPart, childRefEditPart, data);
    if (a != null)
    {   
      int draggedFigureBoundsY = draggedFigureBounds.y + draggedFigureBounds.height/2;

      pointList.addPoint(a); 
      //pointList.addPoint(new Point(draggedFigureBounds.x, draggedFigureBoundsY));
      
      if (data[0] == 0) // insert between 2 items
      {                                         
        int x = a.x + (draggedFigureBounds.x - a.x)/2;
        pointList.addPoint(new Point(x, a.y));
        pointList.addPoint(new Point(x, draggedFigureBoundsY));        
        pointList.addPoint(new Point(draggedFigureBounds.x, draggedFigureBoundsY));
      }
      else // insert at first or last position
      {
        pointList.addPoint(new Point(a.x, draggedFigureBoundsY));   
        pointList.addPoint(new Point(draggedFigureBounds.x, draggedFigureBoundsY));
      }
    }       
    return pointList;
  }

         
  // This method supports the preview connection line function related to drag and drop
  //     
  protected Point getConnectionPoint(GraphicalEditPart parentEditPart, GraphicalEditPart childRefEditPart, int[] data)
  {                      
    Point point = null;     
    List childList = parentEditPart.getChildren();         
                                                                                                                                               
    if (parentEditPart.getFigure() instanceof IConnectedEditPartFigure && childList.size() > 0)
    {   
      point = new Point();

      GraphicalEditPart prev = null;    
      GraphicalEditPart next = null;
                               
      for (Iterator i = childList.iterator(); i.hasNext(); )
      {             
        Object o = i.next();    
        if (o instanceof GraphicalEditPart)
        {
          GraphicalEditPart childEditPart = (GraphicalEditPart)o;
          if (childEditPart.getFigure() instanceof IConnectedEditPartFigure)
          {
            if (childEditPart == childRefEditPart)
            {
              next = childEditPart;
              break;
            }                                           
            prev = childEditPart;
          }
        }
      }                            
                          

      if (next != null && prev != null)
      {                   
        int ya = getConnectedEditPartConnectionBounds(prev).getCenter().y;
        int yb = getConnectedEditPartConnectionBounds(next).getCenter().y;
        point.y = ya + (yb - ya)/2;   
        data[0] = 0;
      }                               
      else if (prev != null) // add it last
      {
        point.y = getConnectedEditPartConnectionBounds(prev).getCenter().y;
        data[0] = 1;
      }
      else if (next != null) // add it first!
      {                           
        point.y = getConnectedEditPartConnectionBounds(next).getCenter().y;
        data[0] = -1;
      }
          
      if (next != null || prev != null)
      {
        GraphicalEditPart child = prev != null ? prev : next;
        int startOfChildBox = getConnectedEditPartConnectionBounds(child).x;
        Rectangle r = getConnectedEditPartConnectionBounds(parentEditPart);  
        int x1 = r.x + r.width;
        point.x = x1 + (startOfChildBox - x1) / 3;
      }
    }    
    return point;
  }

  protected Rectangle getConnectedEditPartConnectionBounds(GraphicalEditPart editPart)
  {
    return ((IConnectedEditPartFigure)editPart.getFigure()).getConnectionFigure().getBounds();
  }
}