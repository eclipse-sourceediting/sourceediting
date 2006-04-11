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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.AbstractModelCollection;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;

public class ColumnEditPart extends BaseEditPart
{         
  protected int spacing = 20;  
  protected int minorAlignment = -1;
  
  protected IFigure createFigure()
  {
    Figure figure = new Figure();
    ToolbarLayout layout = new ToolbarLayout(false);
    if (minorAlignment != -1)
    {  
      layout.setMinorAlignment(minorAlignment);
    }  
    layout.setStretchMinorAxis(false);
    layout.setSpacing(spacing);
    figure.setLayoutManager(layout);
    return figure;
  }
  
  public void setSpacing(int spacing)
  {
    this.spacing = spacing;
    if (figure != null)
    {  
      ((ToolbarLayout)figure.getLayoutManager()).setSpacing(spacing);
    }  
  }
  
  public IComplexType getComplexType()
  {
    return (IComplexType)getModel();   
  }

  protected void createEditPolicies()
  {
    // TODO Auto-generated method stub
  }
  
  protected List getModelChildren()
  { 
    AbstractModelCollection collection = (AbstractModelCollection)getModel();
    return collection.getChildren();
  }

  public int getMinorAlignment()
  {
    return minorAlignment;
  }

  public void setMinorAlignment(int minorAlignment)
  {
    this.minorAlignment = minorAlignment;
  }
}



