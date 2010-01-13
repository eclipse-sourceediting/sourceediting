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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDComplexTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.Compartment;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.KeyBoardAccessibilityEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IStructureFigure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.design.editparts.SpaceFillerForFieldEditPart;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;

public class StructureEditPart extends BaseTypeConnectingEditPart implements INamedEditPart
{  
  protected List compartmentList = null;
  protected ADTDirectEditPolicy adtDirectEditPolicy = new ADTDirectEditPolicy();
  protected TypeReferenceConnection connectionFigure;
  protected TypeReferenceConnection connectionFeedbackFigure;

  /**
   * TODO cs... I'm sure this has something to do with the way we wanted to rework compartment creation
   * I suppose we could have subclasses override this method instead of getModelChildren()
   * 
   * @deprecated
   */
  protected Compartment[] getCompartments()
  {
    return null;
  }
  
  protected IFigure createFigure()
  {
    IStructureFigure figure = getFigureFactory().createStructureFigure(getModel());
    return figure;
  }
  
  public IStructureFigure getStructureFigure()
  {
    return (IStructureFigure)getFigure();
  }
  
  public IFigure getContentPane()
  {
    return getStructureFigure().getContentPane();
  }
  
  
  public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
  {
    EditPart result = null;               
    if (direction == KeyBoardAccessibilityEditPolicy.IN_TO_FIRST_CHILD)
    {          
      for (Iterator i = getChildren().iterator(); i.hasNext();)
      {            
        CompartmentEditPart compartment = (CompartmentEditPart)i.next();
        if (compartment.hasContent())
        {
          for (Iterator contentChildren = compartment.getChildren().iterator(); contentChildren.hasNext(); )
          {
            Object child = contentChildren.next();
            if (child instanceof SectionEditPart)
            {
              SectionEditPart part = (SectionEditPart) child;
              result = (EditPart)part.getChildren().get(0);
              return result;
            }
            else if (child instanceof BaseFieldEditPart && (!(child instanceof SpaceFillerForFieldEditPart)))
            {
              result = (EditPart)child;
              return result;
            }
          }
        }  
      }  
    }    
    else
    {
      return super.doGetRelativeEditPart(editPart, direction);
    }  
    return result;           
  }
  
  protected void createEditPolicies()
  {        
    super.createEditPolicies();  
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, adtDirectEditPolicy);
  }
  
  protected IStructure getStructure()
  {
    return (IStructure)getModel();
  }
  
  protected List getModelChildren()
  {
    if (compartmentList == null)
    {
      compartmentList = new ArrayList();
      
      // TODO.. this needs to be moved to the xsd specific version of this class 
      compartmentList.add(new Compartment(getStructure(), "attribute")); //$NON-NLS-1$
      compartmentList.add(new Compartment(getStructure(), "element"));    //$NON-NLS-1$
    }  
    return compartmentList;
  }
  
  protected void refreshChildren()
  {   
    super.refreshChildren();
    //getFigure().invalidateTree();    
  }
  
  protected void refreshVisuals()
  {
    super.refreshVisuals();
    getStructureFigure().refreshVisuals(getModel());
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

  public Label getNameLabelFigure()
  {
    return getStructureFigure().getNameLabel();
  }

  public void performDirectEdit(Point cursorLocation)
  {
    
  }

  public void performRequest(Request request)
  {  
    if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
    {
      if (request instanceof LocationRequest)
      {
     // uncomment for direct edit of name (add else)
//        LocationRequest locationRequest = (LocationRequest)request;
//        Point p = locationRequest.getLocation();
//        if (hitTest(getNameLabelFigure(), p))
//        {
//          LabelEditManager manager = new LabelEditManager(this, new LabelCellEditorLocator(this, p));
//          NameUpdateCommandWrapper wrapper = new NameUpdateCommandWrapper();
//          adtDirectEditPolicy.setUpdateCommand(wrapper);
//          manager.show();
//        }
      }
    }
    else if (request.getType() == RequestConstants.REQ_OPEN)
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
          if (model instanceof XSDComplexTypeDefinitionAdapter)     
          {
            XSDComplexTypeDefinitionAdapter adapter = (XSDComplexTypeDefinitionAdapter)model;
            XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)adapter.getTarget();
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
            Object schema = editorPart.getAdapter(XSDSchema.class);
            ActionRegistry registry = getEditorActionRegistry(editorPart);
            if (registry != null)
            {
              if (schema == ct.getSchema())
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
    return false;
  }
  
  public TypeReferenceConnection createConnectionFigure()
  {
    return null;
  }
  public String getReaderText()
  {
  	 // return getStructureFigure().getNameLabel().getText();
  	return getNameLabelFigure().getText();
  }
}