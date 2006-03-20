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
package org.eclipse.wst.xsd.adt.design.editparts;

import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.wst.xsd.adt.design.editparts.model.Annotation;
import org.eclipse.wst.xsd.adt.design.editparts.model.Compartment;
import org.eclipse.wst.xsd.adt.design.figures.ICompartmentFigure;
import org.eclipse.wst.xsd.adt.facade.IField;

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

  protected void createEditPolicies()
  {
    // installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new
    // SelectionFeedbackEditPolicy(this));
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
