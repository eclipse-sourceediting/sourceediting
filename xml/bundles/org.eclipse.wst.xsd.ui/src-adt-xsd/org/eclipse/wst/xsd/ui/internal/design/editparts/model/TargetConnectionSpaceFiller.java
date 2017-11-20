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
package org.eclipse.wst.xsd.ui.internal.design.editparts.model;

import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;

public class TargetConnectionSpaceFiller implements IADTObject
{
  private XSDBaseAdapter adapter;
  
  public TargetConnectionSpaceFiller(XSDBaseAdapter adapter)
  {
    this.adapter = adapter;
  }

  public XSDBaseAdapter getAdapter()
  {
    return adapter;
  }

  public void registerListener(IADTObjectListener listener)
  {
    
  }

  public void unregisterListener(IADTObjectListener listener)
  {
   
  }

  public boolean isReadOnly()
  {
    return false;
  }
  
}
