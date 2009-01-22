/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;

                                   
public class GraphNodeDragTracker extends DragEditPartsTracker 
{                                     
  protected EditPart editPart; 
           
  public GraphNodeDragTracker(EditPart editPart)
  {
    super(editPart);
    this.editPart = editPart;
  } 
                                              
  protected Command getCommand() 
  { 
	  Request request = getTargetRequest();
    return editPart.getCommand(request); 
  }
  
  protected void performSelection()
  {
    performAdditionalSelection();
    super.performSelection();
  }
  
  protected void performAdditionalSelection()
  {
    EditPartViewer viewer = getCurrentViewer();
    // This code selects the fields in-between the last selected field and the newly
    // selected field, if the shift key is held down.  Note that the selection logic
    // can be improved so that already selected but, a) incompatible edit part figures, or b)
    // like-edit part figures from other parents, should be unselected.
    if (getCurrentInput().isShiftKeyDown())
    {
      // This list contains the fields
      List list = editPart.getParent().getChildren();
      // Get the index of the current selection
      int currentIndex = list.indexOf(editPart);
      // List of all the currently selected edit parts
      List currentSelected = viewer.getSelectedEditParts();
      int size = currentSelected.size();
      if (size > 0)
      {
        Object lastSelected = currentSelected.get(size - 1);
        if (lastSelected instanceof BaseEditPart)
        {
          // Here, we determine the upper and lower limit of the indices
          int lowerIndex = -1, upperIndex = -1;
          int lastSelectedIndex = list.indexOf(lastSelected);
          if (lastSelectedIndex >= 0 && lastSelectedIndex < currentIndex)
          {
            lowerIndex = lastSelectedIndex;
            upperIndex = currentIndex;
          } 
          else if (lastSelectedIndex >= 0 && lastSelectedIndex > currentIndex)
          {
            lowerIndex = currentIndex;
            upperIndex = lastSelectedIndex;
          }
          if (lowerIndex >= 0 && upperIndex >= 0)
          {          
            for (int i = lowerIndex; i < upperIndex; i++)
            {
              viewer.appendSelection((EditPart) list.get(i));
            }
          }
        }
      }
    }
  }
} 
