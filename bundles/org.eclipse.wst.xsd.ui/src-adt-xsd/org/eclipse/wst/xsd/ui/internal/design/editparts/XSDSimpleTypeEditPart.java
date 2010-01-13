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

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSimpleTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseTypeConnectingEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CenteredConnectionAnchor;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ColumnEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.TypeReferenceConnection;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.HeadingFigure;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.StructureFigure;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class XSDSimpleTypeEditPart extends BaseTypeConnectingEditPart
{
  protected StructureFigure figure;
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
    return figure;
  }
  
  protected void refreshVisuals()
  {
    XSDSimpleTypeDefinitionAdapter adapter = (XSDSimpleTypeDefinitionAdapter)getModel();
    String name = adapter.getDisplayName();
    HeadingFigure headingFigure = figure.getHeadingFigure();
    headingFigure.setIsReadOnly(adapter.isReadOnly());
    Label label = headingFigure.getLabel();
    label.setText(name);
    label.setIcon(XSDCommonUIUtils.getUpdatedImage(adapter.getXSDTypeDefinition(), adapter.getImage(), adapter.isReadOnly()));    
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
    super.createEditPolicies();
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
    TypeReferenceConnection connectionFigure = null;
    XSDSimpleTypeDefinitionAdapter adapter = (XSDSimpleTypeDefinitionAdapter)getModel();
    IType superType = adapter.getSuperType();

    if (superType != null)
    {      
      AbstractGraphicalEditPart referenceTypePart = (AbstractGraphicalEditPart)getTargetEditPart(superType);
      
      if (referenceTypePart != null)
      {
        connectionFigure = new TypeReferenceConnection(true);
        // draw a line out from the top         
        connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(getFigure(), CenteredConnectionAnchor.TOP, 1));
        
        // TODO (cs) need to draw the target anchor to look like a UML inheritance relationship
        // adding a label to the connection would help to
        connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(referenceTypePart.getFigure(), CenteredConnectionAnchor.BOTTOM, 0, 0));
        connectionFigure.setConnectionRouter(new ManhattanConnectionRouter());
        ((CenteredConnectionAnchor)connectionFigure.getSourceAnchor()).setOther((CenteredConnectionAnchor)connectionFigure.getTargetAnchor());
        connectionFigure.setHighlight(false);

      }
    }    
    return connectionFigure;
  }
  
  protected EditPart getTargetEditPart(IType type)
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
      for (Iterator i = columnEditPart.getChildren().iterator(); i.hasNext(); )
      {
        EditPart child = (EditPart)i.next();
        if (child.getModel() == type)
        {
          return child;
        }         
      }  
    }
    return null;
  }

  public void performRequest(Request request)
  {  
    if (request.getType() == RequestConstants.REQ_OPEN)
    {
      Object model = getModel();
      if (request instanceof LocationRequest)
      {
        LocationRequest locationRequest = (LocationRequest)request;
        Point p = locationRequest.getLocation();
         
        if (getStructureFigure().hitTestHeader(p))
        {          
          // TODO: !!! This should be moved to the adt-xsd package
          // 
          if (model instanceof XSDSimpleTypeDefinitionAdapter)     
          {
            XSDSimpleTypeDefinitionAdapter adapter = (XSDSimpleTypeDefinitionAdapter)model;
            XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)adapter.getTarget();
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
            Object schema = editorPart.getAdapter(XSDSchema.class);
            ActionRegistry registry = getEditorActionRegistry(editorPart);
            if (registry != null)
            {
              if (schema == st.getSchema())
              {
                IAction action = registry.getAction(SetInputToGraphView.ID);
                action.run();
              }
              else
              {
                IAction action = registry.getAction(OpenInNewEditor.ID);
                action.run();
              }
            }
          }          
        }
      }
    }
  }
  
  protected ActionRegistry getEditorActionRegistry(IEditorPart editor)
  {
    return (ActionRegistry) editor.getAdapter(ActionRegistry.class);
  }

  protected boolean shouldDrawConnection()
  {
    if (getModel() instanceof IType)
    {
      if (((IType)getModel()).getSuperType() != null)
        return true;
    } 
    return false;
  }
  
  public String getReaderText()
  {
	  HeadingFigure headingFigure = figure.getHeadingFigure();
	  return headingFigure.getLabel().getText();
  }

}
