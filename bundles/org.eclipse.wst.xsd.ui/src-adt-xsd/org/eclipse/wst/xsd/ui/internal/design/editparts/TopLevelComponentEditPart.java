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

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.ui.parts.AbstractEditPartViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDComplexTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IFeedbackHandler;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.FieldFigure;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.design.layouts.FillLayout;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class TopLevelComponentEditPart extends BaseEditPart implements IFeedbackHandler
{
  protected Label label;
  // protected Label arrowLabel;
  protected Figure labelHolder = new Figure();
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();
  protected boolean isReadOnly;
  protected boolean isSelected;
  protected Font font;

  protected IFigure createFigure()
  {
    Figure typeGroup = new Figure();
    typeGroup.setLayoutManager(new ToolbarLayout());

    labelHolder = new Figure();
    FillLayout fillLayout = new FillLayout();
    labelHolder.setLayoutManager(fillLayout);
    typeGroup.add(labelHolder);

    label = new Label();
    label.setOpaque(true);
    label.setBorder(new MarginBorder(0, 2, 2, 1));
    label.setForegroundColor(ColorConstants.black);
    labelHolder.add(label);

    try
    {
      // evil hack to provide underlines
      Object model = getModel();

      boolean isLinux = java.io.File.separator.equals("/");
      if (model instanceof XSDComplexTypeDefinitionAdapter || model instanceof XSDElementDeclarationAdapter || model instanceof XSDModelGroupDefinitionAdapter)
      {
        if (!isLinux)
        {
          FontData oldData = ((GraphicalEditPart) getParent()).getFigure().getFont().getFontData()[0]; // GraphicsConstants.medium.getFontData()[0];
          FontData fontData = new FontData(oldData.getName(), oldData.getHeight(), SWT.NONE);

          // TODO... clean this awful code up... we seem to be leaking here too
          // we can't call this directly since the methods are OS dependant
          // fontData.data.lfUnderline = 1
          // so instead we use reflection
          Object data = fontData.getClass().getField("data").get(fontData);
          // System.out.println("data" + data.getClass());
          data.getClass().getField("lfUnderline").setByte(data, (byte) 1);
          font = new Font(Display.getCurrent(), fontData);
          label.setFont(font);
        }
      }
    }
    catch (Exception e)
    {

    }

    return typeGroup;
  }

  public void deactivate()
  {
    super.deactivate();
    if (font != null)
    {
      font.dispose();
      font = null;
    }
  }

  public void refreshVisuals()
  {
    XSDBaseAdapter adapter = (XSDBaseAdapter) getModel();
    if (adapter != null)
    {
      isReadOnly = adapter.isReadOnly();
      label.setForegroundColor(computeLabelColor());
      label.setText((String) adapter.getText());
      Image image = (Image) adapter.getImage();
      if (image != null)
        label.setIcon(image);
      // arrowLabel.setVisible(Boolean.TRUE.equals(adapter.getProperty(getModel(),
      // "drillDown")));
    }
    else
    {
      label.setText(XSDEditorPlugin.getXSDString("_UI_GRAPH_UNKNOWN_OBJECT") + getModel().getClass().getName());
      // arrowLabel.setVisible(false);
    }

    if (reselect)
    {
      getViewer().select(this);
      setReselect(false);
    }
  }

  // public XSDNamedComponent getXSDNamedComponent()
  // {
  // return (XSDNamedComponent) getModel();
  // }

  public List getModelChildren()
  {
    return Collections.EMPTY_LIST;
  }

  protected void createEditPolicies()
  {
    // installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new
    // NonResizableEditPolicy());
    // selectionHandlesEditPolicy = new SelectionHandlesEditPolicyImpl();
    // installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
    // selectionHandlesEditPolicy);

    SelectionHandlesEditPolicyImpl policy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, policy);

    SelectionEditPolicy feedBackSelectionEditPolicy = new SelectionEditPolicy()
    {
      protected void hideSelection()
      {
        EditPart editPart = getHost();
        if (editPart instanceof IFeedbackHandler)
        {
          ((IFeedbackHandler) editPart).removeFeedback();
        }
      }

      protected void showSelection()
      {
        EditPart editPart = getHost();
        if (editPart instanceof IFeedbackHandler)
        {
          ((IFeedbackHandler) editPart).addFeedback();
        }
      }
    };
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, feedBackSelectionEditPolicy);

    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, simpleDirectEditPolicy);
  }

  public Color computeLabelColor()
  {
    Color color = ColorConstants.black;
    if (isSelected)
    {
      color = ColorConstants.black;
    }
    else if (isReadOnly)
    {
      color = ColorConstants.gray;
    }
    return color;
  }

  public void addFeedback()
  {
    isSelected = true;

    labelHolder.setBackgroundColor(FieldFigure.cellColor);
    label.setForegroundColor(computeLabelColor());
    // labelHolder.setFill(true);

    if (doScroll)
    {
      CategoryEditPart categoryEP = (CategoryEditPart) getParent();
      categoryEP.scrollTo(this);
      setScroll(false);
    }
  }

  private boolean doScroll = false;

  public void setScroll(boolean doScroll)
  {
    this.doScroll = doScroll;
  }

  public void removeFeedback()
  {
    isSelected = false;
    labelHolder.setBackgroundColor(null);
    label.setForegroundColor(computeLabelColor());
    // labelHolder.setFill(false);
  }

  public void performRequest(Request request)
  {
    if (request.getType() == RequestConstants.REQ_DIRECT_EDIT || request.getType() == RequestConstants.REQ_OPEN)
    {

      Object model = getModel();
      if (model instanceof XSDComplexTypeDefinitionAdapter || model instanceof XSDElementDeclarationAdapter || model instanceof XSDModelGroupDefinitionAdapter)
      {
        if (request instanceof LocationRequest)
        {
          LocationRequest locationRequest = (LocationRequest) request;
          Point p = locationRequest.getLocation();

          if (hitTest(labelHolder, p))
          {
            performDrillDownAction();
          }
        }
      }
    }
  }

  public boolean hitTest(IFigure target, Point location)
  {
    Rectangle b = target.getBounds().getCopy();
    target.translateToAbsolute(b);
    return b.contains(location);
  }

  protected void performDrillDownAction()
  {
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        // ((XSDComponentViewer)getViewer()).setInput((XSDConcreteComponent)getModel());

        EditPart editPart = ((AbstractEditPartViewer) getViewer()).getRootEditPart().getContents();
        if (editPart instanceof RootContentEditPart)
        {
          RootContentEditPart rootEditPart = (RootContentEditPart) editPart;
          rootEditPart.setInput(getModel());
        }
        // else if (editPart instanceof BaseEditPart)
        // {
        // ((XSDComponentViewer)getViewer()).setInput((XSDConcreteComponent)getModel());
        // }
      }
    };
    Display.getCurrent().asyncExec(runnable);
  }

  public void doEditName()
  {
    removeFeedback();
    Object object = getModel();
    // if (object instanceof XSDNamedComponent)
    // {
    // ComponentNameDirectEditManager manager = new
    // ComponentNameDirectEditManager(this, label, (XSDNamedComponent)object);
    // simpleDirectEditPolicy.setDelegate(manager);
    // manager.show();
    // }
  }

  static boolean reselect = false;

  public void setReselect(boolean state)
  {
    reselect = state;
  }
}
