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
package org.eclipse.wst.xsd.ui.internal.adapters;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

public abstract class XSDTypeDefinitionAdapter extends XSDBaseAdapter implements IType, IActionProvider, IGraphElement
{
  public XSDTypeDefinition getXSDTypeDefinition()
  {
    return (XSDTypeDefinition)target;
  }

  public String getName()
  {
    EObject eContainer = getXSDTypeDefinition().eContainer();
    if (eContainer instanceof XSDSchema || eContainer instanceof XSDRedefine)
    {  
      return getXSDTypeDefinition().getName();
    }
    else 
    {
      if (eContainer instanceof XSDNamedComponent)
      {
         XSDNamedComponent ed = (XSDNamedComponent)eContainer;
         return "(" + ed.getName() + "Type)";                //$NON-NLS-1$ //$NON-NLS-2$
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
