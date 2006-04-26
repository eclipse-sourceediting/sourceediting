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
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSimpleTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseTypeConnectingEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.TypeReferenceConnection;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.StructureFigure;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class XSDSimpleTypeEditPart extends BaseTypeConnectingEditPart
{
  StructureFigure figure;
  protected ADTDirectEditPolicy adtDirectEditPolicy = new ADTDirectEditPolicy();
  
  public XSDSimpleTypeEditPart()
  {
    super();
  }
  
  public XSDSimpleTypeDefinition getXSDSimpleTypeDefinition()
  {
    return (XSDSimpleTypeDefinition)((XSDSimpleTypeDefinitionAdapter)getModel()).getTarget();
  }

  protected IFigure createFigure()
  {
    figure = new StructureFigure();
    figure.setBorder(new RoundedLineBorder(1, 10));    
    ToolbarLayout toolbarLayout = new ToolbarLayout();
    toolbarLayout.setStretchMinorAxis(true);
    figure.setLayoutManager(toolbarLayout);
//    figure.getHeadingFigure().getLabel().setIcon(XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif")); //$NON-NLS-1$
    return figure;
  }
  
  protected void refreshVisuals()
  {
    XSDSimpleTypeDefinitionAdapter adapter = (XSDSimpleTypeDefinitionAdapter)getModel();
    String name = adapter.getDisplayName();
    figure.headingFigure.setIsReadOnly(adapter.isReadOnly());
    figure.headingFigure.getLabel().setText(name);
    if (adapter.isReadOnly())
    {
      figure.getHeadingFigure().getLabel().setIcon(XSDEditorPlugin.getPlugin().getIcon("obj16/simpletypedis_obj.gif")); //$NON-NLS-1$
    }
    else
    {
      figure.getHeadingFigure().getLabel().setIcon(XSDEditorPlugin.getPlugin().getIcon("obj16/simpletype_obj.gif")); //$NON-NLS-1$
    }
  }
  
  public IStructureFigure getStructureFigure()
  {
    return (IStructureFigure)getFigure();
  }

  public IFigure getContentPane()
  {
    return getStructureFigure().getContentPane();
  }
  
  
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, adtDirectEditPolicy);
  }

  public void addFeedback()
  {
    getStructureFigure().addSelectionFeedback();
    super.addFeedback();
  }
  
  public void removeFeedback()
  {
    getStructureFigure().removeSelectionFeedback();
    super.removeFeedback();    
  }

  public ReferenceConnection createConnectionFigure(BaseEditPart child)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public TypeReferenceConnection createConnectionFigure()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
