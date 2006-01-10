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
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;


public class XPathPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  /**
   * 
   */
  public XPathPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public XPathPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
// From Field     
// WorkbenchHelp.setHelp(comp, XSDEditorContextIds.XSDE_UNIQUE_BASE_FIELDS_GROUP);
//    fieldField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_FIELD_TEXT"));
//    WorkbenchHelp.setHelp(fieldField, XSDEditorContextIds.XSDE_UNIQUE_BASE_SOURCE);

// From Selector
// WorkbenchHelp.setHelp(comp, XSDEditorContextIds.XSDE_UNIQUE_BASE_SELECTOR_GROUP);
// WorkbenchHelp.setHelp(selectorField, XSDEditorContextIds.XSDE_UNIQUE_BASE_SELECTOR);
// selectorField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_SELECTOR_TEXT"));
    
  }
  /**
   * @param xsdSchema
   */
  public XPathPropertySource(XSDSchema xsdSchema)
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
    PropertyDescriptor xpathDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.XPATH_ATTRIBUTE,
        XSDConstants.XPATH_ATTRIBUTE);
    list.add(xpathDescriptor);

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
    if (value != null)
     {
      if (value instanceof String)
      {
        if (XSDDOMHelper.inputEquals(element, XSDConstants.FIELD_ELEMENT_TAG, false))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_FIELD_XPATH_CHANGE"), element);
        }
        else if (XSDDOMHelper.inputEquals(element, XSDConstants.SELECTOR_ELEMENT_TAG, false))
        {
          beginRecording(XSDEditorPlugin.getXSDString("_UI_SELECTOR_XPATH_CHANGE"), element);
        }
        
        element.setAttribute(XSDConstants.XPATH_ATTRIBUTE, (String)value);
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
