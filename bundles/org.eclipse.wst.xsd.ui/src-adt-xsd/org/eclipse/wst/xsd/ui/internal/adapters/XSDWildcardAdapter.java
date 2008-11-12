/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDWildcardAdapter extends XSDParticleAdapter implements IField, IActionProvider, IGraphElement
{
//  public static final Image ANYELEMENT_ICON = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAny.gif"); //$NON-NLS-1$
//  public static final Image ANYELEMENT_DISABLED_ICON = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAny.gif"); //$NON-NLS-1$
  
  public XSDWildcardAdapter()
  {

  }
  
  public Image getImage()
  {
    XSDWildcard xsdWildcard = (XSDWildcard) target;
    
    if (xsdWildcard.eContainer() instanceof XSDParticle)
    {
      if (isReadOnly())
      {
        return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAnydis.gif"); //$NON-NLS-1$
      }
      return XSDEditorPlugin.getXSDImage("icons/XSDAny.gif"); //$NON-NLS-1$
    }
    else
    {
      if (isReadOnly())
      {
        return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAnyAttributedis.gif"); //$NON-NLS-1$
      }
      return XSDEditorPlugin.getXSDImage("icons/XSDAnyAttribute.gif"); //$NON-NLS-1$
    }
  }

  public String getText()
  {
    XSDWildcard xsdWildcard = (XSDWildcard) target;

    StringBuffer result = new StringBuffer();
    Element element = xsdWildcard.getElement();

    if (element != null)
    {
      result.append(element.getNodeName());
      boolean hasMinOccurs = element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
      boolean hasMaxOccurs = element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);

      if (hasMinOccurs || hasMaxOccurs)
      {
        result.append(" ["); //$NON-NLS-1$
        if (hasMinOccurs)
        {

          int min = ((XSDParticle) xsdWildcard.getContainer()).getMinOccurs();
          if (min == XSDParticle.UNBOUNDED)
          {
            result.append("*"); //$NON-NLS-1$
          }
          else
          {
            result.append(String.valueOf(min));
          }
        }
        else
        // print default
        {
          int min = ((XSDParticle) xsdWildcard.getContainer()).getMinOccurs();
          result.append(String.valueOf(min));
        }
        if (hasMaxOccurs)
        {
          int max = ((XSDParticle) xsdWildcard.getContainer()).getMaxOccurs();
          result.append(".."); //$NON-NLS-1$
          if (max == XSDParticle.UNBOUNDED)
          {
            result.append("*"); //$NON-NLS-1$
          }
          else
          {
            result.append(String.valueOf(max));
          }
        }
        else
        // print default
        {
          result.append(".."); //$NON-NLS-1$
          int max = ((XSDParticle) xsdWildcard.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));
        }
        result.append("]"); //$NON-NLS-1$
      }
    }
    return result.toString();

  }

  public boolean hasChildren()
  {
    return false;
  }

  public Object getParent(Object object)
  {
    XSDWildcard xsdWildcard = (XSDWildcard) target;
    return xsdWildcard.getContainer();
  }

  public Command getDeleteCommand()
  {
	  return new DeleteCommand((XSDWildcard) target);	
  }

  public String getKind()
  {
    XSDWildcard xsdWildcard = (XSDWildcard) target;
    if (xsdWildcard.eContainer() instanceof XSDParticle)
    {
      return "element"; //$NON-NLS-1$
    }
    return "attribute";
  }

  public IModel getModel()
  {
    return null;
  }

  public String getName()
  {
    XSDWildcard xsdWildcard = (XSDWildcard) target;
    if (xsdWildcard.eContainer() instanceof XSDParticle)
    {
      return "any"; //$NON-NLS-1$
    }
    return "anyAttribute"; //$NON-NLS-1$
  }
  
  public IType getType()
  {
    return null;
  }

  public String getTypeName()
  {
    return ""; //$NON-NLS-1$
  }

  public String getTypeNameQualifier()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateMaxOccursCommand(int maxOccurs)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateMinOccursCommand(int minOccurs)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateNameCommand(String name)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateTypeNameCommand(String typeName, String quailifier)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isGlobal()
  {
    return false;
  }

  public boolean isReference()
  {
    return false;
  }

  public String[] getActions(Object object)
  {
    Collection actionIDs = new ArrayList();
    actionIDs.add(DeleteAction.ID);
    actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
    actionIDs.add(ShowPropertiesViewAction.ID);
    return (String [])actionIDs.toArray(new String[0]);
  }

  public IADTObject getTopContainer()
  {
    return getGlobalXSDContainer((XSDWildcard) target);
  }

  public boolean isFocusAllowed()
  {
    return false;
  }
}
