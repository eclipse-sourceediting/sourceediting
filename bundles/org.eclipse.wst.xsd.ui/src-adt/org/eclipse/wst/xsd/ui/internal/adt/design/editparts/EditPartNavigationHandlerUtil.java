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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;
import java.util.List;
import org.eclipse.gef.EditPart;


public class EditPartNavigationHandlerUtil
{  
  public static EditPart getFirstChild(EditPart editPart)
  {      
    EditPart result = null;
    if (editPart.getChildren().size() > 0)
    {
      result = (EditPart)editPart.getChildren().get(0);
    }      
    return result;
  }
  
  public static EditPart getLastChild(EditPart editPart)
  {      
    EditPart result = null;
    int size = editPart.getChildren().size();
    if (size > 0)
    {
      result = (EditPart)editPart.getChildren().get(size - 1);
    }      
    return result;
  }  
  
  public static EditPart getNextSibling(EditPart editPart)
  {    
    EditPart result = null;    
    EditPart parent = editPart.getParent();
    if (parent != null)
    {  
      List children = parent.getChildren();
      int index = children.indexOf(editPart);
      if (index + 1 < children.size())
      {
        result = (EditPart)children.get(index + 1);
      }
    }
    return result;
  }
  
  public static EditPart getPrevSibling(EditPart editPart)
  {    
    EditPart result = null;
    EditPart parent = editPart.getParent();
    if (parent != null)
    {  
      List children = parent.getChildren();
      int index = children.indexOf(editPart);
      if (index - 1 >= 0)
      {
        // if this is the first child
        //        
        result = (EditPart)children.get(index - 1);
      } 
    }
    return result;
  }     
}
