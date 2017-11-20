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
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

public class XSDAttributeDeclarationAdapter extends XSDBaseAttributeAdapter implements IActionProvider
{
  protected XSDAttributeDeclaration getXSDAttributeDeclaration()
  {
    return (XSDAttributeDeclaration)target;
  }
 
  protected XSDAttributeDeclaration getResolvedXSDAttributeDeclaration()
  {
    return getXSDAttributeDeclaration().getResolvedAttributeDeclaration();
  }
  
  public boolean isGlobal()
  {
    return getXSDAttributeDeclaration().eContainer() instanceof XSDSchema;
  }
  
  public boolean isReference()
  {
    return getXSDAttributeDeclaration().isAttributeDeclarationReference();
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDAttributeDeclaration().getSchema());
    return (IModel)adapter;
  }

  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=251008
  public String[] getActions(Object object)
  {    
    if(!isGlobal())
    {    	    	
    	List defaultActions = new ArrayList();
    	defaultActions = Arrays.asList(super.getActions(object));
    	
    	ArrayList list = new ArrayList();
    	list.add(BaseSelectionAction.SUBMENU_START_ID + Messages._UI_ACTION_INSERT_ATTRIBUTE);
    	list.add(AddXSDAttributeDeclarationAction.BEFORE_SELECTED_ID);
    	list.add(AddXSDAttributeDeclarationAction.AFTER_SELECTED_ID);
    	list.add(BaseSelectionAction.SUBMENU_END_ID);
    	
    	int len = defaultActions.size();
    	for(int i = 0; i < len; i++)
    	{
    		if (defaultActions.get(i).equals(AddXSDAttributeDeclarationAction.ID))
    		{
    		  continue;
    		}
    		list.add(defaultActions.get(i));
    	}
      return (String [])list.toArray(new String[0]);
    }
    else
    {
    	return super.getActions(object);
    }    
  }  
  
  
  public boolean isFocusAllowed()
  {
    return isGlobal();
  }
  
  public String getTypeNameQualifier()
  {
    XSDTypeDefinition type = getResolvedXSDAttributeDeclaration().getTypeDefinition();
    if (type != null)
    {
      return type.getTargetNamespace();
    }
    return "";
  }

  public IADTObject getTopContainer()
  {
    if (!isGlobal())
    {
      return getGlobalXSDContainer(getXSDAttributeDeclaration());
    }
    return this;
  }
}
