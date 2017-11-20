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
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAttributeGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSimpleTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ADTEditPartFactory;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ColumnEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CompartmentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.Annotation;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.ICompartmentFigure;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IFieldFigure;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.TypeVizFigureFactory;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.SpaceFiller;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.TargetConnectionSpaceFiller;
import org.eclipse.wst.xsd.ui.internal.design.figures.IExtendedFigureFactory;
import org.eclipse.wst.xsd.ui.internal.design.figures.IModelGroupFigure;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;

public class XSDEditPartFactory extends ADTEditPartFactory implements IExtendedFigureFactory
{
  protected IExtendedFigureFactory delegate;
  
  public XSDEditPartFactory()
  {
    delegate = XSDEditorPlugin.getPlugin().getXSDEditorConfiguration().getFigureFactory();
    if (delegate == null)
      delegate = new TypeVizFigureFactory();
  }
  
  public XSDEditPartFactory(IExtendedFigureFactory figureFactory)
  {
    delegate = figureFactory;
  }
  

  public EditPart doCreateEditPart(EditPart context, Object model)
  {
    EditPart child = null;
    // Override edit part where desired
    
    if (model instanceof IField)
    {
      if (model instanceof SpaceFiller)
      {
        child = new SpaceFillerForFieldEditPart();
      }
      else if (context instanceof CompartmentEditPart)
      {  
        child = new XSDBaseFieldEditPart();
      }
    }
    else if (model instanceof XSDSchemaAdapter)
    {
      child = new XSDSchemaEditPart();
    }
    else if (model instanceof CategoryAdapter)
    {
      child = new CategoryEditPart();
    }
    else if (model instanceof XSDSimpleTypeDefinitionAdapter)
    {
      child = new XSDSimpleTypeEditPart();
    }
    else if (model instanceof XSDModelGroupAdapter)
    {
      child = new ModelGroupEditPart();
    }
    else if (model instanceof Annotation)
    {
      Annotation annotation = (Annotation) model;
      String kind = annotation.getCompartment().getKind();
      if (kind.equals("element")) //$NON-NLS-1$
      {
        child = new XSDGroupsForAnnotationEditPart();
      }
      else if (kind.equals("attribute")) //$NON-NLS-1$
      {
        child = new XSDAttributesForAnnotationEditPart();
      }
    }
    else if (!(context instanceof ColumnEditPart))
    {   
      if (model instanceof TargetConnectionSpaceFiller)
      {
        child = new TargetConnectionSpacingFigureEditPart();
      }
      else if (model instanceof XSDModelGroupDefinitionAdapter)
      {
        child = new ModelGroupDefinitionReferenceEditPart();
      }
      else if (model instanceof XSDAttributeGroupDefinitionAdapter)
      {
        child = new AttributeGroupDefinitionEditPart();
      }
    }
    // if we don't have a specialzied XSD edit part to create
    // then we simply call the super class to create a generic ADT edit part
    //
    if (child == null)
    {
      child = super.doCreateEditPart(context, model);
    }

    // if at this this point we have not created an edit part we simply
    // create a placeholder edit part to provide the most robust behaviour possible
    //    
    if (child == null)
    {
      // TODO (cs) log an error message here, since we shouldn't really get here 
      child = new SpaceFillerForFieldEditPart();
    }  
    return child;
  }

  public ICompartmentFigure createCompartmentFigure(Object model)
  {
    return delegate.createCompartmentFigure(model);
  }
  
  public IStructureFigure createStructureFigure(Object model)
  {
    IStructureFigure figure = delegate.createStructureFigure(model);
    if (model instanceof XSDBaseAdapter)
    {
      XSDConcreteComponent comp = (XSDConcreteComponent) ((XSDBaseAdapter)model).getTarget();
      boolean isReadOnly = ((XSDBaseAdapter)model).isReadOnly();
      figure.getNameLabel().setIcon(XSDCommonUIUtils.getUpdatedImage( comp, ((XSDBaseAdapter)model).getImage(), isReadOnly));
    }
    return figure;
  }  

  public IFieldFigure createFieldFigure(Object model)
  {
    return delegate.createFieldFigure(model);
  }
  
  public IModelGroupFigure createModelGroupFigure(Object model)
  {
    return delegate.createModelGroupFigure(model);
  }
}
