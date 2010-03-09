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
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;


public class MakeAnonymousTypsGlobalEnablementTester extends RefactorEnablementTester
{

  protected boolean canEnable(XSDConcreteComponent selectedObject, XSDSchema schema)
  {
    if (selectedObject == null)
    {
      return false;
    }

    XSDSchema selectedComponentSchema = null;
    boolean enable = false;
    selectedComponentSchema = selectedObject.getSchema();

    if (selectedComponentSchema != null && selectedComponentSchema == schema)
    {
      enable = true;
    }

    if (enable && selectedObject instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition typeDef = (XSDComplexTypeDefinition)selectedObject;
      XSDConcreteComponent parent = typeDef.getContainer();
      if (parent instanceof XSDElementDeclaration)
      {
        return true;
      }
    }
    else if (enable && selectedObject instanceof XSDSimpleTypeDefinition)
    {
      XSDSimpleTypeDefinition typeDef = (XSDSimpleTypeDefinition)selectedObject;
      XSDConcreteComponent parent = typeDef.getContainer();
      if (parent instanceof XSDElementDeclaration)
      {
        return true;
      }
      else if (parent instanceof XSDAttributeDeclaration)
      {
        return true;
      }
    }
    return false;
  }
}
