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
package org.eclipse.wst.xsd.ui.internal.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.wst.xsd.ui.internal.actions.MoveXSDBaseAction;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ConnectableEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.TargetConnectionSpacingFigureEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDBaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;

public abstract class BaseDragAndDropCommand extends BaseCommand
{
  protected static int ABOVE_IS_CLOSER = 0;
  protected static int BELOW_IS_CLOSER = 1;
  
  protected EditPartViewer viewer;    
  protected ChangeBoundsRequest request;
  protected boolean canExecute;
  protected GraphicalEditPart target;
  
  protected GraphicalEditPart leftSiblingEditPart;
  protected GraphicalEditPart rightSiblingEditPart;
  protected Point location;

  protected ConnectableEditPart parentEditPart;
  protected XSDConcreteComponent previousRefComponent = null, nextRefComponent = null, xsdComponentToDrag;
  protected XSDBaseFieldEditPart itemToDrag;
  protected Rectangle originalLocation;
  protected Polyline polyLine;
  
  protected MoveXSDBaseAction action;
  protected List targetSpacesList = new ArrayList();
  protected int closerSibling;
  
  public BaseDragAndDropCommand(EditPartViewer viewer, ChangeBoundsRequest request)
  {
    this.viewer = viewer;                    
    this.request = request;
  }
  
  protected abstract void setup();
 
  /**
   * Provides the DOM element associated with the parent XSD component. 
   * This element is used in the the undo/redo mechanism.   
   * @return the DOM element associated with the parent XSD component.
   */
  protected abstract Element getElement();
  
  public PointList getConnectionPoints(Rectangle draggedFigureBounds)
  {
    PointList pointList = null;
    if (target != null && itemToDrag != null && parentEditPart != null)
    {
      pointList = getConnectionPoints(parentEditPart, itemToDrag, draggedFigureBounds);
    }
    return pointList != null ? pointList : new PointList();
  }
  
  // This method supports the preview connection line function related to drag and drop
  //     
  public PointList getConnectionPoints(ConnectableEditPart parentEditPart, BaseFieldEditPart childRefEditPart, Rectangle draggedFigureBounds)
  {           
    PointList pointList = new PointList();                         
    int[] data = new int[1];
    Point a = getConnectionPoint(parentEditPart, childRefEditPart, data);
    if (a != null)
    {   
      int draggedFigureBoundsY = draggedFigureBounds.y + draggedFigureBounds.height/2;

      pointList.addPoint(a); 
      
      if (data[0] == 0) // insert between 2 items
      {                                         
        int x = a.x + 5;
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
  protected Point getConnectionPoint(ConnectableEditPart parentEditPart, BaseFieldEditPart childRefEditPart, int[] data)
  {                      
    Point point = null;     
    List childList = parentEditPart.getChildren();         
    if (parentEditPart.getFigure() instanceof GenericGroupFigure && childList.size() > 0)
    {
      point = new Point();

      Rectangle r = getConnectedEditPartConnectionBounds(parentEditPart);  
      point.x = r.x + r.width;
      point.y = r.y + r.height/2;
    }    
    return point;
  }

  protected Rectangle getConnectedEditPartConnectionBounds(ConnectableEditPart editPart)
  {
    return getZoomedBounds(((GenericGroupFigure)editPart.getFigure()).getIconFigure().getBounds());
  }
    
  public void redo()
  {
  }

  public void undo()
  {
  }

  public void execute()
  {
    if (canExecute)
    {
    	// Wrap the drag and drop operation for easy undo and redo.
    	beginRecording(this.getElement());
		action.run();
		endRecording();
    }
  }
  
  public boolean canExecute()
  {
    return canExecute;
  }
  
  protected void commonSetup(List siblings, GraphicalEditPart movingEditPart)
  {
    closerSibling = ABOVE_IS_CLOSER;
    int pointerYLocation = location.y;
    int index;
    
    for (index = 0; index < siblings.size(); index++)
    {
      GraphicalEditPart sibling = (GraphicalEditPart) siblings.get(index);
      if (sibling instanceof BaseFieldEditPart)
      {
        int siblingYLocation = getZoomedBounds(sibling.getFigure().getBounds()).getCenter().y;

        if (siblingYLocation > pointerYLocation)
        {
          rightSiblingEditPart = sibling;
          if (index > 0)
          {
            leftSiblingEditPart = (GraphicalEditPart) siblings.get(index - 1);
          }

          if (leftSiblingEditPart != null && Math.abs(getZoomedBounds(leftSiblingEditPart.getFigure().getBounds()).getCenter().y - pointerYLocation) > Math.abs(siblingYLocation - pointerYLocation))
          {
            closerSibling = BELOW_IS_CLOSER;
          }
          break;
        }
      }
    }

    boolean isHandled = handleFirstAndLastDropTargets(index, siblings);
    if (!isHandled)
      handleOtherTargets(index);

    calculateLeftAndRightXSDComponents();
    
    xsdComponentToDrag = (XSDConcreteComponent) ((XSDBaseAdapter) itemToDrag.getModel()).getTarget();    
  }
  
  protected void calculateLeftAndRightXSDComponents()
  {
    if (leftSiblingEditPart instanceof XSDBaseFieldEditPart)
    {
      Object leftModel = ((XSDBaseFieldEditPart) leftSiblingEditPart).getModel();
      previousRefComponent = null;
      if (leftModel instanceof XSDBaseAdapter)
      {
        XSDBaseAdapter leftAdapter = (XSDBaseAdapter) leftModel;
        previousRefComponent = (XSDConcreteComponent) leftAdapter.getTarget();
      }
    }

    if (rightSiblingEditPart instanceof XSDBaseFieldEditPart)
    {
      Object rightModel = ((XSDBaseFieldEditPart) rightSiblingEditPart).getModel();
      nextRefComponent = null;
      if (rightModel instanceof XSDBaseAdapter)
      {
        XSDBaseAdapter rightAdapter = (XSDBaseAdapter) rightModel;
        nextRefComponent = (XSDConcreteComponent) rightAdapter.getTarget();
      }
    }
  }
  
  protected boolean handleFirstAndLastDropTargets(int index, List siblings)
  {
    // Handle case where you drop to first position
    if (index == 0 && siblings.size() > 0)
    {
      leftSiblingEditPart = null;
      rightSiblingEditPart = (GraphicalEditPart) siblings.get(0);
      closerSibling = BELOW_IS_CLOSER;
    }
    // Handle case where you drop to last position
    else if (index > 0 && index == siblings.size())
    {
      leftSiblingEditPart = (GraphicalEditPart) siblings.get(index - 1);
      rightSiblingEditPart = null;
    }
    return false;
  }

  protected void handleOtherTargets(int index)
  {
    int in = 0;
    ConnectableEditPart previousModelEditPart = null;
    for (Iterator i = targetSpacesList.iterator(); i.hasNext();)
    {
      Object o = i.next();
      previousModelEditPart = parentEditPart;
      TargetConnectionSpacingFigureEditPart sp = (TargetConnectionSpacingFigureEditPart) o;
      if (sp.getParent() instanceof ConnectableEditPart)
        parentEditPart = (ConnectableEditPart)sp.getParent();
      else
        parentEditPart = null;
      in++;
      if (in > index)
      {
        if (closerSibling == ABOVE_IS_CLOSER)
        {
          parentEditPart = previousModelEditPart;
        }
        break;
      }
    }    
  }
  
  protected List calculateFieldEditParts()
  {
    List list = target.getParent().getChildren();
    List listOfFields = new ArrayList();
    for (Iterator i = list.iterator(); i.hasNext();)
    {
      Object o = i.next();
      if (o instanceof BaseFieldEditPart)
      {
        listOfFields.add(o);
      }
    }
    return listOfFields;
  }
  
  protected PointList drawLines(Polyline polyLine)
  {
    PointList pointList = new PointList();

    if (leftSiblingEditPart != null)
    {
      Rectangle leftRectangle = getZoomedBounds(leftSiblingEditPart.getFigure().getBounds());
      int xCoord = leftRectangle.x;
      int yCoord = leftRectangle.y;
      int height = leftRectangle.height;
      int width = leftRectangle.width;

      // Draw left end line
      addLineToPolyline(polyLine, xCoord, yCoord + height + 3, xCoord, yCoord + height - 3);
      addLineToPolyline(polyLine, xCoord, yCoord + height - 3, xCoord, yCoord + height);

      // Draw horizontal line
      addLineToPolyline(polyLine, xCoord, yCoord + height, xCoord + width, yCoord + height);

      // Draw right end line
      addLineToPolyline(polyLine, xCoord + width, yCoord + height, xCoord + width, yCoord + height - 3);
      addLineToPolyline(polyLine, xCoord + width, yCoord + height, xCoord + width, yCoord + height + 3);
    }
    else if (rightSiblingEditPart != null)
    {
      Rectangle rightRectangle = getZoomedBounds(rightSiblingEditPart.getFigure().getBounds());
      int xCoord = rightRectangle.x;
      int yCoord = rightRectangle.y;
      int width = rightRectangle.width;

      // Draw left end line
      addLineToPolyline(polyLine, xCoord, yCoord + 3, xCoord, yCoord - 3);
      addLineToPolyline(polyLine, xCoord, yCoord - 3, xCoord, yCoord);

      // Draw horizontal line
      addLineToPolyline(polyLine, xCoord, yCoord, xCoord + width, yCoord);

      // Draw right end line
      addLineToPolyline(polyLine, xCoord + width, yCoord, xCoord + width, yCoord - 3);
      addLineToPolyline(polyLine, xCoord + width, yCoord, xCoord + width, yCoord + 3);
    }

    return pointList;
  }

  protected Polyline addLineToPolyline(Polyline polyline, int x1, int y1, int x2, int y2)
  {
    polyline.addPoint(new Point(x1, y1));
    polyline.addPoint(new Point(x2, y2));

    return polyline;
  }
  
  public IFigure getFeedbackFigure()
  {
    Figure panel = new Figure();
    panel.setLayoutManager(new FreeformLayout());
    panel.setOpaque(false);

    Polyline feedbackFigure = new Polyline();
    feedbackFigure.setLineWidth(2);
    drawLines(feedbackFigure);
    originalLocation = new Rectangle(feedbackFigure.getBounds());
    panel.add(feedbackFigure);

    polyLine = new Polyline();
    polyLine.setLineStyle(Graphics.LINE_DASHDOT);
    polyLine.setLineWidth(1);
    panel.add(polyLine);
    
    panel.setBounds(originalLocation);
    
    addConnectorToParent(panel);

    if (parentEditPart != null && parentEditPart.getFigure() instanceof GenericGroupFigure)
    {
      GenericGroupFigure fig = (GenericGroupFigure)parentEditPart.getFigure();
      Rectangle iconBounds = getZoomedBounds(fig.getIconFigure().getBounds());
      RoundedRectangle roundedRectangle = new RoundedRectangle();
      roundedRectangle.setFill(false);
      roundedRectangle.setOpaque(true);
//      roundedRectangle.setBounds(new Rectangle(iconBounds.x, iconBounds.y, iconBounds.width - 1, iconBounds.height - 1));
      roundedRectangle.setBounds(iconBounds);
      panel.add(roundedRectangle);
    }
    return panel;
  }
  
  protected void addConnectorToParent(IFigure p)
  {
    Rectangle r = originalLocation.getCopy();
    Rectangle pBounds = r.getCopy();
    PointList pointList = getConnectionPoints(r);

    if (pointList != null && pointList.size() > 0)
    {
      polyLine.setPoints(pointList);
      Point firstPoint = pointList.getFirstPoint();
      if (firstPoint != null)
      {
        pBounds = pBounds.getUnion(new Rectangle(firstPoint.x, firstPoint.y, 1, 1));
      }
    }

    if (parentEditPart != null)
    {
      if (parentEditPart.getFigure() instanceof GenericGroupFigure)
      {
        GenericGroupFigure fig = (GenericGroupFigure)parentEditPart.getFigure();
        Rectangle iconBounds = getZoomedBounds(fig.getIconFigure().getBounds());
        pBounds = pBounds.getUnion(iconBounds);
      }
    }

    p.setBounds(pBounds);
    p.validate();
  }
  
  public Point getZoomedPoint(Point p)
  {
    double factor = ((ScalableRootEditPart)viewer.getRootEditPart()).getZoomManager().getZoom();

    int x = (int)Math.round(p.x * factor);
    int y = (int)Math.round(p.y * factor);

    return new Point(x, y);
  }
  
  public Rectangle getZoomedBounds(Rectangle r)
  {
    double factor = ((ScalableRootEditPart)viewer.getRootEditPart()).getZoomManager().getZoom();

    int x = (int)Math.round(r.x * factor);
    int y = (int)Math.round(r.y * factor);
    int width = (int)Math.round(r.width * factor);
    int height = (int)Math.round(r.height * factor);

    return new Rectangle(x, y, width, height);
  }

  protected void handleKeyboardDragAndDrop(XSDBaseFieldEditPart leftField, XSDBaseFieldEditPart rightField, int direction)
  {
    target = leftField;
    if (direction == PositionConstants.SOUTH)
    {
      if (itemToDrag == target)
        return;
      target = rightField;
    }
    this.location = null;
    if (target != null)
      this.location = target.getFigure().getBounds().getCenter();
  }
}
