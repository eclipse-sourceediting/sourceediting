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
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;


public class AnyElementPropertySource
  extends BasePropertySource
  implements IPropertySource
{

  private String[] namespaceComboValues = {
        "",
        "##any",
        "##other",
        "##targetNamespace",
        "##local"
  };
  
  private String[] processContentsComboValues = {
     "",
     XSDEditorPlugin.getXSDString("_UI_COMBO_LAX"),
     XSDEditorPlugin.getXSDString("_UI_COMBO_SKIP"),
     XSDEditorPlugin.getXSDString("_UI_COMBO_STRICT")
  };
  
  /**
   * 
   */
  public AnyElementPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public AnyElementPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public AnyElementPropertySource(XSDSchema xsdSchema)
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

    XSDComboBoxPropertyDescriptor namespaceDescriptor = new XSDComboBoxPropertyDescriptor(
        XSDConstants.NAMESPACE_ATTRIBUTE,
        XSDConstants.NAMESPACE_ATTRIBUTE,
        namespaceComboValues);
    list.add(namespaceDescriptor);
    
    XSDComboBoxPropertyDescriptor processContentsDescriptor = new XSDComboBoxPropertyDescriptor(
        XSDConstants.PROCESSCONTENTS_ATTRIBUTE,
        XSDConstants.PROCESSCONTENTS_ATTRIBUTE,
        processContentsComboValues);
    list.add(processContentsDescriptor);

// These are moved to the tabbed properties general section     
//    PropertyDescriptor minOccursDescriptor =
//    new TextPropertyDescriptor(
//        XSDConstants.MINOCCURS_ATTRIBUTE,
//        XSDConstants.MINOCCURS_ATTRIBUTE);
//    list.add(minOccursDescriptor);
//    PropertyDescriptor maxOccursDescriptor =
//    new TextPropertyDescriptor(
//        XSDConstants.MAXOCCURS_ATTRIBUTE,
//        XSDConstants.MAXOCCURS_ATTRIBUTE);
//    list.add(maxOccursDescriptor);
    
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
      if (result == null)
      {
        result = "";
      }
      return result;

//      if (((String) id).equals(XSDConstants.NAMESPACE_ATTRIBUTE))
//       {
//      }
//      else if (((String) id).equals(XSDConstants.PROCESSCONTENTS_ATTRIBUTE))
//       {
//      }      
    }
    return "";
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
      if (((String) id).equals(XSDConstants.MAXOCCURS_ATTRIBUTE))
      {
        String max = (String)value;
        String min = element.getAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
        beginRecording(XSDEditorPlugin.getXSDString("_UI_MAXOCCURS_CHANGE"), element);
        if (max.length() > 0)
        {
          element.setAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE, max);
        }
        else
        {
          element.removeAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);
        }
        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.MINOCCURS_ATTRIBUTE))
      {
        String min = (String)value;
        String max = element.getAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);
        beginRecording(XSDEditorPlugin.getXSDString("_UI_MINOCCURS_CHANGE"), element);
        if (min.length() > 0)
        {
          element.setAttribute(XSDConstants.MINOCCURS_ATTRIBUTE, min);
        }
        else
        {
          element.removeAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
        }
        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.NAMESPACE_ATTRIBUTE))
      {
        String namespace = (String)value;
        beginRecording(XSDEditorPlugin.getXSDString("_UI_NAMESPACE_CHANGE"), element);
        if (namespace.length() > 0)
        {
          element.setAttribute(XSDConstants.NAMESPACE_ATTRIBUTE, namespace);
        }
        else
        {
          element.removeAttribute(XSDConstants.NAMESPACE_ATTRIBUTE);
        }
        endRecording(element);
      }
      else if (((String) id).equals(XSDConstants.PROCESSCONTENTS_ATTRIBUTE))
      {
        String processContents = (String)value;
        beginRecording(XSDEditorPlugin.getXSDString("_UI_PROCESSCONTENTS_CHANGE"), element);
        if (processContents.length() > 0)
        {
          element.setAttribute(XSDConstants.PROCESSCONTENTS_ATTRIBUTE, processContents);
        }
        else
        {
          element.removeAttribute(XSDConstants.PROCESSCONTENTS_ATTRIBUTE);
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
