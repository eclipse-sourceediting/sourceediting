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

import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDAnnotationAdapter extends XSDAbstractAdapter
{
  public XSDAnnotationAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object element)
  {
    return XSDEditorPlugin.getPlugin().getImage("icons/XSDAnnotate.gif");
  }
  
  public String getText(Object object)
  {
    XSDAnnotation xsdAnnotation = ((XSDAnnotation)object);
    String result = "";
    List userInformation = xsdAnnotation.getUserInformation();
    if (!userInformation.isEmpty())
    {
      Element element = (Element)userInformation.get(0);
      if (element.hasAttribute(XSDConstants.SOURCE_ATTRIBUTE))
      {
        result = element.getAttribute(XSDConstants.SOURCE_ATTRIBUTE);
      }
      else
      {
        org.w3c.dom.Node text = element.getFirstChild(); 
        while (text instanceof Element)
        {
          text = ((Element)text).getFirstChild();
        }
        if (text != null && text.getNodeValue() != null)
        {
          result = text.getNodeValue();
          result = result.trim();
          if (result.length() > 50)
          {
            result = result.substring(0, 50) + "...";
          }
        }
      }
    }

    return result;
  }
  
  public Object[] getChildren(Object parentElement)
  {
    return null;  
  }
  
  public boolean hasChildren(Object object)
  {
    return true;
  }

  public Object getParent(Object object)
  {
    XSDAnnotation element = (XSDAnnotation)object;
    return element.getContainer();
  }
}
