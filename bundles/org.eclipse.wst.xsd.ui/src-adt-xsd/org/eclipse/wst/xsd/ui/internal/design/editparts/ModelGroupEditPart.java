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
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CenteredConnectionAnchor;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.TargetConnectionSpaceFiller;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.wst.xsd.ui.internal.design.figures.ModelGroupFigure;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;

public class ModelGroupEditPart extends ConnectableEditPart
{
  protected IFigure createFigure()
  {
    return getExtendedFigureFactory().createModelGroupFigure(getModel());
  }
  
  public XSDParticle getXSDParticle()
  {
    Object o = getXSDModelGroup().getContainer();
    return (o instanceof XSDParticle) ? (XSDParticle) o : null;
  }

  public XSDModelGroup getXSDModelGroup()
  {
    if (getModel() instanceof XSDModelGroupAdapter)
    {
      XSDModelGroupAdapter adapter = (XSDModelGroupAdapter) getModel();
      return (XSDModelGroup) adapter.getTarget();
    }
//    else if (getModel() instanceof XSDModelGroup)
//    {
//      return (XSDModelGroup) getModel();
//    }
    return null;

  }

  
  
  protected void refreshVisuals()
  {
    GenericGroupFigure modelGroupFigure = (GenericGroupFigure)getFigure();
    switch (getXSDModelGroup().getCompositor().getValue())
    {
      case XSDCompositor.ALL:
      {
        modelGroupFigure.getIconFigure().image = ModelGroupFigure.ALL_ICON_IMAGE;
        break;
      }
      case XSDCompositor.CHOICE:
      {
        modelGroupFigure.getIconFigure().image = ModelGroupFigure.CHOICE_ICON_IMAGE;
        break;
      }
      case XSDCompositor.SEQUENCE:
      {
        modelGroupFigure.getIconFigure().image = ModelGroupFigure.SEQUENCE_ICON_IMAGE;
        break;
      }
    }

    XSDModelGroupAdapter adapter = (XSDModelGroupAdapter) getModel();
//    String occurenceDescription = adapter.getNameAnnotationToolTipString();
//    modelGroupFigure.getIconFigure().setToolTip(occurenceDescription);

    // TODO: commmon this up with XSDParticleAdapter's code
    
    // -2 means the user didn't specify (so the default is 1)
    int minOccurs = adapter.getMinOccurs();
    int maxOccurs = adapter.getMaxOccurs();
    String occurenceDescription = ""; //$NON-NLS-1$
    
    if (minOccurs == -3 && maxOccurs == -3)
    {
      occurenceDescription = ""; //$NON-NLS-1$
    }
    else if (minOccurs == 0 && (maxOccurs == -2 || maxOccurs == 1))
    {
      occurenceDescription = "[0..1]"; //$NON-NLS-1$
    }
    else if ((minOccurs == 1 && maxOccurs == 1) ||
             (minOccurs == -2 && maxOccurs == 1) ||
             (minOccurs == 1 && maxOccurs == -2))
    {
      occurenceDescription = "[1..1]"; //$NON-NLS-1$
    }
    else if (minOccurs == -2 && maxOccurs == -2)
    {
      occurenceDescription = ""; //$NON-NLS-1$
    }
    else
    {
      if (maxOccurs == -2) maxOccurs = 1;
      String maxSymbol = maxOccurs == -1 ? "*" : "" + maxOccurs; //$NON-NLS-1$ //$NON-NLS-2$
      
      String minSymbol = minOccurs == -2 ? "1" : "" + minOccurs; //$NON-NLS-1$ //$NON-NLS-2$
      occurenceDescription = "[" + minSymbol + ".." + maxSymbol + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    modelGroupFigure.getIconFigure().setToolTipText(occurenceDescription);
    modelGroupFigure.getIconFigure().repaint();

    refreshConnection();
  }

  protected List getModelChildren()
  {
    List list = new ArrayList();
    XSDModelGroup xsdModelGroup = getXSDModelGroup();
    for (Iterator i = xsdModelGroup.getContents().iterator(); i.hasNext();)
    {
      XSDParticle next = (XSDParticle) i.next();
      if (next.getContent() instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) next.getContent();
        Adapter adapter = XSDAdapterFactory.getInstance().adapt(elementDeclaration);
        list.add(new TargetConnectionSpaceFiller((XSDBaseAdapter)adapter));
      }
      if (next.getContent() instanceof XSDModelGroupDefinition)
      {
        XSDModelGroupDefinition def = (XSDModelGroupDefinition) next.getContent();
        Adapter adapter = XSDAdapterFactory.getInstance().adapt(def);
        list.add(adapter);
      }
      else if (next.getTerm() instanceof XSDModelGroup)
      {
        XSDModelGroup modelGroup = (XSDModelGroup) next.getTerm();
        Adapter adapter = XSDAdapterFactory.getInstance().adapt(modelGroup);
        list.add(adapter);
      }
    }

    if (list.size() == 0)
      list.add(new TargetConnectionSpaceFiller(null));

    return list;
  }

  public ReferenceConnection createConnectionFigure(BaseEditPart child)
  {
    ReferenceConnection connectionFigure = new ReferenceConnection();
    GenericGroupFigure modelGroupFigure = (GenericGroupFigure)getFigure();
    connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(modelGroupFigure.getIconFigure(), CenteredConnectionAnchor.RIGHT, 0, 0));

    if (child instanceof ModelGroupEditPart)
    {
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(((ModelGroupEditPart) child).getTargetFigure(), CenteredConnectionAnchor.LEFT, 0, 0));
    }
    else if (child instanceof TargetConnectionSpacingFigureEditPart)
    {
      TargetConnectionSpacingFigureEditPart elem = (TargetConnectionSpacingFigureEditPart) child;
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(elem.getFigure(), CenteredConnectionAnchor.LEFT, 0, 0));
    }
    else if (child instanceof ModelGroupDefinitionReferenceEditPart)
    {
      ModelGroupDefinitionReferenceEditPart elem = (ModelGroupDefinitionReferenceEditPart) child;
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(elem.getFigure(), CenteredConnectionAnchor.LEFT, 0, 0));
    }
    connectionFigure.setHighlight(false);
    return connectionFigure;
  }
}
