/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDRedefineContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;


public class XSDRedefineAdapter extends XSDSchemaDirectiveAdapter
{

  public XSDRedefineAdapter()
  {
    super();
  }

  public ITreeElement[] getChildren()
  {
    XSDRedefine xsdRedefine = (XSDRedefine)getTarget();

    children = new ArrayList();

    if (attributesCategory != null)
    {
      List attributes = getCategoryChildren(CategoryAdapter.ATTRIBUTES);
      List groups = getCategoryChildren(CategoryAdapter.GROUPS);
      List types = getCategoryChildren(CategoryAdapter.TYPES);

      attributesCategory.setChildren(attributes);
      attributesCategory.setAllChildren(attributes);
      typesCategory.setChildren(types);
      typesCategory.setAllChildren(types);
      groupsCategory.setChildren(groups);
      groupsCategory.setAllChildren(groups);
    }
    else
    {
      createCategoryAdapters(xsdRedefine);
    }

    children.add(attributesCategory);
    children.add(typesCategory);
    children.add(groupsCategory);

    return (ITreeElement[])children.toArray(new ITreeElement [0]);
  }

  public IADTObject getTopContainer()
  {
    return this;
  }

  public boolean isFocusAllowed()
  {
    return true;
  }

  protected List types = null;

  protected List children, allChildren;

  protected CategoryAdapter attributesCategory;

  protected CategoryAdapter typesCategory;

  protected CategoryAdapter groupsCategory;

  /**
   * Create all the category adapters
   * 
   * @param xsdRedefine the parent redefine component
   */
  private void createCategoryAdapters(XSDRedefine xsdRedefine)
  {
    List attributes = getCategoryChildren(CategoryAdapter.ATTRIBUTES);
    List groups = getCategoryChildren(CategoryAdapter.GROUPS);
    List types = getCategoryChildren(CategoryAdapter.TYPES);

    XSDEditorPlugin xsdEditorPlugin = XSDEditorPlugin.getDefault();
    attributesCategory = new RedefineCategoryAdapter(
      Messages._UI_GRAPH_REDEFINE_ATTRIBUTE_GROUPS,
      xsdEditorPlugin.getIconImage("obj16/attributesheader"), attributes, xsdRedefine, this, CategoryAdapter.ATTRIBUTES); //$NON-NLS-1$
    attributesCategory.setAllChildren(attributes);
    registerListener(attributesCategory);

    typesCategory = new RedefineCategoryAdapter(
      Messages._UI_GRAPH_REDEFINE_TYPES,
      xsdEditorPlugin.getIconImage("obj16/typesheader"), types, xsdRedefine, this, CategoryAdapter.TYPES); //$NON-NLS-1$
    typesCategory.setAllChildren(types);
    registerListener(typesCategory);

    groupsCategory = new RedefineCategoryAdapter(
      Messages._UI_GRAPH_REDEFINE_GROUPS,
      xsdEditorPlugin.getIconImage("obj16/groupsheader"), groups, xsdRedefine, this, CategoryAdapter.GROUPS); //$NON-NLS-1$
    groupsCategory.setAllChildren(groups);
    registerListener(groupsCategory);
  }

  public void notifyChanged(final Notification msg)
  {
    class CategoryNotification extends NotificationImpl
    {
      protected Object category;

      public CategoryNotification(Object category)
      {
        super(msg.getEventType(), msg.getOldValue(), msg.getNewValue(), msg.getPosition());
        this.category = category;
      }

      public Object getNotifier()
      {
        return category;
      }

      public Object getFeature()
      {
        return msg.getFeature();
      }
    }

    if (children == null)
    {
      getChildren();
    }

    Object newValue = msg.getNewValue();
    Object oldValue = msg.getOldValue();

    if (XSDPackage.eINSTANCE.getXSDRedefine_Contents() == msg.getFeature())
    {
      if ((newValue instanceof XSDAttributeGroupDefinition) || oldValue instanceof XSDAttributeGroupDefinition)
      {
        CategoryAdapter adapter = getCategory(CategoryAdapter.ATTRIBUTES);
        Assert.isTrue(adapter != null);
        List list = getCategoryChildren(CategoryAdapter.ATTRIBUTES);
        adapter.setChildren(list);
        adapter.setAllChildren(list);
       
        if (adapter.getModel() instanceof XSDSchemaAdapter)
        {
        	XSDSchemaAdapter schemaAdapter = (XSDSchemaAdapter)adapter.getModel();
        	schemaAdapter.notifyChanged(msg);
        }
       
        notifyListeners(new CategoryNotification(adapter), adapter.getText());
        return;
      }
      else if ((newValue instanceof XSDComplexTypeDefinition || newValue instanceof XSDSimpleTypeDefinition) || (oldValue instanceof XSDComplexTypeDefinition || oldValue instanceof XSDSimpleTypeDefinition))
      {
        CategoryAdapter adapter = getCategory(CategoryAdapter.TYPES);
        Assert.isTrue(adapter != null);
        List types = getCategoryChildren(CategoryAdapter.TYPES);
        adapter.setChildren(types);
        adapter.setAllChildren(types);
        if (adapter.getModel() instanceof XSDSchemaAdapter)
        {
        	XSDSchemaAdapter schemaAdapter = (XSDSchemaAdapter)adapter.getModel();
        	schemaAdapter.notifyChanged(msg);
        }
        notifyListeners(new CategoryNotification(adapter), adapter.getText());
        return;
      }
      else if (newValue instanceof XSDModelGroupDefinition || oldValue instanceof XSDModelGroupDefinition)
      {
        CategoryAdapter adapter = getCategory(CategoryAdapter.GROUPS);
        Assert.isTrue(adapter != null);
        List list = getCategoryChildren(CategoryAdapter.GROUPS);
        adapter.setChildren(list);
        adapter.setAllChildren(list); 
        if (adapter.getModel() instanceof XSDSchemaAdapter)
        {
        	XSDSchemaAdapter schemaAdapter = (XSDSchemaAdapter)adapter.getModel();
        	schemaAdapter.notifyChanged(msg);
        }
        notifyListeners(new CategoryNotification(adapter), adapter.getText());       
        return;
      }
      else if (msg.getFeature() == XSDPackage.eINSTANCE.getXSDSchema_Annotations())
      {
        return;
      }
    }
    super.notifyChanged(msg);
  }

  private void updateCategories()
  {
    getChildren();
  }

  public CategoryAdapter getCategory(int category)
  {
    if (children == null)
    {
      updateCategories();
    }
    int length = children.size();
    CategoryAdapter adapter = null;
    for (int index = 0; index < length; index++)
    {
      adapter = (CategoryAdapter)children.get(index);
      if (adapter.getGroupType() == category)
      {
        break;
      }
    }
    return adapter;
  }

  public String[] getActions(Object object)
  {
    Collection actionIDs = new ArrayList();

    actionIDs.add(DeleteAction.ID);
    actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
    actionIDs.add(SetInputToGraphView.ID);
    actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
    actionIDs.add(ShowPropertiesViewAction.ID);

    return (String[])actionIDs.toArray(new String [0]);
  }

  public void propertyChanged(Object object, String property)
  {
    notifyListeners(object, property);
  }

  public Image getImage()
  {
    return XSDEditorPlugin.getXSDImage("icons/XSDRedefine.gif"); //$NON-NLS-1$
  }

  private List getCategoryChildren(int category)
  {
    List list = new ArrayList();
    XSDRedefine redefine = (XSDRedefine)target;
    Iterator iterator = redefine.getContents().iterator();
    while (iterator.hasNext())
    {
      XSDRedefineContent redefineContent = (XSDRedefineContent)iterator.next();

      if (redefineContent instanceof XSDAttributeGroupDefinition && category == CategoryAdapter.ATTRIBUTES)
      {
        list.add(redefineContent);
      }
      else if (redefineContent instanceof XSDModelGroupDefinition && category == CategoryAdapter.GROUPS)
      {
        list.add(redefineContent);
      }
      else if (redefineContent instanceof XSDComplexTypeDefinition && category == CategoryAdapter.TYPES)
      {
        list.add(redefineContent);
      }
      else if (redefineContent instanceof XSDSimpleTypeDefinition && category == CategoryAdapter.TYPES)
      {
        list.add(redefineContent);
      }
    }
    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return adapterList;
  }
}