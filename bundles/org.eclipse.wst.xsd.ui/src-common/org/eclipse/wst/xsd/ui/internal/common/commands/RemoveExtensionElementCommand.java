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

import org.eclipse.gef.commands.Command;
import org.eclipse.xsd.XSDAnnotation;
import org.w3c.dom.Node;

/**
 * @deprecated
 */
public class RemoveExtensionElementCommand extends Command
{
  XSDAnnotation xsdAnnotation;
  Node appInfo;

  public RemoveExtensionElementCommand(String label, XSDAnnotation xsdAnnotation, Node appInfo)
  {
    super(label);
    this.xsdAnnotation = xsdAnnotation;
    this.appInfo = appInfo;
  }

  public void execute()
  {
    super.execute();
    xsdAnnotation.getApplicationInformation().remove(appInfo);
    xsdAnnotation.getElement().removeChild(appInfo);
    xsdAnnotation.updateElement();
  }

  public void undo()
  {
    super.undo();
    xsdAnnotation.getApplicationInformation().add(appInfo);
    xsdAnnotation.getElement().appendChild(appInfo);
  }
}
