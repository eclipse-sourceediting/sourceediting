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
                                      
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.ComponentNameDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.CenteredIconFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.FillLayout;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.xsd.XSDComplexTypeDefinition;

              

public class ComplexTypeDefinitionEditPart extends GraphNodeEditPart
{
  protected ContainerFigure contentFigure;   
  protected Label label;     
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();
  protected CenteredIconFigure centeredIconFigure;  
  protected RectangleFigure preceedingSpace;  
  protected ContainerFigure contentPane;

  public XSDComplexTypeDefinition getXSDComplexTypeDefinition()
  {         
    return (XSDComplexTypeDefinition)getModel();
  }  

  protected boolean isConnectedEditPart()
  {
    return false;
  } 

  protected GraphNodeFigure createGraphNodeFigure()  
  {
    XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)getModel();
    GraphNodeFigure figure = new GraphNodeFigure();

    figure.getOutlinedArea().setBorder(new RoundedLineBorder(1, 6));
    figure.getOutlinedArea().setFill(true);
    figure.getOutlinedArea().setLayoutManager(new FillLayout(true));

    figure.getInnerContentArea().getContainerLayout().setHorizontal(true);

    preceedingSpace = new RectangleFigure();
    preceedingSpace.setVisible(false);
    figure.getInnerContentArea().add(preceedingSpace, 0);

    contentPane = new ContainerFigure();
    contentPane.getContainerLayout().setHorizontal(false); 
    contentPane.getContainerLayout().setSpacing(5);   
    contentPane.setBorder(new MarginBorder(5, 5, 5, 5));
    figure.getInnerContentArea().add(contentPane);
    
    label = new Label();    
    label.setBorder(new MarginBorder(0, 5, 5, 5));
    figure.getIconArea().add(label);     
    label.setFont(mediumBoldFont);
 
    figure.getInnerContentArea().getContainerLayout().setSpacing(5);   
    figure.getInnerContentArea().setBorder(new MarginBorder(5, 5, 5, 5));
    
    return figure;
  }      
                                                                    
  protected EditPart createChild(Object model) 
  {   
    EditPart editPart = null;                                     
    if (model == getModel())
    {
      editPart = new ComplexTypeInheritedContentEditPart();
      editPart.setModel(model);
      editPart.setParent(this);
    }
    else
    {
      editPart = super.createChild(model);
    }            
    return editPart;
  } 

  protected List getModelChildren() 
  {                           
    List list = new ArrayList();

    XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)getModel();

    if (ct.getDerivationMethod().getName().equals("extension"))
    {
      list.add(getModel());
    }  

    list.addAll(XSDChildUtility.getModelChildren(getModel()));
    return list;
  }

  public IFigure getContentPane()  
  {
    return contentPane;
  }
                        
  protected void refreshVisuals()
  { 
    XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)getModel();                   

    String name = ctd.getName();
    if (name == null)
    {
      try
      {
        if (label != null)
        {
          graphNodeFigure.getIconArea().remove(label);
        }
        label = null;
      }
      catch (Exception e)
      {
      }
    }
    else
    {
      if (label == null)
      {
        label = new Label();    
        label.setBorder(new MarginBorder(0, 5, 5, 5));
        ((GraphNodeFigure)getFigure()).getIconArea().add(label);     
        label.setFont(mediumBoldFont);
      }
      graphNodeFigure.getIconArea().add(label);
      label.setText(name);
    }

    // provides some room if we need to draw lines for the inherited
    boolean includesInheritedContent = getModelChildren().contains(getModel());
    preceedingSpace.setPreferredSize(includesInheritedContent ? new Dimension(10, 1) : new Dimension(0, 0));

    if (XSDGraphUtil.isEditable(getModel()))
    { 
      graphNodeFigure.setForegroundColor(isSelected ? ColorConstants.black : elementBorderColor);
      if (label != null)
      label.setForegroundColor(elementBorderColor);
    }
    else
    {
      graphNodeFigure.setForegroundColor(isSelected ? ColorConstants.black : readOnlyBorderColor);
      if (label != null)
      label.setForegroundColor(readOnlyBorderColor);
    }
  }
             
  protected void performDirectEdit()
  {  
    ComponentNameDirectEditManager manager = new ComponentNameDirectEditManager(this, label, (XSDComplexTypeDefinition)getModel());   
    simpleDirectEditPolicy.setDelegate(manager);
    manager.show();
  }    
            

  protected void createEditPolicies()
  {  
    super.createEditPolicies();
  	installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, simpleDirectEditPolicy);
  } 
       

  public void performRequest(Request request)
  {
  	if (request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
        request.getType() == RequestConstants.REQ_OPEN)
    {     
      if (XSDGraphUtil.isEditable(getModel()))
      {                 
        LocationRequest locationRequest = (LocationRequest)request;
        Point p = locationRequest.getLocation();
        
        if (label != null && hitTest(label, p))
        {
  		    performDirectEdit();
        }
      }
    }
  } 
} 