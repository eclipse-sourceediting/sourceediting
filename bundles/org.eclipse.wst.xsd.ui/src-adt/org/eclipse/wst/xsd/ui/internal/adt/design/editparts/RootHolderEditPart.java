/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.FocusTypeColumn;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.ReferencedTypeColumn;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.RootHolder;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;

public class RootHolderEditPart extends BaseEditPart implements IHolderEditPart
{
  protected Panel panel;
  
  public RootHolderEditPart()
  {
  }

  protected IFigure createFigure()
  {
    panel = new Panel();    
    ToolbarLayout layout = new ToolbarLayout(true);
    layout.setStretchMinorAxis(false);
    layout.setSpacing(100);
    panel.setLayoutManager(layout);    

    return panel;
  }

  protected List getModelChildren()
  {
    List collections = new ArrayList();
    RootHolder holder = (RootHolder)getModel();
    IADTObject focusObject = holder.getModel();
    collections.add(new FocusTypeColumn(focusObject));
    collections.add(new ReferencedTypeColumn(focusObject));
    return collections;
  }

  protected void createEditPolicies()
  {
    super.createEditPolicies();
  }

  public boolean isSelectable()
  {
    return false;
  }
}
