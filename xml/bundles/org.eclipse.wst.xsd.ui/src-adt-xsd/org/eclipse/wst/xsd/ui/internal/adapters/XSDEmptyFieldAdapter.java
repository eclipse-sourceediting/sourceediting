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
package org.eclipse.wst.xsd.ui.internal.adapters;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;

/**
 * @deprecated not used
 */
public class XSDEmptyFieldAdapter extends XSDBaseAdapter implements IField
{
  String kind;
  public XSDEmptyFieldAdapter()
  {
    super();
  }

  public String getKind()
  {
    return kind;
  }
  
  public void setKind(String kind)
  {
    this.kind = kind;
  }

  public String getName()
  {
    return null;
  }

  public String getTypeName()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public String getTypeNameQualifier()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public IType getType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public int getMinOccurs()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getMaxOccurs()
  {
    // TODO Auto-generated method stub
    return 0;
  }
  
  public boolean isGlobal()
  {
    return false;
  }

  public Command getUpdateMinOccursCommand(int minOccurs)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateMaxOccursCommand(int maxOccurs)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateTypeNameCommand(String typeName, String quailifier)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateNameCommand(String name)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getDeleteCommand()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public IModel getModel()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isReference()
  {
    // TODO Auto-generated method stub
    return false;
  }
}
