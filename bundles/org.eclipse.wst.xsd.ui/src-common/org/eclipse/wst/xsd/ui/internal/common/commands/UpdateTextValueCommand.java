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

import org.eclipse.wst.xml.ui.internal.tabletree.TreeContentHelper;
import org.w3c.dom.Element;

/*
 * This command is used from the extension view to edit extension elements
 * and which are implemented as DOM objects (not part of the EMF model)
 */
public class UpdateTextValueCommand  extends BaseCommand
{
  Element element;
  String value;
  
  public UpdateTextValueCommand(Element element, String value)
  {
    this.element = element;
    this.value = value;
  }

  
  public void execute()
  {
    try
    {
      beginRecording(element);
      new TreeContentHelper().setElementTextValue(element, value);
    }
    finally
    {
      endRecording();
    }
  } 
}
