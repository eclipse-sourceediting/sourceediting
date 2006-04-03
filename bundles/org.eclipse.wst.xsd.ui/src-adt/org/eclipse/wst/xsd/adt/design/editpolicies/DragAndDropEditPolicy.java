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
package org.eclipse.wst.xsd.adt.design.editpolicies;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ChangeBoundsRequest;

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
    DragAndDropCommand command = null;                            
    if (request instanceof ChangeBoundsRequest)
    {
      command = new DragAndDropCommand(viewer, (ChangeBoundsRequest)request);
      selectionHandlesEditPolicy.setDragAndDropCommand(command);
    } 
    return command;             
  }                                                     
}
