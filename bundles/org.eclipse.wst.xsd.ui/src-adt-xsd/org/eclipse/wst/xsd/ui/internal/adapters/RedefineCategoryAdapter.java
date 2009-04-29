/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adapters;


import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedAttributeGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedComplexTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefinedSimpleTypeAction;
import org.eclipse.xsd.XSDRedefine;


public class RedefineCategoryAdapter extends CategoryAdapter
{
  protected XSDRedefine xsdRedefine;
  private XSDRedefineAdapter xsdRedefineAdapter;

  public RedefineCategoryAdapter(String label, Image image, Collection children, XSDRedefine xsdRedefine, XSDRedefineAdapter xsdRedefineAdapter, int groupType)
  {
    super(label, image, children, xsdRedefine.getSchema(), groupType);
    this.xsdRedefine = xsdRedefine;
    this.target = xsdRedefine;
    this.xsdRedefineAdapter = xsdRedefineAdapter;
  }

  public XSDRedefine getXSDRedefine()
  {
    return xsdRedefine;
  }

  public String[] getActions(Object object)
  {
    Collection actionIDs = new ArrayList();

    switch (groupType)
    {
      case TYPES:
      {
        actionIDs.add(AddXSDRedefinedComplexTypeAction.ID);
        actionIDs.add(AddXSDRedefinedSimpleTypeAction.ID);
        break;
      }
      case GROUPS:
      {
        actionIDs.add(AddXSDRedefinedModelGroupAction.ID);
        break;
      }
      case ATTRIBUTES:
      case ATTRIBUTE_GROUPS:
      {
        actionIDs.add(AddXSDRedefinedAttributeGroupAction.ID);
        break;
      }
    }
    actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
    actionIDs.add(ShowPropertiesViewAction.ID);
    return (String[])actionIDs.toArray(new String [0]);
  }

  public IModel getModel()
  {
    return (IModel)XSDAdapterFactory.getInstance().adapt(xsdRedefine.getSchema());
  }


  public XSDRedefineAdapter getXsdRedefineAdapter() {
	return xsdRedefineAdapter;
  }
  
}