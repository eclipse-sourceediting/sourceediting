/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adapters;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDTypeDefinition;

public class XSDAttributeUseAdapter extends XSDBaseAttributeAdapter implements IActionProvider
{
  protected XSDAttributeDeclaration getXSDAttributeDeclaration()
  {
    return getXSDAttributeUse().getAttributeDeclaration();
  }

  protected XSDAttributeDeclaration getResolvedXSDAttributeDeclaration()
  {
    return getXSDAttributeDeclaration().getResolvedAttributeDeclaration();
  }
  
  protected XSDAttributeUse getXSDAttributeUse()
  {
    return (XSDAttributeUse)target;
  }

  public XSDAttributeUseAdapter()
  {
    super();
  }

  public String getText()
  {
    return getTextForAttributeUse(getXSDAttributeUse(), true);
  }

  public String getTextForAttributeUse(XSDAttributeUse attributeUse, boolean showType)
  {
    XSDAttributeDeclaration ad = attributeUse.getAttributeDeclaration();
      
    StringBuffer result  = new StringBuffer();
    result.append(getTextForAttribute(ad, showType));
    /*
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
    */
    return result.toString();
  }
  
  public boolean isGlobal()
  {
    return false;
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDAttributeDeclaration().getSchema());
    return (IModel)adapter;
  }
  
  public String getTypeNameQualifier()
  {
    XSDTypeDefinition type = getResolvedXSDAttributeDeclaration().getTypeDefinition();
    if (type != null)
    {
      return type.getTargetNamespace();
    }
    return "";
  }

  public IADTObject getTopContainer()
  {
    return getGlobalXSDContainer(getXSDAttributeUse());
  }

  public boolean isReference()
  {
    return getXSDAttributeUse().getAttributeDeclaration().isAttributeDeclarationReference();
  }
}