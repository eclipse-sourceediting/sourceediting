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
import org.w3c.dom.Element;

/*
 * This command is used from the extension view to edit extension elements
 * and attributes which are implemented as DOM objects (not part of the EMF model)
 */
public class UpdateAttributeValueCommand  extends Command
{
  Element element;
  String attributeName;
  String attributeValue;
  
  public UpdateAttributeValueCommand(Element element, String attributeName, String attributeValue)
  {
    this.element = element;
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
  }

  
  public void execute()
  {
    element.setAttribute(attributeName, attributeValue);
  } 
}
