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
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class XSDWildcardAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDWildcardAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object object)
  {
    XSDWildcard xsdWildcard = (XSDWildcard)object;
    return XSDEditorPlugin.getXSDImage
      (xsdWildcard.eContainer() instanceof XSDParticle ?
        "icons/XSDAny.gif" :
        "icons/XSDAnyAttribute.gif");
  }
  
  public String getText(Object object)
  {
    XSDWildcard xsdWildcard = ((XSDWildcard)object);
    
    StringBuffer result = new StringBuffer();
    Element element = xsdWildcard.getElement();
    
    if (element != null)
    {
      result.append(element.getNodeName());
      boolean hasMinOccurs = element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
      boolean hasMaxOccurs = element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);
      
      if (hasMinOccurs || hasMaxOccurs)
      {
        result.append(" [");   //$NON-NLS-1$
        if (hasMinOccurs)
        {
          
          int min = ((XSDParticle)xsdWildcard.getContainer()).getMinOccurs();
          if (min == XSDParticle.UNBOUNDED)
          {
            result.append("*");
          }
          else
          {
            result.append(String.valueOf(min));
          }
        }
        else // print default
        {
          int min = ((XSDParticle)xsdWildcard.getContainer()).getMinOccurs();
          result.append(String.valueOf(min));
        }
        if (hasMaxOccurs)
        {
          int max = ((XSDParticle)xsdWildcard.getContainer()).getMaxOccurs();
          result.append("..");
          if (max == XSDParticle.UNBOUNDED)
          {
            result.append("*");
          }
          else
          {
            result.append(String.valueOf(max));
          }
        }
        else // print default
        {
          result.append("..");
          int max = ((XSDParticle)xsdWildcard.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));
        }
        result.append("]");
      }
    }
    return result.toString();

  }

  public boolean hasChildren(Object object)
  {
    return false;
  }

  public Object getParent(Object object)
  {
    XSDWildcard xsdWildcard = (XSDWildcard)object;
    return xsdWildcard.getContainer();
  }
}
