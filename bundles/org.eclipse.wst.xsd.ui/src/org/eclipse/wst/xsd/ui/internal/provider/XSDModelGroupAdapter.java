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
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class XSDModelGroupAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDModelGroupAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object element)
  {
    XSDModelGroup xsdModelGroup = ((XSDModelGroup)element);
    if (XSDCompositor.CHOICE_LITERAL == xsdModelGroup.getCompositor())
    {
      // return XSDEditorPlugin.getPlugin().getIconImage("full/obj16/XSDModelGroupChoice");
      return XSDEditorPlugin.getXSDImage("icons/XSDChoice.gif");
    }
    else if (XSDCompositor.ALL_LITERAL == xsdModelGroup.getCompositor())
    {
      // return XSDEditorPlugin.getPlugin().getIconImage("full/obj16/XSDModelGroupAll");
      return XSDEditorPlugin.getXSDImage("icons/XSDAll.gif");
    }
    else 
    {
      // return XSDEditorPlugin.getPlugin().getIconImage("full/obj16/XSDModelGroupSequence");
      return XSDEditorPlugin.getXSDImage("icons/XSDSequence.gif");
    }
  }
  
  public String getText(Object object)
  {
    XSDModelGroup xsdModelGroup = ((XSDModelGroup)object);
    
    StringBuffer result = new StringBuffer();
    String name = xsdModelGroup.getCompositor().getName();
    if (name != null)
    {
      result.append(name);
    }
    
    Element element = xsdModelGroup.getElement();
    
    if (element != null)
    {
      boolean hasMinOccurs = element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
      boolean hasMaxOccurs = element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);
      
      if (hasMinOccurs || hasMaxOccurs)
      {
  	    result.append(" [");  
  	    if (hasMinOccurs)
  	    {
  	      int min = ((XSDParticle)xsdModelGroup.getContainer()).getMinOccurs();
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
          int min = ((XSDParticle)xsdModelGroup.getContainer()).getMinOccurs();
          result.append(String.valueOf(min));
        }
  	    if (hasMaxOccurs)
  	    {
  	      int max = ((XSDParticle)xsdModelGroup.getContainer()).getMaxOccurs();
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
          int max = ((XSDParticle)xsdModelGroup.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));
        }
  	    result.append("]");
      }
    }
    return result.toString();
  }
  
  public Object[] getChildren(Object parentElement)
  {
    XSDModelGroup xsdModelGroup = (XSDModelGroup)parentElement;
    List list = new ArrayList();
    // list.addAll(xsdModelGroup.getContents());
    list.addAll(XSDChildUtility.getModelChildren(xsdModelGroup));
    return list.toArray();
  }
  
  public boolean hasChildren(Object object)
  {
    return true;
  }

  public Object getParent(Object object)
  {
    XSDModelGroup element = (XSDModelGroup)object;
    return element.getContainer();
  }

}
