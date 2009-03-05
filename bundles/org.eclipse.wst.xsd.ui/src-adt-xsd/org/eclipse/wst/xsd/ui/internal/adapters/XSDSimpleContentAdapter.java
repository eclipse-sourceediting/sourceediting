/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
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
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class XSDSimpleContentAdapter extends XSDBaseAdapter implements IActionProvider, IGraphElement
{

  public XSDSimpleContentAdapter()
  {
  }
  
  public XSDSimpleTypeDefinition getXSDSimpleTypeContent()
  {
    return (XSDSimpleTypeDefinition)target;
  }

  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    return (String [])list.toArray(new String[0]);
  }

  public Command getDeleteCommand()
  {
    return new DeleteCommand(getXSDSimpleTypeContent());
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDSimpleTypeContent().getSchema());
    return (IModel)adapter;
  }

  public IADTObject getTopContainer()
  {
    XSDConcreteComponent c = getXSDSimpleTypeContent().getContainer();
    if (c instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition ct = (XSDComplexTypeDefinition) c;
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(ct);
      if (adapter instanceof IADTObject)
        return (IADTObject)adapter;
    }
 
    return null; 
  }

  public boolean isFocusAllowed()
  {
    return false;
  }
  
  public Image getImage()
  {
    if (isReadOnly())
    {
      return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDSimpleContent.gif"); //$NON-NLS-1$
    }
    return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDSimpleContent.gif"); //$NON-NLS-1$
  }
  
  public String getText()
  {
    return "";
  }


}
