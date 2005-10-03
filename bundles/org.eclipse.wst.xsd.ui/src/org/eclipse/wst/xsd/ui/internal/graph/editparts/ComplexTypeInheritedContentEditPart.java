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
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.ComponentNameDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;


// This is a dashed box that displays the inherited content of a complex type
//
public class ComplexTypeInheritedContentEditPart extends BaseEditPart
{
  protected Label label;     
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();
  protected boolean isParentExpanded;
               
  public ComplexTypeInheritedContentEditPart()
  {
    super();
  }   
                 
  public XSDComplexTypeDefinition getXSDComplexTypeDefinition()
  {         
    return (XSDComplexTypeDefinition)getModel();
  }
                  
  protected IFigure createFigure()
  {                            
    ContainerFigure figure = new ContainerFigure();
    figure.getContainerLayout().setHorizontal(false);
    figure.getContainerLayout().setBorder(5);
    figure.getContainerLayout().setSpacing(5);
    figure.setBorder(new RoundedLineBorder(ColorConstants.gray, 1, 6, Graphics.LINE_DASH));
    return figure;
  }    
           
  protected List getModelChildren() 
  {                                              
    XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition)getModel();

    List list = new ArrayList();

    if (ct.getDerivationMethod().getName().equals("extension") && !isParentExpanded)
    {
      XSDTypeDefinition type = ct.getBaseTypeDefinition();
      Iterator iter = XSDChildUtility.getModelChildren(type).iterator();
      boolean cont = true;
      while (cont)
      {
        while (iter.hasNext())
        {
          list.add(0, iter.next());
        }
        
        if (type instanceof XSDComplexTypeDefinition)
        {
          XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)type;
          type = ctd.getBaseTypeDefinition();
                    
					// defect 264957 - workbench hangs when modifying complex content
					// Since we don't filter out the current complexType from
					// the combobox, we can potentially have an endless loop
					if (ctd == type)
					{
						cont = false;
						break;
					}

          if (ctd.getDerivationMethod().getName().equals("extension"))
          {
            iter = XSDChildUtility.getModelChildren(type).iterator();
          }
          else
          {
            cont = false;
          }
        }
        else
        {
          cont = false;
        }
      }
    }
    return list;
  }

  protected void refreshVisuals()
  { 
    List children = getModelChildren();
    figure.setVisible(children.size() > 0);   
    // todo set preferredSize to 0 ?
  }

  protected void performDirectEdit()
  {
    // Why are we allowing direct editing when the label is null?
    // Should remove the policy
    if (label != null)
    {
      ComponentNameDirectEditManager manager = new ComponentNameDirectEditManager(this, label, (XSDComplexTypeDefinition)getModel());   
      simpleDirectEditPolicy.setDelegate(manager);
      manager.show();
    }
  }    
            

  protected void createEditPolicies()
  {  
    super.createEditPolicies();
    installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, simpleDirectEditPolicy);
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
