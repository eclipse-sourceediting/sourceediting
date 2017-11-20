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
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;

public class Compartment implements IADTObject
{
  String kind;
  IStructure owner;

  public Compartment(IStructure owner, String kind)
  {
    this.kind = kind;
    this.owner = owner;
  }

  public List getChildren()
  {
    List list = new ArrayList();
    for (Iterator i = owner.getFields().iterator(); i.hasNext();)
    {
      IField field = (IField) i.next();
      if (kind == null || kind.equals(field.getKind()))
      {
        list.add(field);
      }
    }
    return list;
  }

  public String getKind()
  {
    return kind;
  }
  
  public IStructure getOwner()
  {
    return owner;
  }

  public void registerListener(IADTObjectListener listener)
  {
    // really we want to listen to the owner
    owner.registerListener(listener);
  }

  public void unregisterListener(IADTObjectListener listener)
  {
    // really we want to listen to the owner
    owner.unregisterListener(listener);
  }

  public boolean isReadOnly()
  {
    return false;
  }
}
