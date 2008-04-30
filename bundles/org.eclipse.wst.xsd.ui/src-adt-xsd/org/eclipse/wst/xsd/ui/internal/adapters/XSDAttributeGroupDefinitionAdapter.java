/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAnyAttributeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.DeleteXSDConcreteComponentAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDWildcard;

public class XSDAttributeGroupDefinitionAdapter extends XSDBaseAdapter implements IStructure, IActionProvider, IGraphElement, IADTObjectListener
{
  public static final Image ATTRIBUTE_GROUP_REF_ICON_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAttributeGroupRef.gif");
  public static final Image ATTRIBUTE_GROUP_REF_DISABLED_ICON_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAttributeGroupRefdis.gif");
  public static final Image ATTRIBUTE_GROUP_ICON_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAttributeGroup.gif");
  public static final Image ATTRIBUTE_GROUP_DISABLED_ICON_IMAGE = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAttributeGroupdis.gif");
	  
  protected List fields = null;
  protected List otherThingsToListenTo = null;

  public XSDAttributeGroupDefinitionAdapter()
  {
    super();
  }

  public XSDAttributeGroupDefinition getXSDAttributeGroupDefinition()
  {
    return (XSDAttributeGroupDefinition) target;
  }

  public Image getImage()
  {
    XSDAttributeGroupDefinition xsdAttributeGroupDefinition = (XSDAttributeGroupDefinition) target;
    if (xsdAttributeGroupDefinition.isAttributeGroupDefinitionReference())
    {
      return isReadOnly() ? ATTRIBUTE_GROUP_REF_DISABLED_ICON_IMAGE : ATTRIBUTE_GROUP_REF_ICON_IMAGE;
    }
    else
    {
      return isReadOnly() ? ATTRIBUTE_GROUP_DISABLED_ICON_IMAGE : ATTRIBUTE_GROUP_ICON_IMAGE;
    }
  }

  public String getText()
  {
    XSDAttributeGroupDefinition xsdAttributeGroupDefinition = (XSDAttributeGroupDefinition) target;
    String result = xsdAttributeGroupDefinition.isAttributeGroupDefinitionReference() ? xsdAttributeGroupDefinition.getQName() : xsdAttributeGroupDefinition.getName();
    return result == null ? Messages._UI_LABEL_ABSENT : result;
  }

  public ITreeElement[] getChildren()
  {
    XSDAttributeGroupDefinition xsdAttributeGroup = (XSDAttributeGroupDefinition) target;
    List list = new ArrayList();
    list.addAll(xsdAttributeGroup.getContents());
    XSDWildcard wildcard = xsdAttributeGroup.getAttributeWildcardContent();
    if (wildcard != null)
    {
      list.add(wildcard);
    }
    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);
  }
  
  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    list.add(AddXSDAttributeDeclarationAction.ID);
    list.add(AddXSDAnyAttributeAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(DeleteXSDConcreteComponentAction.DELETE_XSD_COMPONENT_ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    Object schema = getEditorSchema();
    if (getXSDAttributeGroupDefinition().getSchema() == schema)
    {
      if (getXSDAttributeGroupDefinition().getContainer() == schema)
      {
        list.add(SetInputToGraphView.ID);
      }
    }
    else
    {
      list.add(OpenInNewEditor.ID);
    }
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(ShowPropertiesViewAction.ID);
    return (String [])list.toArray(new String[0]);
  }

  public Command getAddNewFieldCommand(String fieldKind)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getDeleteCommand()
  {
    return new DeleteCommand("", getXSDAttributeGroupDefinition()); //$NON-NLS-1$
  }

  public List getFields()
  {
    if (fields == null)
    {
      fields = new ArrayList();
      otherThingsToListenTo = new ArrayList();
      XSDVisitorForFields visitor = new XSDVisitorForFields();
      visitor.visitAttributeGroupDefinition(getXSDAttributeGroupDefinition());
      populateAdapterList(visitor.concreteComponentList, fields);
      populateAdapterList(visitor.thingsWeNeedToListenTo, otherThingsToListenTo);
      for (Iterator i = otherThingsToListenTo.iterator(); i.hasNext();)
      {
        Adapter adapter = (Adapter) i.next();
        if (adapter instanceof IADTObject)
        {
          IADTObject adtObject = (IADTObject) adapter;
          adtObject.registerListener(this);
        }
      }
    }
    return fields;
  }

  protected void clearFields()
  {
    if (otherThingsToListenTo != null)
    {
      for (Iterator i = otherThingsToListenTo.iterator(); i.hasNext();)
      {
        Adapter adapter = (Adapter) i.next();
        if (adapter instanceof IADTObject)
        {
          IADTObject adtObject = (IADTObject) adapter;
          adtObject.unregisterListener(this);
        }
      }
    }
    fields = null;
    otherThingsToListenTo = null;
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDAttributeGroupDefinition().getSchema());
    return (IModel)adapter;
  }

  public String getName()
  {
    // TODO (cs) ... review this
    return getText();
  }

  public boolean isFocusAllowed()
  {
    XSDAttributeGroupDefinition xsdAttributeGroupDefinition = (XSDAttributeGroupDefinition) target;
    if (xsdAttributeGroupDefinition.isAttributeGroupDefinitionReference())
    {
      return false;
    }
    return true;
  }

  public IADTObject getTopContainer()
  {
    return getGlobalXSDContainer(getXSDAttributeGroupDefinition());
  }

  public void notifyChanged(Notification msg)
  {
    clearFields();
    super.notifyChanged(msg);
  }
  
  public void propertyChanged(Object object, String property)
  {
    clearFields();
    notifyListeners(this, null);
  }
  

}
