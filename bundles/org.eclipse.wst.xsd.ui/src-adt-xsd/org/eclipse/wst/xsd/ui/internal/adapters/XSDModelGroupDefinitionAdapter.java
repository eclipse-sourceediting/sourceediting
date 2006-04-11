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
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.DeleteXSDConcreteComponentAction;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;

public class XSDModelGroupDefinitionAdapter extends XSDBaseAdapter implements IStructure, IActionProvider
{
  public XSDModelGroupDefinitionAdapter()
  {
    super();
  }

  public XSDModelGroupDefinition getXSDModelGroupDefinition()
  {
    return (XSDModelGroupDefinition) target;
  }

  public Image getImage()
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition) target;

    if (xsdModelGroupDefinition.isModelGroupDefinitionReference())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDGroupRef.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDGroup.gif");
    }
  }

  public String getText()
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition) target;
    String result = xsdModelGroupDefinition.isModelGroupDefinitionReference() ? xsdModelGroupDefinition.getQName() : xsdModelGroupDefinition.getName();
    return result == null ? "'absent'" : result;
  }

  public ITreeElement[] getChildren()
  {
    List list = new ArrayList();
    XSDModelGroup xsdModelGroup = ((XSDModelGroupDefinition) target).getResolvedModelGroupDefinition().getModelGroup();
    if (xsdModelGroup != null)
      list.add(xsdModelGroup);

    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider#getActions(java.lang.Object)
   */
  public String[] getActions(Object object)
  {
    Collection list = new ArrayList();

    if (!getXSDModelGroupDefinition().isModelGroupDefinitionReference())
    {
      list.add(AddXSDElementAction.ID);
      list.add(AddXSDElementAction.REF_ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(AddXSDModelGroupAction.SEQUENCE_ID);
      list.add(AddXSDModelGroupAction.CHOICE_ID);
    }
    
    list.add(DeleteXSDConcreteComponentAction.DELETE_XSD_COMPONENT_ID);
    return (String [])list.toArray(new String[0]);
  }

  public Command getAddNewFieldCommand(String fieldKind)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getDeleteCommand()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public List getFields()
  {
    List fields = new ArrayList();
    XSDVisitorForFields visitor = new XSDVisitorForFields();
    visitor.visitModelGroupDefinition(getXSDModelGroupDefinition());
    populateAdapterList(visitor.concreteComponentList, fields);
    
    // TODO (cs) common a base class for a structure thingee
    //
    /*
    populateAdapterList(visitor.thingsWeNeedToListenTo, otherThingsToListenTo);
    for (Iterator i = otherThingsToListenTo.iterator(); i.hasNext();)
    {
      Adapter adapter = (Adapter) i.next();
      if (adapter instanceof IADTObject)
      {
        IADTObject adtObject = (IADTObject) adapter;
        adtObject.registerListener(this);
      }
    }*/
    // System.out.println("fields[" + this + "].size() = " + fields.size());
    return fields;
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDModelGroupDefinition().getSchema());
    return (IModel)adapter;
  }
  public String getName()
  {
    return getText();
  }    
}
