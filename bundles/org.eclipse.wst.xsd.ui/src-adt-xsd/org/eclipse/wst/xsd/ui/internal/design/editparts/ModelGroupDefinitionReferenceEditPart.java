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
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CenteredConnectionAnchor;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;

public class ModelGroupDefinitionReferenceEditPart extends ConnectableEditPart
{
  GenericGroupFigure figure;
  
  public ModelGroupDefinitionReferenceEditPart()
  {
    super();
  }

  protected IFigure createFigure()
  {
    figure = new GenericGroupFigure();
    return figure;
  }
  
  protected void refreshVisuals()
  {
    super.refreshVisuals();
    XSDModelGroupDefinitionAdapter adapter = (XSDModelGroupDefinitionAdapter)getModel();
    figure.getIconFigure().image = adapter.getImage();
  }

  protected List getModelChildren()
  {
    List list = new ArrayList();

    XSDModelGroupDefinitionAdapter adapter = (XSDModelGroupDefinitionAdapter)getModel();
    XSDModelGroup xsdModelGroup = ((XSDModelGroupDefinition) adapter.getTarget()).getResolvedModelGroupDefinition().getModelGroup();
    if (xsdModelGroup != null)
      list.add(XSDAdapterFactory.getInstance().adapt(xsdModelGroup));
    return list;
  }

  public ReferenceConnection createConnectionFigure(BaseEditPart child)
  {
    ReferenceConnection connectionFigure = new ReferenceConnection();

    connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(((GenericGroupFigure)getFigure()).getIconFigure(), CenteredConnectionAnchor.RIGHT, 0, 0));

    if (child instanceof ModelGroupEditPart)
    {
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(((ModelGroupEditPart) child).getTargetFigure(), CenteredConnectionAnchor.LEFT, 0, 0));
    }
    connectionFigure.setHighlight(false);
    return connectionFigure;
  }


}
