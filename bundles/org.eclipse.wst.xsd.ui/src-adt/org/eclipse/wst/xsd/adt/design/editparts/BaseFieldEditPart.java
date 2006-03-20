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
package org.eclipse.wst.xsd.adt.design.editparts;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.wst.xsd.adt.design.directedit.ComboBoxCellEditorManager;
import org.eclipse.wst.xsd.adt.design.directedit.LabelCellEditorLocator;
import org.eclipse.wst.xsd.adt.design.directedit.LabelEditManager;
import org.eclipse.wst.xsd.adt.design.directedit.TypeReferenceDirectEditManager;
import org.eclipse.wst.xsd.adt.design.editparts.model.FocusTypeColumn;
import org.eclipse.wst.xsd.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.adt.design.editpolicies.IADTUpdateCommand;
import org.eclipse.wst.xsd.adt.design.figures.IFieldFigure;
import org.eclipse.wst.xsd.adt.facade.IField;
import org.eclipse.wst.xsd.adt.facade.IType;

public class BaseFieldEditPart extends BaseTypeConnectingEditPart implements INamedEditPart
{
  protected TypeReferenceConnection connectionFigure;
  protected ADTDirectEditPolicy adtDirectEditPolicy = new ADTDirectEditPolicy();
  TypeUpdateCommand typeUpdateCommand = new TypeUpdateCommand();
  
  protected IFigure createFigure()
  {          
    IFieldFigure figure = getFigureFactory().createFieldFigure(getModel());    
    figure.setForegroundColor(ColorConstants.black);
    return figure;
  }
  
  public IFieldFigure getFieldFigure()
  {
    return (IFieldFigure)figure;
  }
  

  public void activate()
  {
    super.activate();
    activateConnection();
  }
  
  public void deactivate()
  {
    deactivateConnection();
    super.deactivate();
  }

  protected boolean shouldDrawConnection()
  {
    boolean result = false;
    
    // For now we only want to produce outbound lines from a Field to a Type
    // when the field in contained in the 'focus' edit part    
    for (EditPart parent = getParent(); parent != null; parent = parent.getParent())
    {  
      if (parent.getModel() instanceof FocusTypeColumn)
      {        
        result = true;
        break;
      }  
    }    
    return result;
  }
  
  public TypeReferenceConnection createConnectionFigure()
  {
    connectionFigure = null;
    IField field = (IField)getModel();
    IType type = field.getType();
    if (type != null) // && type.isComplexType())
    {      
      AbstractGraphicalEditPart referenceTypePart = (AbstractGraphicalEditPart)getViewer().getEditPartRegistry().get(type);
      if (referenceTypePart != null)
      {
        connectionFigure = new TypeReferenceConnection();

        if (getFigure().getParent() == referenceTypePart.getFigure())
        {
          connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(getFigure(), CenteredConnectionAnchor.LEFT, 1)); 
        }
        else
        {
          connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(getFigure(), CenteredConnectionAnchor.RIGHT, 5));
        }
        int targetAnchorYOffset = 16;

        connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(referenceTypePart.getFigure(), CenteredConnectionAnchor.HEADER_LEFT, 0, targetAnchorYOffset)); 
        connectionFigure.setHighlight(false);
      }
    }    
    return connectionFigure;
  }


  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, adtDirectEditPolicy);
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
  }

  public void refresh()
  {
    super.refresh();
    
    // todo... perhaps this is aggressive?
    // really we only need to update the connection when the IType changes
    
    deactivateConnection();
    activateConnection();      
  }

  protected void refreshVisuals()
  {
    super.refreshVisuals();
    IFieldFigure figure = getFieldFigure();
    IField field = (IField) getModel();
    
    figure.getNameLabel().setText(field.getName());
    figure.getTypeLabel().setText(field.getTypeName());
    figure.refreshVisuals(getModel());

    figure.recomputeLayout();

    ((GraphicalEditPart)getRoot()).getFigure().invalidateTree();
  }

  public DragTracker getDragTracker(Request request)
  {
    return super.getDragTracker(request);
  }
  
  /*
  public IAction[] getActions(Object object)
  {
    // when a FieldEditPart is selected it provides it's own actions
    // as well as those of it's parent 'type' edit part
    List list = new ArrayList();
    EditPart compartment = getParent();
    if (compartment != null)
    {  
      EditPart type = compartment.getParent();
      if (type != null && type instanceof IActionProvider)
      {
        IActionProvider provider = (IActionProvider)type;
        addActionsToList(list, provider.getActions(object));
      }
    }
    addActionsToList(list, super.getActions(object));
    IAction[] result = new IAction[list.size()];
    list.toArray(result);
    return result;
  }*/
  
  public Label getNameLabelFigure()
  {
    return getFieldFigure().getNameLabel();
  }

  public void performDirectEdit(Point cursorLocation)
  {
    
  }
  
  public void performRequest(Request request)
  {  
    if (request.getType() == RequestConstants.REQ_DIRECT_EDIT||
        request.getType() == RequestConstants.REQ_OPEN)
    {
      IFieldFigure fieldFigure = getFieldFigure();
      Object model = getModel();
      if (request instanceof LocationRequest)
      {
        LocationRequest locationRequest = (LocationRequest)request;
        Point p = locationRequest.getLocation();
       
        if (hitTest(fieldFigure.getTypeLabel(), p))
        {
          TypeReferenceDirectEditManager manager = new TypeReferenceDirectEditManager((IField)model, this, fieldFigure.getTypeLabel());
          typeUpdateCommand.setDelegate(manager);
          adtDirectEditPolicy.setUpdateCommand(typeUpdateCommand);
          manager.show();
        }
        else if (hitTest(fieldFigure.getNameLabel(), p))
        {
          LabelEditManager manager = new LabelEditManager(this, new LabelCellEditorLocator(this, p));
          NameUpdateCommandWrapper wrapper = new NameUpdateCommandWrapper();
          adtDirectEditPolicy.setUpdateCommand(wrapper);
          manager.show();
        }
      }
    }
  }
  
  
  class NameUpdateCommandWrapper extends Command implements IADTUpdateCommand
  {
    Command command;
    protected DirectEditRequest request;
    
    public NameUpdateCommandWrapper()
    {
      super("Update Name");
    }

    public void setRequest(DirectEditRequest request)
    {
      this.request = request;
    }
    
    public void execute()
    {
      IField field = (IField)getModel();
      Object newValue = request.getCellEditor().getValue();
      if (newValue instanceof String)
      {
        command = field.getUpdateNameCommand((String)newValue);
      }
      if (command != null)
        command.execute();
    }
  }
  
  class TypeUpdateCommand extends Command implements IADTUpdateCommand
  {
    protected ComboBoxCellEditorManager delegate;
    protected DirectEditRequest request;
    
    public TypeUpdateCommand()
    {
      super("Update type");
    }

    public void setDelegate(ComboBoxCellEditorManager delegate)
    {                                           
      this.delegate = delegate;
    }
    
    public void setRequest(DirectEditRequest request)
    {
      this.request = request;
    }
    
    public void execute()
    {
      if (delegate != null)
      {
        delegate.performEdit(request.getCellEditor());
      }
    }

    public boolean canExecute()
    {
      return true;
    }
  }

  TypeReferenceConnection connectionFeedbackFigure;

  public void addFeedback()
  {
    // Put back connection figure so it won't get overlayed by other non highlighted connections
    if (connectionFigure != null)
    {
      connectionFeedbackFigure = new TypeReferenceConnection();
      connectionFeedbackFigure.setSourceAnchor(connectionFigure.getSourceAnchor());
      connectionFeedbackFigure.setTargetAnchor(connectionFigure.getTargetAnchor());
      connectionFeedbackFigure.setHighlight(true);
      getLayer(LayerConstants.FEEDBACK_LAYER).add(connectionFeedbackFigure);
    }
     super.addFeedback();
     getFieldFigure().addSelectionFeedback();
  }
  
  public void removeFeedback()
  {
    if (connectionFeedbackFigure != null)
    {
      connectionFeedbackFigure.setHighlight(false);
      getLayer(LayerConstants.FEEDBACK_LAYER).remove(connectionFeedbackFigure);
    }
    connectionFeedbackFigure = null;
    super.removeFeedback();
    getFieldFigure().removeSelectionFeedback();
  }
}

