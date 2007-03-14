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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.util.Assert;
import org.eclipse.wst.xsd.ui.internal.adt.design.ADTFloatingToolbar.ADTFloatingToolbarModel;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.AbstractModelCollection;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.Compartment;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.FocusTypeColumn;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.RootHolder;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;

public class ADTEditPartFactory implements EditPartFactory
{
  public EditPart createEditPart(EditPart context, Object model)
  {
    EditPart child = doCreateEditPart(context, model);
    checkChild(child, model);
    return child;
  }
  
  protected EditPart doCreateEditPart(EditPart context, Object model)
  {
    EditPart child = null;
    if (model instanceof Compartment)
    {
      child = new CompartmentEditPart();
    }
    else if (model instanceof RootHolder)
    {
      child = new RootHolderEditPart();
    }
    else if (model instanceof ADTFloatingToolbarModel)
    {
      child = new ADTFloatingToolbarEditPart(((ADTFloatingToolbarModel)model).getModel());
    }
    else if (model instanceof AbstractModelCollection)
    {
      child = new ColumnEditPart();
      if (model instanceof FocusTypeColumn)
      {
        ColumnEditPart columnEditPart = (ColumnEditPart)child;
        columnEditPart.setSpacing(60);
        columnEditPart.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
      }  
    }
    else if (model instanceof IComplexType)
    {
      child = new ComplexTypeEditPart();
    }
    else if (model instanceof IStructure)
    {
      child = new StructureEditPart();
    }  
    else if (model instanceof IField)
    {
      if (context instanceof CompartmentEditPart)
      {  
        child = new FieldEditPart();
      }
      else
      {
        child = new TopLevelFieldEditPart();
      }  
    }
    else if (model instanceof IModel)
    {
      child = new RootContentEditPart();
    }
    return child;   
  }

  /**
   * Subclasses can override and not check for null
   * 
   * @param child
   * @param model
   */
  protected void checkChild(EditPart child, Object model)
  {
    if (child == null)
    {
      // Thread.dumpStack();
    }
    Assert.isNotNull(child);
    child.setModel(model);
  }
}
