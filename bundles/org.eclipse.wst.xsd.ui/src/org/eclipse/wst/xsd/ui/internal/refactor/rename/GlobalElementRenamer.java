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
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalElementRenamer extends BaseRenamer
{
  /**
   * Constructor for GlobalElementRenamer.
   * @param globalComponent
   */
  public GlobalElementRenamer(XSDNamedComponent globalComponent, String newName)
  {
    super(globalComponent, newName);
  }
  
  /**
   * @see org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor#visitElementDeclaration(XSDElementDeclaration)
   */
  public void visitElementDeclaration(XSDElementDeclaration element)
  {
    super.visitElementDeclaration(element);
    if (element.isElementDeclarationReference() &&
        globalComponent.equals(element.getResolvedElementDeclaration()))
    {
      element.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getNewQName());
    }
  }

}
