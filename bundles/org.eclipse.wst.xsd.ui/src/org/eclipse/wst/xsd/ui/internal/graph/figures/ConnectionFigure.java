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
package org.eclipse.wst.xsd.ui.internal.graph.figures;
       
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.GraphNodeEditPart;

              

public class ConnectionFigure extends RectangleFigure
{               
  protected boolean isOutlined = true;
  protected IFigure xsdFigure;

  public ConnectionFigure(IFigure xsdFigure)
  {
    setOpaque(false);                   
    this.xsdFigure = xsdFigure;
    //setFocusTraversable(false); 
    //setEnabled(false); 
  }
           
  protected boolean isMouseEventTarget()
  {
    return false;
  }        

  public boolean containsPoint(int x, int y){return false;}

  protected void fillShape(Graphics graphics)
  { 
    graphics.setForegroundColor(ColorConstants.black);
    drawLines(graphics, xsdFigure);
  }

  protected void drawLines(Graphics graphics, IFigure figure)
  {
    if (figure instanceof GraphNodeFigure)
    {
      GraphNodeFigure graphNodeFigure = (GraphNodeFigure)figure;         
      if (graphNodeFigure.isConnected() && graphNodeFigure.isExpanded())
      {
        List childList = graphNodeFigure.getChildGraphNodeFigures();
        if (childList.size() > 0)
        {                                         
          Rectangle r = graphNodeFigure.getConnectionRectangle();    
          
          int x1 = r.x + r.width;
          int y1 = r.y + r.height/2;
          
          int startOfChildBox = ((GraphNodeFigure)childList.get(0)).getConnectionRectangle().x;

          int x2 = x1 + (startOfChildBox - x1) / 3;
          int y2 = y1;
                    
          if (childList.size() > 1)
          {                                 
            graphics.drawLine(x1, y1, x2, y2);
          
            int minY = Integer.MAX_VALUE;
            int maxY = -1;
          
            for (Iterator i = childList.iterator(); i.hasNext(); )
            {
              GraphNodeFigure childGraphNodeFigure = (GraphNodeFigure)i.next();
              Rectangle childConnectionRectangle = childGraphNodeFigure.getConnectionRectangle();
              int y = childConnectionRectangle.y + childConnectionRectangle.height / 2;
             
              minY = Math.min(minY, y);
              maxY = Math.max(maxY, y);             
              graphics.drawLine(x2, y, childConnectionRectangle.x, y);            
            }                   
            graphics.drawLine(x2, minY, x2, maxY);
          }
          else
          {
            graphics.drawLine(x1, y1, startOfChildBox, y2);
          }
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
  public PointList getConnectionPoints(GraphNodeEditPart parentEditPart, GraphNodeEditPart childRefEditPart, Rectangle draggedFigureBounds)
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
  protected Point getConnectionPoint(GraphNodeEditPart parentEditPart, GraphNodeEditPart childRefEditPart, int[] data)
  {                      
    Point point = null;     
    List childList = parentEditPart.getChildren();         

    //TreeNodeIconFigure icon = ((TreeNodeFigure)parent.getFigure()).treeNodeIconFigure;
                                                                                                                                               
    if (childList.size() > 0)
    {   
      point = new Point();

      GraphNodeEditPart prev = null;    
      GraphNodeEditPart next = null;
                               
      for (Iterator i = childList.iterator(); i.hasNext(); )
      {             
        Object o = i.next();    
        if (o instanceof GraphNodeEditPart)
        {
          GraphNodeEditPart childEditPart = (GraphNodeEditPart)o;        
          if (childEditPart == childRefEditPart)
          {
            next = childEditPart;
            break;
          }                                           
          prev = childEditPart;
        }
      }                            

      if (next != null && prev != null)
      { 
        int ya = prev.getConnectionRectangle().getCenter().y;
        int yb = next.getConnectionRectangle().getCenter().y;
        point.y = ya + (yb - ya)/2;   
        data[0] = 0;
      }                               
      else if (prev != null) // add it last
      {
        point.y = prev.getConnectionRectangle().getCenter().y;
        data[0] = 1;
      }
      else if (next != null) // add it first!
      {                           
        point.y = next.getConnectionRectangle().getCenter().y;
        data[0] = -1;
      }
          
      if (next != null || prev != null)
      {
        GraphNodeEditPart child = prev != null ? prev : next;
        int startOfChildBox = child.getConnectionRectangle().x;
        Rectangle r = parentEditPart.getConnectionRectangle();  
        int x1 = r.x + r.width;
        point.x = x1 + (startOfChildBox - x1) / 3;
      }
    }    
    return point;
  }
         

  protected void outlineShape(Graphics graphics)
  { 
    if (isOutlined)
    {
      super.outlineShape(graphics);
    }
  } 
}
