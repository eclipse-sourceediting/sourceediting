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
package org.eclipse.wst.xsd.ui.internal.commands;

import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;

public class SetMultiplicityCommand extends AbstractCommand
{
  int maxOccurs, minOccurs;

  /**
   * @param parent
   */
  public SetMultiplicityCommand(XSDConcreteComponent parent)
  {
    super(parent);
  }
  
  public void setMaxOccurs(int i)
  {
    maxOccurs=i;
  }

  public void setMinOccurs(int i)
  {
    minOccurs=i;    
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#run()
   */
  public void run()
  {
    XSDConcreteComponent parent = getParent();
    XSDConcreteComponent owner = null;
    if (parent instanceof XSDParticleContent)
    {
      XSDParticleContent xsdParticleContent = (XSDParticleContent)parent;
      XSDParticle xsdParticle = (XSDParticle)xsdParticleContent.getContainer();
      if (maxOccurs < 0)
      {
        maxOccurs = XSDParticle.UNBOUNDED;
      }
      xsdParticle.setMaxOccurs(maxOccurs);
      xsdParticle.setMinOccurs(minOccurs);
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.commands.AbstractCommand#adopt(org.eclipse.xsd.XSDConcreteComponent)
   */
  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }

}
