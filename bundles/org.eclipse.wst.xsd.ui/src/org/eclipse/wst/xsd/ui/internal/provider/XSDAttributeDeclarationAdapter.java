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
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

public class XSDAttributeDeclarationAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDAttributeDeclarationAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object object)
  {
    XSDAttributeDeclaration xsdAttributeDeclaration = ((XSDAttributeDeclaration)object);
    XSDAttributeDeclaration resolvedAttributeDeclaration = xsdAttributeDeclaration.getResolvedAttributeDeclaration();
    
    if (resolvedAttributeDeclaration.getContainer() == null)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif");
    }
    // if (xsdAttributeDeclaration.getResolvedAttributeDeclaration() == xsdAttributeDeclaration)
    if (xsdAttributeDeclaration.isAttributeDeclarationReference())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttributeRef.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif");
    }
    
//    return 
//    XSDEditorPlugin.getPlugin().getIconImage
//     (resolvedAttributeDeclaration.getContainer() == null ?
//        "full/obj16/XSDAttributeUnresolved" :
//          xsdAttributeDeclaration.getResolvedAttributeDeclaration() == xsdAttributeDeclaration ?
//          "full/obj16/XSDAttributeDeclaration" :
//          "full/obj16/XSDAttributeUse");
  }

  public String getText(Object object)
  {
    XSDAttributeDeclaration xsdAttributeDeclaration = ((XSDAttributeDeclaration)object);
    XSDAttributeDeclaration resolvedAttributeDeclaration = xsdAttributeDeclaration.getResolvedAttributeDeclaration();
    String name =
      xsdAttributeDeclaration != resolvedAttributeDeclaration ?
        xsdAttributeDeclaration.getQName() :
        xsdAttributeDeclaration.getName();

    StringBuffer result = new StringBuffer();
    if (name == null)
    {
      result.append("'absent'");
    }
    else
    {
      result.append(name);
    }

    if (resolvedAttributeDeclaration.getAnonymousTypeDefinition() == null && resolvedAttributeDeclaration.getTypeDefinition() != null)
    {
      result.append(" : ");
      result.append(resolvedAttributeDeclaration.getTypeDefinition().getQName(xsdAttributeDeclaration));
    }

    return result.toString();
  }

  public Object[] getChildren(Object parentElement)
  {
    XSDAttributeDeclaration xsdAttributeDeclaration = (XSDAttributeDeclaration)parentElement;
    List list = new ArrayList();
    
    XSDTypeDefinition type = xsdAttributeDeclaration.getAnonymousTypeDefinition();
    if (type != null)
    {
      list.add(type);
    }
    return list.toArray();
  }
  
  public boolean hasChildren(Object object)
  {
    return true;
  }

  public Object getParent(Object object)
  {
    XSDAttributeDeclaration element = (XSDAttributeDeclaration)object;
    return element.getContainer();
  }

}
