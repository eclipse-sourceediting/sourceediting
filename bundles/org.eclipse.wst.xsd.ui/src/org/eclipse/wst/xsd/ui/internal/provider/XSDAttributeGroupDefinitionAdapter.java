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
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDWildcard;


public class XSDAttributeGroupDefinitionAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDAttributeGroupDefinitionAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object object)
  {
    XSDAttributeGroupDefinition xsdAttributeGroupDefinition = ((XSDAttributeGroupDefinition)object);
    XSDAttributeGroupDefinition resolvedAttributeGroupDefinition = xsdAttributeGroupDefinition.getResolvedAttributeGroupDefinition();

    if (xsdAttributeGroupDefinition.isAttributeGroupDefinitionReference())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttributeGroupRef.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttributeGroup.gif");
    }
  }

  public String getText(Object object)
  {
    XSDAttributeGroupDefinition xsdAttributeGroupDefinition = ((XSDAttributeGroupDefinition)object);
    String result =  
      xsdAttributeGroupDefinition.isAttributeGroupDefinitionReference() ?
        xsdAttributeGroupDefinition.getQName() :
        xsdAttributeGroupDefinition.getName();
    return result == null ? "'absent'" : result;
  }

  public Object[] getChildren(Object parentElement)
  {
    XSDAttributeGroupDefinition xsdAttributeGroup = (XSDAttributeGroupDefinition)parentElement;
    List list = new ArrayList();
    list.addAll(xsdAttributeGroup.getContents());   
//    Iterator i = xsdAttributeGroup.getContents().iterator();
//    while (i.hasNext())
//    {
//      XSDAttributeGroupContent attrGroupContent = (XSDAttributeGroupContent)i.next();
//      if (attrGroupContent instanceof XSDAttributeUse)
//      {
//        list.add(((XSDAttributeUse)attrGroupContent).getAttributeDeclaration());
//      }
//      else
//      {
//        list.add(attrGroupContent);
//      }
//    }
    
    XSDWildcard wildcard = xsdAttributeGroup.getAttributeWildcardContent();
    if (wildcard != null)
    {
      list.add(wildcard);
    }
    return list.toArray();
  }
  
  public boolean hasChildren(Object object)
  {
    return true;
  }

  public Object getParent(Object object)
  {
    XSDAttributeGroupDefinition element = (XSDAttributeGroupDefinition)object;
    return element.getContainer();
  }

}
