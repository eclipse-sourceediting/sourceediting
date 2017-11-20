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
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IModelProxy;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDComplexTypeDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDSchemaDirectiveAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDSimpleTypeDefinitionAction;
import org.eclipse.xsd.XSDSchema;

public class CategoryAdapter extends XSDBaseAdapter implements IModelProxy, IActionProvider, IADTObjectListener
{
  protected String text;
  protected Image image;
  protected Object parent;
  protected int groupType;
  protected Collection children, allChildren;  // children from current schema, children from current schema and includes
  XSDSchema xsdSchema;

  public CategoryAdapter(String label, Image image, Collection children, XSDSchema xsdSchema, int groupType)
  {
    this.text = label;
    this.image = image;
    this.parent = xsdSchema;
    this.xsdSchema = xsdSchema;
    this.target = xsdSchema;
    this.children = children;
    this.groupType = groupType;
  }

  public final static int ATTRIBUTES = 1;
  public final static int ELEMENTS = 2;
  public final static int TYPES = 3;
  public final static int GROUPS = 5;
  public final static int DIRECTIVES = 6;
  public final static int NOTATIONS = 7;
  public final static int ATTRIBUTE_GROUPS = 8;
  public final static int IDENTITY_CONSTRAINTS = 9;
  public final static int ANNOTATIONS = 10;

  public XSDSchema getXSDSchema()
  {
    return xsdSchema;
  }

  public int getGroupType()
  {
    return groupType;
  }

  public Image getImage()
  {
    return image;
  }

  public String getText()
  {
    return text;
  }

  public ITreeElement[] getChildren()
  {
    return (ITreeElement[]) children.toArray(new ITreeElement[0]);
  }
  
  public ITreeElement[] getAllChildren()
  {
    return (ITreeElement[]) allChildren.toArray(new ITreeElement[0]);
  }

  public void setChildren(Collection list)
  {
    children = list;
  }

  public void setAllChildren(Collection list)
  {
    allChildren = list;
  }

  public Object getParent(Object element)
  {
    return xsdSchema;
  }

  public boolean hasChildren(Object element)
  {
    return true;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {

  }

  public String[] getActions(Object object)
  {    
    Collection actionIDs = new ArrayList();
    
    switch (groupType)
    {
      case TYPES : {
        actionIDs.add(AddXSDComplexTypeDefinitionAction.ID);
        actionIDs.add(AddXSDSimpleTypeDefinitionAction.ID);
        break;
      }
      case ELEMENTS : {
        actionIDs.add(AddXSDElementAction.ID);
        break;
      }
      case GROUPS : {
        actionIDs.add(AddXSDModelGroupDefinitionAction.MODELGROUPDEFINITION_ID);
        break;
      }
      case ATTRIBUTES : {
        actionIDs.add(AddXSDAttributeDeclarationAction.ID);
        actionIDs.add(AddXSDAttributeGroupDefinitionAction.ID);
        break;
      }
      case ATTRIBUTE_GROUPS : {
        actionIDs.add(AddXSDAttributeGroupDefinitionAction.ID);
        break;
      }
      case DIRECTIVES : {
        actionIDs.add(AddXSDSchemaDirectiveAction.INCLUDE_ID);
        actionIDs.add(AddXSDSchemaDirectiveAction.IMPORT_ID);
        actionIDs.add(AddXSDSchemaDirectiveAction.REDEFINE_ID);
        break;
      }
    }
    actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
    actionIDs.add(ShowPropertiesViewAction.ID);
    return (String [])actionIDs.toArray(new String[0]);
  }
  
  public void propertyChanged(Object object, String property)
  {
    if (getText().equals(property))
      notifyListeners(this, property);
  }

  public List getTypes()
  {
    return null;
  }

  public IModel getModel()
  {
    return (IModel)XSDAdapterFactory.getInstance().adapt(xsdSchema);
  }
}
