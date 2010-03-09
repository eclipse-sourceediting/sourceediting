/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor;


import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;


public class RenameEnablementTester extends RefactorEnablementTester
{
  protected boolean canEnable(XSDConcreteComponent selectedObject, XSDSchema schema)
  {
    if (selectedObject == null)
    {
      return false;
    }

    XSDNamedComponent selectedComponent = null;
    boolean enable = false;

    XSDSchema selectedComponentSchema = null;
    selectedComponentSchema = selectedObject.getSchema();
    if (selectedComponentSchema != null && selectedComponentSchema == schema)
    {
      enable = true;
    }

    if (enable && selectedObject instanceof XSDNamedComponent)
    {
      selectedComponent = (XSDNamedComponent)selectedObject;

      if (selectedComponent instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration element = (XSDElementDeclaration)selectedComponent;
        if (element.isElementDeclarationReference())
        {
          return false;
        }
      }
      if (selectedComponent instanceof XSDTypeDefinition)
      {
        XSDTypeDefinition type = (XSDTypeDefinition)selectedComponent;
        XSDConcreteComponent parent = type.getContainer();
        if (parent instanceof XSDElementDeclaration)
        {
          XSDElementDeclaration element = (XSDElementDeclaration)parent;
          if (element.getAnonymousTypeDefinition().equals(type))
          {
            return false;
          }
        }
        else if (parent instanceof XSDAttributeDeclaration)
        {
          XSDAttributeDeclaration element = (XSDAttributeDeclaration)parent;
          if (element.getAnonymousTypeDefinition().equals(type))
          {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }
}