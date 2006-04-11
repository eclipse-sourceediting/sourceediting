/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;

public class XSDSimpleTypeDefinitionAdapter extends XSDTypeDefinitionAdapter implements IType
{
  public Image getImage()
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;

    if (xsdSimpleTypeDefinition.getContainer() == null)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleType.gif");
    }

    if (XSDVariety.LIST_LITERAL == xsdSimpleTypeDefinition.getVariety())
    {
      return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_list_obj");
    }
    else if (XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety())
    {
      return XSDEditorPlugin.getPlugin().getIconImage("obj16/smpl_union_obj");
    }
    else if (XSDVariety.ATOMIC_LITERAL == xsdSimpleTypeDefinition.getVariety())
    {
      if (xsdSimpleTypeDefinition.getPrimitiveTypeDefinition() != null)
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
  }
  
  public String getDisplayName()
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;
    return (xsdSimpleTypeDefinition.getName() == null ? "local type" : xsdSimpleTypeDefinition.getName());
  }

  public String getText()
  {
    return getText(true);
  }

  public String getText(boolean showType)
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;

    StringBuffer result = new StringBuffer();

    result.append(xsdSimpleTypeDefinition.getName() == null ? "local type" : xsdSimpleTypeDefinition.getName());

    if (showType)
    {
      XSDSimpleTypeDefinition baseTypeDefinition = xsdSimpleTypeDefinition.getBaseTypeDefinition();
      if (baseTypeDefinition != null && XSDVariety.ATOMIC_LITERAL == xsdSimpleTypeDefinition.getVariety())
      {
        if (baseTypeDefinition.getName() != null && !xsdSimpleTypeDefinition.getContents().contains(baseTypeDefinition) && !XSDConstants.isAnySimpleType(baseTypeDefinition))
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
            for (Iterator members = memberTypeDefinitions.iterator(); members.hasNext();)
            {
              XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition) members.next();
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

  public boolean hasChildren()
  {
    return false;
  }
  
  public boolean isComplexType()
  {
    return false;
  }

  public boolean isFocusAllowed()
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;
    if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(xsdSimpleTypeDefinition.getTargetNamespace()))
    {
      return false;
    }
    if (xsdSimpleTypeDefinition.getName() == null)
    {
      return false;
    }
    return true;
  }
}
