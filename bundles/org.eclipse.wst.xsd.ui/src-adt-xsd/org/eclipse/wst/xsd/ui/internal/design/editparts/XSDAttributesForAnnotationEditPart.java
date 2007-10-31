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

import java.util.Collections;
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.adapters.XSDComplexTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.SectionEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.Annotation;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;

public class XSDAttributesForAnnotationEditPart extends SectionEditPart
{
  public XSDAttributesForAnnotationEditPart()
  {
    super();
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
