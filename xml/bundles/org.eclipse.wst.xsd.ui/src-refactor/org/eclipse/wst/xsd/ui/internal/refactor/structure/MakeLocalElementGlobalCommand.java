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
package org.eclipse.wst.xsd.ui.internal.refactor.structure;


import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDSchema;


public final class MakeLocalElementGlobalCommand extends AbstractCommand
{
  public MakeLocalElementGlobalCommand(XSDConcreteComponent element)
  {
    super(element.getContainer());
    setModelObject(element);
  }

  public void run()
  {
    if (getModelObject() instanceof XSDElementDeclaration)
    {

      XSDElementDeclaration localElementDeclaration = (XSDElementDeclaration)getModelObject();
      XSDConcreteComponent parent = getParent();
      XSDConcreteComponent container = parent.getContainer();

      // Clone the local element with its content and set it global

      XSDElementDeclaration globalElementDeclaration = (XSDElementDeclaration)localElementDeclaration.cloneConcreteComponent(true, false);

      // The schema may already have a global element declaration with this name. In that case, ensure the name of the newly created
      // element does not collide with an existing one.

      XSDSchema schema = container.getSchema();
      String localElementName = localElementDeclaration.getName();
      XSDElementDeclaration existingGlobalElement = schema.resolveElementDeclaration(localElementName);
      boolean elementExists = existingGlobalElement != null && existingGlobalElement.getSchema() != null;
      if (elementExists)
      {
        String newElementName = XSDCommonUIUtils.createUniqueElementName(localElementName, schema.getElementDeclarations());
        globalElementDeclaration.setName(newElementName);
      }

      EList schemaContents = schema.getContents();
      schemaContents.add(globalElementDeclaration);

      // Modify the local element to become a reference to the global element.

      localElementDeclaration.setName(null);
      localElementDeclaration.setTypeDefinition(null);
      localElementDeclaration.setAnonymousTypeDefinition(null);
      localElementDeclaration.setResolvedElementDeclaration(globalElementDeclaration);
      XSDModelGroup modelGroup = (XSDModelGroup)container;

      // Format the markup. 

      formatChild(modelGroup.getElement());
      formatChild(globalElementDeclaration.getElement());
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#adopt(org.eclipse.xsd.XSDConcreteComponent)
   */
  protected boolean adopt(XSDConcreteComponent model)
  {
    return true;
  }
}