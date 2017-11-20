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
package org.eclipse.wst.xsd.ui.internal.design.editparts.model;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

/**
 * Dummy class to add space to field list
 *
 */
public class SpaceFiller implements IField
{
  String kind;
  public SpaceFiller(String kind)
  {
    super();
    this.kind = kind;
  }

  public Image getImage()
  {
    if (kind.equals("attribute")) //$NON-NLS-1$
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif"); //$NON-NLS-1$
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDElement.gif"); //$NON-NLS-1$
    }
  }
  
  public String getKind()
  {
    return kind;
  }
  
  public void setKind(String kind)
  {
    this.kind = kind;
  }
  
  public boolean isGlobal()
  {
    return false;
  }

  public IComplexType getContainerType()
  {
    return null;
  }

  public String getName()
  {
    // TODO Auto-generated method stub
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

  public void registerListener(IADTObjectListener listener)
  {
    // TODO Auto-generated method stub
    
  }

  public void unregisterListener(IADTObjectListener listener)
  {
    // TODO Auto-generated method stub
    
  }
  
  public boolean isReadOnly()
  {
    return true;
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

  public boolean isAbstract()
  {
    // TODO Auto-generated method stub
    return false;
  }
}


