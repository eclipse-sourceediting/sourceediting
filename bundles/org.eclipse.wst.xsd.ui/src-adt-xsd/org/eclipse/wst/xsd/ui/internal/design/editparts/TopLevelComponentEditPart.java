/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.AbstractEditPartViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaDirectiveAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.IAutoDirectEdit;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.INamedEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.RootContentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IFeedbackHandler;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.IADTUpdateCommand;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.FieldFigure;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.GraphNodeDragTracker;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.TopLevelComponentLabelCellEditorLocator;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.TopLevelNameDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.design.figures.HyperLinkLabel;
import org.eclipse.wst.xsd.ui.internal.design.layouts.FillLayout;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.utils.OpenOnSelectionHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.impl.XSDImportImpl;

public class TopLevelComponentEditPart extends BaseEditPart implements IFeedbackHandler, INamedEditPart, IAutoDirectEdit
{
  protected Label label;
  // protected Label arrowLabel;
  protected Figure labelHolder = new Figure();
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;
  protected ADTDirectEditPolicy adtDirectEditPolicy = new ADTDirectEditPolicy();
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();
  protected boolean isReadOnly;
  protected boolean isSelected;
  private Font italicFont;

  protected IFigure createFigure()
  {
    Figure typeGroup = new Figure()
    {
      public void paint(Graphics graphics)
      {
        super.paint(graphics);
        if (hasFocus)
        {
          try
          {
            graphics.pushState();
            Rectangle r = getBounds();
            graphics.setXORMode(true);
            graphics.drawFocus(r.x, r.y + 1, r.width - 1, r.height - 2);
          }
          finally
          {
            graphics.popState();
          }
        }
        
      }
    };
    typeGroup.setLayoutManager(new ToolbarLayout());

    labelHolder = new Figure();
    FillLayout fillLayout = new FillLayout();
    labelHolder.setLayoutManager(fillLayout);
    typeGroup.add(labelHolder);

    label = new HyperLinkLabel();
    label.setOpaque(true);
    label.setBorder(new MarginBorder(1, 2, 2, 5));
    if (!isHighContrast)
      label.setForegroundColor(ColorConstants.black);
    labelHolder.add(label);

    return typeGroup;
  }

  public void deactivate()
  {
    if (italicFont != null)
    {
      italicFont.dispose();
      italicFont = null;
    }
    super.deactivate();
  }

  public void refreshVisuals()
  {
    XSDBaseAdapter adapter = (XSDBaseAdapter) getModel();
    if (adapter != null)
    {
      isReadOnly = adapter.isReadOnly();
      label.setForegroundColor(computeLabelColor());
      label.setText(adapter.getText());
      Image image = adapter.getImage();
      if (image != null)
      {
        label.setIcon(XSDCommonUIUtils.getUpdatedImage((XSDConcreteComponent) adapter.getTarget(), image, isReadOnly));
      }
      // arrowLabel.setVisible(Boolean.TRUE.equals(adapter.getProperty(getModel(),
      // "drillDown")));
      
      if (adapter.isAbstract())
      {
        if (italicFont == null)
        {
          Font font = label.getFont();
          italicFont = getItalicFont(font);
        }
        if (italicFont != null)
        {
          label.setFont(italicFont);
        }
      }
      else
      {
        label.setFont(label.getParent().getFont());
      }
    }
    else
    {
      label.setText(Messages._UI_GRAPH_UNKNOWN_OBJECT + getModel().getClass().getName());
      // arrowLabel.setVisible(false);
    }

    if (reselect)
    {
      getViewer().select(this);
      setReselect(false);
    }
  }

  public List getModelChildren()
  {
    return Collections.EMPTY_LIST;
  }
  
  public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
  {
    return ((BaseEditPart)this.getParent()).doGetRelativeEditPart(editPart, direction);
  }

  protected void createEditPolicies()
  {
    super.createEditPolicies();
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

    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, adtDirectEditPolicy);
  }

  public Color computeLabelColor()
  {
    Color color = ColorConstants.black;
    if (isHighContrast)
    { 
      color = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
    }

    if (isSelected)
    {
      if (isHighContrast)
      {
        color = ColorConstants.listBackground;
      }
      else
      {
        color = ColorConstants.black;
      }
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

    if (isHighContrast)
    {
      labelHolder.setBackgroundColor(ColorConstants.listForeground);
    }
    else
    {
      labelHolder.setBackgroundColor(FieldFigure.cellColor);
    }
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
    // Do not open on or set focus on direct edit type 
    if (request.getType() == RequestConstants.REQ_OPEN)
    {

      Object model = getModel();
      if (model instanceof IGraphElement)
      {
        if (((IGraphElement)model).isFocusAllowed())
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
      else if (model instanceof XSDSchemaDirectiveAdapter)
      {
        if (request instanceof LocationRequest)
        {
          LocationRequest locationRequest = (LocationRequest) request;
          Point p = locationRequest.getLocation();

          if (hitTest(labelHolder, p))
          {
            XSDSchemaDirective dir = (XSDSchemaDirective)((XSDSchemaDirectiveAdapter)model).getTarget();
            String schemaLocation = "";
            // force load of imported schema
            if (dir instanceof XSDImportImpl)
            {
              ((XSDImportImpl)dir).importSchema();
            }
            if (dir.getResolvedSchema() != null)
            {
              schemaLocation = URIHelper.removePlatformResourceProtocol(dir.getResolvedSchema().getSchemaLocation());
              if (schemaLocation != null)
              {
                OpenOnSelectionHelper.openXSDEditor(dir.getResolvedSchema());
              }
            }
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
        EditPart editPart = ((AbstractEditPartViewer) getViewer()).getRootEditPart().getContents();
        if (editPart instanceof RootContentEditPart)
        {
          IEditorPart editorPart = getEditorPart();
//          ActionRegistry registry = (ActionRegistry) editorPart.getAdapter(ActionRegistry.class);
//          IAction action = registry.getAction(SetInputToGraphView.ID);
//          action.run();
          ActionRegistry registry = (ActionRegistry) editorPart.getAdapter(ActionRegistry.class);
          if (registry != null)
          {
            IAction action = registry.getAction(OpenInNewEditor.ID);
            if (action != null)
            action.run();
            return;
          }
        }
      }
    };
    Display.getCurrent().asyncExec(runnable);
  }
  
  public void doEditName(boolean addFromDesign)
  {
    if (!addFromDesign) return;
    
//    removeFeedback();

    Object object = ((XSDBaseAdapter) getModel()).getTarget();
    if (object instanceof XSDNamedComponent)
    {
      Point p = label.getLocation();
      TopLevelNameDirectEditManager manager = new TopLevelNameDirectEditManager(TopLevelComponentEditPart.this, new TopLevelComponentLabelCellEditorLocator(TopLevelComponentEditPart.this, p), (XSDNamedComponent) object);
      NameUpdateCommandWrapper wrapper = new NameUpdateCommandWrapper();
      adtDirectEditPolicy.setUpdateCommand(wrapper);
      manager.show();
    }
  }
  
  class NameUpdateCommandWrapper extends Command implements IADTUpdateCommand
  {
    Command command;
    protected DirectEditRequest request;
    
    public NameUpdateCommandWrapper()
    {
      super(Messages._UI_ACTION_UPDATE_NAME);
    }

    public void setRequest(DirectEditRequest request)
    {
      this.request = request;
    }
    
    public void execute()
    {
      XSDBaseAdapter adapter = (XSDBaseAdapter)getModel();
      Object newValue = request.getCellEditor().getValue();
      if (newValue instanceof String && ((String)newValue).length() > 0)
      {
        UpdateNameCommand command = new UpdateNameCommand(Messages._UI_ACTION_UPDATE_NAME, (XSDNamedComponent)adapter.getTarget(), (String)newValue);
        if (command != null)
          command.execute();
      }
     }
  }

  static boolean reselect = false;

  public void setReselect(boolean state)
  {
    reselect = state;
  }

  public Label getNameLabelFigure()
  {
    return label;
  }

  public void performDirectEdit(Point cursorLocation)
  {
   
  }
  
  public void setSelected(int value)
  {
    // if it is selected, we want to scroll to it
    if (doScroll)
      setScroll(true);
    super.setSelected(value);
  }

  public void setFocus(boolean b)
  {
    super.setFocus(b);
    hasFocus = b;
    getFigure().repaint();
  }
  
  public DragTracker getDragTracker(Request request)
  {
    return new GraphNodeDragTracker(this);
  }

  public String getReaderText()
  {
	  return getNameLabelFigure().getText();
  }

}
