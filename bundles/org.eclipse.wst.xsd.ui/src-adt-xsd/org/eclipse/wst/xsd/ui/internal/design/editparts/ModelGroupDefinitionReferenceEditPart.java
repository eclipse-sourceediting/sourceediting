/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
import org.eclipse.gef.EditPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CenteredConnectionAnchor;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.TargetConnectionSpaceFiller;
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

    GenericGroupFigure groupFigure = (GenericGroupFigure)getFigure();
    String nodeName = "";
    
    // TODO: commmon this up with ModelGroupEditPart, XSDParticleAdapter's code
    
    // -2 means the user didn't specify (so the default is 1)
    int minOccurs = adapter.getMinOccurs();
    int maxOccurs = adapter.getMaxOccurs();
    String occurenceDescription = ""; //$NON-NLS-1$
    
    if (minOccurs == -3 && maxOccurs == -3)
    {
      occurenceDescription = nodeName;
      groupFigure.setText(null);
    }
    else if (minOccurs == 0 && (maxOccurs == -2 || maxOccurs == 1))
    {
      occurenceDescription = nodeName + " [0..1]"; //$NON-NLS-1$
      groupFigure.setText("0..1");
    }
    else if ((minOccurs == 1 && maxOccurs == 1) ||
             (minOccurs == -2 && maxOccurs == 1) ||
             (minOccurs == 1 && maxOccurs == -2))
    {
      occurenceDescription = nodeName + " [1..1]"; //$NON-NLS-1$
      groupFigure.setText("1..1");
    }
    else if (minOccurs == -2 && maxOccurs == -2)
    {
      occurenceDescription = nodeName;
      groupFigure.setText(null);
    }
    else
    {
      if (maxOccurs == -2) maxOccurs = 1;
      String maxSymbol = maxOccurs == -1 ? "*" : "" + maxOccurs; //$NON-NLS-1$ //$NON-NLS-2$
      
      String minSymbol = minOccurs == -2 ? "1" : "" + minOccurs; //$NON-NLS-1$ //$NON-NLS-2$
      occurenceDescription = nodeName + " [" + minSymbol + ".." + maxSymbol + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      groupFigure.setText(minSymbol + ".." + maxSymbol);
    }

    groupFigure.getIconFigure().setToolTipText(occurenceDescription);
    groupFigure.getIconFigure().repaint();

  }

  protected List getModelChildren()
  {
    List list = new ArrayList();

    XSDModelGroupDefinitionAdapter adapter = (XSDModelGroupDefinitionAdapter)getModel();
    XSDModelGroupDefinition groupDef = ((XSDModelGroupDefinition) adapter.getTarget());
    XSDModelGroupDefinition resolvedGroupDef = groupDef.getResolvedModelGroupDefinition();
    XSDModelGroup xsdModelGroup = resolvedGroupDef.getModelGroup();
    
    ArrayList listOfVisibleGroupRefs = new ArrayList();
    for (EditPart ep = getParent(); ep != null; )
    {
      Object object = ep.getModel();
      if (object instanceof XSDModelGroupDefinitionAdapter)
      {
        Object model = ((XSDModelGroupDefinitionAdapter)object).getTarget();
        if (model instanceof XSDModelGroupDefinition)
        {
          listOfVisibleGroupRefs.add(((XSDModelGroupDefinition)model).getResolvedModelGroupDefinition());          
        }
      }
      ep = ep.getParent();
    }
    
    boolean isCyclic = (listOfVisibleGroupRefs.contains(resolvedGroupDef));
    
    if (xsdModelGroup != null && !isCyclic)
      list.add(XSDAdapterFactory.getInstance().adapt(xsdModelGroup));
    
    if (isCyclic)
      list.add(new TargetConnectionSpaceFiller(null));

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
    else // for the cyclic group references
    {
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(((GenericGroupFigure) getFigure()).getIconFigure(), CenteredConnectionAnchor.RIGHT, 0, 0));
    }
    
    connectionFigure.setHighlight(false);

    return connectionFigure;
  }
}
