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
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;


public class GroupRefPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String[] refComboValues = { "" };
  /**
   * 
   */
  public GroupRefPropertySource()
  {
    super();
  }
  
//  public void setReferenceComboContextHelp(String contextId)
//  {
//    WorkbenchHelp.setHelp(refCombo, contextId);
//  }
  
  /**
   * @param viewer
   * @param xsdSchema
   */
  public GroupRefPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public GroupRefPropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);
  }
  
  public void setInput(Element element)
  {
    this.element = element;
    TypesHelper helper = new TypesHelper(xsdSchema);
    java.util.List items = helper.getGlobalElements();
    if (XSDDOMHelper.inputEquals(element, XSDConstants.GROUP_ELEMENT_TAG, true))
    {
      items = helper.getModelGroups();
      // Need tooltip for Group Ref
//        minimumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MINIMUM"));
//        WorkbenchHelp.setHelp(minimumField, XSDEditorContextIds.XSDE_GROUP_REF_MINIMUM);
//        maximumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MAXIMUM"));
//        WorkbenchHelp.setHelp(maximumField, XSDEditorContextIds.XSDE_GROUP_REF_MAXIMUM);
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.ELEMENT_ELEMENT_TAG, true))
    {
      items = helper.getGlobalElements();
//        minimumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MINIMUM"));
//        WorkbenchHelp.setHelp(minimumField, XSDEditorContextIds.XSDE_ELEMENT_REF_MINIMUM);
//        maximumField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_MAXIMUM"));
//        WorkbenchHelp.setHelp(maximumField, XSDEditorContextIds.XSDE_ELEMENT_REF_MAXIMUM);
    }
    
    int size = items.size() + 1;
    refComboValues = new String[size];
    refComboValues[0] = "";
    if (items != null)
     {
      for (int i = 0; i < items.size(); i++)
       {
        refComboValues[i + 1] = (String) items.get(i);
      }
    }
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
    XSDComboBoxPropertyDescriptor refDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.REF_ATTRIBUTE,
        XSDConstants.REF_ATTRIBUTE,
        refComboValues);
    list.add(refDescriptor);
    
    PropertyDescriptor minOccursDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.MINOCCURS_ATTRIBUTE,
        XSDConstants.MINOCCURS_ATTRIBUTE);
    list.add(minOccursDescriptor);
    PropertyDescriptor maxOccursDescriptor =
    new TextPropertyDescriptor(
        XSDConstants.MAXOCCURS_ATTRIBUTE,
        XSDConstants.MAXOCCURS_ATTRIBUTE);
    list.add(maxOccursDescriptor);
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
      
//      if (((String) id).equals(XSDConstants.REF_ATTRIBUTE))
//      {
//        return result;
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
      String newValue = (String)value;
      if (((String) id).equals(XSDConstants.MAXOCCURS_ATTRIBUTE))
      {
        String max = (String)value;
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
      else if (((String) id).equals(XSDConstants.REF_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_GROUP_REF_CHANGE"), element);
        element.setAttribute((String) id, newValue);
        endRecording(element);
      }
    }
//    Runnable delayedUpdate = new Runnable()
//    {
//      public void run()
//      {
        if (viewer != null)
          viewer.refresh();
//      }
//    };
//    Display.getCurrent().asyncExec(delayedUpdate);
    
  }

}
