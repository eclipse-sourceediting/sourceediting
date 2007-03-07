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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;

public class FocusTypeColumn extends AbstractModelCollection
{
  protected boolean isFocusObject = false;
  
  public FocusTypeColumn(IADTObject model)
  {
    super(model, "FocusTypeColumn"); //$NON-NLS-1$  
  }

  public FocusTypeColumn(IADTObject model, boolean isFocusObject)
  {
    super(model, "FocusTypeColumn"); //$NON-NLS-1$
    this.isFocusObject = isFocusObject;
  }

  public List getChildren()
  {
    List result = new ArrayList();  
    if (model instanceof IType)
    {
      IType type = (IType)model;
      if (type.getSuperType() != null)
      {  
        result.add(type.getSuperType());
      }
      result.add(type);
    }  
    else if (model instanceof IField ||
             model instanceof IStructure)
    {   
      result.add(model);
    }       
    return result;       
  }
  
  public boolean isFocusObject()
  {
    return isFocusObject;
  }
}
