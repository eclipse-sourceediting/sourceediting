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
package org.eclipse.wst.xsd.ui.internal.commands;

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
import org.eclipse.wst.xsd.ui.internal.actions.MoveAction;
import org.eclipse.wst.xsd.ui.internal.actions.MoveAttributeAction;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAttributeAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CompartmentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ComplexTypeEditPart;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;
import org.eclipse.wst.xsd.ui.internal.design.editparts.AttributeGroupDefinitionEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ConnectableEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ModelGroupDefinitionReferenceEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ModelGroupEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.TargetConnectionSpacingFigureEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDAttributesForAnnotationEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDBaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDGroupsForAnnotationEditPart;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroup;

// TODO : clean and common up code
public class DragAndDropCommand extends BaseCommand
{ 
  protected EditPartViewer viewer;    
  protected ChangeBoundsRequest request;
  protected BaseFieldEditPart previousChildRefEditPart, nextChildRefEditPart;    
  public ModelGroupEditPart parentEditPart;
  protected AttributeGroupDefinitionEditPart parentAttributeGroupEditPart;
  protected XSDConcreteComponent parentComponent;
  public Point location;
  protected MoveAction action;
  protected MoveAttributeAction moveAttributeAction;
  protected boolean canExecute, isElementToDrag;
  EditPart target;
  XSDBaseFieldEditPart selected;
  List modelGroupsList = new ArrayList();
  List targetSpacesList = new ArrayList();
  List attributeGroupsList = new ArrayList();
  XSDConcreteComponent previousRefComponent = null, nextRefComponent = null;
  ComplexTypeEditPart complexTypeEditPart;

  public DragAndDropCommand(EditPartViewer viewer, ChangeBoundsRequest request)
  {
    this.viewer = viewer;                    
    this.request = request;
    setup();
  }
  
  protected void setup()
  {
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
    canExecute = false;
    // allow drag and drop of only one selected object
    if (list.size() == 1 && target instanceof BaseFieldEditPart)
    {
      List editPartsList = request.getEditParts();
      List concreteComponentList = new ArrayList(editPartsList.size());
      for (Iterator i = editPartsList.iterator(); i.hasNext(); )
      {                                                       
        EditPart editPart = (EditPart)i.next();
        concreteComponentList.add(((XSDBaseAdapter)editPart.getModel()).getTarget());
      }

      Object itemToDrag = list.get(0);
      if (itemToDrag instanceof XSDBaseFieldEditPart)
      {
    	  selected = (XSDBaseFieldEditPart) itemToDrag;
    	  if (selected.getModel() instanceof XSDElementDeclarationAdapter)
    	  {
    		  isElementToDrag = true;
    	  }
    	  else if (selected.getModel() instanceof XSDBaseAttributeAdapter)
    	  {
    		  isElementToDrag = false;
    	  }
    	  else
    	  {
    		  return;
    	  }

    	  if (!isElementToDrag)
    	  {
    		  XSDAttributeGroupDefinition attributeGroup = null;
    		  attributeGroupsList.clear();
    		  targetSpacesList.clear();
          parentAttributeGroupEditPart = null;
          calculateAttributeGroupList();
          EditPart compartment = target.getParent();

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
                  if (tSpace.getParent() instanceof AttributeGroupDefinitionEditPart)
                  {
                    parentAttributeGroupEditPart = (AttributeGroupDefinitionEditPart)tSpace.getParent();
                    attributeGroup = parentAttributeGroupEditPart.getXSDAttributeGroupDefinition().getResolvedAttributeGroupDefinition();
                    parentComponent = attributeGroup;
                  }
                  else if (tSpace.getParent() instanceof XSDAttributesForAnnotationEditPart)
                  {
                    parentComponent = (XSDConcreteComponent)((XSDBaseAdapter)complexTypeEditPart.getModel()).getTarget(); 
                  }
                  break;
                }            
              }
              else
              {
              // This is the annotation edit part
              }
            }  
          }
          calculatePreviousAndNextEditParts();
          moveAttributeAction = new MoveAttributeAction(parentComponent, concreteComponentList, previousRefComponent, nextRefComponent);
          canExecute = moveAttributeAction.canMove();
    	  }
    	  else if (isElementToDrag)
    	  {
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
        
          EditPart compartment = target.getParent();
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
              }
              else
              {
           	  // This is the annotation edit part
              }
            }  
          }
          calculatePreviousAndNextEditParts();
          action = new MoveAction(targetModelGroup, concreteComponentList, previousRefComponent, nextRefComponent);
          canExecute = action.canMove();
        }
      }            
    }     
  }
  
  protected void calculatePreviousAndNextEditParts()
  {
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

  }
  
  protected void calculateAttributeGroupList()
  {
    EditPart editPart = target;
    while (editPart != null)
    {                     
      if (editPart instanceof ComplexTypeEditPart)
      {
        complexTypeEditPart = (ComplexTypeEditPart)editPart;
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
              if (obj instanceof XSDAttributesForAnnotationEditPart)
              {
                XSDAttributesForAnnotationEditPart groups = (XSDAttributesForAnnotationEditPart)obj;
                List groupList = groups.getChildren();
                for (Iterator iter = groupList.iterator(); iter.hasNext(); )
                {
                  Object groupChild = iter.next();
                  if (groupChild instanceof TargetConnectionSpacingFigureEditPart)
                  {
                    targetSpacesList.add(groupChild);
                  }
                  else if (groupChild instanceof AttributeGroupDefinitionEditPart)
                  {
                    AttributeGroupDefinitionEditPart attributeGroupEditPart = (AttributeGroupDefinitionEditPart)groupChild;
                    attributeGroupsList.add(attributeGroupEditPart);
                    attributeGroupsList.addAll(getAttributeGroupEditParts(attributeGroupEditPart));
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
  
  protected List getAttributeGroupEditParts(AttributeGroupDefinitionEditPart attributeGroupEditPart)
  {
    List groupList = new ArrayList();
    List list = attributeGroupEditPart.getChildren();
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      Object object = i.next();
      if (object instanceof TargetConnectionSpacingFigureEditPart)
      {
        targetSpacesList.add(object);
      }
      else if (object instanceof AttributeGroupDefinitionEditPart)
      {
        AttributeGroupDefinitionEditPart groupRef = (AttributeGroupDefinitionEditPart)object;
        List groupRefChildren = groupRef.getChildren();
        for (Iterator it = groupRefChildren.iterator(); it.hasNext(); )
        {
         Object o = it.next();
         if (o instanceof TargetConnectionSpacingFigureEditPart)
         {
           targetSpacesList.add(o);
         }
         else if (o instanceof AttributeGroupDefinitionEditPart)
         {
           AttributeGroupDefinitionEditPart aGroup = (AttributeGroupDefinitionEditPart)o;
           groupList.add(aGroup);
           groupList.addAll(getAttributeGroupEditParts(aGroup));
         }
        }
      }
    }   
    return groupList;   
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
      if (isElementToDrag)
        action.run();
      else
        moveAttributeAction.run();
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
    if (isElementToDrag)
    {
      if (parentEditPart != null && nextChildRefEditPart != null)
      {                                
        pointList = getConnectionPoints(parentEditPart, nextChildRefEditPart, draggedFigureBounds);      
      }
    }
    else
    {
      if (parentAttributeGroupEditPart!= null && nextChildRefEditPart != null)
      {
        pointList = getConnectionPoints(parentAttributeGroupEditPart, nextChildRefEditPart, draggedFigureBounds);
      }
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
	return ((GenericGroupFigure)editPart.getFigure()).getIconFigure().getBounds();
  }

}
