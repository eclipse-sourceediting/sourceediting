/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor;


import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;


public class MakeLocalElementGlobalEnablementTester extends RefactorEnablementTester
{
  protected boolean canEnable(XSDConcreteComponent selectedObject, XSDSchema schema)
  {
    if (selectedObject == null)
    {
      return false;
    }
    
    boolean enable = false;
    XSDSchema selectedComponentSchema = null;
    selectedComponentSchema = selectedObject.getSchema();
    if (selectedComponentSchema != null && selectedComponentSchema == schema)
    {
      enable = true;
    }

    if (enable && selectedObject instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration element = (XSDElementDeclaration)selectedObject;
      if (!element.isElementDeclarationReference() && !element.isGlobal())
      {
        return true;
      }
    }

    return false;
  }
}
