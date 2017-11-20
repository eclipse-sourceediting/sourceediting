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
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;

public class XSDSchemaDirectiveAdapter extends XSDBaseAdapter implements IActionProvider, IGraphElement
{
  public Image getImage()
  {
    XSDSchemaDirective object = (XSDSchemaDirective) target;
    if (object instanceof XSDImport)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDImport.gif"); //$NON-NLS-1$
    }
    else if (object instanceof XSDInclude)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDInclude.gif"); //$NON-NLS-1$
    }
    else if (object instanceof XSDRedefine)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDRedefine.gif"); //$NON-NLS-1$
    }
    return null;
  }

  public String getText()
  {
    XSDSchemaDirective directive = (XSDSchemaDirective) target;
    String result = "";

    String location = directive.getSchemaLocation();
    if (location == null || location.equals("") )
    {
      result = "(" + Messages._UI_LABEL_NO_LOCATION_SPECIFIED + ")"; 
    }
    else
    {
      result = location;
    }  

    // only show the namespace when the directiave is an import
    // (otherwise the namespace is obviously the same as the containing schema's)
    if (directive instanceof XSDImport)
    {
      XSDImport importObj = (XSDImport) directive;
      String namespace = importObj.getNamespace();
      if (namespace != null)
      {  
        result += "  {" + namespace + "}";
      }      
    }
    return result;
  }

  public ITreeElement[] getChildren()
  {
    List list = new ArrayList();

    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);
  }
  
  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    list.add(OpenInNewEditor.ID);
    list.add(DeleteAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(ShowPropertiesViewAction.ID);
    
    return (String [])list.toArray(new String[0]);
  }

  public Command getDeleteCommand()
  {
    XSDSchemaDirective object = (XSDSchemaDirective) target;
    return new DeleteCommand(object);
  }

  public IModel getModel()
  {
    XSDSchema object = ((XSDSchemaDirective) target).getSchema();
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(object);
    return (IModel)adapter;
  }

  public IADTObject getTopContainer()
  {
    // There is currently no drill-down details view of directives
    // The top level container is the schema
    return getModel();
  }

  public boolean isFocusAllowed()
  {
    return false;
  }

}
