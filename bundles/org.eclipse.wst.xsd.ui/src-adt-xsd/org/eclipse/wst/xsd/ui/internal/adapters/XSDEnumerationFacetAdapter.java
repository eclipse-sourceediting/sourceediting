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
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDEnumerationFacetAction;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDEnumerationFacet;

public class XSDEnumerationFacetAdapter extends XSDBaseAdapter implements IActionProvider, IGraphElement
{
  public XSDEnumerationFacet getXSDEnumerationFacet()
  {
    return (XSDEnumerationFacet)target;
  }
  
  public XSDEnumerationFacetAdapter()
  {
    super();
  }

  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    
    list.add(BaseSelectionAction.SUBMENU_START_ID + Messages._UI_ACTION_INSERT_ENUMERATION);
    list.add(AddXSDEnumerationFacetAction.BEFORE_SELECTED_ID);
    list.add(AddXSDEnumerationFacetAction.AFTER_SELECTED_ID);
    list.add(BaseSelectionAction.SUBMENU_END_ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(DeleteAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(ShowPropertiesViewAction.ID);
    return (String [])list.toArray(new String[0]);
  }
  
  public Image getImage()
  {
    return XSDEditorPlugin.getXSDImage("icons/XSDSimpleEnum.gif"); //$NON-NLS-1$
  }

  public Command getDeleteCommand()
  {
    return new DeleteCommand(getXSDEnumerationFacet());
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDEnumerationFacet().getSchema());
    return (IModel)adapter;
  }

  public IADTObject getTopContainer()
  {
    return getGlobalXSDContainer(getXSDEnumerationFacet());
  }

  public boolean isFocusAllowed()
  {
    return false;
  }
  
  public String getText()
  {
    return getXSDEnumerationFacet().getLexicalValue();
  }

}
