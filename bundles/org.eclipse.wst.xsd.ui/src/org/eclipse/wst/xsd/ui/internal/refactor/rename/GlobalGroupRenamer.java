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

import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;

public class GlobalGroupRenamer extends BaseRenamer
{

  /**
   * Constructor for GlobalGroupRenamer.
   * @param globalComponent
   */
  public GlobalGroupRenamer(XSDNamedComponent globalComponent, String newName)
  {
    super(globalComponent, newName);
  }

  /**
   * @see org.eclipse.wst.xsd.ui.internal.refactor.XSDVisitor#visitModelGroupDefinition(XSDModelGroupDefinition)
   */
  public void visitModelGroupDefinition(XSDModelGroupDefinition modelGroup)
  {
    super.visitModelGroupDefinition(modelGroup);
    if (modelGroup.isModelGroupDefinitionReference())
    {
      if (globalComponent.equals(modelGroup.getResolvedModelGroupDefinition()))
      {
        modelGroup.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, getNewQName());
      }
    }      
  }
}
