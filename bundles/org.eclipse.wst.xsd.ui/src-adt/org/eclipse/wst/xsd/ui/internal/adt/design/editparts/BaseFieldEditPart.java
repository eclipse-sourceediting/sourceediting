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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.directedit.ComboBoxCellEditorManager;
import org.eclipse.wst.xsd.ui.internal.adt.design.directedit.ElementReferenceDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.design.directedit.LabelCellEditorLocator;
import org.eclipse.wst.xsd.ui.internal.adt.design.directedit.LabelEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.design.directedit.TypeReferenceDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.FocusTypeColumn;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.IADTUpdateCommand;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IFieldFigure;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.design.editparts.ConnectableEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.TargetConnectionSpacingFigureEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.TargetConnectionSpaceFiller;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.GraphNodeDragTracker;
import org.eclipse.xsd.XSDNamedComponent;

public class BaseFieldEditPart extends BaseTypeConnectingEditPart implements INamedEditPart, IAutoDirectEdit
{
  protected TypeReferenceConnection connectionFigure;
  protected ADTDirectEditPolicy adtDirectEditPolicy = new ADTDirectEditPolicy();
  protected TypeReferenceConnection connectionFeedbackFigure;
  
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
  
  private EditPart getTargetEditPart(IType type)
  {
    ColumnEditPart columnEditPart = null;
    for (EditPart editPart = this; editPart != null; editPart = editPart.getParent())
    {
      if (editPart instanceof ColumnEditPart)
      {
        columnEditPart = (ColumnEditPart)editPart;
        break;
      }  
    }     
    if (columnEditPart != null)
    {
      // get the next column
      EditPart parent = columnEditPart.getParent();
      List columns = parent.getChildren();
      int index = columns.indexOf(columnEditPart);
      if (index + 1 < columns.size())
      {  
        EditPart nextColumn = (EditPart)columns.get(index + 1);
        for (Iterator i = nextColumn.getChildren().iterator(); i.hasNext(); )
        {
          EditPart child = (EditPart)i.next();
          if (child.getModel() == type)
          {
            return child;
          }  
        }  
      }  
    }
    return null;
  }
  
  private EditPart getTargetConnectionEditPart()
  {
    EditPart result = null; 
    IField field = (IField)getModel();
    IType type = field.getType();
    if (type != null)
    {      
      result = getTargetEP(type); //getTargetEditPart(type);
    }
    return result;
  }
  
  /**
   * Do not override
   * @param type
   * @return
   */
  protected EditPart getTargetEP(IType type)
  {
    return getTargetEditPart(type);
  }
  
  public TypeReferenceConnection createConnectionFigure()
  {
    connectionFigure = null;   
    AbstractGraphicalEditPart referenceTypePart = (AbstractGraphicalEditPart)getTargetConnectionEditPart();
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
      int targetAnchorYOffset = 8;

      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(referenceTypePart.getFigure(), CenteredConnectionAnchor.HEADER_LEFT, 0, targetAnchorYOffset)); 
      connectionFigure.setHighlight(false);
    }

    return connectionFigure;
  }

  protected void createEditPolicies()
  {
    super.createEditPolicies();
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, adtDirectEditPolicy);
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
  }
  
  public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
  {
    EditPart result = null;        
    if (direction == PositionConstants.EAST)
    {    
      result = getTargetConnectionEditPart();
    }
    else if (direction == PositionConstants.WEST)
    {
      for (Iterator iter = getParent().getChildren().iterator(); iter.hasNext(); )
      {
        Object child = iter.next();
        if (child instanceof SectionEditPart)
        {
          SectionEditPart groups = (SectionEditPart) child;
          for (Iterator i = groups.getChildren().iterator(); i.hasNext(); )
          {
            Object groupChild = i.next();
            EditPart connectable = getParentConnectableEditPart((EditPart)groupChild);
            if (connectable != null)
            {
              result = connectable;
            }
          }
        }
      }
      if (result == null)
      {
        result = this;
      }
    }
    else
    {
      result = super.doGetRelativeEditPart(editPart, direction);
      if (result == null)
      {  
        result = ((BaseEditPart)getParent()).doGetRelativeEditPart(editPart, direction);
      }  
    }  
    return result;      
  }
  
  protected ConnectableEditPart getParentConnectableEditPart(EditPart connectable)
  {
    if (connectable instanceof TargetConnectionSpacingFigureEditPart)
    {
      TargetConnectionSpaceFiller space = (TargetConnectionSpaceFiller) ((TargetConnectionSpacingFigureEditPart)connectable).getModel();
      if (space.getAdapter() == this.getModel())
      {
        return (ConnectableEditPart)connectable;
      }
    }
    
    for (Iterator i = connectable.getChildren().iterator(); i.hasNext(); )
    {
      Object child = i.next();
      if (child instanceof ConnectableEditPart)
      {
        ConnectableEditPart r = getParentConnectableEditPart((EditPart)child);
        if (r != null) return r;
      }
      else if (child instanceof TargetConnectionSpacingFigureEditPart)
      {
        TargetConnectionSpaceFiller space = (TargetConnectionSpaceFiller) ((TargetConnectionSpacingFigureEditPart)child).getModel();
        if (space.getAdapter() == this.getModel() && connectable instanceof ConnectableEditPart)
        {
          return (ConnectableEditPart)connectable;
        }
      }
    }
    return null;
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
    return new GraphNodeDragTracker(this);
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
    if (((IADTObject)getModel()).isReadOnly() || isFileReadOnly())
    {
      return;
    }
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
          TypeUpdateCommand typeUpdateCommand = new TypeUpdateCommand();
          typeUpdateCommand.setDelegate(manager);
          adtDirectEditPolicy.setUpdateCommand(typeUpdateCommand);
          manager.show();
        }
        else if (hitTest(fieldFigure.getNameLabel(), p))
        {
        	directEditNameField();
        }
      }
      else {
    	  directEditNameField();
      }
    }
  }
  
  protected void directEditNameField()
  {
    if (isFileReadOnly()) return;
	  Object model = getModel();
	  IFieldFigure fieldFigure = getFieldFigure();
  	if ( model instanceof IField) 
    {
      IField field = (IField) model;
      if (field.isReference())
      {
        ElementReferenceDirectEditManager manager = new ElementReferenceDirectEditManager((IField) model, this, fieldFigure.getNameLabel());
        ReferenceUpdateCommand elementUpdateCommand = new ReferenceUpdateCommand();
        elementUpdateCommand.setDelegate(manager);
        adtDirectEditPolicy.setUpdateCommand(elementUpdateCommand);
        manager.show();
      }
      else
      {
        LabelEditManager manager = new LabelEditManager(this, new LabelCellEditorLocator(this, null));
        NameUpdateCommandWrapper wrapper = new NameUpdateCommandWrapper();
        adtDirectEditPolicy.setUpdateCommand(wrapper);
        manager.show();
      }
  	}
  }
  
  public void doEditName(boolean addFromDesign)
  {
    if (!addFromDesign) return;
    
//    removeFeedback();

    Runnable runnable = new Runnable()
    {
      public void run()
      {
        Object object = ((XSDBaseAdapter)getModel()).getTarget();
        if (object instanceof XSDNamedComponent)
        {
          Point p = getNameLabelFigure().getLocation();
          LabelEditManager manager = new LabelEditManager(BaseFieldEditPart.this, new LabelCellEditorLocator(BaseFieldEditPart.this, p));
          NameUpdateCommandWrapper wrapper = new NameUpdateCommandWrapper();
          adtDirectEditPolicy.setUpdateCommand(wrapper);
          manager.show();
        }
      }
    };
    Display.getCurrent().asyncExec(runnable);

  }
  
  protected class NameUpdateCommandWrapper extends Command implements IADTUpdateCommand
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
      super(Messages._UI_ACTION_UPDATE_TYPE);
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
      IField field = (IField)getModel();
      Object newValue = ((CCombo)request.getCellEditor().getControl()).getText();
      if (newValue instanceof String)
      {
        return !newValue.equals(field.getTypeName());
      }
      return true;
    }
  }
  
  protected class ReferenceUpdateCommand extends Command implements IADTUpdateCommand
  {
	    protected ComboBoxCellEditorManager delegate;
	    protected DirectEditRequest request;
	    
	    public ReferenceUpdateCommand()
	    {
	      super(Messages._UI_ACTION_UPDATE_ELEMENT_REFERENCE);
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


  public void addFeedback()
  {
    // Put back connection figure so it won't get overlayed by other non highlighted connections
    if (connectionFigure != null && !isSelected)
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
  public String getReaderText()
  {
	  IFieldFigure fieldFigure = getFieldFigure();
	  
	  String name="";
	  if (fieldFigure.getNameLabel() !=null&& fieldFigure.getNameLabel().getText().trim().length() > 0)
		  name +=fieldFigure.getNameLabel().getText();
	  if (fieldFigure.getNameAnnotationLabel() !=null && fieldFigure.getNameAnnotationLabel().getText().trim().length() > 0)
		  name += " "+fieldFigure.getNameAnnotationLabel().getText();
	  if (fieldFigure.getTypeLabel() !=null && fieldFigure.getTypeLabel().getText().trim().length() > 0)
		  name += " type "+fieldFigure.getTypeLabel().getText();
	  if (fieldFigure.getTypeAnnotationLabel() !=null && fieldFigure.getTypeAnnotationLabel().getText().trim().length() >0)
		  name += " "+fieldFigure.getTypeAnnotationLabel().getText();
	  return name;

  }
}

