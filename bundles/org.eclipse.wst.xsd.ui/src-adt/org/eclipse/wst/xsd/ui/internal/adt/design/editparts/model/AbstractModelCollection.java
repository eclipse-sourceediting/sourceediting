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

import java.util.List;

import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;

public abstract class AbstractModelCollection implements IADTObject
{
  protected IADTObject model;
  protected String kind;
  
  public AbstractModelCollection(IADTObject model, String kind)
  {
    this.model = model;
    this.kind = kind;
  }

  public Object getModel()
  {
    return model;
  }

  public void setModel(IADTObject model)
  {
    this.model = model;
  }

  public String getKind()
  {
    return kind;
  }

  public void setKind(String kind)
  {
    this.kind = kind;
  }
  
  public abstract List getChildren();

  public void registerListener(IADTObjectListener listener)
  {
    model.registerListener(listener);
  }

  public void unregisterListener(IADTObjectListener listener)
  {
    model.unregisterListener(listener);
  }
  
  public boolean isReadOnly()
  {
    return false;
  }
}
