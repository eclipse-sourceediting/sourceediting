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
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAttributeGroupDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CenteredConnectionAnchor;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.TargetConnectionSpaceFiller;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;

public class AttributeGroupDefinitionEditPart extends ConnectableEditPart
{
  public static final Image ATTRIBUTE_GROUP_REF_ICON_IMAGE = XSDEditorPlugin.getImageDescriptor("attgrref_obj.gif", true).createImage();

  public AttributeGroupDefinitionEditPart()
  {
    super();
  }

  public XSDAttributeGroupDefinition getXSDAttributeGroupDefinition()
  {
    if (getModel() instanceof XSDAttributeGroupDefinitionAdapter)
    {
      XSDAttributeGroupDefinitionAdapter adapter = (XSDAttributeGroupDefinitionAdapter) getModel();
      return (XSDAttributeGroupDefinition) adapter.getTarget();
    }
//    else if (getModel() instanceof XSDAttributeGroupDefinition)
//    {
//      return (XSDAttributeGroupDefinition) getModel();
//    }
    return null;

  }

  protected IFigure createFigure()
  {
    GenericGroupFigure figure = new GenericGroupFigure();
    figure.getIconFigure().image = ATTRIBUTE_GROUP_REF_ICON_IMAGE;
    return figure;
  }

  protected List getModelChildren()
  {
    List list = new ArrayList();
    
    XSDAttributeGroupDefinitionAdapter adapter = (XSDAttributeGroupDefinitionAdapter)getModel();
    XSDAttributeGroupDefinition attributeGroupDefinition = adapter.getXSDAttributeGroupDefinition();
    Iterator i = attributeGroupDefinition.getResolvedAttributeGroupDefinition().getContents().iterator();

    while (i.hasNext())
    {
      XSDAttributeGroupContent attrGroupContent = (XSDAttributeGroupContent) i.next();

      if (attrGroupContent instanceof XSDAttributeGroupDefinition)
      {
        list.add(XSDAdapterFactory.getInstance().adapt(attrGroupContent));
      }
      else
      {
        list.add(new TargetConnectionSpaceFiller((XSDBaseAdapter)getModel()));
      }
    }
    
    if (list.isEmpty())
    {
      list.add(new TargetConnectionSpaceFiller((XSDBaseAdapter)getModel()));
    }

    return list;
  }


  public ReferenceConnection createConnectionFigure(BaseEditPart child)
  {
    ReferenceConnection connectionFigure = new ReferenceConnection();

    connectionFigure.setSourceAnchor(new CenteredConnectionAnchor(((GenericGroupFigure)getFigure()).getIconFigure(), CenteredConnectionAnchor.RIGHT, 0, 0));

    if (child instanceof AttributeGroupDefinitionEditPart)
    {
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(((AttributeGroupDefinitionEditPart) child).getTargetFigure(), CenteredConnectionAnchor.LEFT, 0, 0));
    }
    else if (child instanceof TargetConnectionSpacingFigureEditPart)
    {
      TargetConnectionSpacingFigureEditPart elem = (TargetConnectionSpacingFigureEditPart) child;
      connectionFigure.setTargetAnchor(new CenteredConnectionAnchor(((TargetConnectionSpacingFigureEditPart) child).getFigure(), CenteredConnectionAnchor.LEFT, 0, 0));
    }

    connectionFigure.setHighlight(false);
    return connectionFigure;
  }

}
