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
                                   
import java.util.List;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.XSDInheritanceViewer;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.ComponentNameDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ExpandableGraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.FillLayout;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.xsd.XSDComplexTypeDefinition;

              

public class RootComplexTypeDefinitionEditPart extends ExpandableGraphNodeEditPart
{                      
  public Label label;  
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();                              
        

  protected GraphNodeFigure createGraphNodeFigure()
  {
    ExpandableGraphNodeFigure figure = new ExpandableGraphNodeFigure();
    figure.getOutlinedArea().setBorder(new RoundedLineBorder(1, 6));
    figure.getOutlinedArea().setLayoutManager(new FillLayout());
    figure.getOutlinedArea().setFill(true);
    
    if (getViewer() instanceof XSDInheritanceViewer)
    {
      figure.getOuterContentArea().getContainerLayout().setSpacing(10);
    }

    label = new Label();       
    label.setFont(mediumBoldFont);
    label.setBorder(new MarginBorder(5, 8, 5, 8));  
    figure.getIconArea().add(label);  

    return figure;
  }          
   
                                                                                   
  protected void refreshVisuals()
  { 
    super.refreshVisuals();                                                                

    XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)getModel();                   
    label.setText(ctd.getName() != null ? ctd.getName(): "");

    if (XSDGraphUtil.isEditable(ctd))
    { 
      figure.setForegroundColor(elementBorderColor);
      label.setForegroundColor(elementBorderColor);
    }
    else
    {
      figure.setForegroundColor(readOnlyBorderColor);
      label.setForegroundColor(readOnlyBorderColor);
    }
  }


  protected List getModelChildrenHelper()
  {
    XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)getModel();
    if (getViewer() instanceof XSDInheritanceViewer)
    {
      return XSDChildUtility.getImmediateDerivedTypes(ct);
    }
    else
    {
      return XSDChildUtility.getModelChildren(getModel());
    }
  }     


  protected void createEditPolicies()
  {  
    SelectionHandlesEditPolicyImpl policy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, policy);     
  	installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, simpleDirectEditPolicy);
  }  
        

  protected void performDirectEdit()
  {                                  
    ComponentNameDirectEditManager manager = new ComponentNameDirectEditManager(this, label, (XSDComplexTypeDefinition)getModel());     
    simpleDirectEditPolicy.setDelegate(manager);
    manager.show();
  }    


  public void performRequest(Request request)
  {
  	if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
    {
      if (XSDGraphUtil.isEditable(getModel()))
      {
  		  performDirectEdit();
      }
    }
  } 
}
