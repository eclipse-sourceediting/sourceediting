/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDModelGroupDefinition;


public class XSDModelGroupDefinitionAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDModelGroupDefinitionAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object object)
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = ((XSDModelGroupDefinition)object);

    if (xsdModelGroupDefinition.isModelGroupDefinitionReference())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDGroupRef.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDGroup.gif");
    }
  }
  
  public String getText(Object object)
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = ((XSDModelGroupDefinition)object);
    String result = 
      xsdModelGroupDefinition.isModelGroupDefinitionReference() ?
        xsdModelGroupDefinition.getQName() :
        xsdModelGroupDefinition.getName();
    return result == null ? "'absent'" : result;
  }
  
  boolean hasChildren = false;
  
  public Object[] getChildren(Object parentElement)
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition)parentElement;
    List list = new ArrayList();
    if (xsdModelGroupDefinition.isModelGroupDefinitionReference())
    {
      if (((XSDModelAdapterFactoryImpl)adapterFactory).getShowReferences())
      {
        list.add(xsdModelGroupDefinition.getResolvedModelGroupDefinition().getModelGroup());
        hasChildren = true;
      }
    }
    else
    {
      if (xsdModelGroupDefinition.getModelGroup() != null)
      {
        list.add(xsdModelGroupDefinition.getModelGroup());
        hasChildren = true;
      }
    }
    
    return list.toArray();
 
  }
  
  public boolean hasChildren(Object object)
  {
    return hasChildren;
  }

  public Object getParent(Object object)
  {
    XSDModelGroupDefinition element = (XSDModelGroupDefinition)object;
    return element.getContainer();
  }

}
