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
package org.eclipse.wst.xsd.ui.internal.graph.editpolicies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.wst.xsd.ui.internal.actions.MoveAction;
import org.eclipse.wst.xsd.ui.internal.gef.util.figures.IConnectionRenderingViewer;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.ComplexTypeDefinitionEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.GraphNodeEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.ModelGroupDefinitionEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.ModelGroupEditPart;
import org.eclipse.xsd.XSDConcreteComponent;


public class DragAndDropCommand extends Command //AbstractCommand
{ 
  protected EditPartViewer viewer;    
  protected ChangeBoundsRequest request;
  protected GraphNodeEditPart childRefEditPart;    
  public GraphNodeEditPart parentEditPart; 
  public Point location;
  protected MoveAction action;    
  protected boolean canExecute;

  public DragAndDropCommand(EditPartViewer viewer, ChangeBoundsRequest request)
  {
    this.viewer = viewer;                    
    this.request = request;

    location = request.getLocation();   
    EditPart target = viewer.findObjectAt(location); 
    if (viewer instanceof ScrollingGraphicalViewer)
    {  
      ScrollingGraphicalViewer sgv = (ScrollingGraphicalViewer)viewer;
      Point p = ((FigureCanvas)sgv.getControl()).getViewport().getViewLocation();
      location.y += p.y;
      location.x += p.x;
    }
     
    List list = request.getEditParts();
    if (list.size() > 0)
    {           
      parentEditPart = getParentEditPart(target);
      if (parentEditPart != null)
      {                                          
        for (Iterator i = parentEditPart.getChildren().iterator(); i.hasNext(); )
        {
          EditPart child = (EditPart)i.next();
          if (child instanceof GraphNodeEditPart)
          {
            GraphNodeEditPart childGraphNodeEditPart = (GraphNodeEditPart)child;
            Rectangle rectangle = childGraphNodeEditPart.getSelectionFigure().getBounds();
     
            if (location.y < rectangle.getCenter().y)
            {                                    
              childRefEditPart = childGraphNodeEditPart;   
              break;
            }
          }
        } 
            
        List editPartsList = request.getEditParts();
        List concreteComponentList = new ArrayList(editPartsList.size());
        for (Iterator i = editPartsList.iterator(); i.hasNext(); )
        {                                                       
          EditPart editPart = (EditPart)i.next();
          concreteComponentList.add((XSDConcreteComponent)editPart.getModel());
        } 
        XSDConcreteComponent refComponent = childRefEditPart != null ? (XSDConcreteComponent)childRefEditPart.getModel() : null;

        action = new MoveAction((XSDConcreteComponent)parentEditPart.getModel(), concreteComponentList, refComponent);
        canExecute = action.canMove();                                            
      }            
    }     
  }
             
  protected GraphNodeEditPart getParentEditPart(EditPart target)
  {
    GraphNodeEditPart result = null;    
    while (target != null)
    {                     
      if (target instanceof ModelGroupEditPart)
      {
        result = (GraphNodeEditPart)target;
        break;
      }
      else if (target instanceof ComplexTypeDefinitionEditPart ||
               target instanceof ModelGroupDefinitionEditPart)
      {
        List list = target.getChildren();
        for (Iterator i = list.iterator(); i.hasNext(); )
        {
          Object child = i.next();
          if (child instanceof ModelGroupEditPart)
          {
            result = (GraphNodeEditPart)child;
            break;
          }
        }   
        if (result != null)
        {
          break;
        }
      }
      target = target.getParent();
    }
    return result;
  }

  public void execute()
  {           
    if (canExecute)
    { 
      action.run(); 
    }
  }     
  
  public void redo()
  {

  }  
  
  public void undo()
  {
  }     
  
  public boolean canExecute()
  { 
    return canExecute;
  } 

  public PointList getConnectionPoints(Rectangle draggedFigureBounds)
  {             
    PointList pointList = null;      
    if (parentEditPart != null && childRefEditPart != null && viewer instanceof IConnectionRenderingViewer)
    {                                
      pointList = ((IConnectionRenderingViewer)viewer).getConnectionRenderingFigure().getConnectionPoints(parentEditPart, childRefEditPart, draggedFigureBounds);      
    }               
    return pointList != null ? pointList : new PointList();
  }                                                                  
}
