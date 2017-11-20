/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CompartmentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.ADTSelectionFeedbackEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.KeyBoardAccessibilityEditPolicy;
import org.eclipse.wst.xsd.ui.internal.design.figures.CenteredIconFigure;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.wst.xsd.ui.internal.design.figures.IExtendedFigureFactory;
import org.eclipse.xsd.XSDConcreteComponent;

public abstract class ConnectableEditPart extends BaseEditPart
{
  protected ArrayList connectionFigures = new ArrayList();
  
  public IExtendedFigureFactory getExtendedFigureFactory()
  {
    EditPartFactory factory = getViewer().getEditPartFactory();
    Assert.isTrue(factory instanceof IExtendedFigureFactory, "EditPartFactory must be an instanceof of IExtendedFigureFactory");     //$NON-NLS-1$
    return (IExtendedFigureFactory)factory; 
  }
  
  public ConnectableEditPart()
  {
    super();
  }
  
  protected IFigure createFigure()
  {
    GenericGroupFigure figure = new GenericGroupFigure();
    return figure;
  }

  public XSDConcreteComponent getXSDConcreteComponent()
  {
    return (XSDConcreteComponent)((XSDBaseAdapter)getModel()).getTarget();
  }
  
  public List getConnectionFigures()
  {
    return connectionFigures;
  }
  
  public abstract ReferenceConnection createConnectionFigure(BaseEditPart child);
  
  public void activate()
  {
    super.activate();
    activateConnection();
  }

  protected void activateConnection()
  {
    if (connectionFigures == null)
    {
      connectionFigures = new ArrayList();
    }
    for (Iterator i = getChildren().iterator(); i.hasNext();)
    {
      Object o = i.next();
      if (o instanceof BaseEditPart)
      {
        BaseEditPart g = (BaseEditPart) o;
        ReferenceConnection figure = createConnectionFigure(g);
        connectionFigures.add(figure);
        figure.setPoints(figure.getPoints());

        getLayer(LayerConstants.CONNECTION_LAYER).add(figure);
      }
    }
  }
  
  public void deactivate()
  {
    super.deactivate();
    deactivateConnection();
  }

  protected void deactivateConnection()
  {
    // if we have a connection, remove it
    ReferenceConnection connectionFigure;
    if (connectionFigures != null && !connectionFigures.isEmpty())
    {
      for (Iterator i = connectionFigures.iterator(); i.hasNext();)
      {
        connectionFigure = (ReferenceConnection) i.next();

        if (getLayer(LayerConstants.CONNECTION_LAYER).getChildren().contains(connectionFigure))
        {
          getLayer(LayerConstants.CONNECTION_LAYER).remove(connectionFigure);
        }
      }
      connectionFigures = null;
    }
  }

  public void refresh()
  {
    super.refresh();
    refreshConnection();
  }

  protected void refreshConnection()
  {
    if (!isActive())
      return;

    if (connectionFigures == null || connectionFigures.isEmpty())
    {
      activateConnection();
    }
    else
    {
      deactivateConnection();
      activateConnection();
    }
  }
  
  public void addFeedback()
  {
    ReferenceConnection connectionFigure;
    if (connectionFigures != null && !connectionFigures.isEmpty())
    {
      for (Iterator i = connectionFigures.iterator(); i.hasNext();)
      {
        connectionFigure = (ReferenceConnection) i.next();
        connectionFigure.setHighlight(true);
      }
    }
    GenericGroupFigure figure = (GenericGroupFigure)getFigure();
    figure.getIconFigure().setMode(CenteredIconFigure.SELECTED);
    figure.getIconFigure().refresh();
  }
  
  public void removeFeedback()
  {
    ReferenceConnection connectionFigure;
    if (connectionFigures != null && !connectionFigures.isEmpty())
    {
      for (Iterator i = connectionFigures.iterator(); i.hasNext();)
      {
        connectionFigure = (ReferenceConnection) i.next();
        connectionFigure.setHighlight(false);
      }
    }
    GenericGroupFigure figure = (GenericGroupFigure)getFigure();
    figure.getIconFigure().setMode(CenteredIconFigure.NORMAL);
    figure.getIconFigure().refresh();
  }
  
  protected void refreshVisuals()
  {
    super.refreshVisuals();
    GenericGroupFigure figure = (GenericGroupFigure)getFigure();
    figure.getIconFigure().refresh();
  }

  protected void createEditPolicies()
  {
    super.createEditPolicies();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ADTSelectionFeedbackEditPolicy());
  }

  protected void addChildVisual(EditPart childEditPart, int index)
  {
    IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
    getContentPane().add(child, index);
  }

  protected void removeChildVisual(EditPart childEditPart)
  {
    IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
    getContentPane().remove(child);
  }
  
  public IFigure getContentPane()
  {
    return ((GenericGroupFigure)getFigure()).getContentFigure();
  }

  public Figure getTargetFigure()
  {
    return ((GenericGroupFigure)getFigure()).getTargetFigure();
  }
  
  public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
  {
    EditPart result = null;
    if (direction == PositionConstants.WEST)
    {
      if (getParent() instanceof ConnectableEditPart)
      {
        result = getParent();
      }
      else
      {
        result = this;
      }
    }
    else if (direction == PositionConstants.EAST)
    {    
      result = (EditPart) editPart.getChildren().get(0);
      TargetConnectionSpacingFigureEditPart target = null;
      boolean foundSelectableGroup = false;
      for (Iterator i = editPart.getChildren().iterator(); i.hasNext(); )
      {
        EditPart child = (EditPart)i.next();
        if (!(child instanceof TargetConnectionSpacingFigureEditPart))
        {
          foundSelectableGroup = true;
          result = child;
          break;
        }
      }
      if (result instanceof TargetConnectionSpacingFigureEditPart)
      {
        target = (TargetConnectionSpacingFigureEditPart) result;
      }
      if (!foundSelectableGroup && target != null)
      {
        EditPart parent = editPart.getParent();
        while (!(parent instanceof CompartmentEditPart))
        {
          parent = parent.getParent();
          if (parent == null) break;
        }
        for (Iterator i = parent.getChildren().iterator(); i.hasNext(); )
        {
          EditPart child = (EditPart) i.next();
          if (child instanceof BaseFieldEditPart && !(child instanceof SpaceFillerForFieldEditPart))
          {
            BaseFieldEditPart field = (BaseFieldEditPart) child;
            if (field.getFigure().getBounds().getCenter().y < target.getFigure().getBounds().bottom() &&
                field.getFigure().getBounds().getCenter().y > target.getFigure().getBounds().y)
            {
              return child;
            }
          }
        }
        return editPart;
      }
    }
    else if (direction == KeyBoardAccessibilityEditPolicy.IN_TO_FIRST_CHILD)
    {
      result = (EditPart) editPart.getChildren().get(0);
    }
    return result;
  }
}
