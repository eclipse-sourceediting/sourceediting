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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class XSDComplexTypeDefinitionAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDComplexTypeDefinitionAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object object)
  {
    return XSDEditorPlugin.getXSDImage("icons/XSDComplexType.gif");
//    XSDComplexTypeDefinition xsdComplexTypeDefinition = ((XSDComplexTypeDefinition)object);
//    return 
//      XSDEditorPlugin.getPlugin().getIconImage
//        (xsdComplexTypeDefinition.getContainer() == null ?
//          "full/obj16/XSDComplexTypeDefinitionUnresolved" :
//          "full/obj16/XSDComplexTypeDefinition");
  }
  
  public String getText(Object object)
  {
    return getText(object, true);
  }
  
  public String getText(Object object, boolean showType)
  {
    XSDComplexTypeDefinition xsdComplexTypeDefinition = ((XSDComplexTypeDefinition)object);

    StringBuffer result = new StringBuffer();
    
    result.append
     (xsdComplexTypeDefinition.getName() == null ? 
        // xsdComplexTypeDefinition.getAliasName() :
        "local type" :
        xsdComplexTypeDefinition.getName());

    if (showType)
    {
      XSDTypeDefinition baseTypeDefinition = xsdComplexTypeDefinition.getBaseTypeDefinition();
      if (baseTypeDefinition != null && 
            baseTypeDefinition != xsdComplexTypeDefinition.getContent() &&
            baseTypeDefinition.getName() != null &&
            !XSDConstants.isURType(baseTypeDefinition))
      {
        result.append(" : ");
        result.append(baseTypeDefinition.getQName(xsdComplexTypeDefinition));
      }
    }

    return result.toString();
  }

  public Object[] getChildren(Object parentElement)
  {
    XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition)parentElement;
    List list = new ArrayList();
    
    if (xsdComplexTypeDefinition.getContent() != null)
    {
      XSDComplexTypeContent xsdComplexTypeContent = xsdComplexTypeDefinition.getContent();
      if (xsdComplexTypeContent instanceof XSDParticle)
      {
        list.add(((XSDParticle)xsdComplexTypeContent).getContent());
      }
    }
    
//    if (((XSDModelAdapterFactoryImpl)adapterFactory).getShowInherited())
//    {
//      if (xsdComplexTypeDefinition.getSyntheticParticle() != null)
//      {
//        XSDTypeDefinition type = xsdComplexTypeDefinition.getBaseTypeDefinition();
//        list.addAll(XSDChildUtility.getModelChildren(type));
//      }
//      if (xsdComplexTypeDefinition.getSyntheticWildcard() != null)
//      {
//        list.add(xsdComplexTypeDefinition.getSyntheticWildcard());
//      }
//    }
    
//    List attributes = xsdComplexTypeDefinition.getAttributeContents();
//    if (attributes.size() > 0)
//    {
//      list.addAll(attributes);
//    }

//  list = new ArrayList();
    if (((XSDModelAdapterFactoryImpl)adapterFactory).getShowInherited())
    {
      if (xsdComplexTypeDefinition.getDerivationMethod().getName().equals("extension"))
      {
        XSDTypeDefinition type = xsdComplexTypeDefinition.getBaseTypeDefinition();
        Iterator iter = XSDChildUtility.getModelChildren(type).iterator();
        boolean cont = true;
        while (cont)
        {
          while (iter.hasNext())
          {
            list.add(0, iter.next());
          }
          
          if (type instanceof XSDComplexTypeDefinition)
          {
            XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition)type;
            type = ctd.getBaseTypeDefinition();
                      
            // defect 264957 - workbench hangs when modifying complex content
            // Since we don't filter out the current complexType from
            // the combobox, we can potentially have an endless loop
            if (ctd == type)
            {
              cont = false;
              break;
            }

            if (ctd.getDerivationMethod().getName().equals("extension"))
            {
              iter = XSDChildUtility.getModelChildren(type).iterator();
            }
            else
            {
              cont = false;
            }
          }
          else
          {
            cont = false;
          }
        }

      }

          
//          list.addAll(XSDChildUtility.getModelChildren(baseType));
      }

//    list.addAll(XSDChildUtility.getModelChildren(xsdComplexTypeDefinition));

    return list.toArray();
 
  }
  
  public boolean hasChildren(Object object)
  {
    return true;
  }

  public Object getParent(Object object)
  {
    XSDComplexTypeDefinition element = (XSDComplexTypeDefinition)object;
    return element.getContainer();
  }

}
