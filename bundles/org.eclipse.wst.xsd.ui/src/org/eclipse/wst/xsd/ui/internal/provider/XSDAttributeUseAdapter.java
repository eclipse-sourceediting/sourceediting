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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;

public class XSDAttributeUseAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDAttributeUseAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }
  
  protected XSDAttributeDeclaration getDelegate(XSDAttributeUse xsdAttributeUse)
  {
    return xsdAttributeUse.getContent();
  }
  
  /**
   * This returns XSDAttributeUse.gif.
   */
  public Image getImage(Object object)
  {
    XSDAttributeUse xsdAttributeUse = ((XSDAttributeUse)object);
    
    XSDAttributeDeclaration xsdAttributeDeclaration = getDelegate(xsdAttributeUse);
    if (xsdAttributeDeclaration.isAttributeDeclarationReference())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttributeRef.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif");
    }
    //return XSDEditorPlugin.getPlugin().getIconImage("full/obj16/XSDAttributeUse");  
    
  }

  public String getText(Object object)
  {
    return getText(object, true);
  }

  public String getText(Object object, boolean showType)
  {
    XSDAttributeUse xsdAttributeUse = ((XSDAttributeUse)object);
    XSDAttributeDeclaration xsdAttributeDeclaration = getDelegate(xsdAttributeUse);
    if (xsdAttributeDeclaration.isAttributeDeclarationReference())
    {
      return xsdAttributeDeclaration.getResolvedAttributeDeclaration().getName();
    }
    StringBuffer result  = new StringBuffer();
    if (xsdAttributeDeclaration != null)
    {
      result.append(xsdAttributeDeclaration.getName());
    }

    if (xsdAttributeUse.isSetConstraint())
    {
      if (result.length() != 0)
      {
        result.append("  ");
      }
      result.append('<');
      result.append(xsdAttributeUse.getConstraint());
      result.append("=\"");
      result.append(xsdAttributeUse.getLexicalValue());
      result.append("\">");
    }

    return result.toString();
  }

}
