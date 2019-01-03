/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
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
package org.eclipse.wst.xsd.ui.internal.common.commands;


import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;


/**
 * Creates a redefined complex type based on the input complex type and adds it to the parent redefine.
 */
public class RedefineComplexTypeCommand extends AddRedefinedComponentCommand
{
  public RedefineComplexTypeCommand(String label, XSDRedefine redefine, XSDRedefinableComponent redefinableComponent)
  {
    super(label, redefine, redefinableComponent);
  }

  protected void doExecute()
  {
    XSDComplexTypeDefinition redefinedComplexTypeDefinition = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
    redefinedComplexTypeDefinition.setName(redefinableComponent.getName());
    redefinedComplexTypeDefinition.setContent(XSDFactory.eINSTANCE.createXSDComplexTypeDefinition().getContent());
    redefinedComplexTypeDefinition.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
    redefinedComplexTypeDefinition.setBaseTypeDefinition((XSDComplexTypeDefinition)redefinableComponent);
    addedXSDConcreteComponent = redefinedComplexTypeDefinition;
  }
}