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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IFeedbackHandler;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ADTMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.design.figures.CenteredIconFigure;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class BackToSchemaEditPart extends BaseEditPart implements IFeedbackHandler
{
  protected MultiPageEditorPart multipageEditor;
  protected ADTDirectEditPolicy adtDirectEditPolicy = new ADTDirectEditPolicy();
  protected boolean isEnabled;
  protected CenteredIconFigure backToSchema;
  protected MouseListener mouseListener;

  public BackToSchemaEditPart(ADTMultiPageEditor multipageEditor)
  {
    super();
    this.multipageEditor = multipageEditor;
  }

  protected IFigure createFigure()
  {
    backToSchema = new CenteredIconFigure();
    backToSchema.setBackgroundColor(ColorConstants.white);
    backToSchema.image = XSDEditorPlugin.getPlugin().getIcon("elcl16/schemaview_co.gif");
    // TODO, look at why the editpolicy doesn't work
    mouseListener = new MouseListener()
    {
      public void mouseDoubleClicked(org.eclipse.draw2d.MouseEvent me)
      {

      }

      public void mousePressed(org.eclipse.draw2d.MouseEvent me)
      {
        if (isEnabled)
        {
          addFeedback();
        }
      }

      public void mouseReleased(org.eclipse.draw2d.MouseEvent me)
      {
        if (isEnabled)
        {
          removeFeedback();
          SetInputToGraphView action = new SetInputToGraphView(multipageEditor, getModel());
          action.run();
        }
      }
    };
    backToSchema.addMouseListener(mouseListener);
    return backToSchema;
  }
  
  
  public void setEnabled(boolean isEnabled)
  {
    this.isEnabled = isEnabled;
    refreshVisuals();
  }

  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, adtDirectEditPolicy);
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
  }

  public void performRequest(Request request)
  {
    if (request.getType() == RequestConstants.REQ_DIRECT_EDIT || request.getType() == RequestConstants.REQ_OPEN)
    {
      SetInputToGraphView action = new SetInputToGraphView(multipageEditor, getModel());
      action.run();
    }
  }

  protected void refreshVisuals()
  {
    super.refreshVisuals();
    CenteredIconFigure figure = (CenteredIconFigure) getFigure();
    if (isEnabled)
    {
      backToSchema.image = XSDEditorPlugin.getPlugin().getIcon("elcl16/schemaview_co.gif");
    }
    else
    {
      backToSchema.image = XSDEditorPlugin.getPlugin().getIcon("dlcl16/schemaview_co.gif");
    }
    figure.refresh();
  }

  public void addFeedback()
  {
    CenteredIconFigure figure = (CenteredIconFigure) getFigure();
    figure.setMode(CenteredIconFigure.SELECTED);
    figure.refresh();
  }

  public void removeFeedback()
  {
    CenteredIconFigure figure = (CenteredIconFigure) getFigure();
    figure.setMode(CenteredIconFigure.NORMAL);
    figure.refresh();
  }
  
  public void deactivate()
  {
    backToSchema.removeMouseListener(mouseListener);
    super.deactivate();
  }

}
