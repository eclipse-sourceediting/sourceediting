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
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchemaDirective;

public class XSDSchemaDirectiveAdapter extends XSDAbstractAdapter
{

  /**
   * @param adapterFactory
   */
  public XSDSchemaDirectiveAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  
  public Image getImage(Object object)
  {
    if (object instanceof XSDImport)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDImport.gif");
    }
    else if (object instanceof XSDInclude)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDInclude.gif");
    }
    else if (object instanceof XSDRedefine)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDRedefine.gif");
    }
    return null;
  }

  public String getText(Object object)
  {
    XSDSchemaDirective directive = (XSDSchemaDirective)object;
    String result = directive.getSchemaLocation();
    if (result == null) result = "(" + XSDEditorPlugin.getXSDString("_UI_LABEL_NO_LOCATION_SPECIFIED") + ")";
    if (result.equals("")) result = "(" + XSDEditorPlugin.getXSDString("_UI_LABEL_NO_LOCATION_SPECIFIED")+ ")";
    return result;
    
//    XSDImport xsdImport = ((XSDImport)object);
//    String result = xsdImport.getSchemaLocation();
//    return result == null ? "" : result;
    
//    XSDInclude xsdInclude = ((XSDInclude)object);
//    String result = xsdInclude.getSchemaLocation();
//    return result == null ? "" : result;
    
//    XSDRedefine xsdRedefine = ((XSDRedefine)object);
//    String result = xsdRedefine.getSchemaLocation();
//    return result == null ? "" : result;


  }

  public Object[] getChildren(Object parentElement)
  {
    List list = new ArrayList();
    if (parentElement instanceof XSDRedefine)
    {
      XSDRedefine redefine = (XSDRedefine)parentElement;
      list = redefine.getContents();
      if (list == null)
      {
        list = new ArrayList();
      }
    }
    return list.toArray();
  }
  
  public boolean hasChildren(Object object)
  {
    return false;
  }

  public Object getParent(Object object)
  {
    return null;
    
  }

}
