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


import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDRedefinableComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;


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
    XSDModelGroupDefinition redefinedModelGroupDefinition = XSDFactory.eINSTANCE.createXSDModelGroupDefinition();
    redefinedModelGroupDefinition.setName(redefinableComponent.getName());
    XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
    XSDParticle particle = factory.createXSDParticle();
    XSDModelGroup modelGroup = factory.createXSDModelGroup();
    modelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
    particle.setContent(modelGroup);
    redefinedModelGroupDefinition.setModelGroup(modelGroup);
    addedXSDConcreteComponent = redefinedModelGroupDefinition;
  }
}