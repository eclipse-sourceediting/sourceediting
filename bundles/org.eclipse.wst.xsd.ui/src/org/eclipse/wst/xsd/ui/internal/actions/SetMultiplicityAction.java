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

import org.eclipse.wst.xsd.ui.internal.commands.SetMultiplicityCommand;
import org.eclipse.xsd.XSDConcreteComponent;

public class SetMultiplicityAction extends AbstractAction
{
  SetMultiplicityCommand command;
  
  public SetMultiplicityAction(XSDConcreteComponent parent, String multiplicity)
  {
    super(multiplicity, parent);
    command = new SetMultiplicityCommand(parent);
  }
  
  public void setMaxOccurs(int i)
  {
    command.setMaxOccurs(i);
  }

  public void setMinOccurs(int i)
  {
    command.setMinOccurs(i);
  }

  public void run()
  {
    beginRecording(getText());
    command.run();
    endRecording();
  }
}
