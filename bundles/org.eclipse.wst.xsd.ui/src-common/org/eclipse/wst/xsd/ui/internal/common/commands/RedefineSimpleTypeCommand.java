/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.commands;


import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSimpleTypeDefinition;


/**
 * Creates a redefined simple type based on the input simple type and adds it to the parent redefine.
 */
public class RedefineSimpleTypeCommand extends AddRedefinedComponentCommand
{
  public RedefineSimpleTypeCommand(String label, XSDRedefine redefine, XSDRedefinableComponent redefinableComponent)
  {
    super(label, redefine, redefinableComponent);
  }

  protected void doExecute()
  {
    XSDSimpleTypeDefinition redefinedSimpleTypeDefinition = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
    redefinedSimpleTypeDefinition.setName(redefinableComponent.getName());
    redefinedSimpleTypeDefinition.setBaseTypeDefinition((XSDSimpleTypeDefinition)redefinableComponent);
    addedXSDConcreteComponent = redefinedSimpleTypeDefinition;
  }
}