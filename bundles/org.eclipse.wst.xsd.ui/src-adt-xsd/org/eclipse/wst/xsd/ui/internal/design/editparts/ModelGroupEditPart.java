/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDModelGroupAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CenteredConnectionAnchor;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.TargetConnectionSpaceFiller;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.wst.xsd.ui.internal.design.figures.ModelGroupFigure;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;

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
    boolean isReadOnly = false;
    GenericGroupFigure modelGroupFigure = (GenericGroupFigure)getFigure();
    
    XSDModelGroupAdapter adapter = (XSDModelGroupAdapter) getModel();
    isReadOnly = adapter.isReadOnly();
    
    String nodeName = "";
    Image image;
    switch (getXSDModelGroup().getCompositor().getValue())
    {
      case XSDCompositor.ALL:
      {
        image = isReadOnly ? ModelGroupFigure.ALL_ICON_DISABLED_IMAGE :ModelGroupFigure.ALL_ICON_IMAGE;
        modelGroupFigure.getIconFigure().image = XSDCommonUIUtils.getUpdatedImage((XSDConcreteComponent)adapter.getTarget(), image, isReadOnly);
        nodeName = XSDConstants.ALL_ELEMENT_TAG;
        break;
      }
      case XSDCompositor.CHOICE:
      {
        image = isReadOnly ? ModelGroupFigure.CHOICE_ICON_DISABLED_IMAGE : ModelGroupFigure.CHOICE_ICON_IMAGE;
        modelGroupFigure.getIconFigure().image = XSDCommonUIUtils.getUpdatedImage((XSDConcreteComponent)adapter.getTarget(), image, isReadOnly); 
        nodeName = XSDConstants.CHOICE_ELEMENT_TAG;
        break;
      }
      case XSDCompositor.SEQUENCE:
      {
        image = isReadOnly ? ModelGroupFigure.SEQUENCE_ICON_DISABLED_IMAGE : ModelGroupFigure.SEQUENCE_ICON_IMAGE;
        modelGroupFigure.getIconFigure().image = XSDCommonUIUtils.getUpdatedImage((XSDConcreteComponent)adapter.getTarget(), image, isReadOnly); 
        nodeName = XSDConstants.SEQUENCE_ELEMENT_TAG;
        break;
      }
    }
    
//    String occurenceDescription = adapter.getNameAnnotationToolTipString();
//    modelGroupFigure.getIconFigure().setToolTip(occurenceDescription);

    // TODO: commmon this up with XSDParticleAdapter's code
    
    // -2 means the user didn't specify (so the default is 1)
    int minOccurs = adapter.getMinOccurs();
    int maxOccurs = adapter.getMaxOccurs();
    String occurenceDescription = ""; //$NON-NLS-1$
    
    if (minOccurs == -3 && maxOccurs == -3)
    {
      occurenceDescription = nodeName;
      modelGroupFigure.setText(null);
    }
    else if (minOccurs == 0 && (maxOccurs == -2 || maxOccurs == 1))
    {
      occurenceDescription = nodeName + " [0..1]"; //$NON-NLS-1$
      modelGroupFigure.setText("0..1");
    }
    else if ((minOccurs == 1 && maxOccurs == 1) ||
             (minOccurs == -2 && maxOccurs == 1) ||
             (minOccurs == 1 && maxOccurs == -2))
    {
      occurenceDescription = nodeName + " [1..1]"; //$NON-NLS-1$
      modelGroupFigure.setText("1..1");
    }
    else if (minOccurs == -2 && maxOccurs == -2)
    {
      occurenceDescription = nodeName;
      modelGroupFigure.setText(null);
    }
    else
    {
      if (maxOccurs == -2) maxOccurs = 1;
      String maxSymbol = maxOccurs == -1 ? "*" : "" + maxOccurs; //$NON-NLS-1$ //$NON-NLS-2$
      
      String minSymbol = minOccurs == -2 ? "1" : "" + minOccurs; //$NON-NLS-1$ //$NON-NLS-2$
      occurenceDescription = nodeName + " [" + minSymbol + ".." + maxSymbol + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      modelGroupFigure.setText(minSymbol + ".." + maxSymbol);
    }

    modelGroupFigure.getIconFigure().setToolTipText(occurenceDescription);
    modelGroupFigure.getIconFigure().repaint();

    refreshConnection();
  }

  protected List getModelChildren()
  {
//    XSDModelGroupAdapter modelGroupAdapter = (XSDModelGroupAdapter)getModel();
//    ArrayList ch = new ArrayList();
//    ITreeElement [] tree = modelGroupAdapter.getChildren();
//    int length = tree.length;
//    for (int i = 0; i < length; i++)
//    {
//      ch.add(tree[i]);
//    }

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
      else if (next.getTerm() instanceof XSDWildcard)
      {
        XSDWildcard wildCard = (XSDWildcard)next.getTerm();
        Adapter adapter = XSDAdapterFactory.getInstance().adapt(wildCard);
        list.add(new TargetConnectionSpaceFiller((XSDBaseAdapter)adapter));
      }
    }

    if (list.size() == 0)
      list.add(new TargetConnectionSpaceFiller(null));

    return list;
//    return ch;
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
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(elem.getFigure(), CenteredConnectionAnchor.LEFT, 0, 1));
    }
    else if (child instanceof ModelGroupDefinitionReferenceEditPart)
    {
      ModelGroupDefinitionReferenceEditPart elem = (ModelGroupDefinitionReferenceEditPart) child;
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(elem.getFigure(), CenteredConnectionAnchor.LEFT, 0, 1));
    }
    connectionFigure.setHighlight(false);
    return connectionFigure;
  }
  
  public String getReaderText()
  {
	  XSDModelGroup xsdModelGroup = getXSDModelGroup();
	  return  xsdModelGroup.getCompositor().getName();
  }
}
