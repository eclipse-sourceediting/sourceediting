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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

public abstract class XSDTypeDefinitionAdapter extends XSDBaseAdapter implements IType
{
  public XSDTypeDefinition getXSDTypeDefinition()
  {
    return (XSDTypeDefinition)target;
  }

  public String getName()
  {
    if (getXSDTypeDefinition().eContainer() instanceof XSDSchema)
    {  
      return getXSDTypeDefinition().getName();
    }
    else 
    {
      EObject o = getXSDTypeDefinition().eContainer();
      if (o instanceof XSDNamedComponent)
      {
         XSDNamedComponent ed = (XSDNamedComponent)o;
         return "(" + ed.getName() + "Type)";               
      }
    }
    return null;
  }

  public String getQualifier()
  {
    return getXSDTypeDefinition().getTargetNamespace();
  }

  public IType getSuperType()
  {
    XSDTypeDefinition td = getXSDTypeDefinition().getBaseType();
    return td != null ? (IType)XSDAdapterFactory.getInstance().adapt(td) : null;
  }

  public Command getUpdateNameCommand(String newName)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isComplexType()
  {
    return false;
  }    
}
