/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.Annotation;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.Compartment;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.KeyBoardAccessibilityEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.ICompartmentFigure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;

// TODO (cs) common-up with BoxEditPart (?)
public class CompartmentEditPart extends BaseEditPart // implements
                                                      // IFeedbackHandler
{
  Annotation annotation = new Annotation();

  protected IFigure createFigure()
  {
    ICompartmentFigure figure = getFigureFactory().createCompartmentFigure(getModel());
    return figure;
  }

  public IFigure getContentPane()
  {
    return getCompartmentFigure().getContentPane();
  }
  
  public boolean hasContent()
  {
    // since the annotation always takes up 1 child, here's a convenience method to figure out if
    return getChildren().size() > 1;
  }

  public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
  {
    EditPart result = null;   
    if (getChildren().contains(editPart))
    {  
      if (direction == KeyBoardAccessibilityEditPolicy.OUT_TO_PARENT)
      {
        Compartment compartment = (Compartment)getModel();
        for (EditPart parent = editPart.getParent(); parent != null; parent = parent.getParent())
        {            
          if (parent.getModel() == compartment.getOwner())
          {
            result = parent;
            break;
          }  
        }  
      }        
      else if (direction == PositionConstants.SOUTH)
      {    
        int size = getChildren().size();
        if (size > 1)
        {                              
          if (getChildren().get(size - 2) == editPart)
          {  
            CompartmentEditPart nextCompartment = (CompartmentEditPart)EditPartNavigationHandlerUtil.getNextSibling(CompartmentEditPart.this);
            if (nextCompartment != null && nextCompartment.hasContent())
            {            
              result = EditPartNavigationHandlerUtil.getFirstChild(nextCompartment);
            }  
          }
        }
      }
      else if (direction == PositionConstants.NORTH)
      {
        if (EditPartNavigationHandlerUtil.getFirstChild(CompartmentEditPart.this) == editPart)
        {  
          EditPart prevCompartment = EditPartNavigationHandlerUtil.getPrevSibling(CompartmentEditPart.this);
          if (prevCompartment != null)
          {
            int size = prevCompartment.getChildren().size();
            if (size > 1)
            {  
              result = (EditPart)prevCompartment.getChildren().get(size - 2);
            }  
          }              
        }
      }
    }
    return result;      
  }
 
  protected void addChildVisual(EditPart childEditPart, int index)
  {
    Object model = childEditPart.getModel();

    IFigure child = ((GraphicalEditPart) childEditPart).getFigure();

    if (model instanceof IField)
    {
      getCompartmentFigure().getContentPane().add(child, index);
      return;
    }
    else if (model instanceof Annotation)
    {
      getCompartmentFigure().getAnnotationPane().add(child);
      return;
    }
    super.addChildVisual(childEditPart, index);
  }

  protected void removeChildVisual(EditPart childEditPart)
  {
    Object model = childEditPart.getModel();
    IFigure child = ((GraphicalEditPart) childEditPart).getFigure();

    if (model instanceof IField)
    {
      getCompartmentFigure().getContentPane().remove(child);
      return;
    }
    else if (model instanceof Annotation)
    {
      getCompartmentFigure().getAnnotationPane().remove(child);
      return;
    }
    super.removeChildVisual(childEditPart);
  }

  protected Compartment getCompartment()
  {
    return (Compartment) getModel();
  }

  protected List getModelChildren()
  {
    List children = getCompartment().getChildren();
    children.add(annotation);
    return children;
  }
  
  public void setModel(Object model)
  {
    super.setModel(model);
    annotation.setCompartment(getCompartment());
  }

  protected void refreshChildren()
  {
    super.refreshChildren();
    // ((AbstractGraphicalEditPart)getParent()).getContentPane().invalidate();
  }

  protected void refreshVisuals()
  {
    super.refreshVisuals();
  }

  public void addFeedback()
  {
    // getFigure().setBackgroundColor(ColorConstants.blue);
    // ((CompartmentFigure)getFigure()).setBorderColor(ColorConstants.black);
    getFigure().repaint();
  }

  public void removeFeedback()
  {
    // getFigure().setBackgroundColor(ColorConstants.lightBlue);
    // ((CompartmentFigure)getFigure()).setBorderColor(ColorConstants.lightGray);
    getFigure().repaint();
  }
  
  public ICompartmentFigure getCompartmentFigure()
  {
    return (ICompartmentFigure)figure;
  }

  public void addNotify()
  {  
    super.addNotify();
    getCompartmentFigure().editPartAttached(this);   
  }   
}
