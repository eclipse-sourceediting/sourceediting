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
package org.eclipse.wst.xsd.ui.internal.graph.editparts;
                                        
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.wst.xsd.ui.internal.gef.util.figures.ConnectedEditPartFigure;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.GraphNodeDragTracker;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;

              

public abstract class GraphNodeEditPart extends BaseEditPart
{
  protected GraphNodeFigure graphNodeFigure;
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;
                                 
  protected boolean isConnectedEditPart()
  {
    return true;
  }

  protected IFigure createFigure()
  {                           
    IFigure result = graphNodeFigure = createGraphNodeFigure();    
    addGraphNodeFigureListeners();

    if (isConnectedEditPart())
    {
      ConnectedEditPartFigure connectedEditPartFigure = createConnectedEditPartFigure();                                    
      connectedEditPartFigure.add(graphNodeFigure);
      result = connectedEditPartFigure;
    }
    return result;
  }       
           
  protected ConnectedEditPartFigure createConnectedEditPartFigure()
  {
    ConnectedEditPartFigure connectedEditPartFigure = new ConnectedEditPartFigure(this)
    {
      public IFigure getSelectionFigure()
      {
        return graphNodeFigure.getOutlinedArea();
      }

      public IFigure getConnectionFigure()
      {
        return graphNodeFigure.getConnectionFigure();
      }
    };
    return connectedEditPartFigure;
  }                                

  protected abstract GraphNodeFigure createGraphNodeFigure();           
  
  protected void addGraphNodeFigureListeners()
  {
  }
 
  public IFigure getSelectionFigure()
  {
    return graphNodeFigure.getOutlinedArea();
  }

  public Rectangle getConnectionRectangle()
  {
    return graphNodeFigure.getConnectionRectangle();
  }

  protected void createEditPolicies()
  { 
    //installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());    
    selectionHandlesEditPolicy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionHandlesEditPolicy);   
  }  

  public DragTracker getDragTracker(Request request)
  {
    return new GraphNodeDragTracker((EditPart)this);
  }   

 protected EditPart getApplicableEditPart(EditPart editPart, Point p)
  {        
    while (true)
    {    
      EditPart parent = null;
      if (editPart instanceof GraphNodeEditPart)
      {                                  
        IFigure f = ((GraphNodeEditPart)editPart).getSelectionFigure();
        if (!hitTest(f, p))
        {                                 
          parent = editPart.getParent();        
        }             
      }   

      if (parent != null)
      {
        editPart = parent;
      }                   
      else
      {
        break;
      }
    }         
    return editPart;
  }

  public EditPart getTargetEditPart(Request request)   
  { 
    EditPart editPart = null;
    if (request.getType() == REQ_SELECTION)
    {                                                                       
      if (request instanceof LocationRequest)
      {
        LocationRequest locationRequest = (LocationRequest)request;
        Point p = locationRequest.getLocation();
        editPart = getApplicableEditPart(this, p);
      }
    }
    return (editPart != null) ? editPart : super.getTargetEditPart(request);
  }   

  public boolean hitTest(IFigure target, Point location)
  {
    Rectangle b = target.getBounds().getCopy();
    target.translateToAbsolute(b);  
    return b.contains(location);
  }
}
