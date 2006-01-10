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
package org.eclipse.wst.xsd.ui.internal.properties;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;


public class NotationPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  /**
   * 
   */
  public NotationPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public NotationPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public NotationPropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
   */
  public Object getEditableValue()
  {
    return null;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    List list = new ArrayList();
    // Create a descriptor and set a category
// Removed for tabbed properties
//    PropertyDescriptor nameDescriptor =
//    new TextPropertyDescriptor(
//        XSDConstants.NAME_ATTRIBUTE,
//        XSDConstants.NAME_ATTRIBUTE);
//    list.add(nameDescriptor);

    PropertyDescriptor publicDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.PUBLIC_ATTRIBUTE,
        XSDConstants.PUBLIC_ATTRIBUTE);
    list.add(publicDescriptor);
    
    PropertyDescriptor systemDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.SYSTEM_ATTRIBUTE,
        XSDConstants.SYSTEM_ATTRIBUTE);
    list.add(systemDescriptor);

    IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
    list.toArray(result);
    return result;
    
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
   */
  public Object getPropertyValue(Object id)
  {
    Object result = null;
    if (id instanceof String)
    {
      result = element.getAttribute((String) id);
    }
    if (result == null)
    {
      result = "";
    }
    return result;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
   */
  public boolean isPropertySet(Object id)
  {
    return false;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
   */
  public void resetPropertyValue(Object id)
  {
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
   */
  public void setPropertyValue(Object id, Object value)
  {
    if (value == null)
    {
      value = "";
    }
    if (value instanceof String)
    {
      String newValue = (String)value;
      if (((String)id).equals(XSDConstants.NAME_ATTRIBUTE))
      {
        if (validateName(newValue))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_NOTATION_NAME_CHANGE"), element);
          if (newValue.length() > 0)
          {
            element.setAttribute(XSDConstants.NAME_ATTRIBUTE, newValue);
          }
          else
          {
            element.setAttribute(XSDConstants.NAME_ATTRIBUTE, "");
          }
          endRecording(element);
        }
      }
      else if (((String)id).equals(XSDConstants.PUBLIC_ATTRIBUTE))
       {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_NOTATION_PUBLIC_CHANGE"), element);
        if (newValue.length() > 0)
        {
          element.setAttribute(XSDConstants.PUBLIC_ATTRIBUTE, newValue);
        }
        else
        {
          element.setAttribute(XSDConstants.PUBLIC_ATTRIBUTE, "");
        }
        endRecording(element);
      }
      else if (((String)id).equals(XSDConstants.SYSTEM_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_NOTATION_SYSTEM_CHANGE"), element);
        if (newValue.length() > 0)
        {
          element.setAttribute(XSDConstants.SYSTEM_ATTRIBUTE, newValue);
        }
        else
        {
          element.removeAttribute(XSDConstants.SYSTEM_ATTRIBUTE);
        }
        endRecording(element);
      }
    }
    Runnable delayedUpdate = new Runnable()
    {
      public void run()
      {
        if (viewer != null)
          viewer.refresh();
      }
    };
    Display.getCurrent().asyncExec(delayedUpdate);
    
  }
}
