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
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class AttributeGroupRefPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  private String[] refComboValues = { "" };
  /**
   * 
   */
  public AttributeGroupRefPropertySource()
  {
    super();
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public AttributeGroupRefPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public AttributeGroupRefPropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);
  }
  
  public void setInput(Element element)
  {
    this.element = element;
    TypesHelper helper = new TypesHelper(xsdSchema);
    java.util.List items = helper.getGlobalAttributes();
      
    if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true))
    {
      items = helper.getGlobalAttributes();
//      WorkbenchHelp.setHelp(client, XSDEditorContextIds.XSDE_ATTRIBUTE_REF_DESIGN_VIEW);
//      WorkbenchHelp.setHelp(refCombo, XSDEditorContextIds.XSDE_ATTRIBUTE_REF_NAME);      
    }
    else if (XSDDOMHelper.inputEquals(element, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true))
    {
      items = helper.getGlobalAttributeGroups();
//      WorkbenchHelp.setHelp(client, XSDEditorContextIds.XSDE_ATTRIBUTE_GROUP_REF_DESIGN_VIEW);
//      WorkbenchHelp.setHelp(refCombo, XSDEditorContextIds.XSDE_ATTRIBUTE_GROUP_REF_NAME);
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
    // Create a descriptor and set a category
    XSDComboBoxPropertyDescriptor refDescriptor =
    new XSDComboBoxPropertyDescriptor(
        XSDConstants.REF_ATTRIBUTE,
        XSDConstants.REF_ATTRIBUTE,
        refComboValues);
    list.add(refDescriptor);

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
//      if (((String) id).equals(XSDConstants.REF_ATTRIBUTE))
//      {
//      }
      return result;
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
      if (((String) id).equals(XSDConstants.REF_ATTRIBUTE))
      {
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ATTRIBUTEGROUP_REF_CHANGE"), element);
        element.setAttribute(XSDConstants.REF_ATTRIBUTE, (String) value);
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
