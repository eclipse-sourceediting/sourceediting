/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ADTFloatingToolbarEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;

public class ADTFloatingToolbar extends GraphicalViewerImpl
{
  protected IModel model;
  protected boolean isDrilledDown;
  protected ADTFloatingToolbarEditPart editPart;

  public ADTFloatingToolbar(IModel model)
  {
    this.model = model;
  }

  public void setModel(IModel model)
  {
    this.model = model;
    editPart = (ADTFloatingToolbarEditPart)getEditPartFactory().createEditPart(null, new ADTFloatingToolbarModel(model));
    if (editPart == null)
    {
      editPart = new ADTFloatingToolbarEditPart(model);
    }
    editPart.setModel(model);
    setContents(editPart);
  }

  public Control createControl(Composite composite)
  {
    Canvas canvas = new Canvas(composite, SWT.NONE);
    canvas.setBackground(ColorConstants.white);
    setControl(canvas);
    return getControl();
  }

  public void refresh(boolean isDrilledDown)
  {
    this.isDrilledDown = isDrilledDown;
    if (editPart != null) {
    	editPart.setIsDrilledDown(isDrilledDown);
    }
    EditPart contents = getContents();
    if (contents != null) {
    	contents.refresh();
    }
  }
  
  public class ADTFloatingToolbarModel
  {
    IModel model;
    public ADTFloatingToolbarModel(IModel model)
    {
      this.model = model;
    }
    
    public IModel getModel()
    {
      return model;
    }
  }
}
