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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.xsd.XSDTypeDefinition;


public class TypeEditPart extends BaseEditPart
{
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();

  protected IFigure createFigure()
  {
    ContainerFigure typeGroup = new ContainerFigure();
//    typeGroup.setBorder(new SimpleRaisedBorder(1));
//    typeGroup.setBorder(new LineBorder(1));
//    typeGroup.setBorder(new RoundedLineBorder(1,5));

    Label typeLabel = new Label("type");
    typeLabel.setBorder(new MarginBorder(0,2,2,1));
    typeGroup.add(typeLabel);

    return typeGroup;
  }    

  protected void refreshVisuals()
  {                 
    super.refreshVisuals();
  }

  public XSDTypeDefinition getXSDTypeDefinition()
  {         
    return (XSDTypeDefinition)getModel();
  }

                    
  public List getModelChildren() 
  {                   
    return Collections.EMPTY_LIST;
  }  

  protected void createEditPolicies()
  {        
    SelectionHandlesEditPolicyImpl policy = new SelectionHandlesEditPolicyImpl();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, policy);      
  } 
  
               
  public void performRequest(Request request)
  {
    super.performRequest(request);
  }  
}
