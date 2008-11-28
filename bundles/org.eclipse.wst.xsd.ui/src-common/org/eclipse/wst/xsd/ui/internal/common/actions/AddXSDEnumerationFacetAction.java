/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddEnumerationsCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class AddXSDEnumerationFacetAction extends XSDBaseAction
{
  public static String ID = "org.eclipse.wst.xsd.ui.AddXSDEnumerationFacetAction"; //$NON-NLS-1$
  
  public AddXSDEnumerationFacetAction(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_ADD_ENUMERATION);
    setId(ID);
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();

      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)selection;
      List enumList = st.getEnumerationFacets();
      StringBuffer newName = new StringBuffer("value1"); //$NON-NLS-1$
      int suffix = 1;
      for (Iterator i = enumList.iterator(); i.hasNext();)
      {
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) i.next();
        String value = enumFacet.getLexicalValue();
        if (value != null)
        {
          if (value.equals(newName.toString()))
          {
            suffix++;
            newName = new StringBuffer("value" + String.valueOf(suffix)); //$NON-NLS-1$
          }
        }
      }
      
      AddEnumerationsCommand command = new AddEnumerationsCommand(Messages._UI_ACTION_ADD_ENUMERATION, st);
      command.setValue(newName.toString());
      getCommandStack().execute(command);
      addedComponent = command.getAddedComponent();
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
      selectAddedComponent(adapter);
    }
  }
  
}
