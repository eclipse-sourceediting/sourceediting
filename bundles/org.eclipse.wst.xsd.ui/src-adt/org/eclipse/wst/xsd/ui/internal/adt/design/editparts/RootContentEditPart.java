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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.FocusTypeColumn;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.ReferencedTypeColumn;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.design.layouts.ContainerLayout;

public class RootContentEditPart extends AbstractGraphicalEditPart
{
  List collections = null;
  Figure contentPane;
  
  protected IFigure createFigure()
  {    
    Panel panel = new Panel();    
    // why do we need to use a container layout? can we just set a
    // margin border and get the same effect?
    ContainerLayout clayout = new ContainerLayout();
    clayout.setBorder(60);
    panel.setLayoutManager(clayout);
    
    contentPane = new Figure();
    panel.add(contentPane);
        
    ToolbarLayout layout = new ToolbarLayout(true);
    layout.setStretchMinorAxis(false);
    layout.setSpacing(100);
    contentPane.setLayoutManager(layout);
    return panel;
  }
  
  public IFigure getContentPane()
  {
    return contentPane;
  }
  
  
  public IComplexType getSelectedComplexType()
  {
    IComplexType result = null;
    IModel model = (IModel)getModel();
    List types = model.getTypes();
    if (types.size() > 0)
    {
      if (types.get(0) instanceof IComplexType) 
        result = (IComplexType)types.get(0);
    }  
    return result;
  }

  protected void createEditPolicies()
  {
    // TODO Auto-generated method stub
  }
  
  protected List getModelChildren()
  { 
    collections = new ArrayList();
    if (getModel() != null)
    {
      Object obj = getModel();
      IADTObject focusObject = null;
      if (obj instanceof IStructure)
      {
        if (obj instanceof IGraphElement)
        {
          if (((IGraphElement)obj).isFocusAllowed())
            focusObject = (IStructure)obj;          
        }
      }
      else if (obj instanceof IField)
      {
        focusObject = (IField)obj;
      }  
      else if (obj instanceof IModel)
      {
        focusObject = (IModel)obj;
        collections.add(focusObject);
        return collections;
      }
      else if (obj instanceof IType)
      {
        if (obj instanceof IGraphElement)
        {
          if (((IGraphElement)obj).isFocusAllowed())
          {
            focusObject = (IType)obj;
          }
        }
      }
      if (focusObject != null)
      {
        collections.add(new FocusTypeColumn(focusObject));
        collections.add(new ReferencedTypeColumn(focusObject));
      }
    }
    return collections;
  }
  
  public void setInput(Object component)
  {
    setModel(component);
    refresh();
  }
  
  public Object getInput()
  {
    return getModel();
  }
  
  public void refresh()
  {
    super.refresh();
    /*
    // once we're done refreshing we can assume all of the child editparts
    // now we iteratre thru the list again and tell the children to update
    // their connections
    for (Iterator i = getChildren().iterator(); i.hasNext(); )
    {
      Object obj = i.next();
      if (obj instanceof IConnectionContainer)
      {
        ((IConnectionContainer)obj).refreshConnections();
      }
    }*/
  }
}
