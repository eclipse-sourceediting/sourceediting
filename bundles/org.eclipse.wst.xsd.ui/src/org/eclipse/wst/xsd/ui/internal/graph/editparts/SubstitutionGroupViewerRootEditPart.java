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
                                      
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.EditPart;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerLayout;

              

public class SubstitutionGroupViewerRootEditPart extends BaseEditPart
{
  protected final static String MESSAGE_PLACE_HOLDER = "MESSAGE_PLACE_HOLDER";
  protected Object input;              

  public void setInput(Object input)
  {
    this.input = input;
    refreshChildren();
  }

  protected IFigure createFigure()
  {
    Panel panel = new Panel();
    ContainerLayout layout = new ContainerLayout();
    layout.setBorder(60);
    panel.setLayoutManager(layout);
    return panel;
  }   
           

  protected List getModelChildren() 
  {               
    List list = new ArrayList();
    if (input != null)
    {
      list.add(input);
    }
    else
    {
      list.add(MESSAGE_PLACE_HOLDER);
    }
    return list;
  }         

  protected EditPart createChild(Object model) 
  { 
    EditPart editPart = null;
    if (model == MESSAGE_PLACE_HOLDER)
    {
      editPart = new MessageEditPart();
      editPart.setModel(model);
    }
    else
    {
      editPart = super.createChild(model);
    }
    return editPart;
  } 

  protected void createEditPolicies()
  {  
  }             
}
