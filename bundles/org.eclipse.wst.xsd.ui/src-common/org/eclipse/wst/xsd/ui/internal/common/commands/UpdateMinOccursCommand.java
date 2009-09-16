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

import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class UpdateMinOccursCommand extends BaseCommand
{
  private int oldMinOccurs;
  private int newMinOccurs;
  private boolean removeMinOccursAttribute;

  XSDComponent component;

  public UpdateMinOccursCommand(String label, XSDComponent component, int minOccurs)
  {
    super(label);
    this.newMinOccurs = minOccurs;
    this.component = component;
  }

  public void execute()
  {
    Element element = component.getElement();
    try
    {
      beginRecording(element);
      removeMinOccursAttribute = (!(element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE))) ? true : false;

      if (component instanceof XSDParticle)
      {
        oldMinOccurs = ((XSDParticle) component).getMinOccurs();
        ((XSDParticle) component).setMinOccurs(newMinOccurs);
      }
      else if (component instanceof XSDAttributeUse)
      {
        oldMinOccurs = (((XSDAttributeUse) component).getUse() == XSDAttributeUseCategory.REQUIRED_LITERAL ? 1 : 0);
        if (newMinOccurs == 1)
          ((XSDAttributeUse) component).setUse(XSDAttributeUseCategory.REQUIRED_LITERAL);
        else
          ((XSDAttributeUse) component).setUse(XSDAttributeUseCategory.OPTIONAL_LITERAL);
      }
    }
    finally
    {
      endRecording();
    }
  }

  public void undo()
  {
    if (component instanceof XSDParticle)
    {
      if (removeMinOccursAttribute)
      {
        ((XSDParticle) component).unsetMinOccurs();
      }
      else
      {
        ((XSDParticle) component).setMinOccurs(oldMinOccurs);
      }
    }
    else if (component instanceof XSDAttributeUse)
    {
      if (removeMinOccursAttribute)
      {
        ((XSDParticle) component).unsetMinOccurs();
      }
      else
      {
        if (oldMinOccurs == 1)
          ((XSDAttributeUse) component).setUse(XSDAttributeUseCategory.REQUIRED_LITERAL);
        else
          ((XSDAttributeUse) component).setUse(XSDAttributeUseCategory.OPTIONAL_LITERAL);
      }
    }
  }
}
