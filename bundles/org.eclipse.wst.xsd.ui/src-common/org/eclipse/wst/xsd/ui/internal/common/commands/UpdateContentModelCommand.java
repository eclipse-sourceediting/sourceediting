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

import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDModelGroup;

public class UpdateContentModelCommand extends BaseCommand
{
  XSDModelGroup xsdModelGroup;
  XSDCompositor oldXSDCompositor, newXSDCompositor;
  
  
  public UpdateContentModelCommand(String label, XSDModelGroup xsdModelGroup, XSDCompositor xsdCompositor)
  {
    super(label);
    this.xsdModelGroup = xsdModelGroup;
    this.newXSDCompositor = xsdCompositor;
    this.oldXSDCompositor = xsdModelGroup.getCompositor();
  }

  
  public void execute()
  {
    try
    {
      super.execute();
      beginRecording(xsdModelGroup.getElement());
      xsdModelGroup.setCompositor(newXSDCompositor);
    }
    finally
    {
      endRecording();
    }
  }
    
  public void undo()
  {
    xsdModelGroup.setCompositor(oldXSDCompositor);
  }
}
