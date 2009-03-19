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


import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;


/**
 * Creates a redefined model group based on the input model group and adds it to the parent redefine.
 */
public class RedefineModelGroupCommand extends AddRedefinedComponentCommand
{

  public RedefineModelGroupCommand(String label, XSDRedefine redefine, XSDRedefinableComponent redefinableComponent)
  {
    super(label, redefine, redefinableComponent);
  }

  protected void doExecute()
  {
    XSDModelGroupDefinition modelGroup = (XSDModelGroupDefinition) redefinableComponent;
    XSDModelGroupDefinition redefinedModelGroupDefinition = (XSDModelGroupDefinition)modelGroup.cloneConcreteComponent(true, false);
    addedXSDConcreteComponent = redefinedModelGroupDefinition;
  }
}