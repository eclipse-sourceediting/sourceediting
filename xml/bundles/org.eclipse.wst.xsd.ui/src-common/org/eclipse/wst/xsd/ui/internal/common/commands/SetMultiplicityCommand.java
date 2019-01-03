/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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

import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;

public class SetMultiplicityCommand extends BaseCommand
{
  XSDConcreteComponent parent;
  int maxOccurs, minOccurs;

  /**
   * @param parent
   */
  public SetMultiplicityCommand(String label)
  {
    super(label);
  }
  
  public void setMaxOccurs(int i)
  {
    maxOccurs=i;
  }

  public void setMinOccurs(int i)
  {
    minOccurs=i;    
  }

  public void setXSDConcreteComponent(XSDConcreteComponent parent)
  {
    this.parent = parent;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#run()
   */
  public void execute()
  {
    try
    {
      beginRecording(parent.getElement());
      if (parent instanceof XSDParticleContent)
      {
        XSDParticleContent xsdParticleContent = (XSDParticleContent) parent;
        XSDParticle xsdParticle = (XSDParticle) xsdParticleContent.getContainer();
        if (maxOccurs < 0)
        {
          maxOccurs = XSDParticle.UNBOUNDED;
        }
        xsdParticle.setMaxOccurs(maxOccurs);
        xsdParticle.setMinOccurs(minOccurs);
      }
    }
    finally
    {
      endRecording();
    }
  }
}
