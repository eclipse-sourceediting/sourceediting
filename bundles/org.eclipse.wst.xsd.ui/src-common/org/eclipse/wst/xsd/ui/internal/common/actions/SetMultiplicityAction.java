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
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.SetMultiplicityCommand;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;

public class SetMultiplicityAction extends XSDBaseAction
{
  public static String REQUIRED_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicity.REQUIRED_ID"; //$NON-NLS-1$
  public static String ZERO_OR_ONE_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicity.ZERO_OR_ONE_ID"; //$NON-NLS-1$
  public static String ZERO_OR_MORE_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicity.ZERO_OR_MORE_ID"; //$NON-NLS-1$
  public static String ONE_OR_MORE_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicity.ONE_OR_MORE_ID"; //$NON-NLS-1$
  
  SetMultiplicityCommand command;
  
  public SetMultiplicityAction(IWorkbenchPart part, String label, String ID)
  {
    super(part);
    setText(label);
    setId(ID);
    command = new SetMultiplicityCommand(label);
  }
  
  public void setMaxOccurs(int i)
  {
    command.setMaxOccurs(i);
  }

  public void setMinOccurs(int i)
  {
    command.setMinOccurs(i);
  }
  
  protected boolean calculateEnabled()
  {
    boolean state = super.calculateEnabled();
    if (state)
    {
      XSDConcreteComponent xsdConcreteComponent = getXSDInput();
      if (xsdConcreteComponent instanceof XSDElementDeclaration)
      {
        return !((XSDElementDeclaration)xsdConcreteComponent).isGlobal();
      }
      else if (xsdConcreteComponent instanceof XSDModelGroup)
      {
        return !(((XSDModelGroup)xsdConcreteComponent).eContainer() instanceof XSDModelGroupDefinition);
      }
    }
    return state;
  }
  
  private XSDConcreteComponent getXSDInput()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    XSDConcreteComponent xsdConcreteComponent = null;
    if (selection instanceof XSDBaseAdapter)
    {
      xsdConcreteComponent = (XSDConcreteComponent)((XSDBaseAdapter) selection).getTarget();
    }
    return xsdConcreteComponent;
  }

  public void run()
  {
    XSDConcreteComponent xsdConcreteComponent = getXSDInput();
    if (xsdConcreteComponent != null)
    {
      command.setXSDConcreteComponent(xsdConcreteComponent);
      getCommandStack().execute(command);
    }
  }
}
