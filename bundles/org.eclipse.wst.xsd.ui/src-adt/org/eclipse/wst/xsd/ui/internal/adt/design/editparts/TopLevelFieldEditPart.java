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

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.BoxFigure;

public class TopLevelFieldEditPart extends BoxEditPart implements INamedEditPart
{
  protected ADTDirectEditPolicy adtDirectEditPolicy = new ADTDirectEditPolicy();
  
  protected boolean shouldDrawConnection()
  {
    IField field = (IField)getModel();
    IType type = field.getType();
    return (type != null);
  }
  
  public TypeReferenceConnection createConnectionFigure()
  {
    TypeReferenceConnection connectionFigure = null;
    IField field = (IField)getModel();
    IType type = field.getType();
    if (type != null)
    {      
      AbstractGraphicalEditPart referenceTypePart = (AbstractGraphicalEditPart)getViewer().getEditPartRegistry().get(type);
      if (referenceTypePart != null)
      {
        connectionFigure = new TypeReferenceConnection();   
        connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(getFigure(), CenteredConnectionAnchor.RIGHT, 0));
        int targetAnchorYOffset = 12;        
        connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(referenceTypePart.getFigure(), CenteredConnectionAnchor.HEADER_LEFT, 0, targetAnchorYOffset)); 
        connectionFigure.setHighlight(false);
      }
    }    
    return connectionFigure;
  }  
  
  protected void createEditPolicies()
  {
    super.createEditPolicies();
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, adtDirectEditPolicy);
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
  }

  protected void refreshVisuals()
  {
    IField field = (IField)getModel();
    BoxFigure boxFigure = (BoxFigure)getFigure();
    boxFigure.getNameLabel().setText(field.getName());
    super.refreshVisuals();
  }
  
  public Label getNameLabelFigure()
  {
    BoxFigure boxFigure = (BoxFigure)getFigure();
    return boxFigure.getNameLabel();
  }

  public void performDirectEdit(Point cursorLocation)
  {
   
  }
  
  public void performRequest(Request request)
  {  
    if (request.getType() == RequestConstants.REQ_DIRECT_EDIT||
        request.getType() == RequestConstants.REQ_OPEN)
    {
//      if (request instanceof LocationRequest)
//      {
//        LocationRequest locationRequest = (LocationRequest)request;
//        Point p = locationRequest.getLocation();
//       
//        if (hitTest(getNameLabelFigure(), p))
//        {
//          LabelEditManager manager = new LabelEditManager(this, new LabelCellEditorLocator(this, p));
//          NameUpdateCommandWrapper wrapper = new NameUpdateCommandWrapper();
//          adtDirectEditPolicy.setUpdateCommand(wrapper);
//          manager.show();
//        }
//      }
    }
  }
}
