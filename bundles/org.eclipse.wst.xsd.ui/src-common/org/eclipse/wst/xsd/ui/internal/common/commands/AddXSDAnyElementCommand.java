/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDWildcard;

public class AddXSDAnyElementCommand extends BaseCommand
{
  XSDModelGroup parent;

  public AddXSDAnyElementCommand(String label, XSDModelGroup parent)
  {
    super(label);
    this.parent = parent;
  }

  public void execute()
  {
    XSDWildcard wildCard = XSDFactory.eINSTANCE.createXSDWildcard();
    XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
    particle.setContent(wildCard);
    parent.getContents().add(particle);
    addedXSDConcreteComponent = wildCard;
    formatChild(parent.getElement());
  }
}
