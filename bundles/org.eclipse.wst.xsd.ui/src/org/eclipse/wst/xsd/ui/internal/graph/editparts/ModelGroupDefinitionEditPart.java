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
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.ComboBoxCellEditorManager;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.ComponentNameDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RepeatableGraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;

              
public class ModelGroupDefinitionEditPart extends RepeatableGraphNodeEditPart
{
  protected Label label;     
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();

  public XSDModelGroupDefinition getXSDModelGroupDefinition()
  {
    return (XSDModelGroupDefinition)getModel();
  } 

  protected boolean isConnectedEditPart()
  {
    return false;
  }          

  public XSDParticle getXSDParticle()
  {                    
    Object o = getXSDModelGroupDefinition().getContainer();
    return (o instanceof XSDParticle) ? (XSDParticle)o : null;
  }
             
  protected GraphNodeFigure createGraphNodeFigure()
  {
    RepeatableGraphNodeFigure figure = new RepeatableGraphNodeFigure();                                                        
    figure.getOutlinedArea().setBorder(new RoundedLineBorder(1, 6));
    figure.getInnerContentArea().setBorder(new MarginBorder(10, 0, 10, 0));

    label = new Label();    
    label.setFont(mediumBoldFont); 
    figure.getIconArea().add(label);     


    return figure;
  }  
 
  public IFigure getContentPane()  
  {
    return graphNodeFigure.getInnerContentArea();
  }
           
  protected List getModelChildren() 
  {                                             
    return XSDChildUtility.getModelChildren(getModel());  
  }

  protected void createEditPolicies()
  {     
    super.createEditPolicies();
  	installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, simpleDirectEditPolicy);
  }             
             
  protected void refreshVisuals()
  { 
    super.refreshVisuals();                                                                
  
    if (getXSDModelGroupDefinition().isModelGroupDefinitionReference())
    {
      label.setText(getXSDModelGroupDefinition().getResolvedModelGroupDefinition().getQName());
      label.setIcon(XSDEditorPlugin.getXSDImage("icons/GraphViewElementRef.gif"));
      label.setBorder(new MarginBorder(0, 0, 0, 4)); 

      // todo update occurence label
      //
    }
    else
    {               
      label.setText(getXSDModelGroupDefinition().getName());      
      label.setIcon(null);
      label.setBorder(new MarginBorder(0, 6, 0, 4));
    }
                       
    if (XSDGraphUtil.isEditable(getModel()))
    { 
      graphNodeFigure.getOutlinedArea().setForegroundColor(isSelected ? ColorConstants.black : elementBorderColor);
      label.setForegroundColor(elementLabelColor);
    }
    else
    {
      graphNodeFigure.getOutlinedArea().setForegroundColor(readOnlyBackgroundColor);
      label.setForegroundColor(readOnlyBackgroundColor);
    }   

    refreshOccurenceLabel(getXSDParticle());
  }
 

  protected void performDirectEdit()
  {              
    if (getXSDModelGroupDefinition().isModelGroupDefinitionReference())
    {
      ComboBoxCellEditorManager manager = new ComboBoxCellEditorManager(this, label)
      {
         protected List computeComboContent()
         {             
           XSDSchema schema = getXSDModelGroupDefinition().getSchema();
           List nameList = new ArrayList();
           if (schema != null)
           {
             TypesHelper typesHelper = new TypesHelper(schema);
             nameList = typesHelper.getModelGroups();
           }                                           
           return nameList;
         }

         public void performModify(String value)
         {  
           Display.getCurrent().asyncExec(new DelayedModelGroupRenameAction(getXSDModelGroupDefinition(), value));
         }
      };                                          
      simpleDirectEditPolicy.setDelegate(manager);
      manager.show();
    }
    else 
    {
      ComponentNameDirectEditManager manager = new ComponentNameDirectEditManager(this, label, getXSDModelGroupDefinition());           
      simpleDirectEditPolicy.setDelegate(manager);
      manager.show();
    }
  }  
       

  protected class DelayedModelGroupRenameAction implements Runnable
  {
     XSDModelGroupDefinition modelGroupDefinition;                                     
     String value;

     DelayedModelGroupRenameAction(XSDModelGroupDefinition modelGroupDefinition, String value)
     {
       this.modelGroupDefinition = modelGroupDefinition;
       this.value = value;
     }

     public void run()
     {
       modelGroupDefinition.getElement().setAttribute("ref", value);
     }
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
 
  // TODO... I added this as a quick fix to makesure the title gets redrawn when the groupRef is changed
  // we should probably fix the ModelListenerUtil to fire both call both 'change' methods for the ref property
  //public void modelChildrenChanged()
  //{   
  //  super.modelChildrenChanged();                                              
  //  refreshVisuals(); 
  //} 
}
