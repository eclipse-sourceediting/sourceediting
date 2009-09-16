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
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class UpdateMaxOccursCommand extends BaseCommand
{
	private int oldMaxOccurs;
	private int newMaxOccurs;
  private boolean removeMaxOccursAttribute;
	
	XSDParticle particle;

  public UpdateMaxOccursCommand(String label, XSDParticle particle, int MaxOccurs)
	{
		super(label);
		this.newMaxOccurs = MaxOccurs;
		this.particle = particle;
	}
	
	public void execute()
	{
    try
    {
      Element element = particle.getElement();
      beginRecording(element);
      removeMaxOccursAttribute = (!(element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE)))? true: false;
		  oldMaxOccurs = particle.getMaxOccurs();
		  particle.setMaxOccurs(newMaxOccurs);
    }
    finally
    {
      endRecording();
    }
	}
	
	public void redo()
	{
		execute();
	}
	
	public void undo()
	{
    if (removeMaxOccursAttribute)
    {
      particle.unsetMaxOccurs();
    }
    else
    {
		  particle.setMaxOccurs(oldMaxOccurs);
    }
	}
}
