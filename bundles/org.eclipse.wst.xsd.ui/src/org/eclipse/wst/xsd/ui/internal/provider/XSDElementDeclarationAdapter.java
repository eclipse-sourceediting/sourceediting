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
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class XSDElementDeclarationAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDElementDeclarationAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object element)
  {
    XSDElementDeclaration xsdElementDeclaration = ((XSDElementDeclaration)element);
    XSDElementDeclaration resolvedElementDeclaration = xsdElementDeclaration.getResolvedElementDeclaration();
    
    if (!xsdElementDeclaration.isElementDeclarationReference())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDElement.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDElementRef.gif");
    }
    
//    return 
//      XSDEditorPlugin.getPlugin().getIconImage
//        (resolvedElementDeclaration.getContainer() == null ?
//           "full/obj16/XSDElementUnresolved" :
//           xsdElementDeclaration.getResolvedElementDeclaration() == xsdElementDeclaration ?
//             "full/obj16/XSDElementDeclaration" :
//             "full/obj16/XSDElementUse");

  }
  
  public String getText(Object object)
  {
    XSDElementDeclaration xsdElementDeclaration = ((XSDElementDeclaration)object);
    XSDElementDeclaration resolvedElementDeclaration = xsdElementDeclaration.getResolvedElementDeclaration();
    String name = 
      xsdElementDeclaration != resolvedElementDeclaration ?
        xsdElementDeclaration.getQName() :
        xsdElementDeclaration.getName();

    StringBuffer result = new StringBuffer();
    if (name == null)
    {
      result.append("'absent'");
    }
    else
    {
      result.append(name);
    }
    
    if (!xsdElementDeclaration.isGlobal())
    {
      Element element = xsdElementDeclaration.getElement();
      boolean hasMinOccurs = element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
      boolean hasMaxOccurs = element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);

      if (hasMinOccurs || hasMaxOccurs)
      {
	      result.append(" ["); 
		    if (hasMinOccurs)
	      {
	        int min = ((XSDParticle)xsdElementDeclaration.getContainer()).getMinOccurs();
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
          int min = ((XSDParticle)xsdElementDeclaration.getContainer()).getMinOccurs();
          result.append(String.valueOf(min));
        }
	      if (hasMaxOccurs)
	      {
	        int max = ((XSDParticle)xsdElementDeclaration.getContainer()).getMaxOccurs();
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
          int max = ((XSDParticle)xsdElementDeclaration.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));
         
        }
	      result.append("]");
      }
    }
    
    if (resolvedElementDeclaration.getAnonymousTypeDefinition() == null && resolvedElementDeclaration.getTypeDefinition() != null)
    {
      result.append(" : ");
      result.append(resolvedElementDeclaration.getTypeDefinition().getQName(xsdElementDeclaration));
    }

    return result.toString();
  }
  
  public Object[] getChildren(Object parentElement)
  {
    XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)parentElement;
    List list = new ArrayList();
    XSDTypeDefinition type = null;
    if (xsdElementDeclaration.isElementDeclarationReference())
    {
      if (((XSDModelAdapterFactoryImpl)adapterFactory).getShowReferences())
      {
        type = xsdElementDeclaration.getResolvedElementDeclaration().getTypeDefinition();
      }
    }
    else
    {
      type = xsdElementDeclaration.getAnonymousTypeDefinition();
      if (type == null)
      {
        if (((XSDModelAdapterFactoryImpl)adapterFactory).getShowReferences())
        {
          type = xsdElementDeclaration.getTypeDefinition();
        }
      }
    }

    if (type != null && type instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition ctType = (XSDComplexTypeDefinition)type;
      if (ctType != null)
      {
        list.add(ctType);
      }
    }

//    if (xsdElementDeclaration.getIdentityConstraintDefinitions() != null)
//    {
//      list.addAll(xsdElementDeclaration.getIdentityConstraintDefinitions());
//    }
    
//    return XSDChildUtility.getModelChildren(xsdElementDeclaration.getResolvedElementDeclaration()).toArray();
    return list.toArray();
  
  }
  
  public boolean hasChildren(Object object)
  {
    return true;
  }

  public Object getParent(Object object)
  {
    XSDElementDeclaration element = (XSDElementDeclaration)object;
    return element.getContainer();
  }
}
