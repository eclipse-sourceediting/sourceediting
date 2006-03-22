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
package org.eclipse.wst.xsd.editor.internal.design.editparts;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.wst.xsd.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.adt.design.editparts.model.Annotation;
import org.eclipse.wst.xsd.adt.facade.IComplexType;
import org.eclipse.wst.xsd.adt.facade.IStructure;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDComplexTypeDefinitionAdapter;

public class XSDAttributesForAnnotationEditPart extends BaseEditPart
{
  IComplexType complexType;
  public XSDAttributesForAnnotationEditPart()
  {
    super();
  }

  protected IFigure createFigure()
  {
    Figure fig = new Figure();
    fig.setLayoutManager(new ToolbarLayout());
    return fig;
  }

  protected void createEditPolicies()
  {

  }

  protected List getModelChildren()
  {
    IStructure structure =  ((Annotation)getModel()).getOwner();
    if (structure instanceof IComplexType)
    {  
      complexType = (IComplexType)structure;
      if (complexType instanceof XSDComplexTypeDefinitionAdapter)
      {
        XSDComplexTypeDefinitionAdapter adapter = (XSDComplexTypeDefinitionAdapter) complexType;
        return adapter.getAttributeGroupContent();
      }
    }
    return Collections.EMPTY_LIST;
  }
}
