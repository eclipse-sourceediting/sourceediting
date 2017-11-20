/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.design.editpolicies;

import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAttributeAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.commands.BaseDragAndDropCommand;
import org.eclipse.wst.xsd.ui.internal.commands.XSDAttributeDragAndDropCommand;
import org.eclipse.wst.xsd.ui.internal.commands.XSDElementDragAndDropCommand;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDBaseFieldEditPart;


public class DragAndDropEditPolicy extends org.eclipse.gef.editpolicies.GraphicalEditPolicy
{ 
  protected EditPartViewer viewer;
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;

  public DragAndDropEditPolicy(EditPartViewer viewer, SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy)
  {
    this.viewer = viewer;
    this.selectionHandlesEditPolicy = selectionHandlesEditPolicy;
  }

  public boolean understandsRequest(Request req)
  {
    return true;
  }                           

  
  public org.eclipse.gef.commands.Command getCommand(Request request)
  {             
    BaseDragAndDropCommand command = null;                            
    if (request instanceof ChangeBoundsRequest)
    {
      ChangeBoundsRequest changeBoundsRequest = (ChangeBoundsRequest)request;
      Point location = changeBoundsRequest.getLocation();
      
      GraphicalEditPart target = (GraphicalEditPart)viewer.findObjectAt(location);
      location = getPointerLocation(changeBoundsRequest.getLocation());
      ((GraphicalEditPart)viewer.getRootEditPart()).getFigure().translateToRelative(location);

      List list = changeBoundsRequest.getEditParts();
      // allow drag and drop of only one selected object
      if (list.size() == 1)
      {
        Object itemToDrag = list.get(0);
        if (itemToDrag instanceof XSDBaseFieldEditPart)
        {
          XSDBaseFieldEditPart selected = (XSDBaseFieldEditPart) itemToDrag;
          if (selected.getModel() instanceof XSDElementDeclarationAdapter)
          {
            command = new XSDElementDragAndDropCommand(viewer, (ChangeBoundsRequest)request, target, selected, location);
            selectionHandlesEditPolicy.setDragAndDropCommand(command);
          }
          else if (selected.getModel() instanceof XSDBaseAttributeAdapter)
          {
            command = new XSDAttributeDragAndDropCommand(viewer, (ChangeBoundsRequest)request, target, selected, location);
            selectionHandlesEditPolicy.setDragAndDropCommand(command);
          }
        }
      }
    } 
    return command;             
  }                         
  
  protected Point getPointerLocation(Point origPointerLocation)
  {
     Point compensatedLocation = origPointerLocation;
     FigureCanvas figureCanvas = (FigureCanvas) viewer.getControl();
     int yOffset = figureCanvas.getViewport().getVerticalRangeModel().getValue();
     int xOffset = figureCanvas.getViewport().getHorizontalRangeModel().getValue();
     compensatedLocation.y = compensatedLocation.y + yOffset;
     compensatedLocation.x = compensatedLocation.x + xOffset;
     return compensatedLocation;
  }
}
