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

import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;

public final class AddElementDeclarationCommand extends AbstractCommand
{
  private String name; // element name
  
  public AddElementDeclarationCommand
    (XSDConcreteComponent parent,
     String name)
  {
    super(parent);
    this.name = name;
  }
  
  public void run()
  {
    XSDElementDeclaration elementDecl = 
      XSDFactory.eINSTANCE.createXSDElementDeclaration();
    elementDecl.setName(name);
    
    if (adopt(elementDecl))
      setModelObject(elementDecl);
  }

  protected boolean adopt(XSDConcreteComponent model)
  {
  	XSDConcreteComponent parent = getParent();
    if (parent instanceof XSDSchema)
      ((XSDSchema)parent).getElementDeclarations().add(model);
    else if (parent instanceof XSDParticle)
      ((XSDParticle)parent).setContent((XSDElementDeclaration)model);
    else
      return false; // invalid parent
    
    return true;
  }
}
