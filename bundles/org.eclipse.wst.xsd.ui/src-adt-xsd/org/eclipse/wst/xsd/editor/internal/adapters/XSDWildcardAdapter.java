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
package org.eclipse.wst.xsd.editor.internal.adapters;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.adt.facade.IField;
import org.eclipse.wst.xsd.adt.facade.IModel;
import org.eclipse.wst.xsd.adt.facade.IType;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDWildcardAdapter extends XSDParticleAdapter implements IField
{
  public XSDWildcardAdapter()
  {

  }

  public Image getImage()
  {
    XSDWildcard xsdWildcard = (XSDWildcard) target;
    return XSDEditorPlugin.getXSDImage(xsdWildcard.eContainer() instanceof XSDParticle ? "icons/XSDAny.gif" : "icons/XSDAnyAttribute.gif");
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
            result.append("*");
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
        else
        // print default
        {
          result.append("..");
          int max = ((XSDParticle) xsdWildcard.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));
        }
        result.append("]");
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
    // TODO Auto-generated method stub
    return null;
  }

  public String getKind()
  {
    return "element";
  }

  public IModel getModel()
  {
    return null;
  }

  public String getName()
  {
    return "anyElement";
  }
  
  public IType getType()
  {
    return null;
  }

  public String getTypeName()
  {
    return "anyType";
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
}
