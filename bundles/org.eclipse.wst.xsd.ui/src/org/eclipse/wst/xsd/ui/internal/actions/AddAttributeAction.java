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
package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.xsd.ui.internal.commands.AddAttributeDeclarationCommand;
import org.eclipse.xsd.XSDConcreteComponent;

public class AddAttributeAction extends AbstractAction
{
  protected AddAttributeDeclarationCommand command;
  
  public AddAttributeAction(String text, XSDConcreteComponent parent)
  {
    super(text, parent);
    command = new AddAttributeDeclarationCommand(parent);
  }

  public AddAttributeAction(String text, ImageDescriptor image, XSDConcreteComponent parent)
  {
    super(text, image, parent);
    command = new AddAttributeDeclarationCommand(parent);
  }

  public void run()
  {
    beginRecording(getText());
    command.run();
    
    endRecording();
  }
}
