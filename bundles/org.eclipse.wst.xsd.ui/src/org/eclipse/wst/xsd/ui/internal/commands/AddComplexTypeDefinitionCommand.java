/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.commands;

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;

public final class AddComplexTypeDefinitionCommand extends AbstractCommand
{
  private String name;
  
  public AddComplexTypeDefinitionCommand
    (XSDConcreteComponent parent,
     String name)
  {
    super(parent);
    this.name = name; // this may be null for anonymous type
  }
	  
  public void run()
  {
  	XSDComplexTypeDefinition typeDef = 
      XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
    typeDef.setName(name);
    	
    if (adopt(typeDef))
      setModelObject(typeDef);
  }
  
  protected boolean adopt(XSDConcreteComponent model)
  {
  	XSDConcreteComponent parent = getParent();
    if (parent instanceof XSDSchema)
      ((XSDSchema)parent).getTypeDefinitions().add(model);
    else if (parent instanceof XSDElementDeclaration)
      ((XSDElementDeclaration)parent).setAnonymousTypeDefinition((XSDComplexTypeDefinition)model);
    else
      return false; // invalid parent
    
    return true;
  }
}
