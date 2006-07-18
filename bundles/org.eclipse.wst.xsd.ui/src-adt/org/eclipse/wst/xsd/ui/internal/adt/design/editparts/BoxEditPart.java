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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.Compartment;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.BoxFigure;

public abstract class BoxEditPart extends BaseTypeConnectingEditPart //IFeedbackHandler
{  
  protected List compartmentList = null;
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();

  protected Compartment[] getCompartments()
  {
    return null;
  }
  
  protected IFigure createFigure()
  {
    BoxFigure figure = new BoxFigure();
    LineBorder boxLineBorder = new LineBorder(1);
    figure.setBorder(boxLineBorder);    
    ToolbarLayout toolbarLayout = new ToolbarLayout();
    toolbarLayout.setStretchMinorAxis(true);
    figure.setLayoutManager(toolbarLayout);
    // we should organize ITreeElement and integrate it with the facade
    if (getModel() instanceof ITreeElement)
    {
      figure.getNameLabel().setIcon(((ITreeElement)getModel()).getImage());
    }
    return figure;
  }
  
  public IFigure getContentPane()
  {
    return ((BoxFigure)getFigure()).getContentPane();
  }
    
  protected void createEditPolicies()
  {
    super.createEditPolicies();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, simpleDirectEditPolicy);
  }
  
   
  public void addFeedback()
  {
    BoxFigure boxFigure = (BoxFigure)figure;
    LineBorder boxFigureLineBorder = (LineBorder)boxFigure.getBorder();
    boxFigureLineBorder.setWidth(2);
    boxFigureLineBorder.setColor(ColorConstants.darkBlue);  
    boxFigure.getHeadingFigure().setSelected(true);
    figure.repaint();
    super.addFeedback();
  }
  
  public void removeFeedback()
  {
    BoxFigure boxFigure = (BoxFigure)figure;
    LineBorder boxFigureLineBorder = (LineBorder)boxFigure.getBorder();
    boxFigureLineBorder.setWidth(1);
    boxFigureLineBorder.setColor(ColorConstants.black);
    boxFigure.getHeadingFigure().setSelected(false);
    figure.repaint();
    super.removeFeedback();    
  }
  
  protected ActionRegistry getEditorActionRegistry(IEditorPart editor)
  {
    return (ActionRegistry) editor.getAdapter(ActionRegistry.class);
  }
}  
