/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.actions;

import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDComplexTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddEnumerationsCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

public class AddXSDEnumerationFacetAction extends XSDBaseAction
{
  public static String ID = "org.eclipse.wst.xsd.ui.AddXSDEnumerationFacetAction"; //$NON-NLS-1$
  public static String BEFORE_SELECTED_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDEnumerationFacetAction.BEFORE_SELECTED_ID"; //$NON-NLS-1$
  public static String AFTER_SELECTED_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDEnumerationFacetAction.AFTER_SELECTED_ID"; //$NON-NLS-1$
  
  public AddXSDEnumerationFacetAction(IWorkbenchPart part, String id, String label)
  {
    super(part);
    setText(label);
    setId(id);
  }
  
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
      int index = -1;
      AddEnumerationsCommand command = null;
      XSDSimpleTypeDefinition st = null;
      if (selection instanceof XSDSimpleTypeDefinition)
      {
        st = (XSDSimpleTypeDefinition)selection;
        command = new AddEnumerationsCommand(getText(), st);
      }
      else if (selection instanceof XSDEnumerationFacet)
      {
        st = ((XSDEnumerationFacet)selection).getSimpleTypeDefinition();
        index = st.getFacetContents().indexOf(selection);
        doDirectEdit = true;
        command = new AddEnumerationsCommand(getText(), st, getId(), index);
      }
      else if (selection instanceof XSDComplexTypeDefinition)  // Support for Complex Type's simple Content with enumerations
      {
    	st = (XSDSimpleTypeDefinition) ((XSDComplexTypeDefinition)selection).getContent();
        command = new AddEnumerationsCommand(getText(), st);
      }
      else // null
      {
        return;
      }
      
      List enumList = st.getEnumerationFacets();
      
      String newName = XSDCommonUIUtils.createUniqueEnumerationValue("value", enumList); //$NON-NLS-1$

      command.setValue(newName);
      getCommandStack().execute(command);
      addedComponent = command.getAddedComponent();
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(addedComponent);
      selectAddedComponent(adapter);
    }
  }
  
  protected boolean calculateEnabled() {
		
		boolean parentResult = super.calculateEnabled();
		boolean endResult = true;
		Object selection = ((IStructuredSelection) getSelection()).getFirstElement();
		if (selection instanceof XSDComplexTypeDefinitionAdapter)
		{
			XSDComplexTypeDefinition definition = ((XSDComplexTypeDefinitionAdapter) selection).getXSDComplexTypeDefinition();
			XSDTypeDefinition baseType = definition.getBaseType();
			if (baseType instanceof XSDSimpleTypeDefinition)
				endResult = false;
		}
		endResult = endResult & parentResult;
		return endResult;
	}
}
