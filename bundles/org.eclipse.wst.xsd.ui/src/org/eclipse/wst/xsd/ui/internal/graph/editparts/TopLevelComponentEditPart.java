/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.graph.editparts;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
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
import org.eclipse.wst.xsd.ui.internal.gef.util.editparts.AbstractComponentViewerRootEditPart;
import org.eclipse.wst.xsd.ui.internal.gef.util.figures.FillLayout;
import org.eclipse.wst.xsd.ui.internal.graph.GraphicsConstants;
import org.eclipse.wst.xsd.ui.internal.graph.XSDComponentViewer;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.model.ModelAdapter;
import org.eclipse.wst.xsd.ui.internal.graph.model.XSDModelAdapterFactory;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;


public class TopLevelComponentEditPart extends BaseEditPart implements IFeedbackHandler
{
  protected Label label;
  //protected Label arrowLabel;
  protected ContainerFigure labelHolder = new ContainerFigure();
  protected SelectionHandlesEditPolicyImpl selectionHandlesEditPolicy;
  protected boolean isReadOnly;
  protected boolean isSelected;
  
  protected IFigure createFigure()
  {
    ContainerFigure typeGroup = new ContainerFigure();
    typeGroup.getContainerLayout().setHorizontal(true);

    //arrowLabel = new Label();
    //arrowLabel.setIcon(XSDEditorPlugin.getPlugin().getImage("icons/forward.gif"));
    //typeGroup.add(arrowLabel);

    labelHolder = new ContainerFigure();
    FillLayout fillLayout = new FillLayout();
    labelHolder.setLayoutManager(fillLayout);
    labelHolder.setFill(true);
    typeGroup.add(labelHolder);

    label = new Label();
    label.setBorder(new MarginBorder(0, 2, 2, 1));
    label.setForegroundColor(ColorConstants.black);
    labelHolder.add(label);
    
    try
	  {
      // evil hack to provide underlines
      Object model = getModel();
      
      boolean isLinux = java.io.File.separator.equals("/");
      if (model instanceof XSDComplexTypeDefinition ||
          model instanceof XSDElementDeclaration ||
          model instanceof XSDModelGroupDefinition)
      {
        if (!isLinux)
        {
          FontData oldData = GraphicsConstants.medium.getFontData()[0];
          FontData fontData = new FontData(oldData.getName(), oldData.getHeight(), SWT.NONE);

            // TODO... clean this awful code up... we seem to be leaking here too
            // we can't call this directly since the methods are OS dependant
            // fontData.data.lfUnderline = 1
            // so instead we use reflection
            Object data = fontData.getClass().getField("data").get(fontData);
            System.out.println("data" + data.getClass());
            data.getClass().getField("lfUnderline").setByte(data, (byte)1);
            Font font = new Font(Display.getCurrent(), fontData);
            label.setFont(font);        

        }
      }
	  }
    catch (Exception e)
	  {

	  }
    //FontData data = label.getFont().getFontData()[0];
    //data.data.lfUnderline = 1;
    
    //RectangleFigure line = new RectangleFigure();    
    //line.setPreferredSize(2, 1);   
    //labelHolder.add(line, 1);    
    
    //label.getFont().getFontData()[0].setStyle()
    
    return typeGroup;
  }
  
  public void refreshVisuals()
  {
    ModelAdapter adapter = XSDModelAdapterFactory.getAdapter(getModel());
    if (adapter != null)
    {
      // isReadOnly = Boolean.TRUE.equals(adapter.getProperty(getModel(), "isReadOnly"));
      isReadOnly = !XSDGraphUtil.isEditable(getModel());
      label.setForegroundColor(computeLabelColor());
      label.setText((String)adapter.getProperty(getModel(), ModelAdapter.LABEL_PROPERTY));
      Image image = (Image)adapter.getProperty(getModel(), ModelAdapter.IMAGE_PROPERTY);
      if (image != null) label.setIcon(image);
      //arrowLabel.setVisible(Boolean.TRUE.equals(adapter.getProperty(getModel(), "drillDown")));
    }
    else
    {
      label.setText("unknown object" + getModel().getClass().getName());
      //arrowLabel.setVisible(false);
    }
  }


  public XSDNamedComponent getXSDNamedComponent()
  {
    return (XSDNamedComponent) getModel();
  }

  public List getModelChildren()
  {
    return Collections.EMPTY_LIST;
  }
  
  protected void createEditPolicies()
  { 
    //installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());    
    //selectionHandlesEditPolicy = new SelectionHandlesEditPolicyImpl();
    //installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionHandlesEditPolicy);  
  	
    SelectionHandlesEditPolicyImpl policy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, policy);

    SelectionEditPolicy feedBackSelectionEditPolicy = new SelectionEditPolicy()
    {
      protected void hideSelection()
      {
        EditPart editPart = getHost();
        if (editPart instanceof IFeedbackHandler)
        {
          ((IFeedbackHandler)editPart).removeFeedback();
        }
      }

      protected void showSelection()
      {
        EditPart editPart = getHost();
        if (editPart instanceof IFeedbackHandler)
        {
          ((IFeedbackHandler)editPart).addFeedback();
        }
      }
    };
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, feedBackSelectionEditPolicy);  	
  }  
  
  public Color computeLabelColor()
  {
    Color color = ColorConstants.black;
    if (isSelected)
    {
      color = ColorConstants.white;
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
    
    labelHolder.setBackgroundColor(ColorConstants.black);
    label.setForegroundColor(computeLabelColor());
    labelHolder.setFill(true);
  }

  public void removeFeedback()
  {
    isSelected = false;
    labelHolder.setBackgroundColor(null);
    label.setForegroundColor(computeLabelColor());
    labelHolder.setFill(false);
  }

  public void performRequest(Request request)
  {  
    if (request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
        request.getType() == RequestConstants.REQ_OPEN)
    {
      
      Object model = getModel();
      if (model instanceof XSDComplexTypeDefinition ||
          model instanceof XSDElementDeclaration ||
          model instanceof XSDModelGroupDefinition)
      {
        if (request instanceof LocationRequest)
        {
          LocationRequest locationRequest = (LocationRequest)request;
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
        //((XSDComponentViewer)getViewer()).setInput((XSDConcreteComponent)getModel());
        
        EditPart editPart = ((AbstractEditPartViewer)getViewer()).getRootEditPart().getContents();
        if (editPart instanceof AbstractComponentViewerRootEditPart)
        {
          AbstractComponentViewerRootEditPart rootEditPart = (AbstractComponentViewerRootEditPart)editPart;
          rootEditPart.setInput((XSDConcreteComponent)getModel());
        }
        else if (editPart instanceof BaseEditPart)
        {
          ((XSDComponentViewer)getViewer()).setInput((XSDConcreteComponent)getModel());
        }
      }
    };
    Display.getCurrent().asyncExec(runnable);
  }
}