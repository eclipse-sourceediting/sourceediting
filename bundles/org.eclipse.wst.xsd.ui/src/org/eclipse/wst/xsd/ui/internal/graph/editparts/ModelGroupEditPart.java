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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.DragAndDropEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.CenteredIconFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ExpandableGraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.graph.model.XSDModelAdapterFactory;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;

              

public class ModelGroupEditPart extends ExpandableGraphNodeEditPart
{
  protected CenteredIconFigure centeredIconFigure;
 
  protected static Color editableBackgroundColor = null;
  protected static Color editableForegroundColor = null;
  protected static Color nonEditableForegroundColor = null;

  public XSDParticle getXSDParticle()
  {                    
    Object o = getXSDModelGroup().getContainer();
    return (o instanceof XSDParticle) ? (XSDParticle)o : null;
  }

  public XSDModelGroup getXSDModelGroup()
  {
    return (XSDModelGroup)getModel();
  }           

  protected GraphNodeFigure createGraphNodeFigure()
  {
    ExpandableGraphNodeFigure figure = new ExpandableGraphNodeFigure();      

    centeredIconFigure = new CenteredIconFigure();
    centeredIconFigure.setPreferredSize(new Dimension(32, 20)); 
    //centeredIconFigure.setBackgroundColor(new Color(Display.getCurrent(), 255, 0, 0)); 
    figure.getIconArea().add(centeredIconFigure);   
    //figure.getIconArea().setLayout(new CenterLayout());

    ContainerFigure outlinedArea = figure.getOutlinedArea();                        
    outlinedArea.setBorder(new RoundedLineBorder(1, 10));   
    //outlinedArea.setPreferredSize(new Dimension(32, 20)); 
                         
    // set layout so that children are aligned vertically with some spacing
    //
    figure.getOuterContentArea().getContainerLayout().setHorizontal(false);  
    figure.getOuterContentArea().getContainerLayout().setSpacing(10);

    return figure;
  }    

  protected List getModelChildrenHelper()
  {
    return XSDChildUtility.getModelChildren(getXSDModelGroup());
  }  
 
  protected void refreshVisuals()
  {         
    String iconName = "icons/XSDSequence.gif";    
    switch (getXSDModelGroup().getCompositor().getValue())
    {
      case XSDCompositor.ALL   : { iconName = "icons/XSDAll.gif"; break; }
      case XSDCompositor.CHOICE   : { iconName = "icons/XSDChoice.gif"; break; }
      case XSDCompositor.SEQUENCE : { iconName = "icons/XSDSequence.gif"; break; }
    }
    centeredIconFigure.image = XSDEditorPlugin.getXSDImage(iconName);
    centeredIconFigure.repaint();
                                                          

    ContainerFigure outlinedArea = graphNodeFigure.getOutlinedArea() ;    
    if (XSDGraphUtil.isEditable(getXSDModelGroup()))
    {
      if (editableForegroundColor == null) 
        editableForegroundColor = new Color(Display.getCurrent(), 120, 152, 184);

      if (editableBackgroundColor == null) 
        editableBackgroundColor = new Color(Display.getCurrent(), 232, 240, 248);
       
      outlinedArea.setForegroundColor(isSelected ? ColorConstants.black : editableForegroundColor);
      outlinedArea.setBackgroundColor(editableBackgroundColor);
    }
    else
    {                                          
      if (nonEditableForegroundColor == null) 
        nonEditableForegroundColor = new Color(Display.getCurrent(), 164, 164, 164);    
        
      outlinedArea.setForegroundColor(isSelected ? ColorConstants.black : nonEditableForegroundColor);
      outlinedArea.setBackgroundColor(ColorConstants.white);
    }  

    refreshOccurenceLabel(getXSDParticle());
  }        

  protected boolean isChildLayoutHorizontal()
  {
    return false;
  } 

  protected boolean isDefaultExpanded()
  {        
    return isPossibleCycle() ? false : true;
  }  
                                                                                         
  // This test ensures that we don't end up with an infinite default expansion (e.g. when a group contains a cyclic group ref)
  // TODO... we probably need some more extensible 'OO' way of computing this information
  protected boolean isPossibleCycle()
  {        
    boolean result = false;
    if (getParent() instanceof ModelGroupDefinitionEditPart)
    {                                                      
      ModelGroupDefinitionEditPart group = (ModelGroupDefinitionEditPart)getParent();
      for (EditPart parent = group.getParent(); parent != null; parent = parent.getParent())
      {
        if (parent.getModel() instanceof ElementDeclarationEditPart)
        {
          break;
        }       
        else 
        {
          if (parent.getModel() == group.getModel())
          {
            result = true;
            break;
          }
        }
      }      
    }      
    return result;
  } 
       
  protected void createEditPolicies()
  { 
    super.createEditPolicies();
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new DragAndDropEditPolicy(getViewer(), selectionHandlesEditPolicy));      
  }
  
  public void activate() 
  {
    super.activate();
    if (getXSDParticle() != null)
    {
      XSDModelAdapterFactory.addModelAdapterListener(getXSDParticle(), this);
    }
  }
  /** 
   * Apart from the deactivation done in super, the source
   * and target connections are deactivated, and the visual
   * part of the this is removed.
   *
   * @see #activate() 
   */
  public void deactivate() 
  {
    if (getXSDParticle() != null)
    {
      XSDModelAdapterFactory.removeModelAdapterListener(getXSDParticle(), this);
    }
    super.deactivate();
  }   

}
