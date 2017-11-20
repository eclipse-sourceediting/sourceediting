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
                                 
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.commands.BaseDragAndDropCommand;

public class SelectionHandlesEditPolicyImpl	extends ADTSelectionFeedbackEditPolicy
{
  protected IFigure feedback;
  protected Rectangle originalLocation;
  protected BaseDragAndDropCommand dragAndDropCommand;
  protected Polyline polyLine;
  protected RectangleFigure ghostShape;

  public boolean understandsRequest(Request request)
  {    
    boolean result = false;

    if (REQ_MOVE.equals(request.getType()))
    {  
      result = false; 
    }
    else
    {
      result = super.understandsRequest(request);
    }
    return result;
  }
  

  public org.eclipse.gef.commands.Command getCommand(Request request) 
  {                                          
    return null;  
  }   

  public void setDragAndDropCommand(BaseDragAndDropCommand dragAndDropCommand)
  {
    this.dragAndDropCommand = dragAndDropCommand;
  }

  public void showSourceFeedback(Request request)
  {   
      eraseChangeBoundsFeedback(null);
      if (dragAndDropCommand != null && dragAndDropCommand.canExecute()) {
        if (REQ_MOVE.equals(request.getType()) || REQ_ADD.equals(request.getType())) {
          if (dragAndDropCommand != null && dragAndDropCommand.getFeedbackFigure() != null) {
            feedback = dragAndDropCommand.getFeedbackFigure();
            addFeedback(feedback);
          }
        }
      }
  }

  public void deactivate()
  {
	  if (feedback != null)
    {
		  removeFeedback(feedback);
		  feedback = null;
	  }
	  hideFocus();
	  super.deactivate();
  }

  /**
   * Erase feedback indicating that the receiver object is 
   * being dragged.  This method is called when a drag is
   * completed or cancelled on the receiver object.
   * @param dragTracker org.eclipse.gef.tools.DragTracker The drag tracker of the tool performing the drag.
   */
  protected void eraseChangeBoundsFeedback(ChangeBoundsRequest request) 
  {
	  if (feedback != null) 
    {		      
		  removeFeedback(feedback);
	  }
	  feedback = null;
	  originalLocation = null;
  }

  /**
   * Erase feedback indicating that the receiver object is 
   * being dragged.  This method is called when a drag is
   * completed or cancelled on the receiver object.
   * @param dragTracker org.eclipse.gef.tools.DragTracker The drag tracker of the tool performing the drag.
   */
  public void eraseSourceFeedback(Request request) 
  {
    if (REQ_MOVE.equals(request.getType()) ||  REQ_ADD.equals(request.getType()))
    {
		  eraseChangeBoundsFeedback((ChangeBoundsRequest)request);
    }
  }
  
}
