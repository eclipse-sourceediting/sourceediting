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
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class XSDSimpleTypeDefinitionAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDSimpleTypeDefinitionAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  public Image getImage(Object object)
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = ((XSDSimpleTypeDefinition)object);
    
    if (xsdSimpleTypeDefinition.getContainer() == null)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
    }
    
    if (XSDVariety.LIST_LITERAL == xsdSimpleTypeDefinition.getVariety())
    {
      // return XSDEditorPlugin.getXSDImage("icons/XSDSimpleList.gif");
      return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_list_obj");
    }
    else if (XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety())
    {
      //return XSDEditorPlugin.getXSDImage("icons/XSDSimpleUnion.gif");
      return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_union_obj");
    }
    else if (XSDVariety.ATOMIC_LITERAL == xsdSimpleTypeDefinition.getVariety())
    {
      // return XSDEditorPlugin.getXSDImage("icons/XSDSimpleRestrict.gif");
      Element stElement = xsdSimpleTypeDefinition.getElement();
      XSDDOMHelper domHelper = new XSDDOMHelper();
      if (domHelper.getChildNode(stElement, XSDConstants.RESTRICTION_ELEMENT_TAG) != null)
      {
        return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_restrict_obj");
      }
      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
    }
    else if (xsdSimpleTypeDefinition.isSetVariety())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
    }
    
//    return 
//      XSDEditorPlugin.getPlugin().getIconImage
//        (xsdSimpleTypeDefinition.getContainer() == null ?
//           "full/obj16/XSDSimpleTypeDefinitionUnresolved" :
//           XSDVariety.LIST_LITERAL == xsdSimpleTypeDefinition.getVariety() ? 
//             "full/obj16/XSDSimpleTypeDefinitionList" :  
//              XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety() ? 
//                "full/obj16/XSDSimpleTypeDefinitionUnion" :
//                  xsdSimpleTypeDefinition.isSetVariety() ?
//                    "full/obj16/XSDSimpleTypeDefinitionAtomic"  :
//                    "full/obj16/XSDSimpleTypeDefinition");
  }
  
  public String getText(Object object)
  {
    return getText(object, true);
  }
 
  public String getText(Object object, boolean showType)
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = ((XSDSimpleTypeDefinition)object);

    StringBuffer result = new StringBuffer();

    result.append
      (xsdSimpleTypeDefinition.getName() == null ? 
         // xsdSimpleTypeDefinition.getAliasName() :
         "local type" :
         xsdSimpleTypeDefinition.getName());

    if (showType)
    {
      XSDSimpleTypeDefinition baseTypeDefinition = xsdSimpleTypeDefinition.getBaseTypeDefinition();
      if (baseTypeDefinition != null && XSDVariety.ATOMIC_LITERAL == xsdSimpleTypeDefinition.getVariety())
      {
        if (baseTypeDefinition.getName() != null && 
              !xsdSimpleTypeDefinition.getContents().contains(baseTypeDefinition) &&
              !XSDConstants.isAnySimpleType(baseTypeDefinition)) 
        {
          result.append(" : ");
          result.append(baseTypeDefinition.getQName(xsdSimpleTypeDefinition));
        }
      }
      else
      {
        XSDSimpleTypeDefinition itemTypeDefinition = xsdSimpleTypeDefinition.getItemTypeDefinition();
        if (itemTypeDefinition != null)
        {
          if (itemTypeDefinition.getName() != null) 
          {
            result.append(" : ");
            result.append(itemTypeDefinition.getQName(xsdSimpleTypeDefinition));
          }
        }
        else 
        {
          List memberTypeDefinitions = xsdSimpleTypeDefinition.getMemberTypeDefinitions();
          if (!memberTypeDefinitions.isEmpty())
          {
            boolean first = true;
            for (Iterator members = memberTypeDefinitions.iterator(); members.hasNext(); )
            {
              XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition)members.next();
              if (memberTypeDefinition.getName() != null) 
              {
                if (first)
                {
                  result.append(" : ");
                  first = false;
                }
                else
                {
                  result.append(" | ");
                }
                result.append(memberTypeDefinition.getQName(xsdSimpleTypeDefinition));
              }
              else
              {
                break;
              }
            }
          } 
          else if (result.length() == 0)
          {
            result.append("'absent'");
          }
        }
      }
    }

    return result.toString();
  }

  public Object[] getChildren(Object parentElement)
  {
    List list = new ArrayList();
    return list.toArray();
  
  }
  
  public boolean hasChildren(Object object)
  {
    return true;
  }

  public Object getParent(Object object)
  {
    XSDSimpleTypeDefinition element = (XSDSimpleTypeDefinition)object;
    return element.getContainer();
  }

}
