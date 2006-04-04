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
package org.eclipse.wst.xsd.editor.internal.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.wst.xsd.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.adt.design.editparts.CompartmentEditPart;
import org.eclipse.wst.xsd.adt.design.editparts.ComplexTypeEditPart;
import org.eclipse.wst.xsd.editor.internal.actions.MoveAction;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.editor.internal.design.editparts.ModelGroupDefinitionReferenceEditPart;
import org.eclipse.wst.xsd.editor.internal.design.editparts.ModelGroupEditPart;
import org.eclipse.wst.xsd.editor.internal.design.editparts.TargetConnectionSpacingFigureEditPart;
import org.eclipse.wst.xsd.editor.internal.design.editparts.XSDGroupsForAnnotationEditPart;
import org.eclipse.wst.xsd.editor.internal.design.figures.GenericGroupFigure;
import org.eclipse.wst.xsd.ui.common.commands.BaseCommand;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroup;


public class DragAndDropCommand extends BaseCommand
{ 
  protected EditPartViewer viewer;    
  protected ChangeBoundsRequest request;
  protected BaseFieldEditPart previousChildRefEditPart, nextChildRefEditPart;    
  public ModelGroupEditPart parentEditPart; 
  public Point location;
  protected MoveAction action;    
  protected boolean canExecute;
  EditPart target;
  List modelGroupsList = new ArrayList();
  List targetSpacesList = new ArrayList();

  public DragAndDropCommand(EditPartViewer viewer, ChangeBoundsRequest request)
  {
    this.viewer = viewer;                    
    this.request = request;

    location = request.getLocation();
    target = viewer.findObjectAt(location); 
    if (viewer instanceof ScrollingGraphicalViewer)
    {  
      ScrollingGraphicalViewer sgv = (ScrollingGraphicalViewer)viewer;
      Point p = ((FigureCanvas)sgv.getControl()).getViewport().getViewLocation();
      location.y += p.y;
      location.x += p.x;
    }
     
    List list = request.getEditParts();
    if (list.size() > 0 && target instanceof BaseFieldEditPart)
    {
      BaseFieldEditPart baseFieldEditPart = (BaseFieldEditPart)target;
      XSDModelGroup targetModelGroup = null;
      modelGroupsList.clear();
      targetSpacesList.clear();
      calculateModelGroupList();

      List modelGroups = new ArrayList(modelGroupsList.size());
      for (Iterator i = modelGroupsList.iterator(); i.hasNext(); )
      {
        ModelGroupEditPart editPart = (ModelGroupEditPart)i.next();
        modelGroups.add(editPart.getXSDModelGroup());
      }
      
      EditPart compartment = baseFieldEditPart.getParent();
      parentEditPart = null;
      if (compartment != null)
      {
        List l = compartment.getChildren();
        Rectangle rectangle = new Rectangle(0, 0, 0, 0), previousRectangle = new Rectangle(0,0,0,0);
        int index = -1;
        BaseFieldEditPart childGraphNodeEditPart = null;
        for (Iterator i = l.iterator(); i.hasNext(); )
        {
          EditPart child = (EditPart)i.next();
          if (child instanceof BaseFieldEditPart)
          {
            index ++;
            previousChildRefEditPart = childGraphNodeEditPart;
            childGraphNodeEditPart = (BaseFieldEditPart)child;
            if (previousChildRefEditPart != null)
            {
              previousRectangle = previousChildRefEditPart.getFigure().getBounds();
            }
            rectangle = childGraphNodeEditPart.getFigure().getBounds();
            
            if (location.y < (rectangle.getCenter().y))
            {
              nextChildRefEditPart = childGraphNodeEditPart;
              TargetConnectionSpacingFigureEditPart tSpace = (TargetConnectionSpacingFigureEditPart)targetSpacesList.get(index-1);
              if (previousRectangle != null && location.y > (previousRectangle.getBottom().y))
              {
            	  tSpace = (TargetConnectionSpacingFigureEditPart)targetSpacesList.get(index);
              }
              parentEditPart = (ModelGroupEditPart)tSpace.getParent();
              targetModelGroup = parentEditPart.getXSDModelGroup();
              break;
            }            
            
//            if (location.y < (rectangle.getCenter().y))
//            {
//                nextChildRefEditPart = childGraphNodeEditPart;
//                TargetConnectionSpacingFigureEditPart previousSpace = null;
//	           	for (Iterator s = targetSpacesList.iterator(); s.hasNext(); )
//	        	{
//	        	  TargetConnectionSpacingFigureEditPart tSpace = (TargetConnectionSpacingFigureEditPart) s.next();
//	              Rectangle tRect = tSpace.getFigure().getBounds();
//	              if (location.y < (tRect.getCenter().y + tRect.height/2))
//	              {
//	            	parentEditPart = (ModelGroupEditPart)tSpace.getParent();
//	            	targetModelGroup = parentEditPart.getXSDModelGroup();
//	            	break;
//	              }
//	        	  previousSpace = tSpace;
//	        	}
//	           	if (parentEditPart != null)
//	           		break;
//            }
          }
          else
          {
        	  // This is the annotation edit part
          }
        } 
           
        List editPartsList = request.getEditParts();
        List concreteComponentList = new ArrayList(editPartsList.size());
        for (Iterator i = editPartsList.iterator(); i.hasNext(); )
        {                                                       
          EditPart editPart = (EditPart)i.next();
          concreteComponentList.add((XSDConcreteComponent) ((XSDBaseAdapter)editPart.getModel()).getTarget());
        }
        XSDConcreteComponent previousRefComponent = null, nextRefComponent = null;
        if (nextChildRefEditPart != null)
        {
          if (nextChildRefEditPart.getModel() instanceof XSDBaseAdapter)
          {
            nextRefComponent = (XSDConcreteComponent)((XSDBaseAdapter)nextChildRefEditPart.getModel()).getTarget();
          }
        }
        if (previousChildRefEditPart != null)
        {
          if (previousChildRefEditPart.getModel() instanceof XSDBaseAdapter)
          {
        	previousRefComponent = (XSDConcreteComponent)((XSDBaseAdapter)previousChildRefEditPart.getModel()).getTarget();
          }
        }
//        System.out.println(previousRefComponent + "\n   " + nextRefComponent);
        action = new MoveAction(targetModelGroup, concreteComponentList, previousRefComponent, nextRefComponent);
        canExecute = action.canMove();
      }            
    }     
  }
             
  protected void calculateModelGroupList()
  {
    EditPart editPart = target;
    while (editPart != null)
    {                     
      if (editPart instanceof ModelGroupEditPart)
      {
        ModelGroupEditPart modelGroupEditPart = (ModelGroupEditPart)editPart;
        modelGroupsList.addAll(getModelGroupEditParts(modelGroupEditPart));
      }
      else if (editPart instanceof ComplexTypeEditPart ||
               editPart instanceof ModelGroupDefinitionReferenceEditPart)
      {
        List list = editPart.getChildren();
        for (Iterator i = list.iterator(); i.hasNext(); )
        {
          Object child = i.next();
          if (child instanceof CompartmentEditPart)
          {
            List compartmentList = ((CompartmentEditPart)child).getChildren();
            for (Iterator it = compartmentList.iterator(); it.hasNext(); )
            {
              Object obj = it.next();
              if (obj instanceof XSDGroupsForAnnotationEditPart)
              {
                XSDGroupsForAnnotationEditPart groups = (XSDGroupsForAnnotationEditPart)obj;
                List groupList = groups.getChildren();
                for (Iterator iter = groupList.iterator(); iter.hasNext(); )
                {
                  Object groupChild = iter.next();
                  if (groupChild instanceof ModelGroupEditPart)
                  {
                    ModelGroupEditPart modelGroupEditPart = (ModelGroupEditPart)groupChild;
                    modelGroupsList.add(modelGroupEditPart);
                    modelGroupsList.addAll(getModelGroupEditParts(modelGroupEditPart));
                  }
                }
              }
            }
          }
        }   
      }
      editPart = editPart.getParent();
    }
  }
  
  protected List getModelGroupEditParts(ModelGroupEditPart modelGroupEditPart)
  {
	List modelGroupList = new ArrayList();
    List list = modelGroupEditPart.getChildren();
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      Object object = i.next();
      if (object instanceof TargetConnectionSpacingFigureEditPart)
      {
    	targetSpacesList.add(object);
      }
      else if (object instanceof ModelGroupDefinitionReferenceEditPart)
      {
    	  ModelGroupDefinitionReferenceEditPart groupRef = (ModelGroupDefinitionReferenceEditPart)object;
    	  List groupRefChildren = groupRef.getChildren();
    	  for (Iterator it = groupRefChildren.iterator(); it.hasNext(); )
    	  {
    		 Object o = it.next();
    		 if (o instanceof ModelGroupEditPart)
    		 {
   		    	ModelGroupEditPart aGroup = (ModelGroupEditPart)o;
   		        modelGroupList.add(aGroup);
   		        modelGroupList.addAll(getModelGroupEditParts(aGroup));
    		 }
    	  }
      }
      else if (object instanceof ModelGroupEditPart)
      {
    	ModelGroupEditPart aGroup = (ModelGroupEditPart)object;
        modelGroupList.add(aGroup);
        modelGroupList.addAll(getModelGroupEditParts(aGroup));
      }
    }   
    return modelGroupList;
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
    if (parentEditPart != null && nextChildRefEditPart != null)
    {                                
      pointList = getConnectionPoints(parentEditPart, nextChildRefEditPart, draggedFigureBounds);      
    }               
    return pointList != null ? pointList : new PointList();
  }
  
  // This method supports the preview connection line function related to drag and drop
  //     
  public PointList getConnectionPoints(ModelGroupEditPart parentEditPart, BaseFieldEditPart childRefEditPart, Rectangle draggedFigureBounds)
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
  protected Point getConnectionPoint(ModelGroupEditPart parentEditPart, BaseFieldEditPart childRefEditPart, int[] data)
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

  protected Rectangle getConnectedEditPartConnectionBounds(ModelGroupEditPart editPart)
  {
	return ((GenericGroupFigure)editPart.getFigure()).getIconFigure().getBounds();
  }

}
